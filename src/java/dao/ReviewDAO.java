package dao;

import dal.DBContext;
import model.Review;
import model.Syllabus;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bang Reviews. Schema moi: Reviews.Syllabus_ID (khong con Curriculum_ID)
 * -> moi Review gan voi 1 Syllabus/Subject cu the, dung voi luong "duyet tung subject".
 */
public class ReviewDAO {

    private static final String BASE_SELECT =
            "SELECT rv.*, s.Subject_Code, s.Subject_Name, u.Full_Name AS Reviewer_Name "
          + "FROM Reviews rv "
          + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
          + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
          + "JOIN Users u ON rv.Reviewer_ID = u.User_ID ";

    private Review mapReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getString("Review_ID"));
        r.setSyllabusId(rs.getString("Syllabus_ID"));
        r.setReviewerId(rs.getString("Reviewer_ID"));
        try {
            boolean approved = rs.getBoolean("Is_Approved");
            r.setStatus(approved ? "Approved" : "Rejected");
        } catch (SQLException ignored) {}
        r.setComment(rs.getString("Comment"));
        r.setReviewDate(rs.getTimestamp("Review_Date"));
        try {
            r.setSubjectCode(rs.getString("Subject_Code"));
            r.setSubjectName(rs.getString("Subject_Name"));
        } catch (SQLException ignored) {}
        try {
            User u = new User();
            u.setUserId(rs.getString("Reviewer_ID"));
            u.setFullName(rs.getString("Reviewer_Name"));
            r.setReviewer(u);
        } catch (SQLException ignored) {}
        return r;
    }

    /** Lay tat ca review (toan he thong), loc theo mon hoc/nguoi duyet. */
    public List<Review> getAllReviews(String keyword) {
        List<Review> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (s.Subject_Name LIKE ? OR s.Subject_Code LIKE ? OR u.Full_Name LIKE ?)");
        }
        sql.append(" ORDER BY rv.Review_Date DESC");
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                ps.setString(3, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapReview(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Reviewer duyet (approve) hoac tu choi (reject) 1 Syllabus cu the.
     * Ghi 1 dong vao Reviews, DONG THOI cap nhat Syllabuses.Status:
     *   approve -> 2 (Approved, subject duoc tinh la "hoan thanh")
     *   reject  -> 0 (Draft, tra ve cho Designer sua lai)
     */
    public boolean addReview(String syllabusId, String reviewerId, boolean isApproved, String comment) {
        String sql = "INSERT INTO Reviews (Review_ID, Syllabus_ID, Reviewer_ID, Is_Approved, Comment, Review_Date) "
                + "VALUES (NEWID(), ?, ?, ?, ?, GETDATE())";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.setString(2, reviewerId);
            ps.setInt(3, isApproved ? 1 : 0);
            ps.setString(4, comment);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                new SyllabusDAO().updateWorkflowStatus(syllabusId,
                        isApproved ? Syllabus.STATUS_APPROVED : Syllabus.STATUS_DRAFT);
            }
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Legacy overload (status string "Approved"/"Rejected") de tuong thich cac cho con goi kieu cu. */
    public boolean addReview(String syllabusId, String reviewerId, String status, String comment) {
        boolean approved = status != null && (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Approve"));
        return addReview(syllabusId, reviewerId, approved, comment);
    }

    public Review getReviewById(String reviewId) {
        String sql = BASE_SELECT + "WHERE rv.Review_ID = ?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapReview(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lich su review cua 1 Syllabus cu the. */
    public List<Review> getReviewsBySyllabus(String syllabusId) {
        List<Review> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE rv.Syllabus_ID = ? ORDER BY rv.Review_Date DESC";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapReview(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lich su review cua TAT CA subject thuoc 1 Curriculum (dung cho tab
     * "Review History" trong trang chi tiet Curriculum).
     */
    public List<Review> getReviewsByCurriculum(String curriculumId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT rv.*, s.Subject_Code, s.Subject_Name, u.Full_Name AS Reviewer_Name "
                + "FROM Reviews rv "
                + "JOIN Syllabuses sy ON rv.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "JOIN Curriculum_Subjects cs ON cs.Subject_ID = s.Subject_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "WHERE cs.Curriculum_ID = ? ORDER BY rv.Review_Date DESC";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapReview(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Danh sach Syllabus dang cho duyet (Status = PendingReview) ma 1 Reviewer
     * duoc phan cong, hoac tat ca neu isAdmin = true.
     */
    public List<model.SyllabusAssignment> getPendingForReviewer(String userId, boolean isAdmin) {
        List<model.SyllabusAssignment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sy.Syllabus_ID, sy.Status AS Syllabus_Status, s.Subject_ID, s.Subject_Code, s.Subject_Name "
              + "FROM Syllabuses sy JOIN Subjects s ON sy.Subject_ID = s.Subject_ID ");
        if (!isAdmin) {
            sql.append("JOIN Syllabus_Assignments sa ON sa.Syllabus_ID = sy.Syllabus_ID "
                     + "AND sa.Assignment_Type = 'Reviewer' AND sa.User_ID = ? ");
        }
        sql.append("WHERE sy.Status = ").append(Syllabus.STATUS_PENDING_REVIEW)
           .append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (!isAdmin) ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.SyllabusAssignment a = new model.SyllabusAssignment();
                a.setSyllabusId(rs.getString("Syllabus_ID"));
                a.setSubjectId(rs.getString("Subject_ID"));
                a.setSubjectCode(rs.getString("Subject_Code"));
                a.setSubjectName(rs.getString("Subject_Name"));
                a.setSyllabusStatusCode(rs.getInt("Syllabus_Status"));
                a.setSyllabusStatusLabel("Pending Review");
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
