package dao;

import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Review;
import model.Subject;
import model.Syllabus;
import model.SyllabusAssignment;
import model.SyllabusReviewItem;
import model.User;
import util.SyllabusReviewRubric;

public class ReviewDAO {

    private static final String BASE_SELECT =
            "SELECT rv.*, s.Subject_Code, s.Subject_Name, u.Full_Name AS Reviewer_Name "
            + "FROM Reviews rv "
            + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
            + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
            + "JOIN Users u ON rv.Reviewer_ID = u.User_ID ";

    private Review mapReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getString("Review_ID"));
        review.setSyllabusId(rs.getString("Syllabus_ID"));
        review.setReviewerId(rs.getString("Reviewer_ID"));
        review.setStatus(rs.getBoolean("Is_Approved") ? "Approved" : "Rejected");
        review.setComment(rs.getString("Comment"));
        review.setReviewDate(rs.getTimestamp("Review_Date"));
        try {
            review.setSubjectCode(rs.getString("Subject_Code"));
            review.setSubjectName(rs.getString("Subject_Name"));
        } catch (SQLException ignored) {
        }
        try {
            User reviewer = new User();
            reviewer.setUserId(rs.getString("Reviewer_ID"));
            reviewer.setFullName(rs.getString("Reviewer_Name"));
            review.setReviewer(reviewer);
        } catch (SQLException ignored) {
        }
        return review;
    }

    private Review mapSyllabusReview(ResultSet rs) throws SQLException {
        Review review = mapReview(rs);
        review.setTotalScore(rs.getDouble("Total_Score"));

        Syllabus syllabus = new Syllabus();
        syllabus.setSyllabusId(rs.getString("Syllabus_ID"));
        syllabus.setSyllabusName(rs.getString("Syllabus_Name"));
        syllabus.setVersion(rs.getString("Version"));
        syllabus.setStatus(rs.getString("Workflow_Status"));

        Subject subject = new Subject();
        subject.setSubjectId(rs.getString("Subject_ID"));
        subject.setSubjectCode(rs.getString("Subject_Code"));
        subject.setSubjectName(rs.getString("Subject_Name"));
        syllabus.setSubject(subject);
        review.setSyllabus(syllabus);
        return review;
    }

    public List<Review> getAllReviews(String keyword) {
        List<Review> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (s.Subject_Name LIKE ? OR s.Subject_Code LIKE ? OR u.Full_Name LIKE ?)");
        }
        sql.append(" ORDER BY rv.Review_Date DESC");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addReview(String syllabusId, String reviewerId, boolean isApproved, String comment) {
        String sql = "INSERT INTO Reviews (Review_ID, Syllabus_ID, Reviewer_ID, Is_Approved, Comment, Review_Date) "
                + "VALUES (NEWID(), ?, ?, ?, ?, GETDATE())";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.setString(2, reviewerId);
            ps.setInt(3, isApproved ? 1 : 0);
            ps.setString(4, comment);
            boolean saved = ps.executeUpdate() > 0;
            if (saved) {
                new SyllabusDAO().updateWorkflowStatus(
                        syllabusId,
                        isApproved ? Syllabus.STATUS_APPROVED : Syllabus.STATUS_DRAFT);
            }
            return saved;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addReview(String syllabusId, String reviewerId, String status, String comment) {
        boolean approved = status != null
                && (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Approve"));
        return addReview(syllabusId, reviewerId, approved, comment);
    }

    public Review getReviewById(String reviewId) {
        String sql = BASE_SELECT + "WHERE rv.Review_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapReview(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Review> getReviewsByCurriculum(String curriculumId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT rv.*, s.Subject_Code, s.Subject_Name, u.Full_Name AS Reviewer_Name "
                + "FROM Reviews rv "
                + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "JOIN Curriculum_Subjects cs ON cs.Subject_ID = s.Subject_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "WHERE cs.Curriculum_ID = ? ORDER BY rv.Review_Date DESC";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapReview(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SyllabusAssignment> getPendingForReviewer(String userId, boolean isAdmin) {
        List<SyllabusAssignment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sy.Syllabus_ID, sy.Status AS Syllabus_Status, s.Subject_ID, s.Subject_Code, s.Subject_Name "
                + "FROM Syllabuses sy JOIN Subjects s ON sy.Subject_ID = s.Subject_ID ");
        if (!isAdmin) {
            sql.append("JOIN Syllabus_Assignments sa ON sa.Syllabus_ID = sy.Syllabus_ID ")
               .append("AND sa.Assignment_Type = 'Reviewer' AND sa.User_ID = ? ");
        }
        sql.append("WHERE sy.Status = ").append(Syllabus.STATUS_PENDING_REVIEW)
           .append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (!isAdmin) {
                ps.setString(1, userId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SyllabusAssignment assignment = new SyllabusAssignment();
                assignment.setSyllabusId(rs.getString("Syllabus_ID"));
                assignment.setSubjectId(rs.getString("Subject_ID"));
                assignment.setSubjectCode(rs.getString("Subject_Code"));
                assignment.setSubjectName(rs.getString("Subject_Name"));
                assignment.setSyllabusStatusCode(rs.getInt("Syllabus_Status"));
                assignment.setSyllabusStatusLabel("Pending Review");
                list.add(assignment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Review> getAllSyllabusReviews(String keyword) {
        List<Review> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT rv.*, sy.Syllabus_Name, sy.Version, sy.Workflow_Status, sy.Subject_ID, "
                + "sub.Subject_Code, sub.Subject_Name, u.Full_Name AS Reviewer_Name, "
                + "COALESCE(SUM(rd.Score), 0) AS Total_Score "
                + "FROM Reviews rv "
                + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects sub ON sy.Subject_ID = sub.Subject_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "LEFT JOIN Review_Details rd ON rv.Review_ID = rd.Review_ID "
                + "WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (sub.Subject_Code LIKE ? OR sub.Subject_Name LIKE ? OR sy.Syllabus_Name LIKE ? OR u.Full_Name LIKE ?)");
        }
        sql.append(" GROUP BY rv.Review_ID, rv.Syllabus_ID, rv.Reviewer_ID, rv.Is_Approved, rv.Comment, rv.Review_Date, ")
           .append("sy.Syllabus_Name, sy.Version, sy.Workflow_Status, sy.Subject_ID, sub.Subject_Code, sub.Subject_Name, u.Full_Name ")
           .append("ORDER BY rv.Review_Date DESC");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapSyllabusReview(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Review> getReviewsBySyllabus(String syllabusId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT rv.*, sy.Syllabus_Name, sy.Version, sy.Workflow_Status, sy.Subject_ID, "
                + "sub.Subject_Code, sub.Subject_Name, u.Full_Name AS Reviewer_Name, "
                + "COALESCE(SUM(rd.Score), 0) AS Total_Score "
                + "FROM Reviews rv "
                + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects sub ON sy.Subject_ID = sub.Subject_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "LEFT JOIN Review_Details rd ON rv.Review_ID = rd.Review_ID "
                + "WHERE rv.Syllabus_ID = ? "
                + "GROUP BY rv.Review_ID, rv.Syllabus_ID, rv.Reviewer_ID, rv.Is_Approved, rv.Comment, rv.Review_Date, "
                + "sy.Syllabus_Name, sy.Version, sy.Workflow_Status, sy.Subject_ID, sub.Subject_Code, sub.Subject_Name, u.Full_Name "
                + "ORDER BY rv.Review_Date DESC";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review review = mapSyllabusReview(rs);
                review.setItems(getReviewItems(review.getReviewId()));
                list.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Review getLatestReviewBySyllabus(String syllabusId) {
        String sql = "SELECT TOP 1 rv.*, sy.Syllabus_Name, sy.Version, sy.Workflow_Status, sy.Subject_ID, "
                + "sub.Subject_Code, sub.Subject_Name, u.Full_Name AS Reviewer_Name, "
                + "COALESCE(score.Total_Score, 0) AS Total_Score "
                + "FROM Reviews rv "
                + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects sub ON sy.Subject_ID = sub.Subject_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "LEFT JOIN (SELECT Review_ID, SUM(Score) AS Total_Score FROM Review_Details GROUP BY Review_ID) score "
                + "ON rv.Review_ID = score.Review_ID "
                + "WHERE rv.Syllabus_ID = ? ORDER BY rv.Review_Date DESC";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Review review = mapSyllabusReview(rs);
                review.setItems(getReviewItems(review.getReviewId()));
                return review;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Review getSyllabusReviewById(String reviewId) {
        String sql = "SELECT rv.*, sy.Syllabus_Name, sy.Version, sy.Workflow_Status, sy.Subject_ID, "
                + "sub.Subject_Code, sub.Subject_Name, u.Full_Name AS Reviewer_Name, "
                + "COALESCE(score.Total_Score, 0) AS Total_Score "
                + "FROM Reviews rv "
                + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects sub ON sy.Subject_ID = sub.Subject_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "LEFT JOIN (SELECT Review_ID, SUM(Score) AS Total_Score FROM Review_Details GROUP BY Review_ID) score "
                + "ON rv.Review_ID = score.Review_ID "
                + "WHERE rv.Review_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Review review = mapSyllabusReview(rs);
                review.setItems(getReviewItems(reviewId));
                return review;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addSyllabusReview(String syllabusId, String reviewerId, String status, String comment,
            List<SyllabusReviewItem> items) {
        String reviewSql = "INSERT INTO Reviews (Review_ID, Syllabus_ID, Reviewer_ID, Is_Approved, Comment, Review_Date) "
                + "OUTPUT INSERTED.Review_ID VALUES (NEWID(), ?, ?, ?, ?, GETDATE())";
        String detailSql = "INSERT INTO Review_Details (Review_Detail_ID, Review_ID, Criterion_Key, Criterion_Name, Max_Score, Score, Comment) "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?)";
        boolean approved = status != null
                && ("Approved".equalsIgnoreCase(status) || "Accept".equalsIgnoreCase(status));

        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement reviewPs = con.prepareStatement(reviewSql)) {
                reviewPs.setString(1, syllabusId);
                reviewPs.setString(2, reviewerId);
                reviewPs.setBoolean(3, approved);
                reviewPs.setString(4, comment);
                ResultSet reviewRs = reviewPs.executeQuery();
                if (!reviewRs.next()) {
                    con.rollback();
                    return false;
                }
                String reviewId = reviewRs.getString(1);

                try (PreparedStatement detailPs = con.prepareStatement(detailSql)) {
                    for (SyllabusReviewItem item : items) {
                        detailPs.setString(1, reviewId);
                        detailPs.setString(2, item.getCriterionKey());
                        detailPs.setString(3, item.getCriterionName());
                        detailPs.setDouble(4, item.getMaxScore());
                        detailPs.setDouble(5, item.getScore());
                        detailPs.setString(6, item.getComment());
                        detailPs.addBatch();
                    }
                    detailPs.executeBatch();
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SyllabusReviewItem> getReviewItems(String reviewId) {
        List<SyllabusReviewItem> items = new ArrayList<>();
        String sql = "SELECT * FROM Review_Details WHERE Review_ID = ? ORDER BY Criterion_Name";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reviewId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SyllabusReviewItem item = new SyllabusReviewItem();
                item.setReviewDetailId(rs.getString("Review_Detail_ID"));
                item.setReviewId(rs.getString("Review_ID"));
                item.setCriterionKey(rs.getString("Criterion_Key"));
                item.setCriterionName(rs.getString("Criterion_Name"));
                item.setMaxScore(rs.getDouble("Max_Score"));
                item.setScore(rs.getDouble("Score"));
                item.setComment(rs.getString("Comment"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (items.isEmpty()) {
            return SyllabusReviewRubric.buildDefaultItems();
        }
        return items;
    }
}
