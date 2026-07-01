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

    /** Insert a single CLO */
    public boolean addCLO(CourseLearningOutcome clo) {
        String sql = "INSERT INTO CLOs (CLO_ID, Syllabus_ID, CLO_Code, Description) VALUES (NEWID(), ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clo.getSyllabusId());
            ps.setString(2, clo.getCloCode());
            ps.setString(3, clo.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Bulk insert CLOs for a syllabus */
    public int addCLOs(String syllabusId, List<CourseLearningOutcome> clos) {
        int count = 0;
        for (CourseLearningOutcome clo : clos) {
            clo.setSyllabusId(syllabusId);
            if (addCLO(clo)) count++;
        }
        return count;
    }
}
