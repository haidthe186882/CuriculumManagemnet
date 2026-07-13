/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dal.DBContext;
import model.Curriculum;
import model.CurriculumAssignments;
import model.Major;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bang Curriculum_Assignments.
 * Phuc vu man hinh "Design List": liet ke cac Curriculum ma Designer
 * dang nhap dang duoc phan cong (Assignment_Type = 'Designer').
 *
 * @author lo pc
 */
public class DesignDAO {

    private CurriculumAssignments mapAssignment(ResultSet rs) throws SQLException {
        CurriculumAssignments ca = new CurriculumAssignments();
        ca.setAssignmentId(rs.getString("Assignment_ID"));
        ca.setCurriculumId(rs.getString("Curriculum_ID"));
        ca.setUserId(rs.getString("User_ID"));
        ca.setAssignmentType(rs.getString("Assignment_Type"));
        ca.setAssignedBy(rs.getString("Assigned_By"));
        ca.setAssignedDate(rs.getTimestamp("Assigned_Date"));

        // join Curriculum (+ Major)
        try {
            Curriculum c = new Curriculum();
            c.setCurriculumId(rs.getString("Curriculum_ID"));
            c.setCurriculumCode(rs.getString("Curriculum_Code"));
            c.setCurriculumName(rs.getString("Curriculum_Name"));
            c.setEnglishName(rs.getString("English_Name"));
            c.setVersion(rs.getString("Version"));
            c.setTotalCredits(rs.getInt("Total_Credits"));
            try {
                c.setStatus(rs.getInt("Status"));
            } catch (SQLException ignored) {
            }
            try {
                c.setIsActive(rs.getBoolean("Is_Active"));
            } catch (SQLException ignored) {
            }
            try {
                c.setMajorName(rs.getString("Major_Name"));
            } catch (SQLException ignored) {
            }
            ca.setCurriculum(c);
        } catch (SQLException ignored) {
        }

        return ca;
    }

    /**
     * Lay danh sach curriculum duoc phan cong cho 1 Designer (User_ID),
     * co the loc theo tu khoa (ma/ten curriculum).
     */
    public List<CurriculumAssignments> getCurriculumsByDesigner(String userId, String keyword) {
        List<CurriculumAssignments> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT ca.*, c.Curriculum_Code, c.Curriculum_Name, c.English_Name, c.Version, "
                + "c.Total_Credits, c.Status, c.Is_Active, m.Major_Name "
                + "FROM Curriculum_Assignments ca "
                + "JOIN Curriculums c ON ca.Curriculum_ID = c.Curriculum_ID "
                + "LEFT JOIN Majors m ON c.Major_ID = m.Major_ID "
                + "WHERE ca.Assignment_Type = 'Designer' AND ca.User_ID = ?");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.Curriculum_Name LIKE ? OR c.Curriculum_Code LIKE ?)");
        }
        sql.append(" ORDER BY ca.Assigned_Date DESC");

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, userId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(2, "%" + keyword + "%");
                ps.setString(3, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAssignment(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Kiem tra 1 Designer co duoc phan cong vao 1 curriculum cu the khong
     * (dung de chan truy cap truc tiep curriculum/detail neu can).
     */
    public boolean isAssignedToCurriculum(String userId, String curriculumId) {
        String sql = "SELECT 1 FROM Curriculum_Assignments "
                + "WHERE Assignment_Type = 'Designer' AND User_ID = ? AND Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, curriculumId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
