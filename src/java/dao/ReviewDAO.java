package dao;

import dal.DBContext;
import model.Review;
import model.Curriculum;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private Review mapReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getString("Review_ID"));
        r.setCurriculumId(rs.getString("Curriculum_ID"));
        r.setReviewerId(rs.getString("Reviewer_ID"));
        // map Is_Approved -> status
        try {
            boolean approved = rs.getBoolean("Is_Approved");
            r.setStatus(approved ? "Approved" : "Rejected");
        } catch (SQLException ex) {
            try {
                r.setStatus(rs.getString("Status"));
            } catch (SQLException ignored) {
            }
        }
        r.setComment(rs.getString("Comment"));
        r.setReviewDate(rs.getTimestamp("Review_Date"));
        // join Curriculum
        try {
            Curriculum c = new Curriculum();
            c.setCurriculumId(rs.getString("Curriculum_ID"));
            c.setCurriculumCode(rs.getString("Curriculum_Code"));
            c.setCurriculumName(rs.getString("Curriculum_Name"));
            try {
                c.setIsActive(rs.getBoolean("Curr_Status"));
            } catch (SQLException ignored) {
                ignored.printStackTrace();
            }
            r.setCurriculum(c);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        // join Reviewer
        try {
            User u = new User();
            u.setUserId(rs.getString("Reviewer_ID"));
            u.setFullName(rs.getString("Reviewer_Name"));
            r.setReviewer(u);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return r;
    }

    /**
     * Lay tat ca review kem thong tin curriculum va reviewer
     */
    public List<Review> getAllReviews(String keyword) {
        List<Review> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT rv.*, c.Curriculum_Code, c.Curriculum_Name, c.Is_Active AS Curr_Status, "
            + "u.Full_Name AS Reviewer_Name FROM Reviews rv "
                + "JOIN Curriculums c ON rv.Curriculum_ID = c.Curriculum_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.Curriculum_Name LIKE ? OR u.Full_Name LIKE ?)");
        }
        sql.append(" ORDER BY rv.Review_Date DESC");
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                if (keyword != null && !keyword.trim().isEmpty()) {
                    ps.setString(1, "%" + keyword + "%");
                    ps.setString(2, "%" + keyword + "%");
                }
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

    /**
     * Them review (Approve hoac Reject)
     */
    public boolean addReview(String curriculumId, String reviewerId, String status, String comment) {
        String sql = "INSERT INTO Reviews (Review_ID, Curriculum_ID, Reviewer_ID, Is_Approved, Comment, Review_Date) "
                + "VALUES (NEWID(), ?, ?, ?, ?, GETDATE())";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.setString(2, reviewerId);
            // map status string to bit
            int approved = (status != null && (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Approve") || status.equals("1"))) ? 1 : 0;
            ps.setInt(3, approved);
            ps.setString(4, comment);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lay review theo ID
     */
    public Review getReviewById(String reviewId) {
        String sql = "SELECT rv.*, c.Curriculum_Code, c.Curriculum_Name, c.Is_Active AS Curr_Status, "
                + "u.Full_Name AS Reviewer_Name FROM Reviews rv "
                + "JOIN Curriculums c ON rv.Curriculum_ID = c.Curriculum_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID WHERE rv.Review_ID = ?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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
 
    /**
     * Lay lich su review cua 1 curriculum
     */
    public List<Review> getReviewsByCurriculum(String curriculumId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT rv.*, c.Curriculum_Code, c.Curriculum_Name, c.Is_Active AS Curr_Status, "
                + "u.Full_Name AS Reviewer_Name FROM Reviews rv "
                + "JOIN Curriculums c ON rv.Curriculum_ID = c.Curriculum_ID "
                + "JOIN Users u ON rv.Reviewer_ID = u.User_ID "
                + "WHERE rv.Curriculum_ID = ? ORDER BY rv.Review_Date DESC";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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
}
