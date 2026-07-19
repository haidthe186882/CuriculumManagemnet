package dao;

import dal.DBContext;
import model.Syllabus;
import model.SyllabusAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bang Syllabus_Assignments (phan cong Designer/Reviewer theo TUNG
 * Subject/Syllabus - dung voi luong moi: Admin import Curriculum tu Excel,
 * subject nao chua co (Subject_Code moi) thi phai duoc gan Designer thiet ke
 * roi Reviewer duyet truoc khi Curriculum co the Publish).
 */
public class DesignDAO {

    private static final String BASE_SELECT =
            "SELECT sa.*, sy.Subject_ID, sy.Status AS Syllabus_Status, sy.Workflow_Status, "
          + "s.Subject_Code, s.Subject_Name "
          + "FROM Syllabus_Assignments sa "
          + "JOIN Syllabuses sy ON sa.Syllabus_ID = sy.Syllabus_ID "
          + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID ";

    private SyllabusAssignment mapAssignment(ResultSet rs) throws SQLException {
        SyllabusAssignment a = new SyllabusAssignment();
        a.setAssignmentId(rs.getString("Assignment_ID"));
        a.setSyllabusId(rs.getString("Syllabus_ID"));
        a.setUserId(rs.getString("User_ID"));
        a.setAssignmentType(rs.getString("Assignment_Type"));
        a.setAssignedBy(rs.getString("Assigned_By"));
        a.setAssignedDate(rs.getTimestamp("Assigned_Date"));
        a.setSubjectId(rs.getString("Subject_ID"));
        a.setSubjectCode(rs.getString("Subject_Code"));
        a.setSubjectName(rs.getString("Subject_Name"));
        int statusCode = 0;
        try { statusCode = rs.getInt("Syllabus_Status"); } catch (SQLException ignored) {}
        a.setSyllabusStatusCode(statusCode);
        a.setSyllabusStatusLabel(statusCode == Syllabus.STATUS_APPROVED ? "Approved"
                : statusCode == Syllabus.STATUS_PENDING_REVIEW ? "Pending Review" : "Draft");
        try {
            a.setSyllabusStatus(rs.getString("Workflow_Status"));
        } catch (SQLException ignored) {}
        try {
            a.setCurriculumId(rs.getString("Curriculum_ID"));
            a.setCurriculumCode(rs.getString("Curriculum_Code"));
            a.setCurriculumName(rs.getString("Curriculum_Name"));
        } catch (SQLException ignored) {}
        return a;
    }

    /**
     * Danh sach subject/syllabus duoc phan cong cho 1 Designer, kem ten
     * Curriculum dau tien co chua subject do (1 subject co the dung chung
     * nhieu curriculum nen chi lay 1 de hien thi ngu canh - dung OUTER APPLY
     * TOP 1 de KHONG bi nhan dong khi 1 subject thuoc nhieu curriculum).
     */
    public List<SyllabusAssignment> getAssignmentsByDesigner(String userId, String keyword) {
        return getAssignmentsByUserAndType(userId, "Designer", keyword);
    }

    public List<SyllabusAssignment> getAssignmentsByReviewer(String userId, String keyword) {
        return getAssignmentsByUserAndType(userId, "Reviewer", keyword);
    }

