package model;

public class ProgramLearningOutcome {
    private String ploId;
    private String curriculumId;
    private String ploCode;
    private String description;

    public ProgramLearningOutcome() {}

    public String getPloId() { return ploId; }
    public void setPloId(String ploId) { this.ploId = ploId; }
    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }
    public String getPloCode() { return ploCode; }
    public void setPloCode(String ploCode) { this.ploCode = ploCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
