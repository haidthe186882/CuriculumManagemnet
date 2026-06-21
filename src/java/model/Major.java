package model;

public class Major {

    private String majorId;
    private String majorCode;
    private String majorName;
    private String description;
    // Giữ theo kiểu boolean (HEAD) hoặc String (main) tùy thuộc vào tầng DAO của dự án. 
    // Dưới đây giữ cấu trúc đầy đủ chuẩn JavaBeans đan xen để dễ quản lý:
    private boolean isActive; 

    public Major() {
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsActive() { // Hoặc getIsActive() tùy theo thói quen đặt tên sinh tự động
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}