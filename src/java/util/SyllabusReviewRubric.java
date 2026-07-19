package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.SyllabusReviewItem;

public final class SyllabusReviewRubric {

    public static final class Criterion {
        private final String key;
        private final String name;
        private final double maxScore;
        private final String guidance;

        public Criterion(String key, String name, double maxScore, String guidance) {
            this.key = key;
            this.name = name;
            this.maxScore = maxScore;
            this.guidance = guidance;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public double getMaxScore() {
            return maxScore;
        }

        public String getGuidance() {
            return guidance;
        }
    }

    private static final List<Criterion> CRITERIA;

    static {
        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("general_information", "General information", 10,
                "Subject code, syllabus name, version, decision number, approved date and key metadata are present and internally consistent."));
        criteria.add(new Criterion("course_overview", "Course overview and description", 10,
                "The syllabus explains the course scope, expected context and how the subject fits the program."));
        criteria.add(new Criterion("time_allocation", "Time allocation and delivery structure", 10,
                "Theory, practice, self-study and session distribution are realistic and clearly presented."));
        criteria.add(new Criterion("clos", "CLO quality", 15,
                "CLOs are specific, measurable, aligned with the subject and written clearly."));
        criteria.add(new Criterion("sessions", "Session plan alignment", 20,
                "Topics, learning-teaching type, LO, ITU, student materials and tasks are coherent across sessions."));
        criteria.add(new Criterion("assessment", "Assessment and scoring scale", 15,
                "Scoring scale, pass threshold and assessment information are complete, balanced and understandable."));
        criteria.add(new Criterion("learning_support", "Student tasks and tools", 10,
                "Student tasks, tools and supporting instructions are practical and support the intended outcomes."));
        criteria.add(new Criterion("materials", "Learning materials and references", 10,
                "Main materials, references, links and notes are adequate, up to date and usable."));
        CRITERIA = Collections.unmodifiableList(criteria);
    }

    private SyllabusReviewRubric() {
    }

    public static List<Criterion> getCriteria() {
        return CRITERIA;
    }

    public static double getMaximumScore() {
        double total = 0;
        for (Criterion criterion : CRITERIA) {
            total += criterion.getMaxScore();
        }
        return total;
    }

    public static List<SyllabusReviewItem> buildDefaultItems() {
        List<SyllabusReviewItem> items = new ArrayList<>();
        for (Criterion criterion : CRITERIA) {
            SyllabusReviewItem item = new SyllabusReviewItem();
            item.setCriterionKey(criterion.getKey());
            item.setCriterionName(criterion.getName());
            item.setMaxScore(criterion.getMaxScore());
            item.setScore(0);
            item.setComment("");
            items.add(item);
        }
        return items;
    }
}