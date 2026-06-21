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
    
    private User mapUserBasic(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getString("User_ID"));
        u.setFullName(rs.getString("Full_Name"));
        u.setEmail(rs.getString("Email"));
        u.setPasswordHash(rs.getString("Password_Hash"));

        // Is_Active is BIT in SQL Server
        try {
            u.setActive(rs.getBoolean("Is_Active"));
        } catch (SQLException ignored) {
            u.setActive(true);
        }

        try {
            u.setCreatedDate(rs.getTimestamp("Created_Date"));
        } catch (SQLException ignored) {}

        return u;
    }

    /** Load all roles for a given user from the User_Roles junction table. */
    private List<Role> loadRolesForUser(Connection con, String userId) throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.Role_ID, r.Role_Name "
                   + "FROM User_Roles ur "
                   + "JOIN Roles r ON ur.Role_ID = r.Role_ID "
                   + "WHERE ur.User_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roles.add(new Role(rs.getInt("Role_ID"), rs.getString("Role_Name")));
            }
        }
        return roles;
    }

    /** Map a User + load its roles. Requires an open connection. */
    private User mapUserWithRoles(ResultSet rs, Connection con) throws SQLException {
        User u = mapUserBasic(rs);
        u.setRoles(loadRolesForUser(con, u.getUserId()));

        // Set backward-compat roleId from primary role
        Role primary = u.getRole();
        if (primary != null) {
            u.setRoleId(primary.getRoleId());
        }
        return u;
    }

    // ---------------------------------------------------------------
    //  LOGIN
    // ---------------------------------------------------------------

    /**
     * Authenticate by email + password hash.
     * Returns a fully loaded User (with roles) or null on failure.
     */
    public User login(String email, String passwordHash) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password_Hash = ? AND Is_Active = 1";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUserWithRoles(rs, con);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Get all users (Admin user-management page). */
    public List<User> getAllUsers(String keyword, String status) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Users WHERE 1=1");
        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (Full_Name LIKE ? OR Email LIKE ?)");
        if (status != null && !status.trim().isEmpty())
            sql.append(" AND Is_Active = ?");
        sql.append(" ORDER BY Created_Date DESC");

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                int activeVal = status.equalsIgnoreCase("Active") ? 1 : 0;
                ps.setInt(idx, activeVal);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapUserWithRoles(rs, con));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Get user by primary key. */
    public User getUserById(String userId) {
        String sql = "SELECT * FROM Users WHERE User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUserWithRoles(rs, con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Get user by email. */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUserWithRoles(rs, con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Password hash. */
    public boolean updatePassword(String userId, String passwordHash) {
        String sql = "UPDATE Users SET Password_Hash = ? WHERE User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Update active status. */
    public boolean updateStatus(String userId, String status) {
        String sql = "UPDATE Users SET Is_Active = ? WHERE User_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int val = (status != null && status.equalsIgnoreCase("Active")) ? 1 : 0;
            ps.setInt(1, val);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add a new user + assign role via User_Roles.
     * Uses roleId from the User object.
     */
    public boolean addUser(User u) {
        String sqlUser = "INSERT INTO Users (User_ID, Full_Name, Email, Password_Hash, Is_Active) "
                       + "VALUES (NEWID(), ?, ?, ?, 1)";
        String sqlGetId = "SELECT User_ID FROM Users WHERE Email = ?";
        String sqlRole  = "INSERT INTO User_Roles (User_ID, Role_ID) VALUES (?, ?)";

        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try {
                // Insert user
                try (PreparedStatement ps = con.prepareStatement(sqlUser)) {
                    ps.setString(1, u.getFullName());
                    ps.setString(2, u.getEmail());
                    ps.setString(3, u.getPasswordHash());
                    ps.executeUpdate();
                }
                // Get the generated User_ID
                String newUserId = null;
                try (PreparedStatement ps = con.prepareStatement(sqlGetId)) {
                    ps.setString(1, u.getEmail());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) newUserId = rs.getString("User_ID");
                }
                // Insert role mapping
                if (newUserId != null && u.getRoleId() > 0) {
                    try (PreparedStatement ps = con.prepareStatement(sqlRole)) {
                        ps.setString(1, newUserId);
                        ps.setInt(2, u.getRoleId());
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user info (name, active status) + reassign role.
     */
    public boolean updateUser(User u) {
        String sqlUser = "UPDATE Users SET Full_Name = ?, Is_Active = ? WHERE User_ID = ?";
        String sqlDelRoles = "DELETE FROM User_Roles WHERE User_ID = ?";
        String sqlAddRole  = "INSERT INTO User_Roles (User_ID, Role_ID) VALUES (?, ?)";

        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try {
                // Update basic info
                try (PreparedStatement ps = con.prepareStatement(sqlUser)) {
                    ps.setString(1, u.getFullName());
                    ps.setInt(2, u.isActive() ? 1 : 0);
                    ps.setString(3, u.getUserId());
                    ps.executeUpdate();
                }
                // Reassign roles: remove old, insert new
                if (u.getRoleId() > 0) {
                    try (PreparedStatement ps = con.prepareStatement(sqlDelRoles)) {
                        ps.setString(1, u.getUserId());
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(sqlAddRole)) {
                        ps.setString(1, u.getUserId());
                        ps.setInt(2, u.getRoleId());
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Check whether email already exists. */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Get all available roles. */
    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles ORDER BY Role_ID";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Role(rs.getInt("Role_ID"), rs.getString("Role_Name")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
