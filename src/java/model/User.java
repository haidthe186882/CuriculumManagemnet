package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mai Duy An
 * @MSSV HE197000
 * @Date 19/6/2026
 */
public class User {

    private String userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private boolean isActive;
    private Date createdDate;

    // Roles loaded from User_Roles + Roles join
    private List<Role> roles = new ArrayList<>();

    public User() {
    }

    public User(String userId, String fullName, String email, String passwordHash, boolean isActive, Date createdDate) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
        this.createdDate = createdDate;
    }

    // --- Getters / Setters ---

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return isActive ? "Active" : "Inactive";
    }

    /** Backward-compatible status setter
     * @param status */
    public void setStatus(String status) {
        this.isActive = status != null && status.equalsIgnoreCase("Active");
    }

    public String getIsActive() {
        return isActive ? "Active" : "Inactive";
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive != null && isActive.equalsIgnoreCase("Active");
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    // --- Roles (many-to-many via User_Roles) ---
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        if (this.roles == null) this.roles = new ArrayList<>();
        this.roles.add(role);
    }

    /**
     * Returns the primary (first) Role, or null if none assigned.Used by LoginServlet / DashboardServlet for redirect logic.
     * @return
     */
    public Role getRole() {
        return (roles != null && !roles.isEmpty()) ? roles.get(0) : null;
    }

    /**
     * Convenience: check whether the user has a specific role name.
     * @param roleName
     * @return 
     */
    public boolean hasRole(String roleName) {
        if (roles == null || roleName == null) return false;
        for (Role r : roles) {
            if (roleName.equalsIgnoreCase(r.getRoleName())) return true;
        }
        return false;
    }

    // --- Backward-compat fields (used by old DAO / admin pages) ---

    private int roleId;           // kept for backward compatibility
    private String phoneNumber;
    private String department;
    private String profileImageUrl;
    private boolean isReviewer;
    private boolean isDesigner;

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isReviewer() { return isReviewer; }
    public void setReviewer(boolean reviewer) { isReviewer = reviewer; }

    public boolean isDesigner() { return isDesigner; }
    public void setDesigner(boolean designer) { isDesigner = designer; }
}
