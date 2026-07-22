package dao;

import dal.DBContext;
import model.Combo;
import model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.CallableStatement;
import java.util.UUID;

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
    
    public void generateDefaultCombos(String curriculumId, String majorId) {
    String sql = "{CALL sp_GenerateDefaultCombos(?, ?)}";
    try (Connection con = getConnection(); 
         CallableStatement cs = con.prepareCall(sql)) {
        
        cs.setString(1, curriculumId);
        cs.setString(2, majorId);
        cs.executeUpdate();
        
    } catch (Exception e) {
        System.out.println("Lỗi khi sinh Combo mặc định: " + e.getMessage());
        e.printStackTrace();
    }
  }
    public boolean addCustomCombo(String curriculumId, String comboCode, String comboName, String englishName, String description, String[] subjectIds) {
        String sqlCombo = "INSERT INTO [dbo].[Combos] (Combo_ID, Curriculum_ID, Combo_Code, Combo_Name, English_Name, Description, Is_Active) VALUES (?, ?, ?, ?, ?, ?, 1)";
        String sqlComboSubject = "INSERT INTO [dbo].[Combo_Subjects] (Combo_Subject_ID, Combo_ID, Subject_ID, Semester_No) VALUES (?, ?, ?, ?)";
        
        try (Connection con = getConnection()) {
            con.setAutoCommit(false); // Bắt đầu Transaction
            try {
                // 1. Tạo Combo
                String newComboId = UUID.randomUUID().toString();
                try (PreparedStatement ps = con.prepareStatement(sqlCombo)) {
                    ps.setString(1, newComboId);
                    ps.setString(2, curriculumId);
                    ps.setString(3, comboCode); // Nhớ nhập mã Combo không được trùng với cái đã có nhé!
                    ps.setString(4, comboName);
                    ps.setString(5, englishName);
                    ps.setString(6, description);
                    ps.executeUpdate();
                }
                
                // 2. Thêm các môn học vào Combo_Subjects
                if (subjectIds != null && subjectIds.length > 0) {
                    try (PreparedStatement ps2 = con.prepareStatement(sqlComboSubject)) {
                        for (String subjectId : subjectIds) {
                            ps2.setString(1, UUID.randomUUID().toString());
                            ps2.setString(2, newComboId);
                            ps2.setString(3, subjectId);
                            // FIX LỖI: Gán cứng học kỳ là 1 thay vì để NULL để tránh DB báo lỗi
                            ps2.setInt(4, 1); 
                            ps2.addBatch();
                        }
                        ps2.executeBatch();
                    }
                }
                
                con.commit(); // Hoàn tất Transaction
                return true;
                
            } catch (Exception e) {
                con.rollback(); // Rollback không lưu gì cả nếu có bất kỳ lỗi nào
                System.out.println("========== LỖI SQL KHI ADD COMBO ==========");
                e.printStackTrace(); // Dòng này sẽ in chữ đỏ ra Console để bắt bệnh
                System.out.println("===========================================");
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}