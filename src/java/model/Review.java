package model;

import java.util.Date;
import java.util.List;

public class Review {
    private String reviewId;
    private String syllabusId;
    private String curriculumId; // giu lai de tuong thich nguoc (khong con cot DB), dung khi can gan ngu canh 1 curriculum cu the
    private String reviewerId;
    private String status;   // Pending / Approved / Rejected
    private String comment;
    private Date reviewDate;
    private double totalScore;
    // joins
    private String subjectCode;
    private String subjectName;
    private Curriculum curriculum;
    private User reviewer;
    private Syllabus syllabus;
    private List<SyllabusReviewItem> items;

    public Review() {}

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getReviewDate() { return reviewDate; }
    public void setReviewDate(Date reviewDate) { this.reviewDate = reviewDate; }
    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    public Curriculum getCurriculum() { return curriculum; }
    public void setCurriculum(Curriculum curriculum) { this.curriculum = curriculum; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    public Syllabus getSyllabus() { return syllabus; }
    public void setSyllabus(Syllabus syllabus) { this.syllabus = syllabus; }
    public List<SyllabusReviewItem> getItems() { return items; }
    public void setItems(List<SyllabusReviewItem> items) { this.items = items; }
}
