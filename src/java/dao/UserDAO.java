package dao;

import dal.DBContext;
import model.Role;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getString("User_ID"));
        u.setRoleId(rs.getInt("Role_ID"));
        u.setFullName(rs.getString("Full_Name"));
        u.setEmail(rs.getString("Email"));
        u.setPasswordHash(rs.getString("Password_Hash"));
        // New schema uses Is_Active (BIT) instead of Status text
        try {
            boolean active = rs.getBoolean("Is_Active");
            u.setStatus(active ? "Active" : "Inactive");
        } catch (SQLException ex) {
            // fallback if column missing
            try { u.setStatus(rs.getString("Status")); } catch (SQLException ignored) {}
        }
        try { u.setCreatedDate(rs.getTimestamp("Created_Date")); } catch (SQLException ignored) {}
        // optional columns added by patch
        try { u.setPhoneNumber(rs.getString("Phone_Number")); } catch (SQLException ignored) {}
        try { u.setDepartment(rs.getString("Department")); } catch (SQLException ignored) {}
        try { u.setProfileImageUrl(rs.getString("Profile_Image_URL")); } catch (SQLException ignored) {}
        // join Role if available
        try {
            Role r = new Role();
            r.setRoleId(rs.getInt("Role_ID"));
            r.setRoleName(rs.getString("Role_Name"));
            u.setRole(r);
        } catch (SQLException ignored) {}
        return u;
    }

    /** Dang nhap bang email + password hash */
    public User login(String email, String passwordHash) {
        String sql = "SELECT u.*, r.Role_Name FROM Users u "
                   + "JOIN Roles r ON u.Role_ID = r.Role_ID "
                   + "WHERE u.Email = ? AND u.Password_Hash = ? AND (u.Is_Active = 1 OR u.Is_Active = '1')";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Lay tat ca user (Admin) */
    public List<User> getAllUsers(String keyword, String status) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT u.*, r.Role_Name FROM Users u JOIN Roles r ON u.Role_ID = r.Role_ID WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (u.Full_Name LIKE ? OR u.Email LIKE ?)");
        if (status != null && !status.trim().isEmpty())
            sql.append(" AND (CASE WHEN u.Is_Active IS NULL THEN 'Inactive' WHEN u.Is_Active = 1 THEN 'Active' ELSE 'Inactive' END) = ?");
        sql.append(" ORDER BY u.Created_Date DESC");
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (status != null && !status.trim().isEmpty())
                ps.setString(idx, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Lay user theo ID */
    public User getUserById(String userId) {
        String sql = "SELECT u.*, r.Role_Name FROM Users u JOIN Roles r ON u.Role_ID = r.Role_ID WHERE u.User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Lay user theo email */
    public User getUserByEmail(String email) {
        String sql = "SELECT u.*, r.Role_Name FROM Users u JOIN Roles r ON u.Role_ID = r.Role_ID WHERE u.Email = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Cap nhat password hash cho user */
    public boolean updatePassword(String userId, String passwordHash) {
        String sql = "UPDATE Users SET Password_Hash = ? WHERE User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Cap nhat trang thai user */
    public boolean updateStatus(String userId, String status) {
        String sql = "UPDATE Users SET Is_Active = ? WHERE User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // expect status 'Active' or 'Inactive'
            int val = (status != null && status.equalsIgnoreCase("Active")) ? 1 : 0;
            ps.setInt(1, val);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Them user moi */
    public boolean addUser(User u) {
        // New schema: use Is_Active (default 1). Only insert needed columns.
        String sql = "INSERT INTO Users (Role_ID, Full_Name, Email, Password_Hash) VALUES (?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, u.getRoleId());
            ps.setString(2, u.getFullName());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPasswordHash());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Cap nhat thong tin user */
    public boolean updateUser(User u) {
        String sql = "UPDATE Users SET Full_Name=?, Role_ID=?, Is_Active=? WHERE User_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setInt(2, u.getRoleId());
            ps.setInt(3, (u.getStatus() != null && u.getStatus().equalsIgnoreCase("Active")) ? 1 : 0);
            ps.setString(4, u.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Kiem tra email da ton tai chua */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Lay danh sach roles */
    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles ORDER BY Role_ID";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Role(rs.getInt("Role_ID"), rs.getString("Role_Name")));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
