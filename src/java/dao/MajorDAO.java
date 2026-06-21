package dao;

import dal.DBContext;
import model.Major;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {

    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();

        // Kế thừa giải pháp CAST UNIQUEIDENTIFIER của HEAD để tránh lỗi Driver và sắp xếp theo tên của main
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code,
                            Major_Name,
                            Description,
                            Is_Active
                     FROM Majors
                     WHERE Is_Active = 1
                     ORDER BY Major_Name
                     """;

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Major m = new Major();
                m.setMajorId(rs.getString("Major_ID_Str"));
                m.setMajorCode(rs.getString("Major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                
                // Tích hợp logic đọc Status dạng String từ nhánh main một cách an toàn
                // Đọc giá trị dạng boolean từ Database một cách an toàn
            try {
                // rs.getBoolean() tự động chuyển đổi 1 thành true, 0 thành false
                m.setIsActive(rs.getBoolean("Is_Active")); 
            } catch (SQLException ignored) {
                ignored.getSQLState();
            }
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Major getMajorById(String id) {
        // Áp dụng CAST tương tự cho hàm tìm kiếm theo ID để đồng bộ hệ thống
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code,
                            Major_Name,
                            Description,
                            Is_Active
                     FROM Majors 
                     WHERE Major_ID = ?
                     """;
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Major m = new Major();
                m.setMajorId(rs.getString("Major_ID_Str"));
                m.setMajorCode(rs.getString("Major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                try {
                    m.setIsActive(rs.getBoolean("Is_Active"));
                } catch (SQLException ignored) {
                    ignored.getSQLState();
                }
                    return m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addMajor(Major m) {
        String sql = "INSERT INTO Majors (Major_ID, Major_Code, Major_Name, Description, Is_Active) "
                + "VALUES (NEWID(), ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getMajorCode());
            ps.setString(2, m.getMajorName());
            ps.setString(3, m.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMajor(Major m) {
        String sql = "UPDATE Majors SET Major_Code=?, Major_Name=?, Description=? WHERE Major_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getMajorCode());
            ps.setString(2, m.getMajorName());
            ps.setString(3, m.getDescription());
            ps.setString(4, m.getMajorId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleActive(String programId, boolean isActive) {
        String sql = "UPDATE Majors SET Is_Active=? WHERE Major_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, isActive ? 1 : 0);
            ps.setString(2, programId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMajor(String programId) {
        String sql = "DELETE FROM Majors WHERE Major_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, programId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}