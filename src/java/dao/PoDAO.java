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

    /**
     * Tao moi 1 PO cho curriculum.
     */
    public boolean addPO(String curriculumId, String poCode, String description) {
        String sql = "INSERT INTO POs (PO_ID, Curriculum_ID, PO_Code, Description) "
                   + "VALUES (NEWID(), ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.setString(2, poCode);
            ps.setString(3, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xoa 1 PO (se xoa luon cac mapping PO_PLO_Mappings lien quan truoc do
     * de tranh vi pham khoa ngoai).
     */
    public boolean deletePO(String poId) {
        String delMap = "DELETE FROM PO_PLO_Mappings WHERE PO_ID = ?";
        String delPo  = "DELETE FROM POs WHERE PO_ID = ?";
        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(delMap)) {
                ps1.setString(1, poId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(delPo)) {
                ps2.setString(1, poId);
                ps2.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Luu lai toan bo ma tran mapping PO-PLO cho 1 curriculum:
     * xoa het mapping cu cua cac PO thuoc curriculum nay, roi insert lai
     * theo danh sach checkedKeys (moi key co dang "PO_ID_PLO_ID").
     */
    public boolean saveMappings(String curriculumId, String[] checkedKeys) {
        String delSql = "DELETE FROM PO_PLO_Mappings "
                       + "WHERE PO_ID IN (SELECT PO_ID FROM POs WHERE Curriculum_ID = ?)";
        String insSql = "INSERT INTO PO_PLO_Mappings (Mapping_ID, PO_ID, PLO_ID) VALUES (NEWID(), ?, ?)";

        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement del = con.prepareStatement(delSql)) {
                del.setString(1, curriculumId);
                del.executeUpdate();
            }

            if (checkedKeys != null && checkedKeys.length > 0) {
                try (PreparedStatement ins = con.prepareStatement(insSql)) {
                    for (String key : checkedKeys) {
                        // key dang "PO_ID_PLO_ID" - GUID chi dung dau '-' nen split theo
                        // dau '_' dau tien la an toan
                        int sep = key.indexOf('_');
                        if (sep < 0) continue;
                        String poId = key.substring(0, sep);
                        String ploId = key.substring(sep + 1);
                        ins.setString(1, poId);
                        ins.setString(2, ploId);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}