package dao;

import dal.DBContext;
import model.Combo;
import model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboDAO extends DBContext {

    // 1. Lấy danh sách Combo theo Curriculum_ID
    public List<Combo> getCombosByCurriculumId(String curriculumId) {
        List<Combo> list = new ArrayList<>();
        String sql = "SELECT * FROM Combos WHERE Curriculum_ID = ? ORDER BY Combo_Code";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Combo c = new Combo();
                c.setComboId(rs.getString("Combo_ID"));
                c.setCurriculumId(rs.getString("Curriculum_ID"));
                c.setComboCode(rs.getString("Combo_Code"));
                c.setComboName(rs.getString("Combo_Name"));
                c.setEnglishName(rs.getString("English_Name"));
                c.setDescription(rs.getString("Description"));
                c.setActive(rs.getBoolean("Is_Active"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy thông tin chi tiết của 1 Combo
    public Combo getComboById(String comboId) {
        String sql = "SELECT * FROM Combos WHERE Combo_ID = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, comboId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Combo c = new Combo();
                c.setComboId(rs.getString("Combo_ID"));
                c.setCurriculumId(rs.getString("Curriculum_ID"));
                c.setComboCode(rs.getString("Combo_Code"));
                c.setComboName(rs.getString("Combo_Name"));
                c.setEnglishName(rs.getString("English_Name"));
                c.setDescription(rs.getString("Description"));
                c.setActive(rs.getBoolean("Is_Active"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. Lấy danh sách các môn học thuộc Combo
    public List<Subject> getSubjectsByComboId(String comboId) {
        List<Subject> list = new ArrayList<>();
        // Sửa câu lệnh SQL để lấy thêm Semester_No từ bảng trung gian Combo_Subjects
        String sql = "SELECT s.*, cs.Semester_No FROM Subjects s "
                + "INNER JOIN Combo_Subjects cs ON s.Subject_ID = cs.Subject_ID "
                + "WHERE cs.Combo_ID = ? ORDER BY cs.Semester_No, s.Subject_Code";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, comboId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject s = new Subject();
                s.setSubjectId(rs.getString("Subject_ID"));
                s.setSubjectCode(rs.getString("Subject_Code"));
                s.setSubjectName(rs.getString("Subject_Name"));
                s.setEnglishName(rs.getString("English_Name"));
                s.setCredits(rs.getInt("Credits"));
                // Gán giá trị semesterNo đã lấy từ SQL
                s.setSemesterNo(rs.getInt("Semester_No"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}