package dao;

import dal.DBContext;
import model.TeacherMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherMaterialDAO {

    private TeacherMaterial mapRow(ResultSet rs) throws SQLException {
        TeacherMaterial tm = new TeacherMaterial();
        tm.setMaterialId(rs.getString("Teacher_Material_ID"));
        tm.setUserId(rs.getString("User_ID"));
        tm.setSyllabusId(rs.getString("Syllabus_ID"));
        tm.setMaterialType(rs.getString("Material_Type"));
        tm.setMaterialName(rs.getString("Material_Name"));
        tm.setMaterialUrl(rs.getString("Material_URL"));
        tm.setDescription(rs.getString("Description"));
        try {
            tm.setIsActive(rs.getBoolean("Is_Active"));
        } catch (SQLException ignored) {
        }
        try {
            tm.setCreatedDate(rs.getTimestamp("Created_Date"));
        } catch (SQLException ignored) {
        }
        // joined fields
        try {
            tm.setSyllabusName(rs.getString("Syllabus_Name"));
        } catch (SQLException ignored) {
        }
        try {
            tm.setSubjectCode(rs.getString("Subject_Code"));
        } catch (SQLException ignored) {
        }
        try {
            tm.setTeacherName(rs.getString("Full_Name"));
        } catch (SQLException ignored) {
        }
        return tm;
    }

    /**
     * Lấy danh sách tài liệu của một teacher (theo User_ID)
     */
    public List<TeacherMaterial> getMaterialsByUser(String userId) {
        List<TeacherMaterial> list = new ArrayList<>();
        String sql = """
                SELECT tm.Teacher_Material_ID, tm.User_ID, tm.Syllabus_ID,
                       tm.Material_Type, tm.Material_Name, tm.Material_URL,
                       tm.Description, tm.Is_Active, tm.Created_Date,
                       sy.Syllabus_Name, s.Subject_Code, u.Full_Name
                FROM Teacher_Materials tm
                LEFT JOIN Syllabuses sy ON tm.Syllabus_ID = sy.Syllabus_ID
                LEFT JOIN Subjects s ON sy.Subject_ID = s.Subject_ID
                LEFT JOIN Users u ON tm.User_ID = u.User_ID
                WHERE tm.User_ID = ?
                  AND tm.Is_Active = 1
                ORDER BY tm.Created_Date DESC
                """;
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            System.out.println("[TeacherMaterialDAO] getMaterialsByUser - userId: [" + userId + "]");
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            System.out.println("[TeacherMaterialDAO] getMaterialsByUser - found: " + list.size() + " materials");
        } catch (Exception e) {
            System.out.println("[TeacherMaterialDAO] getMaterialsByUser - ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy một tài liệu theo ID
     */
    public TeacherMaterial getMaterialById(String materialId) {
        String sql = """
                SELECT tm.Teacher_Material_ID, tm.User_ID, tm.Syllabus_ID,
                       tm.Material_Type, tm.Material_Name, tm.Material_URL,
                       tm.Description, tm.Is_Active, tm.Created_Date,
                       sy.Syllabus_Name, s.Subject_Code, u.Full_Name
                FROM Teacher_Materials tm
                LEFT JOIN Syllabuses sy ON tm.Syllabus_ID = sy.Syllabus_ID
                LEFT JOIN Subjects s ON sy.Subject_ID = s.Subject_ID
                LEFT JOIN Users u ON tm.User_ID = u.User_ID
                WHERE tm.Teacher_Material_ID = ?
                """;
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm tài liệu mới
     */
    public boolean addMaterial(TeacherMaterial tm) {
        String sql = """
                INSERT INTO Teacher_Materials
                  (User_ID, Syllabus_ID, Material_Type, Material_Name, Material_URL, Description, Is_Active)
                VALUES (?, ?, ?, ?, ?, ?, 1)
                """;
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            System.out.println("[TeacherMaterialDAO] addMaterial - userId: " + tm.getUserId() + ", syllabusId: "
                    + tm.getSyllabusId() + ", name: " + tm.getMaterialName());
            ps.setString(1, tm.getUserId());
            ps.setString(2, tm.getSyllabusId());
            ps.setString(3, tm.getMaterialType());
            ps.setString(4, tm.getMaterialName());
            ps.setString(5, tm.getMaterialUrl());
            ps.setString(6, tm.getDescription());
            boolean result = ps.executeUpdate() > 0;
            System.out.println("[TeacherMaterialDAO] addMaterial - result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("[TeacherMaterialDAO] addMaterial - ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật tài liệu
     */
    public boolean updateMaterial(TeacherMaterial tm) {
        String sql = """
                UPDATE Teacher_Materials
                SET Syllabus_ID = ?, Material_Type = ?, Material_Name = ?,
                    Material_URL = ?, Description = ?
                WHERE Teacher_Material_ID = ? AND User_ID = ?
                """;
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tm.getSyllabusId());
            ps.setString(2, tm.getMaterialType());
            ps.setString(3, tm.getMaterialName());
            ps.setString(4, tm.getMaterialUrl());
            ps.setString(5, tm.getDescription());
            ps.setString(6, tm.getMaterialId());
            ps.setString(7, tm.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[TeacherMaterialDAO] updateMaterial - ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa tài liệu (soft delete)
     */
    public boolean deleteMaterial(String materialId, String userId) {
        String sql = "UPDATE Teacher_Materials SET Is_Active = 0 WHERE Teacher_Material_ID = ? AND User_ID = ?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, materialId);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[TeacherMaterialDAO] deleteMaterial - ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
