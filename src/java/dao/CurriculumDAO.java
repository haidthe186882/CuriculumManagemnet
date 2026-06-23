package dao;

import dal.DBContext;
import model.Curriculum;
import model.Major;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurriculumDAO {

    private Curriculum mapCurriculum(ResultSet rs) throws SQLException {
        Curriculum c = new Curriculum();
        c.setCurriculumId(rs.getString("Curriculum_ID"));
        c.setMajorId(rs.getString("Major_ID"));
        c.setMajorName(rs.getString("Major_Name"));
        c.setCreatedBy(rs.getString("Created_By"));
        c.setCurriculumCode(rs.getString("Curriculum_Code"));
        c.setCurriculumName(rs.getString("Curriculum_Name"));
        c.setEnglishName(rs.getString("English_Name"));
        c.setDescription(rs.getString("Description"));
        c.setTotalCredits(rs.getInt("Total_Credits"));
        c.setVersion(rs.getString("Version"));
        c.setDecisionNo(rs.getString("Decision_No"));
        c.setDecisionDate(rs.getDate("Decision_Date"));
        
        // 1. ĐỌC CỘT IS_ACTIVE DẠNG BOOLEAN
        try {
            c.setIsActive(rs.getBoolean("Is_Active"));
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        

        
        try {
            c.setCreatedDate(rs.getTimestamp("Created_Date"));
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        c.setUpdatedDate(rs.getTimestamp("Updated_Date"));
        
        // Máp nối quan hệ với Major
        try {
            Major m = new Major();
            m.setMajorId(rs.getString("Major_ID"));
            m.setMajorName(rs.getString("Major_Name"));
            m.setMajorCode(rs.getString("Major_Code"));
            c.setMajorId(m.getMajorId());
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return c;
    }

    /**
     * Tìm kiếm curriculum lọc theo Status (tiến trình) và Is_Active (kích hoạt)
     */
    public List<Curriculum> searchCurriculums(String keyword, String status, boolean publicOnly) {
        List<Curriculum> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, m.Major_Name, m.Major_Code FROM Curriculums c "
                + "LEFT JOIN Majors m ON c.Major_ID = m.Major_ID WHERE 1=1");
        
        // Nếu là khách/học sinh, chỉ xem các bản ghi đang kích hoạt (Is_Active = 1)
        if (publicOnly) {
            sql.append(" AND c.Is_Active = 1");
        } else if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND c.Is_Active = ?");
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.Curriculum_Name LIKE ? OR c.Curriculum_Code LIKE ? OR c.English_Name LIKE ?)");
        }
        
        sql.append(" ORDER BY c.Created_Date DESC");

        try (Connection con = new DBContext().getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            int idx = 1;
            
            // Điền tham số lọc Status cho Admin/Designer
            if (!publicOnly && status != null && !status.trim().isEmpty()) {
                boolean activeVal = "Approved".equalsIgnoreCase(status) || "Active".equalsIgnoreCase(status) || "1".equals(status);
                ps.setBoolean(idx++, activeVal);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKey = "%" + keyword.trim() + "%";
                ps.setString(idx++, searchKey);
                ps.setString(idx++, searchKey);
                ps.setString(idx++, searchKey);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapCurriculum(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Curriculum getCurriculumById(String id) {
        String sql = "SELECT c.*, m.Major_Name, m.Major_Code FROM Curriculums c "
                + "LEFT JOIN Majors m ON c.Major_ID = m.Major_ID WHERE c.Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapCurriculum(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Curriculum> getAllCurriculums(String keyword, String status) {
        return searchCurriculums(keyword, status, false);
    }

    /**
     * Lấy danh sách chờ duyệt (Status = 2) cho Reviewer
     */
    public List<Curriculum> getPendingCurriculums() {
        return searchCurriculums(null, "2", false);
    }

    /**
     * Thêm mới: DB tự gán Status = 0 và Is_Active = 1 (hoặc 0 tùy ý bạn cấu hình trong SQL)
     */
    public boolean addCurriculum(Curriculum c) {
        String sql = """
                     INSERT INTO Curriculums (
                        Curriculum_ID, Major_ID, Created_By, Curriculum_Code, 
                        Curriculum_Name, English_Name, Description, Total_Credits, 
                        Version, Decision_No, Decision_Date, Is_Active, Created_Date
                     ) VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, GETDATE())
                     """; // Is_Active gán là 0 (đóng), Status để DB tự gán DEFAULT = 0

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getMajorId());
            ps.setString(2, c.getCreatedBy());
            ps.setString(3, c.getCurriculumCode());
            ps.setString(4, c.getCurriculumName());
            ps.setString(5, c.getEnglishName());
            ps.setString(6, c.getDescription());
            ps.setInt(7, c.getTotalCredits());
            ps.setString(8, c.getVersion());
            ps.setString(9, c.getDecisionNo());

            if (c.getDecisionDate() != null) {
                ps.setDate(10, new java.sql.Date(c.getDecisionDate().getTime()));
            } else {
                ps.setNull(10, Types.DATE);
            }

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCurriculum(Curriculum c) {
        String sql = "UPDATE Curriculums SET Curriculum_Name=?, English_Name=?, Description=?, "
                + "Total_Credits=?, Version=?, Decision_No=?, Decision_Date=?, Updated_Date=GETDATE() "
                + "WHERE Curriculum_ID=?";
        try (Connection con = new DBContext().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCurriculumName());
            ps.setString(2, c.getEnglishName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getTotalCredits());
            ps.setString(5, c.getVersion());
            ps.setString(6, c.getDecisionNo());
            ps.setDate(7, c.getDecisionDate() != null ? new java.sql.Date(c.getDecisionDate().getTime()) : null);
            ps.setString(8, c.getCurriculumId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean submitForReview(String curriculumId) {
        return toggleActive(curriculumId, false); // Pending/Draft -> Inactive
    }

    public boolean approveCurriculum(String curriculumId) {
        return toggleActive(curriculumId, true); // Approved -> Active
    }

    public boolean rejectCurriculum(String curriculumId) {
        return toggleActive(curriculumId, false); // Rejected -> Inactive
    }

    /**
     * Bật/Tắt trạng thái hiển thị hệ thống (Is_Active) độc lập
     */
    public boolean toggleActive(String curriculumId, boolean isActive) {
        String sql = "UPDATE Curriculums SET Is_Active = ?, Updated_Date = GETDATE() WHERE Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setString(2, curriculumId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}