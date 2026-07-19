package model;

public class Combo {
    private String comboId;
    private String curriculumId;
    private String comboCode;
    private String comboName;
    private String englishName;
    private String description;
    private boolean active;

    public Combo() {
    }

    // Getters and Setters
    public String getComboId() { return comboId; }
    public void setComboId(String comboId) { this.comboId = comboId; }

    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }

    public String getComboCode() { return comboCode; }
    public void setComboCode(String comboCode) { this.comboCode = comboCode; }

    public String getComboName() { return comboName; }
    public void setComboName(String comboName) { this.comboName = comboName; }

    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}