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
        
        // 2. ĐỌC CỘT STATUS DẠNG INT ĐỂ QUẢN LÝ TIẾN TRÌNH THIẾT KẾ
        try {
            c.setStatus(rs.getInt("Status"));
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }

        // 3. Is_Public: curriculum chi hien thi cong khai khi da Publish
        try {
            c.setIsPublic(rs.getBoolean("Is_Public"));
        } catch (SQLException ignored) {
            // DB chua chay patch them cot Is_Public -> coi nhu chua Publish
            c.setIsPublic(false);
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
     * Tìm kiếm curriculum lọc theo Status (tiến trình), Major_ID và Is_Active (kích hoạt)
     */
    public List<Curriculum> searchCurriculums(String keyword, String status, String majorId, boolean publicOnly) {
        List<Curriculum> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, m.Major_Name, m.Major_Code FROM Curriculums c "
                + "LEFT JOIN Majors m ON c.Major_ID = m.Major_ID WHERE 1=1");
        
        // Nếu là khách/học sinh, chỉ xem các Curriculum đã Publish (Is_Public = 1) và đang kích hoạt (Is_Active = 1)
        if (publicOnly) {
            sql.append(" AND c.Is_Public = 1 AND c.Is_Active = 1");
        } else if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND c.Status = ?");
        }

        if (majorId != null && !majorId.trim().isEmpty()) {
            sql.append(" AND c.Major_ID = ?");
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
                try {
                    ps.setInt(idx++, Integer.parseInt(status));
                } catch (NumberFormatException e) {
                    if ("Pending".equalsIgnoreCase(status)) ps.setInt(idx++, 2);
                    else if ("Draft".equalsIgnoreCase(status)) ps.setInt(idx++, 0);
                    else if ("Active".equalsIgnoreCase(status) || "Approved".equalsIgnoreCase(status)) ps.setInt(idx++, 1);
                    else idx++;
                }
            }

            if (majorId != null && !majorId.trim().isEmpty()) {
                ps.setString(idx++, majorId);
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
        return searchCurriculums(keyword, status, null, false);
    }

    /**
     * @deprecated Luong duyet moi la duyet TUNG SUBJECT/SYLLABUS (xem
     * ReviewDAO#getPendingForReviewer), khong con duyet ca Curriculum mot lan.
     * Giu lai ham nay (tra ve rong) chi de tranh vo cac cho con goi cu;
     * KHONG dung cho logic moi.
     */
    @Deprecated
    public List<Curriculum> getPendingCurriculums(String reviewerId, boolean isAdmin) {
        return new ArrayList<>();
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

    /**
     * Hàm cập nhật tiến trình thiết kế (Status)
     */
    public boolean updateStatus(String curriculumId, int newStatus) {
        String sql = "UPDATE Curriculums SET Status = ?, Updated_Date = GETDATE() WHERE Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newStatus);
            ps.setString(2, curriculumId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean submitForReview(String curriculumId) {
        return updateStatus(curriculumId, 2); // 2: Pending
    }

    public boolean approveCurriculum(String curriculumId) {
        return updateStatus(curriculumId, 1); // 1: Approved
    }

    public boolean rejectCurriculum(String curriculumId) {
        return updateStatus(curriculumId, 0); // 0: Trả về nháp Draft
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
    
    /**
     * Publish / Unpublish 1 Curriculum. Publish (Is_Public=1) chi nen duoc goi
     * SAU KHI CurriculumServlet da kiem tra tat ca subject deu "hoan thanh"
     * (xem SubjectDAO#getIncompleteSubjects). Khi publish thanh cong, Status
     * cung duoc dua ve 1 (Approved) de tuong thich voi cac man hinh cu con
     * doc cot Status. Unpublish dua Status ve 0 (Draft) va an khoi trang cong khai.
     */
    public boolean setPublic(String curriculumId, boolean isPublic) {
        String sql = "UPDATE Curriculums SET Is_Public = ?, Status = ?, Updated_Date = GETDATE() WHERE Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, isPublic);
            ps.setInt(2, isPublic ? 1 : 0);
            ps.setString(3, curriculumId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Danh sach cac Curriculum co dung Subject nay (1 Subject co the dung chung
     * nhieu Curriculum). Dung cho trang "View mapping of CLOs to PLOs": voi moi
     * Curriculum co dung mon hoc, hien 1 bang mapping CLO-PLO rieng (vi PLO
     * thuoc ve tung Curriculum, khac nhau giua cac Curriculum).
     */
    public List<Curriculum> getCurriculumsBySubject(String subjectId) {
        List<Curriculum> list = new ArrayList<>();
        String sql = "SELECT c.*, m.Major_Name, m.Major_Code FROM Curriculums c "
                   + "LEFT JOIN Majors m ON c.Major_ID = m.Major_ID "
                   + "JOIN Curriculum_Subjects cs ON c.Curriculum_ID = cs.Curriculum_ID "
                   + "WHERE cs.Subject_ID = ? ORDER BY c.Curriculum_Code";
        try (Connection con = new dal.DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCurriculum(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean checkCurriculumCodeExists(String curriculumCode) {
        String sql = "SELECT 1 FROM Curriculums WHERE Curriculum_Code = ?";
        try (Connection con = new dal.DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumCode);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Nếu có dữ liệu trả về nghĩa là đã tồn tại
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Gan nhanh 1 Designer va/hoac 1 Reviewer cho TAT CA subject trong Curriculum
     * ma con "chua hoan thanh" (Syllabus.Status != Approved). Day la thao tac
     * bulk tien loi cho Admin, thay the cho viec phai vao tung Subject de gan
     * rieng le. Ghi vao bang Syllabus_Assignments (khong con Curriculum_Assignments).
     */
    public void assignCurriculumRoles(String curriculumId, String designerId, String reviewerId, String adminId) {
        String findIncompleteSyllabusesSql =
                "SELECT sy.Syllabus_ID FROM Curriculum_Subjects cs " +
                "JOIN Subjects s ON cs.Subject_ID = s.Subject_ID " +
                "OUTER APPLY (SELECT TOP 1 sy2.Syllabus_ID, sy2.Status FROM Syllabuses sy2 " +
                "              WHERE sy2.Subject_ID = s.Subject_ID AND sy2.Is_Active = 1 " +
                "              ORDER BY sy2.Status DESC, sy2.Syllabus_ID) sy " +
                "WHERE cs.Curriculum_ID = ? AND sy.Syllabus_ID IS NOT NULL AND sy.Status <> 2";

        java.util.List<String> syllabusIds = new ArrayList<>();
        try (Connection con = new dal.DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(findIncompleteSyllabusesSql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) syllabusIds.add(rs.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        dao.DesignDAO designDAO = new dao.DesignDAO();
        for (String syllabusId : syllabusIds) {
            if (designerId != null && !designerId.trim().isEmpty()) {
                designDAO.assignUser(syllabusId, designerId, "Designer", adminId);
            }
            if (reviewerId != null && !reviewerId.trim().isEmpty()) {
                designDAO.assignUser(syllabusId, reviewerId, "Reviewer", adminId);
            }
        }
    }
    
    /**
     * Kiểm tra xem User có được phân công (Assign) vào Subject/Syllabus nao do
     * trong Curriculum nay hay khong (dua qua Syllabus_Assignments moi).
     */
    public boolean checkAssignment(String curriculumId, String userId, String assignmentType) {
        String sql = "SELECT 1 FROM Syllabus_Assignments sa " +
                     "JOIN Syllabuses sy ON sa.Syllabus_ID = sy.Syllabus_ID " +
                     "JOIN Curriculum_Subjects cs ON cs.Subject_ID = sy.Subject_ID " +
                     "WHERE cs.Curriculum_ID = ? AND sa.User_ID = ? AND sa.Assignment_Type = ?";
        try (Connection con = new dal.DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.setString(2, userId);
            ps.setString(3, assignmentType);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Có dữ liệu -> True (Được phân công)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts a new curriculum and returns the generated Curriculum_ID (UUID string).
     * Used by the Excel import flow to save PLOs and subjects right after creation.
     */
    public String addCurriculumAndReturnId(Curriculum c) {
        String newId = java.util.UUID.randomUUID().toString();
        String sql = """
                     INSERT INTO Curriculums (
                        Curriculum_ID, Major_ID, Created_By, Curriculum_Code,
                        Curriculum_Name, English_Name, Description, Total_Credits,
                        Version, Decision_No, Decision_Date, Is_Active, Created_Date
                     ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, GETDATE())
                     """;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newId);
            ps.setString(2, c.getMajorId());
            ps.setString(3, c.getCreatedBy());
            ps.setString(4, c.getCurriculumCode());
            ps.setString(5, c.getCurriculumName());
            ps.setString(6, c.getEnglishName());
            ps.setString(7, c.getDescription());
            ps.setInt(8, c.getTotalCredits());
            ps.setString(9, c.getVersion());
            ps.setString(10, c.getDecisionNo());
            if (c.getDecisionDate() != null) {
                ps.setDate(11, new java.sql.Date(c.getDecisionDate().getTime()));
            } else {
                ps.setNull(11, Types.DATE);
            }
            if (ps.executeUpdate() > 0) return newId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}