    private List<SyllabusAssignment> getAssignmentsByUserAndType(String userId, String type, String keyword) {
        List<SyllabusAssignment> list = new ArrayList<>();
        // Luu y: KHONG dung BASE_SELECT + LEFT JOIN Curriculum_Subjects/Curriculums truc tiep,
        // vi 1 subject co the thuoc NHIEU curriculum -> se nhan ra nhieu dong trung lap cho
        // cung 1 assignment. Dung OUTER APPLY TOP 1 de chi lay dung 1 curriculum dai dien.
        StringBuilder sql = new StringBuilder(
                "SELECT sa.*, sy.Subject_ID, sy.Status AS Syllabus_Status, sy.Workflow_Status, "
              + "s.Subject_Code, s.Subject_Name, "
              + "c.Curriculum_ID, c.Curriculum_Code, c.Curriculum_Name "
              + "FROM Syllabus_Assignments sa "
              + "JOIN Syllabuses sy ON sa.Syllabus_ID = sy.Syllabus_ID "
              + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
              + "OUTER APPLY (SELECT TOP 1 cur.Curriculum_ID, cur.Curriculum_Code, cur.Curriculum_Name "
              + "FROM Curriculum_Subjects cs JOIN Curriculums cur ON cur.Curriculum_ID = cs.Curriculum_ID "
              + "WHERE cs.Subject_ID = s.Subject_ID ORDER BY cur.Curriculum_Code) c "
              + "WHERE sa.Assignment_Type = ? AND sa.User_ID = ?");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (s.Subject_Name LIKE ? OR s.Subject_Code LIKE ?)");
        }
        sql.append(" ORDER BY sa.Assigned_Date DESC");

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, type);
            ps.setString(2, userId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(3, "%" + keyword + "%");
                ps.setString(4, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAssignment(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Danh sach tat ca assignment (Designer/Reviewer) da duoc gan cho TAT CA
     * subject/syllabus thuoc 1 Curriculum - dung cho trang "Assign" de Admin
     * xem lai ai da duoc phan cong truoc khi gan them.
     */
    public List<SyllabusAssignment> getAssignmentsByCurriculum(String curriculumId) {
        List<SyllabusAssignment> list = new ArrayList<>();
        String sql = "SELECT sa.*, sy.Subject_ID, sy.Status AS Syllabus_Status, "
                + "s.Subject_Code, s.Subject_Name, "
                + "u.Full_Name AS Assignee_Full_Name, u.Email AS Assignee_Email "
                + "FROM Syllabus_Assignments sa "
                + "JOIN Syllabuses sy ON sa.Syllabus_ID = sy.Syllabus_ID "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "JOIN Curriculum_Subjects cs ON cs.Subject_ID = s.Subject_ID "
                + "JOIN Users u ON u.User_ID = sa.User_ID "
                + "WHERE cs.Curriculum_ID = ? "
                + "ORDER BY s.Subject_Code, sa.Assignment_Type";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SyllabusAssignment a = mapAssignment(rs);
                model.User u = new model.User();
                u.setUserId(rs.getString("User_ID"));
                u.setFullName(rs.getString("Assignee_Full_Name"));
                u.setEmail(rs.getString("Assignee_Email"));
                a.setUser(u);
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Danh sach nguoi (Designer/Reviewer) da duoc gan cho 1 Syllabus cu the. */
    public List<SyllabusAssignment> getAssignmentsBySyllabus(String syllabusId) {
        List<SyllabusAssignment> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE sa.Syllabus_ID = ? ORDER BY sa.Assignment_Type";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAssignment(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Kiem tra 1 user co duoc phan cong (Designer hoac Reviewer) vao 1 Syllabus cu the khong. */
    public boolean isAssignedToSyllabus(String userId, String syllabusId, String assignmentType) {
        String sql = "SELECT 1 FROM Syllabus_Assignments WHERE User_ID = ? AND Syllabus_ID = ? AND Assignment_Type = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, syllabusId);
            ps.setString(3, assignmentType);
            return ps.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Admin gan 1 nguoi lam Designer hoac Reviewer cho 1 Syllabus.
     * Bo qua neu da ton tai (UNIQUE constraint tren Syllabus_ID+User_ID+Assignment_Type).
     */
    public boolean assignUser(String syllabusId, String userId, String assignmentType, String assignedBy) {
        String sql = "INSERT INTO Syllabus_Assignments (Assignment_ID, Syllabus_ID, User_ID, Assignment_Type, Assigned_By) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, java.util.UUID.randomUUID().toString());
            ps.setString(2, syllabusId);
            ps.setString(3, userId);
            ps.setString(4, assignmentType);
            ps.setString(5, assignedBy);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // co the la vi pham UNIQUE constraint (da gan roi) - khong coi la loi nghiem trong
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeAssignment(String assignmentId) {
        String sql = "DELETE FROM Syllabus_Assignments WHERE Assignment_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}