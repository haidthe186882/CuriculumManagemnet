package dao;

import dal.DBContext;
import model.ProgramLearningOutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PloDAO {

    public List<ProgramLearningOutcome> getPLOsByCurriculum(String curriculumId) {
        List<ProgramLearningOutcome> list = new ArrayList<>();
        String sql = "SELECT PLO_ID, Curriculum_ID, PLO_Code, Description FROM PLOs WHERE Curriculum_ID = ? ORDER BY PLO_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProgramLearningOutcome plo = new ProgramLearningOutcome();
                    plo.setPloId(rs.getString("PLO_ID"));
                    plo.setCurriculumId(rs.getString("Curriculum_ID"));
                    plo.setPloCode(rs.getString("PLO_Code"));
                    plo.setDescription(rs.getString("Description"));
                    list.add(plo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
