package model;

import java.util.Date;

/**
 * Mapping cho bang Syllabus_Assignments:
 *   Assignment_ID, Syllabus_ID, User_ID, Assignment_Type ('Designer' | 'Reviewer'),
 *   Assigned_By, Assigned_Date
 *
 * Dung cho man hinh "Design list" (Designer) va "Review list" (Reviewer):
 * moi dong la 1 Subject/Syllabus cu the duoc Admin phan cong cho 1 nguoi,
 * khac voi CurriculumAssignments (kieu cu, phan cong theo ca Curriculum).
 */
public class SyllabusAssignment {

    private String assignmentId;
    private String syllabusId;
    private String userId;
    private String assignmentType;   // "Designer" | "Reviewer"
    private String assignedBy;
    private Date assignedDate;

    // joins (phuc vu hien thi danh sach)
    private String subjectId;
    private String subjectCode;
    private String subjectName;
    private int syllabusStatusCode;
    private String syllabusStatusLabel;

    private String curriculumId;
    private String curriculumCode;
    private String curriculumName;

    private User user;
    private User assignedByUser;

    public SyllabusAssignment() {
    }

    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAssignmentType() { return assignmentType; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public Date getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Date assignedDate) { this.assignedDate = assignedDate; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getSyllabusStatusCode() { return syllabusStatusCode; }
    public void setSyllabusStatusCode(int syllabusStatusCode) { this.syllabusStatusCode = syllabusStatusCode; }

    public String getSyllabusStatusLabel() { return syllabusStatusLabel; }
    public void setSyllabusStatusLabel(String syllabusStatusLabel) { this.syllabusStatusLabel = syllabusStatusLabel; }

    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }

    public String getCurriculumCode() { return curriculumCode; }
    public void setCurriculumCode(String curriculumCode) { this.curriculumCode = curriculumCode; }

    public String getCurriculumName() { return curriculumName; }
    public void setCurriculumName(String curriculumName) { this.curriculumName = curriculumName; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getAssignedByUser() { return assignedByUser; }
    public void setAssignedByUser(User assignedByUser) { this.assignedByUser = assignedByUser; }
}
