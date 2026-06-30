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

    /**
     * Tao moi 1 PLO cho curriculum.
     */
    public boolean addPLO(String curriculumId, String ploCode, String description) {
        String sql = "INSERT INTO PLOs (PLO_ID, Curriculum_ID, PLO_Code, Description) "
                   + "VALUES (NEWID(), ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.setString(2, ploCode);
            ps.setString(3, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xoa 1 PLO (xoa luon mapping PO_PLO_Mappings va PLO_CLO_Mappings
     * lien quan truoc de tranh vi pham khoa ngoai).
     */
    public boolean deletePLO(String ploId) {
        String delPoMap  = "DELETE FROM PO_PLO_Mappings WHERE PLO_ID = ?";
        String delCloMap = "DELETE FROM PLO_CLO_Mappings WHERE PLO_ID = ?";
        String delPlo    = "DELETE FROM PLOs WHERE PLO_ID = ?";
        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(delPoMap)) {
                ps1.setString(1, ploId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(delCloMap)) {
                ps2.setString(1, ploId);
                ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = con.prepareStatement(delPlo)) {
                ps3.setString(1, ploId);
                ps3.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes all PLOs for a curriculum (used during Excel re-import).
     */
    public void deletePLOsByCurriculum(String curriculumId) {
        String sql = "DELETE FROM Program_Learning_Outcomes WHERE Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}