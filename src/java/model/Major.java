package model;

public class Major {
    // 1. Tên biến phải viết thường chữ cái đầu tiên (camelCase)
    private String majorId;
    private String majorCode;
    private String majorName;
    private String description;
    private boolean isActive;


    public Major() {
    }

    public String getMajorId() {
        return majorId;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public String getDescription() {
        return description;
    }


    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) { 
        this.isActive = isActive; 
    }

    // Các hàm Setter phía dưới...
    public void setMajorId(String majorId) { this.majorId = majorId; }
    public void setMajorCode(String majorCode) { this.majorCode = majorCode; }
    public void setMajorName(String majorName) { this.majorName = majorName; }
    public void setDescription(String description) { this.description = description; }
    
}