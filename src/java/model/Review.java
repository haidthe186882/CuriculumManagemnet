package model;

import java.util.Date;

public class Review {
    private String reviewId;
    private String curriculumId;
    private String reviewerId;
    private String status;   // Pending / Approved / Rejected
    private String comment;
    private Date reviewDate;
    // joins
    private Curriculum curriculum;
    private User reviewer;

    public Review() {}

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
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
    public Curriculum getCurriculum() { return curriculum; }
    public void setCurriculum(Curriculum curriculum) { this.curriculum = curriculum; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
}
