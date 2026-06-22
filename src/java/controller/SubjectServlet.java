package controller;

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
        req.getRequestDispatcher("/WEB-INF/views/subject/form.jsp").forward(req, res);
    }

    private void doCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        model.Subject s = new model.Subject();
        s.setSubjectCode(req.getParameter("subjectCode"));
        s.setSubjectName(req.getParameter("subjectName"));
        s.setEnglishName(req.getParameter("englishName"));
        try { s.setCredits(Integer.parseInt(req.getParameter("credits"))); } catch (Exception ignored) {}
        s.setDescription(req.getParameter("description"));
        s.setDepartment(req.getParameter("department"));
        subjectDAO.addSubject(s);
        res.sendRedirect(req.getContextPath() + "/subject/list?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
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
}
