package dao;

import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Major;

public class MajorDAO {

    private Major mapRow(ResultSet rs) throws SQLException {
        Major m = new Major();
        m.setMajorId(rs.getString("Major_ID_Str"));
        m.setMajorCode(rs.getString("Major_Code"));
        m.setMajorName(rs.getString("Major_Name"));
        m.setDescription(rs.getString("Description"));
        try {
            m.setIsActive(rs.getBoolean("Is_Active"));
        } catch (SQLException ignored) {}
        return m;
    }

    /** Lấy tất cả Major đang active */
    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code, Major_Name, Description, Is_Active
                     FROM Majors
                     WHERE Is_Active = 1
                     ORDER BY Major_Name
                     """;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lấy Major theo ID */
    public Major getMajorById(String id) {
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code, Major_Name, Description, Is_Active
                     FROM Majors
                     WHERE Major_ID = ?
                     """;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Thêm Major mới */
    public boolean addMajor(Major m) {
        String sql = """
                     INSERT INTO Majors (Major_ID, Major_Code, Major_Name, Description, Is_Active)
                     VALUES (NEWID(), ?, ?, ?, 1)
                     """;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getMajorCode());
            ps.setString(2, m.getMajorName());
            ps.setString(3, m.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật Major */
    public boolean updateMajor(Major m) {
        String sql = """
                     UPDATE Majors
                     SET Major_Code = ?, Major_Name = ?, Description = ?
                     WHERE Major_ID = ?
                     """;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
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

    /** Bật/tắt trạng thái active */
    public boolean toggleActive(String id, boolean active) {
        String sql = "UPDATE Majors SET Is_Active = ? WHERE Major_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa Major (hard delete) */
    public boolean deleteMajor(String id) {
        String sql = "DELETE FROM Majors WHERE Major_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
