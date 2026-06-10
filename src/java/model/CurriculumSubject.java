package model;

public class CurriculumSubject {
    private String curriculumSubjectId;
    private String curriculumId;
    private String subjectId;
    private int semesterNo;
    private boolean isMandatory;
    // joins
    private Subject subject;

    public CurriculumSubject() {}

    public String getCurriculumSubjectId() { return curriculumSubjectId; }
    public void setCurriculumSubjectId(String curriculumSubjectId) { this.curriculumSubjectId = curriculumSubjectId; }
    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public int getSemesterNo() { return semesterNo; }
    public void setSemesterNo(int semesterNo) { this.semesterNo = semesterNo; }
    public boolean isMandatory() { return isMandatory; }
    public void setMandatory(boolean isMandatory) { this.isMandatory = isMandatory; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
}
