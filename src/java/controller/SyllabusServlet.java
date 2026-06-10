package controller;

import dao.SyllabusDAO;
import dao.SubjectDAO;
import model.Syllabus;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SyllabusServlet", urlPatterns = {"/syllabus/*"})
public class SyllabusServlet extends HttpServlet {

    private final SyllabusDAO syllabusDAO = new SyllabusDAO();
    private final SubjectDAO  subjectDAO  = new SubjectDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":   showList(req, res);   break;
            case "/detail": showDetail(req, res); break;
            case "/create": showCreate(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/syllabus/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (!"create".equals(action)) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }
        if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Syllabus s = new Syllabus();
        s.setSubjectId(req.getParameter("subjectId"));
        s.setSyllabusName(req.getParameter("syllabusName"));
        s.setEnglishName(req.getParameter("englishName"));
        s.setVersion(req.getParameter("version"));
        s.setDescription(req.getParameter("description"));
        s.setTimeAllocation(req.getParameter("timeAllocation"));
        s.setScoringScale(req.getParameter("scoringScale"));
        try { s.setMinAvgMarkToPass(Double.parseDouble(req.getParameter("minAvgMarkToPass"))); } catch (Exception ignored) {}
        syllabusDAO.addSyllabus(s);
        res.sendRedirect(req.getContextPath() + "/syllabus/list?msg=created");
    }

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String status  = req.getParameter("status");
        User user = getLoggedUser(req);
        boolean activeOnly = user == null;
        List<Syllabus> list = syllabusDAO.searchSyllabuses(keyword, status, activeOnly);
        req.setAttribute("syllabuses", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher("/WEB-INF/views/syllabus/list.jsp").forward(req, res);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Syllabus s = syllabusDAO.getSyllabusById(id);
        if (s == null) { res.sendRedirect(req.getContextPath() + "/syllabus/list"); return; }
        req.setAttribute("syllabus", s);
        req.getRequestDispatcher("/WEB-INF/views/syllabus/detail.jsp").forward(req, res);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("subjects", subjectDAO.searchSubjects(null, null, null));
        req.getRequestDispatcher("/WEB-INF/views/syllabus/form.jsp").forward(req, res);
    }

    private User getLoggedUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? (User) s.getAttribute("loggedUser") : null;
    }

    private boolean hasRole(HttpServletRequest req, String... roles) {
        User user = getLoggedUser(req);
        if (user == null) return false;
        String userRole = user.getRole() != null ? user.getRole().getRoleName() : "";
        for (String r : roles) if (r.equals(userRole)) return true;
        return false;
    }
}
