package controller;

import dao.ReviewDAO;
import dao.SyllabusDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.CurriculumSubject;
import model.SyllabusReviewItem;
import model.User;
import util.SyllabusReviewRubric;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review/*"})
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final SyllabusDAO syllabusDAO = new SyllabusDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/list";
        }
        switch (pathInfo) {
            case "/list":
                showList(req, res);
                break;
            case "/detail":
                showDetail(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/review/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            res.sendRedirect(req.getContextPath() + "/review/list");
            return;
        }
        switch (pathInfo) {
            case "/submit":
                submitSyllabusReview(req, res);
                break;
            case "/publish":
                publishSyllabus(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/review/list");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireReviewer(req, res)) {
            return;
        }
        User user = (User) req.getSession().getAttribute("loggedUser");
        boolean isAdmin = user.hasRole("Admin");
        String keyword = req.getParameter("keyword");
        List<CurriculumSubject> pendingSyllabuses = syllabusDAO.getAssignedSubjectsForReviewer(user.getUserId(), keyword);

        req.setAttribute("pendingSyllabuses", pendingSyllabuses);
        req.setAttribute("reviews", reviewDAO.getAllSyllabusReviews(keyword));
        if (isAdmin) {
            req.setAttribute("readyToPublish",
                    syllabusDAO.getSyllabusesByWorkflowStatus(SyllabusDAO.STATUS_APPROVED_FOR_PUBLISH, keyword));
        }
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
        boolean isAdmin = user.hasRole("Admin");
        boolean isDesigner = user.hasRole("Designer");
        
        // Admin and Reviewer can access; pure Designer cannot review
        if (!isAdmin && !user.hasRole("Reviewer")) {
            if (isDesigner) {
                res.sendRedirect(req.getContextPath() + "/design/list");
            } else {
                res.sendRedirect(req.getContextPath() + "/curriculum/list");
            }
            return false;
        }
        return true;
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String syllabusId = req.getParameter("syllabusId");
        res.sendRedirect(req.getContextPath() + "/syllabus/detail?id=" + syllabusId);
    }

    private void submitSyllabusReview(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (!requireReviewer(req, res)) {
            return;
        }

        User user = (User) req.getSession().getAttribute("loggedUser");
        boolean isAdmin = user.hasRole("Admin");
        String syllabusId = req.getParameter("syllabusId");
        if (syllabusId == null || syllabusId.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/review/list?msg=missingSyllabus");
            return;
        }
        if (!isAdmin && !syllabusDAO.isUserAssignedAsReviewer(syllabusId, user.getUserId())) {
            res.sendRedirect(req.getContextPath() + "/review/list?msg=forbidden");
            return;
        }

        String decision = req.getParameter("decision");
        String overallComment = req.getParameter("overallComment");
        List<SyllabusReviewItem> items = extractReviewItems(req);
        if (items.isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/syllabus/detail?id=" + syllabusId + "&msg=missingCriteria");
            return;
        }

        boolean approved = "approve".equalsIgnoreCase(decision) || "accepted".equalsIgnoreCase(decision);
        boolean saved = reviewDAO.addSyllabusReview(
                syllabusId,
                user.getUserId(),
                approved ? "Approved" : "Rejected",
                overallComment,
                items);
        if (saved) {
            if (approved) {
                syllabusDAO.approveForPublish(syllabusId);
                res.sendRedirect(req.getContextPath() + "/syllabus/detail?id=" + syllabusId + "&msg=approvedForPublish");
            } else {
                syllabusDAO.requestChanges(syllabusId);
                res.sendRedirect(req.getContextPath() + "/syllabus/detail?id=" + syllabusId + "&msg=changesRequested");
            }
            return;
        }
        res.sendRedirect(req.getContextPath() + "/syllabus/detail?id=" + syllabusId + "&msg=reviewFailed");
    }

    private void publishSyllabus(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("loggedUser");
        if (!user.hasRole("Admin")) {
            res.sendRedirect(req.getContextPath() + "/review/list?msg=forbidden");
            return;
        }

        String syllabusId = req.getParameter("syllabusId");
        if (syllabusId != null && syllabusDAO.publishSyllabus(syllabusId)) {
            res.sendRedirect(req.getContextPath() + "/syllabus/detail?id=" + syllabusId + "&msg=published");
            return;
        }
        res.sendRedirect(req.getContextPath() + "/review/list?msg=publishFailed");
    }

    private List<SyllabusReviewItem> extractReviewItems(HttpServletRequest req) {
        String[] keys = req.getParameterValues("criterionKey");
        String[] names = req.getParameterValues("criterionName");
        String[] maxScores = req.getParameterValues("criterionMaxScore");
        String[] scores = req.getParameterValues("criterionScore");
        String[] comments = req.getParameterValues("criterionComment");

        List<SyllabusReviewItem> items = new ArrayList<>();
        if (keys == null || names == null || maxScores == null || scores == null) {
            return items;
        }

        for (int index = 0; index < keys.length; index++) {
            SyllabusReviewItem item = new SyllabusReviewItem();
            item.setCriterionKey(keys[index]);
            item.setCriterionName(index < names.length ? names[index] : keys[index]);
            item.setMaxScore(parseScore(index < maxScores.length ? maxScores[index] : null));
            item.setScore(parseBoundedScore(index < scores.length ? scores[index] : null, item.getMaxScore()));
            item.setComment(comments != null && index < comments.length ? comments[index] : "");
            items.add(item);
        }

        if (items.size() != SyllabusReviewRubric.getCriteria().size()) {
            return new ArrayList<>();
        }
        return items;
    }

    private double parseScore(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            return 0;
        }
    }

    private double parseBoundedScore(String value, double maxScore) {
        double parsed = parseScore(value);
        if (parsed < 0) {
            return 0;
        }
        if (parsed > maxScore) {
            return maxScore;
        }
        return parsed;
    }
}