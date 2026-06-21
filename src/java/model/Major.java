package model;

public class Major {
<<<<<<< HEAD
    // 1. Tên biến phải viết thường chữ cái đầu tiên (camelCase)
=======
>>>>>>> main
    private String majorId;
    private String majorCode;
    private String majorName;
    private String description;
<<<<<<< HEAD
    private boolean isActive;


    public Major() {
    }
=======
    private String isActive;

    public Major() {}
>>>>>>> main

    public String getMajorId() {
        return majorId;
    }

<<<<<<< HEAD
=======
    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

>>>>>>> main
    public String getMajorCode() {
        return majorCode;
    }

<<<<<<< HEAD
=======
    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

>>>>>>> main
    public String getMajorName() {
        return majorName;
    }

<<<<<<< HEAD
=======
    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

>>>>>>> main
    public String getDescription() {
        return description;
    }

<<<<<<< HEAD

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
=======
    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    
}
>>>>>>> main
