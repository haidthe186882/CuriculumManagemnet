package model;

public class Subject {
    private String subjectId;
    private String subjectCode;
    private String subjectName;
    private String englishName;
    private int credits;
    private String description;
    private String department;
    private String status;

    public Subject() {}

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    private String syllabusId;
    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }

    // Trang thai quy trinh thiet ke cua Syllabus gan voi subject nay, trong ngu canh
    // 1 Curriculum cu the (0=Draft,1=PendingReview,2=Approved="hoan thanh")
    private int syllabusStatusCode;
    public int getSyllabusStatusCode() { return syllabusStatusCode; }
    public void setSyllabusStatusCode(int syllabusStatusCode) { this.syllabusStatusCode = syllabusStatusCode; }
    public boolean isDesignComplete() { return syllabusStatusCode == Syllabus.STATUS_APPROVED; }

    // Danh sach ma mon tien quyet (Subject_Prerequisites), noi bang dau phay de hien thi
    private String prerequisiteCodes;
    public String getPrerequisiteCodes() { return prerequisiteCodes; }
    public void setPrerequisiteCodes(String prerequisiteCodes) { this.prerequisiteCodes = prerequisiteCodes; }

    private java.util.List<Subject> prerequisites = new java.util.ArrayList<>();
    public java.util.List<Subject> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(java.util.List<Subject> prerequisites) { this.prerequisites = prerequisites; }
}
