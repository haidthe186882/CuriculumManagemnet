package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Material {
    private String fileName;
    private String courseCode;
    private String description;
    private long sizeBytes;
    private Date uploadedAt;
    private String status; // Published, Draft

    public Material(String fileName, String courseCode, String description, long sizeBytes, Date uploadedAt, String status) {
        this.fileName = fileName;
        this.courseCode = courseCode;
        this.description = description;
        this.sizeBytes = sizeBytes;
        this.uploadedAt = uploadedAt;
        this.status = status;
    }

    public String getFileName() { return fileName; }
    public String getCourseCode() { return courseCode; }
    public String getDescription() { return description; }
    public long getSizeBytes() { return sizeBytes; }
    public Date getUploadedAt() { return uploadedAt; }
    public String getStatus() { return status; }

    public String getSizeReadable() {
        long bytes = sizeBytes;
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public String getUploadedDateString() {
        if (uploadedAt == null) return "-";
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        return fmt.format(uploadedAt);
    }
}
