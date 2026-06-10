package model;

public class CourseLearningOutcome {
    private String cloId;
    private String syllabusId;
    private String cloCode;
    private String description;

    public CourseLearningOutcome() {}

    public String getCloId() { return cloId; }
    public void setCloId(String cloId) { this.cloId = cloId; }
    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }
    public String getCloCode() { return cloCode; }
    public void setCloCode(String cloCode) { this.cloCode = cloCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
