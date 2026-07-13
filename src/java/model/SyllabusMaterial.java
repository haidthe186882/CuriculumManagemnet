package model;

import java.util.Date;

public class SyllabusMaterial {
    private String materialId;
    private String syllabusId;
    private String materialDescription;
    private String author;
    private String publisher;
    private Date publishedDate;
    private String edition;
    private String isbn;
    private boolean isMainMaterial;
    private boolean isHardCopy;
    private boolean isOnline;
    private String link;
    private String notes;
    private String filePath;        // maps to Download_Link column - server file path e.g. /uploads/documents/xxx.pdf
    private String originalFileName; // original uploaded file name
    private long fileSize;           // file size in bytes

    public SyllabusMaterial() {}

    // Getters and Setters
    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }

    public String getMaterialDescription() { return materialDescription; }
    public void setMaterialDescription(String materialDescription) { this.materialDescription = materialDescription; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isMainMaterial() { return isMainMaterial; }
    public void setMainMaterial(boolean isMainMaterial) { this.isMainMaterial = isMainMaterial; }

    public boolean isHardCopy() { return isHardCopy; }
    public void setHardCopy(boolean isHardCopy) { this.isHardCopy = isHardCopy; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean isOnline) { this.isOnline = isOnline; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFileSizeDisplay() {
        if (fileSize <= 0) return "";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024));
    }
}
