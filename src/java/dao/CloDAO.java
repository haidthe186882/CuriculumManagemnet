package dao;

import dal.DBContext;
import model.CourseLearningOutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CloDAO {

    public List<CourseLearningOutcome> getCLOsBySyllabus(String syllabusId) {
        List<CourseLearningOutcome> list = new ArrayList<>();
        String sql = "SELECT CLO_ID, Syllabus_ID, CLO_Code, Description FROM CLOs WHERE Syllabus_ID = ? ORDER BY CLO_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseLearningOutcome clo = new CourseLearningOutcome();
                    clo.setCloId(rs.getString("CLO_ID"));
                    clo.setSyllabusId(rs.getString("Syllabus_ID"));
                    clo.setCloCode(rs.getString("CLO_Code"));
                    clo.setDescription(rs.getString("Description"));
                    list.add(clo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
