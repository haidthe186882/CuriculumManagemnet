package model;

import java.util.Date;

public class Syllabus {
    private String syllabusId;
    private String subjectId;
    private String syllabusName;
    private String englishName;
    private String version;
    private String description;
    private String timeAllocation;
    private String studentTasks;
    private String tools;
    private String scoringScale;
    private double minAvgMarkToPass;
    private String decisionNo;
    private Date approvedDate;
    private String status;   // Draft / Pending / Approved / Archived
    private boolean isActive;
    private String materialUrl;
    // join
    private Subject subject;

    public Syllabus() {}

    public String getMaterialUrl() { return materialUrl; }
    public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }
    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSyllabusName() { return syllabusName; }
    public void setSyllabusName(String syllabusName) { this.syllabusName = syllabusName; }
    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTimeAllocation() { return timeAllocation; }
    public void setTimeAllocation(String timeAllocation) { this.timeAllocation = timeAllocation; }
    public String getStudentTasks() { return studentTasks; }
    public void setStudentTasks(String studentTasks) { this.studentTasks = studentTasks; }
    public String getTools() { return tools; }
    public void setTools(String tools) { this.tools = tools; }
    public String getScoringScale() { return scoringScale; }
    public void setScoringScale(String scoringScale) { this.scoringScale = scoringScale; }
    public double getMinAvgMarkToPass() { return minAvgMarkToPass; }
    public void setMinAvgMarkToPass(double minAvgMarkToPass) { this.minAvgMarkToPass = minAvgMarkToPass; }
    public String getDecisionNo() { return decisionNo; }
    public void setDecisionNo(String decisionNo) { this.decisionNo = decisionNo; }
    public Date getApprovedDate() { return approvedDate; }
    public void setApprovedDate(Date approvedDate) { this.approvedDate = approvedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
}
