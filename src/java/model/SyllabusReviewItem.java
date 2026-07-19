package model;

public class SyllabusReviewItem {
    private String reviewDetailId;
    private String reviewId;
    private String criterionKey;
    private String criterionName;
    private double maxScore;
    private double score;
    private String comment;

    public SyllabusReviewItem() {
    }

    public String getReviewDetailId() {
        return reviewDetailId;
    }

    public void setReviewDetailId(String reviewDetailId) {
        this.reviewDetailId = reviewDetailId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getCriterionKey() {
        return criterionKey;
    }

    public void setCriterionKey(String criterionKey) {
        this.criterionKey = criterionKey;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}