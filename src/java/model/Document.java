package model;

import java.util.Date;

public class Document {
    private String documentId;
    private String curriculumId;
    private String subjectId;
    private String syllabusId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String classCode;
    private String documentType;
    private String semester;
    private String uploadedBy;
    private Date uploadedDate;
    // join
    private User uploader;

    public Document() {}

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public Date getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(Date uploadedDate) { this.uploadedDate = uploadedDate; }
    public User getUploader() { return uploader; }
    public void setUploader(User uploader) { this.uploader = uploader; }

    public String getFileSizeDisplay() {
        if (fileSize == null) return "N/A";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024));
    }
}
