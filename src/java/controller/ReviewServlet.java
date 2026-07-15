package controller;

import dao.ReviewDAO;
import model.Review;
import model.SyllabusAssignment;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * Man hinh Review: duyet TUNG Subject/Syllabus rieng le (khong con duyet ca
 * Curriculum 1 lan). Reviewer thay danh sach Syllabus dang PendingReview ma
 * minh duoc phan cong, approve/reject se cap nhat Syllabuses.Status va ghi 1
 * dong vao Reviews.
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/review/*"})
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        if ("/list".equals(pathInfo)) {
            showList(req, res);
        } else {
            res.sendRedirect(req.getContextPath() + "/review/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        switch (pathInfo) {
            case "/approve":
                doDecision(req, res, true);
                break;
            case "/reject":
                doDecision(req, res, false);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/review/list");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = requireReviewer(req, res);
        if (user == null) return;

        String primaryRole = user.getRole() != null ? user.getRole().getRoleName() : "";
        boolean isAdmin = "Admin".equalsIgnoreCase(primaryRole) || user.hasRole("Admin");

        String keyword = req.getParameter("keyword");
        List<SyllabusAssignment> pendingSyllabuses = reviewDAO.getPendingForReviewer(user.getUserId(), isAdmin);
        List<Review> reviews = reviewDAO.getAllReviews(keyword);

        req.setAttribute("pendingSyllabuses", pendingSyllabuses);
        req.setAttribute("reviews", reviews);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/WEB-INF/views/review/list.jsp").forward(req, res);
    }

    /** Reviewer (hoac Admin) duyet dat / tu choi 1 Syllabus cu the. */
    private void doDecision(HttpServletRequest req, HttpServletResponse res, boolean approve) throws IOException {
        User user = requireReviewer(req, res);
        if (user == null) return;

        String syllabusId = req.getParameter("syllabusId");
        String comment = req.getParameter("comment");
        if (syllabusId != null && !syllabusId.trim().isEmpty()) {
            reviewDAO.addReview(syllabusId, user.getUserId(), approve, comment);
        }
        res.sendRedirect(req.getContextPath() + "/review/list?msg=" + (approve ? "approved" : "rejected"));
    }

    /** Bat buoc dang nhap va co Role = Reviewer (Admin duoc xem cung de tien debug/quan ly). */
    private User requireReviewer(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        User user = (User) session.getAttribute("loggedUser");
        String primaryRole = user.getRole() != null ? user.getRole().getRoleName() : "";
        boolean isReviewer = "Reviewer".equalsIgnoreCase(primaryRole) || user.hasRole("Reviewer") || user.isReviewer();
        boolean isAdmin = "Admin".equalsIgnoreCase(primaryRole) || user.hasRole("Admin");
        if (!isReviewer && !isAdmin) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return null;
        }
        return user;
    }
}
