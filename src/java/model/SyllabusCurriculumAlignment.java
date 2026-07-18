package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyllabusCurriculumAlignment {
    private Curriculum curriculum;
    private List<ProgramObjective> pos = new ArrayList<>();
    private List<ProgramLearningOutcome> plos = new ArrayList<>();
    private Map<String, Boolean> ploCloMappings = new HashMap<>();

    public SyllabusCurriculumAlignment() {
    }

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }

    public List<ProgramObjective> getPos() {
        return pos;
    }

    public void setPos(List<ProgramObjective> pos) {
        this.pos = pos;
    }

    public List<ProgramLearningOutcome> getPlos() {
        return plos;
    }

    public void setPlos(List<ProgramLearningOutcome> plos) {
        this.plos = plos;
    }

    public Map<String, Boolean> getPloCloMappings() {
        return ploCloMappings;
    }

    public void setPloCloMappings(Map<String, Boolean> ploCloMappings) {
        this.ploCloMappings = ploCloMappings;
    }
}
