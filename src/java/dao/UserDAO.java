package dao;

import dal.DBContext;
import model.Role;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mai Duy An
 * @MSSV HE197000
 * @date 20/6/2026
 * @desc 
 */
public class UserDAO {
    
    // Câu lệnh nền tảng
    private static final String BASE_SELECT_SQL = 
        "SELECT u.User_ID, u.Full_Name, u.Email, u.Password_Hash, u.Is_Active, u.Created_Date, " +
        "(SELECT TOP 1 r.Role_ID FROM User_Roles ur JOIN Roles r ON ur.Role_ID = r.Role_ID WHERE ur.User_ID = u.User_ID ORDER BY ur.Assigned_Date ASC) AS Primary_Role_ID, " +
        "(SELECT TOP 1 r.Role_Name FROM User_Roles ur JOIN Roles r ON ur.Role_ID = r.Role_ID WHERE ur.User_ID = u.User_ID ORDER BY ur.Assigned_Date ASC) AS Primary_Role_Name, " +
        "CAST((CASE WHEN EXISTS (SELECT 1 FROM User_Roles ur2 JOIN Roles r2 ON ur2.Role_ID = r2.Role_ID WHERE ur2.User_ID = u.User_ID AND r2.Role_Name = 'Reviewer') THEN 1 ELSE 0 END) AS BIT) AS Is_Reviewer, " +
        "CAST((CASE WHEN EXISTS (SELECT 1 FROM User_Roles ur2 JOIN Roles r2 ON ur2.Role_ID = r2.Role_ID WHERE ur2.User_ID = u.User_ID AND r2.Role_Name = 'Designer') THEN 1 ELSE 0 END) AS BIT) AS Is_Designer " +
        "FROM Users u ";

    // Map dữ liệu
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getString("User_ID"));
        u.setFullName(rs.getString("Full_Name"));
        u.setEmail(rs.getString("Email"));
        u.setPasswordHash(rs.getString("Password_Hash"));

        // Set Status
        try { u.setStatus(rs.getBoolean("Is_Active") ? "Active" : "Inactive"); } 
        catch (SQLException ignored) { u.setStatus("Active"); }

        // Set Created Date
        try { 
            Timestamp ts = rs.getTimestamp("Created_Date");
            if (ts != null) {
                u.setCreatedDate(new java.util.Date(ts.getTime()));
            }
        } catch (SQLException ignored) {}
        
        // Bắt cờ quyền phụ từ SQL
        boolean isRev = false;
        boolean isDes = false;
        try { isRev = rs.getBoolean("Is_Reviewer"); } catch (SQLException ignored) {}
        try { isDes = rs.getBoolean("Is_Designer"); } catch (SQLException ignored) {}

        // Gán Role chính và LOGIC TRIỆT TIÊU QUYỀN PHỤ
