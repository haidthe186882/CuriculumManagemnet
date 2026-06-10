package model;

import java.util.Date;

public class Curriculum {
    private String curriculumId;
    private String programId;
    private String createdBy;
    private String curriculumCode;
    private String curriculumName;
    private String englishName;
    private String description;
    private int totalCredits;
    private String version;
    private String decisionNo;
    private Date decisionDate;
    private String status;      // Draft / Pending / Approved / Archived
    private boolean isPublic;
    private boolean isActive;
    private Date createdDate;
    private Date updatedDate;
    // joins
    private Program program;
    private User creator;

    public Curriculum() {}

    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCurriculumCode() { return curriculumCode; }
    public void setCurriculumCode(String curriculumCode) { this.curriculumCode = curriculumCode; }
    public String getCurriculumName() { return curriculumName; }
    public void setCurriculumName(String curriculumName) { this.curriculumName = curriculumName; }
    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDecisionNo() { return decisionNo; }
    public void setDecisionNo(String decisionNo) { this.decisionNo = decisionNo; }
    public Date getDecisionDate() { return decisionDate; }
    public void setDecisionDate(Date decisionDate) { this.decisionDate = decisionDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
}
