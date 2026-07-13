package model;

import java.util.Date;

/**
 * Mapping cho bang Curriculum_Assignments:
 *   Assignment_ID, Curriculum_ID, User_ID, Assignment_Type ('Designer' | 'Reviewer'),
 *   Assigned_By, Assigned_Date
 */
public class CurriculumAssignments {

    private String assignmentId;
    private String curriculumId;
    private String userId;
    private String assignmentType;   // "Designer" | "Reviewer"
    private String assignedBy;
    private Date assignedDate;

    // joins (phuc vu hien thi danh sach)
    private Curriculum curriculum;
    private User user;
    private User assignedByUser;

    public CurriculumAssignments() {
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(String curriculumId) {
        this.curriculumId = curriculumId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssignedByUser() {
        return assignedByUser;
    }

    public void setAssignedByUser(User assignedByUser) {
        this.assignedByUser = assignedByUser;
    }
}