//        try {
//            int primaryRoleId = rs.getInt("Primary_Role_ID");
//            String primaryRoleName = rs.getString("Primary_Role_Name");
//            if (primaryRoleName != null) {
//                u.addRole(new Role(primaryRoleId, primaryRoleName)); 
//                u.setRoleId(primaryRoleId);
//                
//                // Nếu Role chính đã là Reviewer thì tắt cờ phụ Reviewer
//                if ("Reviewer".equalsIgnoreCase(primaryRoleName)) {
//                    isRev = false;
//                }
//                // Nếu Role chính đã là Designer thì tắt cờ phụ Designer
//                if ("Designer".equalsIgnoreCase(primaryRoleName)) {
//                    isDes = false;
//                }
//            }
//        } catch (SQLException ignored) {}
        // Gán Role chính
        try {
            int primaryRoleId = rs.getInt("Primary_Role_ID");
            String primaryRoleName = rs.getString("Primary_Role_Name");
            if (primaryRoleName != null) {
                u.addRole(new Role(primaryRoleId, primaryRoleName)); 
                u.setRoleId(primaryRoleId);
                
                if ("Reviewer".equalsIgnoreCase(primaryRoleName)) isRev = false;
                if ("Designer".equalsIgnoreCase(primaryRoleName)) isDes = false;
            }
        } catch (SQLException ignored) {}
        if (isRev) u.addRole(new Role(0, "Reviewer")); 
        if (isDes) u.addRole(new Role(0, "Designer"));
        // Gán lại cờ đã được lọc cho User
        u.setReviewer(isRev);
        u.setDesigner(isDes);
        
        return u;
    }

    // ---------------------------------------------------------------
    //  LOGIN & QUERIES
    // ---------------------------------------------------------------

    public User login(String email, String passwordHash) {
        String sql = BASE_SELECT_SQL + "WHERE u.Email = ? AND u.Password_Hash = ? AND u.Is_Active = 1";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getAllUsers(String keyword, String status) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT_SQL + "WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (u.Full_Name LIKE ? OR u.Email LIKE ?)");
        if (status != null && !status.trim().isEmpty())
            sql.append(" AND u.Is_Active = ?");
        sql.append(" ORDER BY u.Created_Date DESC");

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setInt(idx, status.equalsIgnoreCase("Active") ? 1 : 0);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public User getUserById(String userId) {
        String sql = BASE_SELECT_SQL + "WHERE u.User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = BASE_SELECT_SQL + "WHERE u.Email = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ---------------------------------------------------------------
    //  MUTATIONS (UPDATE / ADD)
    // ---------------------------------------------------------------

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

    public boolean updateStatus(String userId, String status) {
        String sql = "UPDATE Users SET Is_Active = ? WHERE User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, (status != null && status.equalsIgnoreCase("Active")) ? 1 : 0);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean addUser(User u) {
        String newUserId = java.util.UUID.randomUUID().toString();
        String insertUserSql = "INSERT INTO Users (User_ID, Full_Name, Email, Password_Hash, Is_Active) VALUES (?, ?, ?, ?, 1)";
        String insertPrimaryRoleSql = "INSERT INTO User_Roles (User_Role_ID, User_ID, Role_ID, Assigned_Date) VALUES (NEWID(), ?, ?, GETDATE())";
        String insertExtraRoleSql = "INSERT INTO User_Roles (User_Role_ID, User_ID, Role_ID, Assigned_Date) " +
                                    "SELECT NEWID(), ?, Role_ID, DATEADD(second, 1, GETDATE()) FROM Roles " +
                                    "WHERE Role_Name = ? AND NOT EXISTS (SELECT 1 FROM User_Roles ur WHERE ur.User_ID = ? AND ur.Role_ID = Roles.Role_ID)";

        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(insertUserSql)) {
                    ps.setString(1, newUserId);
                    ps.setString(2, u.getFullName());
                    ps.setString(3, u.getEmail());
                    ps.setString(4, u.getPasswordHash());
                    ps.executeUpdate();
                }

                try (PreparedStatement psRole = con.prepareStatement(insertPrimaryRoleSql)) {
                    psRole.setString(1, newUserId);
                    psRole.setInt(2, u.getRoleId());
                    psRole.executeUpdate();
                }

                // Chèn Role phụ
                if (u.isReviewer()) {
                    try (PreparedStatement psExtra = con.prepareStatement(insertExtraRoleSql)) {
                        psExtra.setString(1, newUserId);
                        psExtra.setString(2, "Reviewer");
                        psExtra.setString(3, newUserId);
                        psExtra.executeUpdate();
                    }
                }
                if (u.isDesigner()) {
                    try (PreparedStatement psExtra = con.prepareStatement(insertExtraRoleSql)) {
                        psExtra.setString(1, newUserId);
                        psExtra.setString(2, "Designer");
                        psExtra.setString(3, newUserId);
                        psExtra.executeUpdate();
                    }
                }

                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateUser(User u) {
        String updateUserSql = "UPDATE Users SET Full_Name=?, Is_Active=? WHERE User_ID=?";
        String deleteOldRolesSql = "DELETE FROM User_Roles WHERE User_ID=?";
        String insertPrimaryRoleSql = "INSERT INTO User_Roles (User_Role_ID, User_ID, Role_ID, Assigned_Date) VALUES (NEWID(), ?, ?, GETDATE())";
        String insertExtraRoleSql = "INSERT INTO User_Roles (User_Role_ID, User_ID, Role_ID, Assigned_Date) " +
                                    "SELECT NEWID(), ?, Role_ID, DATEADD(second, 1, GETDATE()) FROM Roles " +
                                    "WHERE Role_Name = ? AND NOT EXISTS (SELECT 1 FROM User_Roles ur WHERE ur.User_ID = ? AND ur.Role_ID = Roles.Role_ID)";

        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(updateUserSql)) {
                    ps.setString(1, u.getFullName());
                    ps.setInt(2, (u.getStatus() != null && u.getStatus().equalsIgnoreCase("Active")) ? 1 : 0);
                    ps.setString(3, u.getUserId());
                    ps.executeUpdate();
                }

                // Xóa toàn bộ Role cũ
                try (PreparedStatement psDel = con.prepareStatement(deleteOldRolesSql)) {
                    psDel.setString(1, u.getUserId());
                    psDel.executeUpdate();
                }

                // Gán quyền chính trước
                try (PreparedStatement psRole = con.prepareStatement(insertPrimaryRoleSql)) {
                    psRole.setString(1, u.getUserId());
                    psRole.setInt(2, u.getRoleId());
                    psRole.executeUpdate();
                }

                // Gán quyền phụ sau
                if (u.isReviewer()) {
                    try (PreparedStatement psExtra = con.prepareStatement(insertExtraRoleSql)) {
                        psExtra.setString(1, u.getUserId());
                        psExtra.setString(2, "Reviewer");
                        psExtra.setString(3, u.getUserId());
                        psExtra.executeUpdate();
                    }
                }
                if (u.isDesigner()) {
                    try (PreparedStatement psExtra = con.prepareStatement(insertExtraRoleSql)) {
                        psExtra.setString(1, u.getUserId());
                        psExtra.setString(2, "Designer");
                        psExtra.setString(3, u.getUserId());
                        psExtra.executeUpdate();
                    }
                }

                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

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

    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles ORDER BY Role_ID";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Role(rs.getInt("Role_ID"), rs.getString("Role_Name")));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
