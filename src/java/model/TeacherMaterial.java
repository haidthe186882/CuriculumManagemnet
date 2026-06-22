package model;

import java.util.Date;

/**
 * Model mapping the Teacher_Materials table.
 * Columns: Material_ID (IDENTITY), User_ID, Syllabus_ID, Material_Type,
 *          Material_Name, Material_URL, Description, Is_Active, Created_Date
 */
public class TeacherMaterial {

    private String materialId;
    private String userId;
    private String syllabusId;
    private String materialType;
    private String materialName;
    private String materialUrl;
    private String description;
    private boolean isActive;
    private Date createdDate;

    // joined fields for display
    private String syllabusName;
    private String subjectCode;
    private String teacherName;

    public TeacherMaterial() {}

    // --- Getters / Setters ---

    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }

    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getMaterialUrl() { return materialUrl; }
    public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public String getSyllabusName() { return syllabusName; }
    public void setSyllabusName(String syllabusName) { this.syllabusName = syllabusName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getCreatedDateString() {
        if (createdDate == null) return "-";
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return fmt.format(createdDate);
    }
}
