package model;

public class ProgramObjective {
    private String poId;
    private String curriculumId;
    private String poCode;
    private String description;

    public ProgramObjective() {}

    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }
    public String getPoCode() { return poCode; }
    public void setPoCode(String poCode) { this.poCode = poCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
