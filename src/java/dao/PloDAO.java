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
        String sql = "SELECT PLO_ID, Curriculum_ID, PLO_Code, Description FROM PLOs WHERE Curriculum_ID = ? "
                   + "ORDER BY LEN(PLO_Code), PLO_Code";
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
     * Xoa toan bo PLO_CLO_Mappings gan voi cac CLO cua 1 Syllabus. BAT BUOC goi
     * truoc khi xoa cac CLO do (CloDAO#deleteCLOsBySyllabus), vi PLO_CLO_Mappings
     * co FK tro toi CLO_ID KHONG co ON DELETE CASCADE -> xoa CLO truoc se bi
     * loi vi pham khoa ngoai neu con mapping tro toi.
     */
    public boolean deleteMappingsBySyllabus(String syllabusId) {
        String sql = "DELETE FROM PLO_CLO_Mappings WHERE CLO_ID IN (SELECT CLO_ID FROM CLOs WHERE Syllabus_ID = ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /**
     * Xoa PLO_CLO_Mappings cua 1 Syllabus, GIOI HAN trong PLO cua 1 Curriculum cu
     * the (khac voi deleteMappingsBySyllabus() o tren la xoa toan bo). Dung cho
     * che do "Mapping-only save" (xem SyllabusServlet#doPost) khi Syllabus DA
     * Approved va dang duoc mapping bo sung cho 1 Curriculum khac dung chung
     * Subject - khong duoc dung tay vao mapping cua cac Curriculum khac.
     */
    public boolean deleteMappingsBySyllabusAndCurriculum(String syllabusId, String curriculumId) {
        String sql = "DELETE FROM PLO_CLO_Mappings WHERE " +
                     "CLO_ID IN (SELECT CLO_ID FROM CLOs WHERE Syllabus_ID = ?) AND " +
                     "PLO_ID IN (SELECT PLO_ID FROM PLOs WHERE Curriculum_ID = ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.setString(2, curriculumId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /**
     * Tra ve TOAN BO mapping CLO-PLO hien co cua 1 Syllabus, kem Curriculum_ID
     * cua PLO va CLO_Code (thay vi CLO_ID, vi CLO_ID se doi moi lan luu lai
     * Syllabus). Dung de "giu lai" mapping cua cac Curriculum KHONG xuat hien
     * trong form dang submit (xem SyllabusServlet#doCreate) truoc khi CLOs cu
     * bi xoa va tao lai voi ID moi.
     * Moi phan tu tra ve: {PLO_ID, CLO_Code, Curriculum_ID}
     */
    public List<String[]> getAllCloCodePloMappings(String syllabusId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT m.PLO_ID, c.CLO_Code, p.Curriculum_ID " +
                     "FROM PLO_CLO_Mappings m " +
                     "JOIN CLOs c ON m.CLO_ID = c.CLO_ID " +
                     "JOIN PLOs p ON m.PLO_ID = p.PLO_ID " +
                     "WHERE c.Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{rs.getString("PLO_ID"), rs.getString("CLO_Code"), rs.getString("Curriculum_ID")});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Ghi 1 cap mapping CLO-PLO. Bo qua neu da co san (tranh loi trung). */
    public boolean addMapping(String cloId, String ploId) {
        String checkSql = "SELECT 1 FROM PLO_CLO_Mappings WHERE CLO_ID = ? AND PLO_ID = ?";
        String insertSql = "INSERT INTO PLO_CLO_Mappings (Mapping_ID, PLO_ID, CLO_ID) VALUES (?, ?, ?)";
        try (Connection con = new DBContext().getConnection()) {
            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setString(1, cloId);
                check.setString(2, ploId);
                if (check.executeQuery().next()) return true; // already mapped
            }
            try (PreparedStatement ins = con.prepareStatement(insertSql)) {
                ins.setString(1, java.util.UUID.randomUUID().toString());
                ins.setString(2, ploId);
                ins.setString(3, cloId);
                return ins.executeUpdate() > 0;
            }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /**
     * Tra ve tap hop cac cap "CLO_ID|PLO_ID" da duoc mapping, gioi han trong
     * CLO cua 1 Syllabus va PLO cua 1 Curriculum cu the (vi 1 Subject co the
     * dung chung nhieu Curriculum, moi Curriculum co bo PLO rieng).
     */
    public java.util.Set<String> getCheckedCloPloPairs(String syllabusId, String curriculumId) {
        java.util.Set<String> set = new java.util.HashSet<>();
        String sql = "SELECT m.CLO_ID, m.PLO_ID FROM PLO_CLO_Mappings m "
                   + "JOIN CLOs c ON m.CLO_ID = c.CLO_ID "
                   + "JOIN PLOs p ON m.PLO_ID = p.PLO_ID "
                   + "WHERE c.Syllabus_ID = ? AND p.Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.setString(2, curriculumId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    set.add(rs.getString("CLO_ID") + "|" + rs.getString("PLO_ID"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set;
    }

    /**
     * Deletes all PLOs for a curriculum (used during Excel re-import).
     */
    public void deletePLOsByCurriculum(String curriculumId) {
        String delMap = "DELETE FROM PO_PLO_Mappings WHERE PLO_ID IN (SELECT PLO_ID FROM PLOs WHERE Curriculum_ID = ?)";
        String delClo = "DELETE FROM PLO_CLO_Mappings WHERE PLO_ID IN (SELECT PLO_ID FROM PLOs WHERE Curriculum_ID = ?)";
        String sql = "DELETE FROM PLOs WHERE Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(delMap)) {
                ps1.setString(1, curriculumId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(delClo)) {
                ps2.setString(1, curriculumId);
                ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = con.prepareStatement(sql)) {
                ps3.setString(1, curriculumId);
                ps3.executeUpdate();
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}