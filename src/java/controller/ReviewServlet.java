package controller;

import dao.ReviewDAO;
import dao.CurriculumDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review/*"})
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO     reviewDAO     = new ReviewDAO();
    private final CurriculumDAO curriculumDAO = new CurriculumDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":   showList(req, res);   break;
            case "/detail": showDetail(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/review/list");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireReviewer(req, res)) return;
        String keyword = req.getParameter("keyword");
        req.setAttribute("reviews", reviewDAO.getAllReviews(keyword));
        req.setAttribute("pendingCurriculums", curriculumDAO.getPendingCurriculums());
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/WEB-INF/views/review/list.jsp").forward(req, res);
    }

    private boolean requireReviewer(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        User user = (User) session.getAttribute("loggedUser");
        String role = user.getRole() != null ? user.getRole().getRoleName() : "";
//        if (!"Reviewer".equals(role) && !"Admin".equals(role)) {
//            res.sendRedirect(req.getContextPath() + "/curriculum/list");
//            return false;
//        }
        boolean isPrimaryReviewer = "Reviewer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role);
        // 2. Kiểm tra trong danh sách Roles 
        boolean hasRoleReviewer = user.hasRole("Reviewer") || user.hasRole("Admin");
        // 3. Kiểm tra cờ phụ
        boolean hasSubReviewer = user.isReviewer();
        if (!isPrimaryReviewer && !hasRoleReviewer && !hasSubReviewer) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return false;
        }
        return true;
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String curriculumId = req.getParameter("curriculumId");
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + curriculumId);
    }
}
