package model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Du lieu de ve 1 bang mapping "CLOs to PLOs of Curriculum X" tren trang
 * /syllabus/clo-mapping. Vi 1 Subject (Syllabus) co the dung chung nhieu
 * Curriculum, va moi Curriculum co bo PLO rieng, nen can 1 bang rieng cho
 * moi Curriculum co dung mon hoc nay.
 */
public class CloPloMappingTable {

    private String curriculumId;
    private String curriculumCode;
    private String curriculumName;
    private List<ProgramLearningOutcome> plos;
    private Set<String> checkedPairs; // chua cac cap "CLO_ID|PLO_ID" da duoc mapping

    // Ma tran cloId -> (ploId -> true) de JSP truy cap an toan bang cu phap
    // bracket EL ${table.matrix[clo.cloId][plo.ploId]}, khong can goi method
    // co tham so trong EL (tranh phu thuoc phien ban EL).
    private Map<String, Map<String, Boolean>> matrix;

    public Map<String, Map<String, Boolean>> getMatrix() { return matrix; }
    public void setMatrix(Map<String, Map<String, Boolean>> matrix) { this.matrix = matrix; }

    public CloPloMappingTable() {
    }

    public CloPloMappingTable(String curriculumId, String curriculumCode, String curriculumName,
                               List<ProgramLearningOutcome> plos, Set<String> checkedPairs) {
        this.curriculumId = curriculumId;
        this.curriculumCode = curriculumCode;
        this.curriculumName = curriculumName;
        this.plos = plos;
        this.checkedPairs = checkedPairs;
    }

    public String getCurriculumId() { return curriculumId; }
    public void setCurriculumId(String curriculumId) { this.curriculumId = curriculumId; }

    public String getCurriculumCode() { return curriculumCode; }
    public void setCurriculumCode(String curriculumCode) { this.curriculumCode = curriculumCode; }

    public String getCurriculumName() { return curriculumName; }
    public void setCurriculumName(String curriculumName) { this.curriculumName = curriculumName; }

    public List<ProgramLearningOutcome> getPlos() { return plos; }
    public void setPlos(List<ProgramLearningOutcome> plos) { this.plos = plos; }

    public Set<String> getCheckedPairs() { return checkedPairs; }
    public void setCheckedPairs(Set<String> checkedPairs) { this.checkedPairs = checkedPairs; }

    /** Dung trong JSP: ${table.isChecked(clo.cloId, plo.ploId)} */
    public boolean isChecked(String cloId, String ploId) {
        return checkedPairs != null && checkedPairs.contains(cloId + "|" + ploId);
    }
}