package dao;

import dal.DBContext;
import model.Curriculum;
import model.Major;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurriculumDAO {

    private Curriculum mapCurriculum(ResultSet rs) throws SQLException {
        Curriculum c = new Curriculum();
        c.setCurriculumId(rs.getString("Curriculum_ID"));
        c.setMajorId(rs.getString("Major_ID"));
        c.setMajorName(rs.getString("Major_Name"));
        c.setCreatedBy(rs.getString("Created_By"));
        c.setCurriculumCode(rs.getString("Curriculum_Code"));
        c.setCurriculumName(rs.getString("Curriculum_Name"));
        c.setEnglishName(rs.getString("English_Name"));
        c.setDescription(rs.getString("Description"));
        c.setTotalCredits(rs.getInt("Total_Credits"));
        c.setVersion(rs.getString("Version"));
        c.setDecisionNo(rs.getString("Decision_No"));
        c.setDecisionDate(rs.getDate("Decision_Date"));
        try {
            c.setIsActive(rs.getBoolean("Is_Active"));
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        try {
            c.setCreatedDate(rs.getTimestamp("Created_Date"));
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        c.setUpdatedDate(rs.getTimestamp("Updated_Date"));
        // join Major
        try {
            Major m = new Major();
            m.setMajorId(rs.getString("Major_ID"));
            m.setMajorName(rs.getString("Major_Name"));
            m.setMajorCode(rs.getString("Major_Code"));
            c.setMajorId(m.getMajorId());
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return c;
    }

    /**
     * Tim kiem curriculum (public: chi hien Approved + IsPublic)
     */
    public List<Curriculum> searchCurriculums(String keyword, String isActive, boolean publicOnly) {
        List<Curriculum> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, m.Major_Name, m.Major_Code FROM Curriculums c "
                + "JOIN Majors m ON c.Major_ID = m.Major_ID WHERE 1=1");
        if (publicOnly) {
            sql.append(" AND c.Is_Active=1");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.Curriculum_Name LIKE ? OR c.Curriculum_Code LIKE ? OR c.English_Name LIKE ?)");
        }
        if (isActive != null && !isActive.trim().isEmpty() && !publicOnly) {
            sql.append(" AND c.Is_Active = ?");
        }
        sql.append(" ORDER BY c.Created_Date DESC");
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (isActive != null && !isActive.trim().isEmpty() && !publicOnly) {
                ps.setInt(idx, Integer.parseInt(isActive));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapCurriculum(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lay curriculum theo ID
     */
    public Curriculum getCurriculumById(String id) {
        String sql = "SELECT c.*, m.Major_Name, m.Major_Code FROM Curriculums c "
                + "JOIN Majors m ON c.Major_ID = m.Major_ID WHERE c.Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapCurriculum(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lay danh sach cho Designer/Admin (tat ca trang thai)
     */
    public List<Curriculum> getAllCurriculums(String keyword, String isActive) {
        return searchCurriculums(keyword, isActive, false);
    }

    /**
     * Lay danh sach curriculum cho Reviewer (Pending)
     */
    public List<Curriculum> getPendingCurriculums() {
        return searchCurriculums(null, "Pending", false);
    }

    /**
     * Them curriculum moi
     */
    public boolean addCurriculum(Curriculum c) {
        String sql = "INSERT INTO Curriculums (Curriculum_ID, Major_Name, Created_By, Curriculum_Code, "
                + "Curriculum_Name, English_Name, Description, Total_Credits, Version, Decision_No, "
                + "Decision_Date, Is_Active, Created_Date) "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, GETDATE())";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getMajorName());
            ps.setString(2, c.getCreatedBy());
            ps.setString(3, c.getCurriculumCode());
            ps.setString(4, c.getCurriculumName());
            ps.setString(5, c.getEnglishName());
            ps.setString(6, c.getDescription());
            ps.setInt(7, c.getTotalCredits());
            ps.setString(8, c.getVersion());
            ps.setString(9, c.getDecisionNo());
            ps.setDate(10, c.getDecisionDate() != null ? new java.sql.Date(c.getDecisionDate().getTime()) : null);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cap nhat curriculum
     */
    public boolean updateCurriculum(Curriculum c) {
        String sql = "UPDATE Curriculums SET Curriculum_Name=?, English_Name=?, Description=?, "
                + "Total_Credits=?, Version=?, Decision_No=?, Decision_Date=?, Updated_Date=GETDATE() "
                + "WHERE Curriculum_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCurriculumName());
            ps.setString(2, c.getEnglishName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getTotalCredits());
            ps.setString(5, c.getVersion());
            ps.setString(6, c.getDecisionNo());
            ps.setDate(7, c.getDecisionDate() != null ? new java.sql.Date(c.getDecisionDate().getTime()) : null);
            ps.setString(8, c.getCurriculumId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Doi trang thai: Submit for Review
     */
    public boolean submitForReview(String curriculumId) {
        return changeStatus(curriculumId, "Pending");
    }

    /**
     * Doi trang thai: Approve
     */
    public boolean approveCurriculum(String curriculumId) {
        String sql = "UPDATE Curriculums SET Is_Active=1, Updated_Date=GETDATE() WHERE Curriculum_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Doi trang thai: Reject
     */
    public boolean rejectCurriculum(String curriculumId) {
        return changeStatus(curriculumId, "Draft");
    }

    /**
     * Toggle Is_Active
     */
    public boolean toggleActive(String curriculumId, boolean isActive) {
        String sql = "UPDATE Curriculums SET Is_Active=?, Updated_Date=GETDATE() WHERE Curriculum_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setString(2, curriculumId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean changeStatus(String id, String status) {
        // Status column removed; just update Updated_Date to track change
        String sql = "UPDATE Curriculums SET Updated_Date=GETDATE() WHERE Curriculum_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
