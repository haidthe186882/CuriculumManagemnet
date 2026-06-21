package dao;

import dal.DBContext;
import model.Major;

<<<<<<< HEAD
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
=======
import java.sql.*;
>>>>>>> main
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {

    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();
<<<<<<< HEAD

        // Ép kiểu UNIQUEIDENTIFIER thành VARCHAR(36) để Java nhận diện mượt mà
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code,
                            Major_Name,
                            Description
                     FROM Majors
                     WHERE Is_Active = 1
                     ORDER BY Major_Code
                     """;

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Major m = new Major();
                
                // Đọc chuỗi ID đã được chuyển đổi an toàn
                m.setMajorId(rs.getString("Major_ID_Str"));
                m.setMajorCode(rs.getString("Major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                
=======
        String sql = "SELECT * FROM Majors WHERE Is_Active=1 ORDER BY Major_Name";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Major m = new Major();
                m.setMajorId(rs.getString("Major_ID"));
                m.setMajorCode(rs.getString("Major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                try {
                    m.setIsActive(rs.getString("Status"));
                } catch (SQLException ignored) {
                    ignored.getSQLState();
                }
>>>>>>> main
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
<<<<<<< HEAD
}
=======

    public Major getMajorById(String id) {
        String sql = "SELECT * FROM Majors WHERE Major_ID = ?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Major m = new Major();
                m.setMajorId(rs.getString("Major_ID"));
                m.setMajorCode(rs.getString("major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                  try {
                    m.setIsActive(rs.getString("Status"));
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
>>>>>>> main
