package controller;

import dao.CurriculumDAO;
import dao.ProgramDAO;
import dao.SubjectDAO;
import dao.ReviewDAO;
import model.Curriculum;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurriculumServlet", urlPatterns = {"/curriculum/*"})
public class CurriculumServlet extends HttpServlet {

    private final CurriculumDAO curriculumDAO = new CurriculumDAO();
    private final ProgramDAO    programDAO    = new ProgramDAO();
    private final SubjectDAO    subjectDAO    = new SubjectDAO();
    private final ReviewDAO     reviewDAO     = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // /list, /detail, /create, /edit
        if (pathInfo == null) pathInfo = "/list";

        switch (pathInfo) {
            case "/list":   showList(req, res);   break;
            case "/detail": showDetail(req, res); break;
            case "/create": showCreate(req, res); break;
            case "/edit":   showEdit(req, res);   break;
            default: res.sendRedirect(req.getContextPath() + "/curriculum/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "create":  doCreate(req, res);  break;
            case "update":  doUpdate(req, res);  break;
            case "submit":  doSubmit(req, res);  break;
            case "approve": doApprove(req, res); break;
            case "reject":  doReject(req, res);  break;
            default: res.sendRedirect(req.getContextPath() + "/curriculum/list");
        }
    }

    // ===== GET handlers =====

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user      = getLoggedUser(req);
        String keyword = req.getParameter("keyword");
        String status  = req.getParameter("status");
        boolean publicOnly = (user == null || isPublicRole(user));

        List<Curriculum> list = curriculumDAO.searchCurriculums(keyword, status, publicOnly);
        req.setAttribute("curriculums", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("totalCount", list.size());
        forward(req, res, "/WEB-INF/views/curriculum/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Curriculum c = curriculumDAO.getCurriculumById(id);
        if (c == null) { res.sendRedirect(req.getContextPath() + "/curriculum/list"); return; }
        req.setAttribute("curriculum", c);
        req.setAttribute("subjects",   subjectDAO.getSubjectsByCurriculum(id));
        req.setAttribute("reviews",    reviewDAO.getReviewsByCurriculum(id));
        forward(req, res, "/WEB-INF/views/curriculum/detail.jsp");
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        req.setAttribute("programs", programDAO.getAllPrograms());
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        String id = req.getParameter("id");
        req.setAttribute("curriculum", curriculumDAO.getCurriculumById(id));
        req.setAttribute("programs",   programDAO.getAllPrograms());
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    // ===== POST handlers =====

    private void doCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        User user = getLoggedUser(req);
        Curriculum c = buildFromRequest(req);
        c.setCreatedBy(user.getUserId());
        curriculumDAO.addCurriculum(c);
        res.sendRedirect(req.getContextPath() + "/curriculum/list?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        Curriculum c = buildFromRequest(req);
        c.setCurriculumId(req.getParameter("curriculumId"));
        curriculumDAO.updateCurriculum(c);
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + c.getCurriculumId() + "&msg=updated");
    }

    private void doSubmit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        String id = req.getParameter("curriculumId");
        curriculumDAO.submitForReview(id);
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + id + "&msg=submitted");
    }

    private void doApprove(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Reviewer", "Admin")) return;
        User user = getLoggedUser(req);
        String id      = req.getParameter("curriculumId");
        String comment = req.getParameter("comment");
        curriculumDAO.approveCurriculum(id);
        reviewDAO.addReview(id, user.getUserId(), "Approved", comment);
        res.sendRedirect(req.getContextPath() + "/review/list?msg=approved");
    }

    private void doReject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Reviewer", "Admin")) return;
        User user = getLoggedUser(req);
        String id      = req.getParameter("curriculumId");
        String comment = req.getParameter("comment");
        curriculumDAO.rejectCurriculum(id);
        reviewDAO.addReview(id, user.getUserId(), "Rejected", comment);
        res.sendRedirect(req.getContextPath() + "/review/list?msg=rejected");
    }

    // ===== Helpers =====

    private Curriculum buildFromRequest(HttpServletRequest req) {
        Curriculum c = new Curriculum();
        c.setProgramId(req.getParameter("programId"));
        c.setCurriculumCode(req.getParameter("curriculumCode"));
        c.setCurriculumName(req.getParameter("curriculumName"));
        c.setEnglishName(req.getParameter("englishName"));
        c.setDescription(req.getParameter("description"));
        try { c.setTotalCredits(Integer.parseInt(req.getParameter("totalCredits"))); } catch (Exception ignored) {}
        c.setVersion(req.getParameter("version"));
        c.setDecisionNo(req.getParameter("decisionNo"));
        return c;
    }

    private User getLoggedUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? (User) s.getAttribute("loggedUser") : null;
    }

    private boolean isPublicRole(User u) {
        String role = u.getRole() != null ? u.getRole().getRoleName() : "";
        return role.equals("Guest") || role.equals("Student");
    }

    private boolean requireRole(HttpServletRequest req, HttpServletResponse res, String... roles)
            throws IOException {
        User user = getLoggedUser(req);
        if (user == null) { res.sendRedirect(req.getContextPath() + "/login"); return false; }
        String userRole = user.getRole() != null ? user.getRole().getRoleName() : "";
        for (String r : roles) if (r.equals(userRole)) return true;
        res.sendRedirect(req.getContextPath() + "/curriculum/list");
        return false;
    }

    private void forward(HttpServletRequest req, HttpServletResponse res, String path)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, res);
    }
}
