package dao;

import dal.DBContext;
import model.ProgramObjective;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PoDAO {

    public List<ProgramObjective> getPOsByCurriculum(String curriculumId) {
        List<ProgramObjective> list = new ArrayList<>();
        String sql = "SELECT PO_ID, Curriculum_ID, PO_Code, Description FROM POs WHERE Curriculum_ID = ? ORDER BY PO_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProgramObjective po = new ProgramObjective();
                    po.setPoId(rs.getString("PO_ID"));
                    po.setCurriculumId(rs.getString("Curriculum_ID"));
                    po.setPoCode(rs.getString("PO_Code"));
                    po.setDescription(rs.getString("Description"));
                    list.add(po);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Boolean> getPoPloMappings(String curriculumId) {
        Map<String, Boolean> map = new HashMap<>();
        String sql = "SELECT PO_ID, PLO_ID FROM PO_PLO_Mappings "
                   + "WHERE PO_ID IN (SELECT PO_ID FROM POs WHERE Curriculum_ID = ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("PO_ID") + "_" + rs.getString("PLO_ID"), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
