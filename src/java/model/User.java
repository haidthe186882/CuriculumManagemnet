package model;

import java.util.Date;

public class User {
    private String userId;
    private int roleId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String status;
    private String phoneNumber;
    private String department;
    private String profileImageUrl;
    private Date createdDate;
    // join
    private Role role;
    private boolean reviewer;
    private boolean designer;

    public User() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isReviewer() { return reviewer; }
    public void setReviewer(boolean reviewer) { this.reviewer = reviewer; }
    public boolean isDesigner() { return designer; }
    public void setDesigner(boolean designer) { this.designer = designer; }
}
