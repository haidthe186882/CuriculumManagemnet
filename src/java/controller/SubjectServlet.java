package controller;

import dao.MajorDAO;
import dao.SubjectDAO;
import dao.SyllabusDAO;
import model.User;
import model.Syllabus;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "SubjectServlet", urlPatterns = {"/subject/*"})
public class SubjectServlet extends HttpServlet {

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final SyllabusDAO syllabusDAO = new SyllabusDAO();
    private final MajorDAO majorDAO = new MajorDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":   showList(req, res);   break;
            case "/detail": showDetail(req, res); break;
            case "/create": showCreate(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/subject/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("create".equals(action))      doCreate(req, res);
        else if ("update".equals(action)) doUpdate(req, res);
        else res.sendRedirect(req.getContextPath() + "/subject/list");
    }

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String keyword    = req.getParameter("keyword");
        String department = req.getParameter("department");
        String creditsStr = req.getParameter("credits");
        Integer credits = null;
        try { if (creditsStr != null && !creditsStr.isEmpty()) credits = Integer.parseInt(creditsStr); } catch (Exception ignored) {}

        req.setAttribute("subjects",    subjectDAO.searchSubjects(keyword, department, credits));
        req.setAttribute("departments", subjectDAO.getAllDepartments());
        req.setAttribute("keyword",     keyword);
        req.setAttribute("department",  department);
        req.getRequestDispatcher("/WEB-INF/views/subject/list.jsp").forward(req, res);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        req.setAttribute("subject", subjectDAO.getSubjectById(id));
        req.setAttribute("syllabus", syllabusDAO.getSyllabusBySubject(id));
        req.getRequestDispatcher("/WEB-INF/views/subject/detail.jsp").forward(req, res);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        req.setAttribute("majors", majorDAO.getAllMajors());
        req.getRequestDispatcher("/WEB-INF/views/subject/form.jsp").forward(req, res);
    }

    private void doCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;

        model.Subject s = new model.Subject();
        s.setSubjectCode(req.getParameter("subjectCode"));
        s.setSubjectName(req.getParameter("subjectName"));
        s.setEnglishName(req.getParameter("englishName"));
        try { s.setCredits(Integer.parseInt(req.getParameter("credits"))); } catch (Exception ignored) {}
        s.setDescription(req.getParameter("description"));
        s.setDepartment(req.getParameter("department"));

        boolean ok = subjectDAO.addSubject(s);
        if (ok) {
            res.sendRedirect(req.getContextPath() + "/subject/list?msg=created");
        } else {
            // Insert that nhat thuong la do Subject_Code da ton tai (UNIQUE constraint)
            req.setAttribute("errorMessage",
                    "Could not create subject. The Subject Code \"" + s.getSubjectCode()
                    + "\" may already exist, please choose another one.");
            req.setAttribute("subject", s);
            req.setAttribute("majors", majorDAO.getAllMajors());
            req.getRequestDispatcher("/WEB-INF/views/subject/form.jsp").forward(req, res);
        }
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;

        model.Subject s = new model.Subject();
        s.setSubjectId(req.getParameter("subjectId"));
        s.setSubjectName(req.getParameter("subjectName"));
        s.setEnglishName(req.getParameter("englishName"));
        try { s.setCredits(Integer.parseInt(req.getParameter("credits"))); } catch (Exception ignored) {}
        s.setDescription(req.getParameter("description"));
        s.setDepartment(req.getParameter("department"));
        subjectDAO.updateSubject(s);
        res.sendRedirect(req.getContextPath() + "/subject/list?msg=updated");
    }

    // ===== Helpers =====

    private User getLoggedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("loggedUser") : null;
    }

    private boolean requireRole(HttpServletRequest req, HttpServletResponse res, String... roles)
            throws IOException {
        User user = getLoggedUser(req);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        String userRole = (user.getRole() != null) ? user.getRole().toString() : "";
        for (String r : roles) {
            if (r.equals(userRole)) return true;
        }
        res.sendRedirect(req.getContextPath() + "/subject/list");
        return false;
    }
}