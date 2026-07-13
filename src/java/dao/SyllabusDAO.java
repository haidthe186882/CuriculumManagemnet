package dao;

import dal.DBContext;
import model.Syllabus;
import model.Subject;
import model.SyllabusMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CurriculumSubject;

public class SyllabusDAO {

    private Syllabus mapSyllabus(ResultSet rs) throws SQLException {
        Syllabus s = new Syllabus();
        s.setSyllabusId(rs.getString("Syllabus_ID"));
        s.setSubjectId(rs.getString("Subject_ID"));
        s.setSyllabusName(rs.getString("Syllabus_Name"));
        s.setEnglishName(rs.getString("English_Name"));
        s.setVersion(rs.getString("Version"));
        s.setDescription(rs.getString("Description"));
        s.setTimeAllocation(rs.getString("Time_Allocation"));
        s.setStudentTasks(rs.getString("Student_Tasks"));
        s.setTools(rs.getString("Tools"));
        s.setScoringScale(rs.getString("Scoring_Scale"));
        s.setMinAvgMarkToPass(rs.getDouble("Min_Avg_Mark_To_Pass"));
        s.setDecisionNo(rs.getString("Decision_No"));
        s.setApprovedDate(rs.getDate("Approved_Date"));
        try {
            s.setActive(rs.getBoolean("Is_Active"));
        } catch (SQLException ignored) {}
        try {
            // Cot Status moi (0=Draft,1=PendingReview,2=Approved)
            s.setStatusCode(rs.getInt("Status"));
        } catch (SQLException ignored) {
            // fallback cho DB chua patch cot Status: suy tu Is_Active
            s.setStatusCode(s.isActive() ? Syllabus.STATUS_APPROVED : Syllabus.STATUS_DRAFT);
        }
        try {
            s.setMaterialUrl(rs.getString("Material_URL"));
        } catch (SQLException ignored) {
        }
        // join Subject
        try {
            Subject sub = new Subject();
            sub.setSubjectId(rs.getString("Subject_ID"));
            sub.setSubjectCode(rs.getString("Subject_Code"));
            sub.setSubjectName(rs.getString("Subject_Name"));
            sub.setCredits(rs.getInt("Credits"));
            s.setSubject(sub);
        } catch (SQLException ignored) {
        }
        return s;
    }

    /** Tim kiem syllabus */
    public List<Syllabus> searchSyllabuses(String keyword, String status, boolean activeOnly) {
        return searchSyllabuses(keyword, status, activeOnly, false);
    }

    /**
     * @param approvedOnly khi true, chi tra ve Syllabus co Status = Approved (2).
     *                      Dung cho tat ca role TRU Admin: chi Admin duoc xem
     *                      Syllabus con dang Draft/PendingReview (chua hoan thanh).
     */
    public List<Syllabus> searchSyllabuses(String keyword, String status, boolean activeOnly, boolean approvedOnly) {
        List<Syllabus> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
          + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
          + "WHERE 1=1");
        if (activeOnly) sql.append(" AND sy.Is_Active=1");
        if (approvedOnly) sql.append(" AND sy.Status = 2");
        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (sy.Syllabus_Name LIKE ? OR s.Subject_Code LIKE ? OR s.Subject_Name LIKE ?)");
        // status filtering removed in new schema (no Status column)
        sql.append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            // status parameter ignored with new schema
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapSyllabus(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lay syllabus theo ID */
    public Syllabus getSyllabusById(String id) {
        String sql = "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "WHERE sy.Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapSyllabus(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lay syllabus theo subject */
    public Syllabus getSyllabusBySubject(String subjectId) {
        // TOP 1 + ORDER BY: neu 1 Subject lo bi 2 Syllabus active (du lieu cu truoc khi
        // fix trung lap), luon lay 1 ban ghi duy nhat va uu tien Syllabus co Status cao nhat
        // (Approved > PendingReview > Draft), tranh loi hien 2 dong trung ma khong dao dong ket qua.
        String sql = "SELECT TOP 1 sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                   + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                   + "WHERE sy.Subject_ID = ? AND sy.Is_Active=1 "
                   + "ORDER BY sy.Status DESC, sy.Syllabus_ID";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapSyllabus(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Them syllabus moi */
    public boolean addSyllabus(Syllabus s) {
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, "
                + "Description, Time_Allocation, Student_Tasks, Tools, Scoring_Scale, Min_Avg_Mark_To_Pass, "
                + "Decision_No, Approved_Date, Is_Active) "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectId());
            ps.setString(2, s.getSyllabusName());
            ps.setString(3, s.getEnglishName());
            ps.setString(4, s.getVersion());
            ps.setString(5, s.getDescription());
            ps.setString(6, s.getTimeAllocation());
            ps.setString(7, s.getStudentTasks());
            ps.setString(8, s.getTools());
            ps.setString(9, s.getScoringScale());
            ps.setDouble(10, s.getMinAvgMarkToPass());
            ps.setString(11, s.getDecisionNo());
            ps.setDate(12, s.getApprovedDate() != null ? new java.sql.Date(s.getApprovedDate().getTime()) : null);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cap nhat trang thai (legacy - Is_Active on/off) */
    public boolean updateStatus(String syllabusId, String status) {
        boolean activeVal = "Approved".equalsIgnoreCase(status) || "Active".equalsIgnoreCase(status)
                || "1".equals(status);
        String sql = "UPDATE Syllabuses SET Is_Active=? WHERE Syllabus_ID=?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activeVal);
            ps.setString(2, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Lay syllabus bat ky (ke ca inactive/draft) theo subjectId */
    public Syllabus findExistingSyllabusBySubjectAny(String subjectId) {
        String sql = "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "WHERE sy.Subject_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapSyllabus(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Cap nhat noi dung Syllabus da ton tai (khi Designer submit) */
    public boolean updateSyllabusContent(String syllabusId, Syllabus s) {
        String sql = "UPDATE Syllabuses SET Syllabus_Name=?, English_Name=?, Version=?, "
                + "Description=?, Time_Allocation=?, Student_Tasks=?, Tools=?, "
                + "Scoring_Scale=?, Min_Avg_Mark_To_Pass=?, Decision_No=?, Approved_Date=?, Is_Active=1 "
                + "WHERE Syllabus_ID=?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSyllabusName());
            ps.setString(2, s.getEnglishName());
            ps.setString(3, s.getVersion());
            ps.setString(4, s.getDescription());
            ps.setString(5, s.getTimeAllocation());
            ps.setString(6, s.getStudentTasks());
            ps.setString(7, s.getTools());
            ps.setString(8, s.getScoringScale());
            ps.setDouble(9, s.getMinAvgMarkToPass());
            ps.setString(10, s.getDecisionNo());
            ps.setDate(11, s.getApprovedDate() != null ? new java.sql.Date(s.getApprovedDate().getTime()) : null);
            ps.setString(12, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tao 1 Syllabus rong (Status=Draft) cho 1 Subject moi vua duoc import tu Excel
     * ma chua tung co trong he thong. Designer se dien noi dung sau khi duoc Admin
     * gan vao. Tra ve Syllabus_ID moi tao hoac null neu that bai.
     */
    public String createEmptySyllabus(String subjectId, String syllabusName) {
        String newId = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, Version, Status, Is_Active) "
                   + "VALUES (?, ?, ?, 'v1.0', 0, 1)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newId);
            ps.setString(2, subjectId);
            ps.setString(3, syllabusName);
            int rows = ps.executeUpdate();
            return rows > 0 ? newId : null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /** Lay Syllabus_ID dang active cua 1 Subject (khong join them cot khac, nhe hon getSyllabusBySubject) */
    public String getActiveSyllabusIdBySubject(String subjectId) {
        String sql = "SELECT TOP 1 Syllabus_ID FROM Syllabuses WHERE Subject_ID = ? AND Is_Active = 1 ORDER BY Status DESC, Syllabus_ID";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Cap nhat trang thai quy trinh thiet ke/duyet (Draft=0 / PendingReview=1 / Approved=2) */
    public boolean updateWorkflowStatus(String syllabusId, int statusCode) {
        String sql = "UPDATE Syllabuses SET Status=? WHERE Syllabus_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, statusCode);
            ps.setString(2, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Designer bam "Submit for Review": Draft(0) -> PendingReview(1) */
    public boolean submitForReview(String syllabusId) {
        return updateWorkflowStatus(syllabusId, Syllabus.STATUS_PENDING_REVIEW);
    }

    /** Lay danh sach tai lieu cua mot syllabus */
    public List<SyllabusMaterial> getMaterialsBySyllabusId(String syllabusId) {
        List<SyllabusMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM Materials WHERE Syllabus_ID = ? ORDER BY Is_Main_Material DESC";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SyllabusMaterial m = new SyllabusMaterial();
                m.setMaterialId(rs.getString("Material_ID"));
                m.setSyllabusId(rs.getString("Syllabus_ID"));
                m.setMaterialDescription(rs.getString("Material_Description"));
                m.setAuthor(rs.getString("Author"));
                m.setPublisher(rs.getString("Publisher"));
                m.setPublishedDate(rs.getDate("Published_Date"));
                m.setEdition(rs.getString("Edition"));
                m.setIsbn(rs.getString("ISBN"));
                m.setMainMaterial(rs.getBoolean("Is_Main_Material"));
                m.setHardCopy(rs.getBoolean("Is_Hard_Copy"));
                m.setOnline(rs.getBoolean("Is_Online"));
                m.setLink(rs.getString("Link"));
                m.setNotes(rs.getString("Notes"));
                try {
                    m.setFilePath(rs.getString("Download_Link"));
                } catch (SQLException ignored) {
                }
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Insert syllabus and return the generated Syllabus_ID */
    /** Xoa toan bo Material cua 1 Syllabus, dung khi Designer luu lai noi dung (tranh trung lap). */
    public boolean deleteMaterialsBySyllabus(String syllabusId) {
        String sql = "DELETE FROM Materials WHERE Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /**
     * Cap nhat noi dung 1 Syllabus DA TON TAI (vi du Syllabus rong duoc tao san
     * khi import Excel cho Subject moi). Dung thay cho addSyllabusAndGetId khi
     * Subject da co san 1 Syllabus active, de KHONG tao ra 1 Syllabus_ID moi
     * (tranh mo coi cac Syllabus_Assignments/Reviews da gan vao Syllabus cu).
     */
    public boolean updateSyllabusContent(String syllabusId, Syllabus s) {
        String sql = "UPDATE Syllabuses SET Syllabus_Name=?, English_Name=?, Version=?, Description=?, "
                   + "Time_Allocation=?, Student_Tasks=?, Tools=?, Scoring_Scale=?, Min_Avg_Mark_To_Pass=?, "
                   + "Decision_No=?, Approved_Date=? WHERE Syllabus_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSyllabusName());
            ps.setString(2, s.getEnglishName());
            ps.setString(3, s.getVersion());
            ps.setString(4, s.getDescription());
            ps.setString(5, s.getTimeAllocation());
            ps.setString(6, s.getStudentTasks());
            ps.setString(7, s.getTools());
            ps.setString(8, s.getScoringScale());
            ps.setDouble(9, s.getMinAvgMarkToPass());
            ps.setString(10, s.getDecisionNo());
            ps.setDate(11, s.getApprovedDate() != null ? new java.sql.Date(s.getApprovedDate().getTime()) : null);
            ps.setString(12, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public String addSyllabusAndGetId(Syllabus s) {
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, "
                + "Description, Time_Allocation, Student_Tasks, Tools, Scoring_Scale, Min_Avg_Mark_To_Pass, "
                + "Decision_No, Approved_Date, Is_Active) "
                + "OUTPUT INSERTED.Syllabus_ID "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectId());
            ps.setString(2, s.getSyllabusName());
            ps.setString(3, s.getEnglishName());
            ps.setString(4, s.getVersion());
            ps.setString(5, s.getDescription());
            ps.setString(6, s.getTimeAllocation());
            ps.setString(7, s.getStudentTasks());
            ps.setString(8, s.getTools());
            ps.setString(9, s.getScoringScale());
            ps.setDouble(10, s.getMinAvgMarkToPass());
            ps.setString(11, s.getDecisionNo());
            ps.setDate(12, s.getApprovedDate() != null ? new java.sql.Date(s.getApprovedDate().getTime()) : null);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Insert a material for a syllabus */
    public boolean addMaterial(SyllabusMaterial m) {
        String sql = "INSERT INTO Materials (Material_ID, Syllabus_ID, Material_Description, Author, Publisher, "
                + "Published_Date, Edition, ISBN, Is_Main_Material, Is_Hard_Copy, Is_Online, Link, Notes, Download_Link) "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getSyllabusId());
            ps.setString(2, m.getMaterialDescription());
            ps.setString(3, m.getAuthor());
            ps.setString(4, m.getPublisher());
            ps.setDate(5, m.getPublishedDate() != null ? new java.sql.Date(m.getPublishedDate().getTime()) : null);
            ps.setString(6, m.getEdition());
            ps.setString(7, m.getIsbn());
            ps.setBoolean(8, m.isMainMaterial());
            ps.setBoolean(9, m.isHardCopy());
            ps.setBoolean(10, m.isOnline());
            ps.setString(11, m.getLink());
            ps.setString(12, m.getNotes());
            ps.setString(13, m.getFilePath());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Bulk insert materials for a syllabus */
    public int addMaterials(String syllabusId, List<SyllabusMaterial> materials) {
        int count = 0;
        for (SyllabusMaterial m : materials) {
            m.setSyllabusId(syllabusId);
            if (addMaterial(m))
                count++;
        }
        return count;
    }
    
    // Hàm này đặt trong SyllabusDAO.java (hoặc CurriculumDAO tùy em thiết kế)
    public boolean assignSyllabusRoles(String curriculumId, String subjectId, String designerId, String reviewerId) {
    // Câu lệnh SQL kiểm tra nếu đã tồn tại bản ghi phân công cho môn này trong CTĐT này thì UPDATE, chưa có thì INSERT
    // Thầy viết theo cú pháp chuẩn để check-insert/update:
    String checkSql = "SELECT COUNT(*) FROM Syllabus_Assignments WHERE curriculum_id = ? AND subject_id = ?";
    String insertSql = "INSERT INTO Syllabus_Assignments (curriculum_id, subject_id, designer_id, reviewer_id) VALUES (?, ?, ?, ?)";
    String updateSql = "UPDATE Syllabus_Assignments SET designer_id = ?, reviewer_id = ? WHERE curriculum_id = ? AND subject_id = ?";
    
    try (Connection conn = new DBContext().getConnection()) {
        // 1. Kiểm tra tồn tại
        try (java.sql.PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setString(1, curriculumId);
            psCheck.setString(2, subjectId);
            try (java.sql.ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 2. Nếu đã có thì chạy lệnh UPDATE
                    try (java.sql.PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setString(1, designerId);
                        psUpdate.setString(2, reviewerId);
                        psUpdate.setString(3, curriculumId);
                        psUpdate.setString(4, subjectId);
                        return psUpdate.executeUpdate() > 0;
                    }
                } else {
                    // 3. Nếu chưa có thì chạy lệnh INSERT
                    try (java.sql.PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setString(1, curriculumId);
                        psInsert.setString(2, subjectId);
                        psInsert.setString(3, designerId);
                        psInsert.setString(4, reviewerId);
                        return psInsert.executeUpdate() > 0;
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    
    // Lấy danh sách Syllabus được giao cho Designer
    public List<CurriculumSubject> getAssignedSubjectsForDesigner(String designerId, String keyword) {
    List<CurriculumSubject> list = new ArrayList<>();
    System.out.println(">>> [DEBUG DESIGNER] ID đang tìm kiếm là: " + designerId);
    // Dùng LEFT JOIN: Đảm bảo dù Syllabus chưa map với Subject/Curriculum thì vẫn lấy ra được Assignment
    String sql = "SELECT sa.Assignment_ID, sa.User_ID, sa.Syllabus_ID, " +
                 "syl.Subject_ID, s.subject_code, s.subject_name, s.credits, " +
                 "cs.curriculum_id, cs.semester_no, c.curriculum_code, c.curriculum_name " +
                 "FROM Syllabus_Assignments sa " +
                 "LEFT JOIN Syllabuses syl ON sa.Syllabus_ID = syl.Syllabus_ID " + 
                 "LEFT JOIN Subjects s ON syl.Subject_ID = s.Subject_ID " +
                 "LEFT JOIN Curriculum_Subjects cs ON s.Subject_ID = cs.Subject_ID " +
                 "LEFT JOIN Curriculums c ON cs.Curriculum_ID = c.Curriculum_ID " +
                 "WHERE CAST(sa.User_ID AS NVARCHAR(50)) = ? AND sa.Assignment_Type LIKE '%Designer%'";
                 
    if (keyword != null && !keyword.trim().isEmpty()) {
        sql += " AND (s.subject_code LIKE ? OR s.subject_name LIKE ? OR c.curriculum_code LIKE ?)";
    }
    
    // In ra Console để debug xem ID truyền vào là gì
    System.out.println(">>> [DEBUG DESIGNER] ID đang đăng nhập: " + designerId);
    
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
         
        ps.setString(1, designerId);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
        }
        
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                CurriculumSubject cs = new CurriculumSubject();
                // Vì dùng LEFT JOIN nên có thể null, cần check null trước khi set nếu cần
                cs.setCurriculumId(rs.getString("curriculum_id"));
                cs.setSubjectId(rs.getString("Subject_ID"));
                cs.setSemesterNo(rs.getInt("semester_no"));
                
                model.Subject s = new model.Subject();
                s.setSubjectId(rs.getString("Subject_ID"));
                s.setSubjectCode(rs.getString("subject_code") != null ? rs.getString("subject_code") : "N/A");
                s.setSubjectName(rs.getString("subject_name") != null ? rs.getString("subject_name") : "Syllabus_ID: " + rs.getString("Syllabus_ID"));
                s.setCredits(rs.getInt("credits"));
                cs.setSubject(s);
                
                model.Curriculum cur = new model.Curriculum();
                cur.setCurriculumCode(rs.getString("curriculum_code") != null ? rs.getString("curriculum_code") : "Unknown");
                cur.setCurriculumName(rs.getString("curriculum_name"));
                cs.setCurriculum(cur); 
                
                list.add(cs);
            }
            System.out.println(">>> [DEBUG DESIGNER] Số lượng bản ghi tìm thấy: " + list.size());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
    public List<CurriculumSubject> getAssignedSubjectsForReviewer(String reviewerId, String keyword) {
    List<CurriculumSubject> list = new ArrayList<>();
    
    // Câu SQL lấy các Syllabus được phân công là Reviewer
    String sql = "SELECT sa.Assignment_ID, sa.User_ID, sa.Syllabus_ID, " +
                 "syl.Subject_ID, s.subject_code, s.subject_name, s.credits, " +
                 "cs.curriculum_id, cs.semester_no, c.curriculum_code, c.curriculum_name " +
                 "FROM Syllabus_Assignments sa " +
                 "JOIN Syllabuses syl ON sa.Syllabus_ID = syl.Syllabus_ID " + 
                 "JOIN Subjects s ON syl.Subject_ID = s.Subject_ID " +
                 "LEFT JOIN Curriculum_Subjects cs ON s.Subject_ID = cs.Subject_ID " +
                 "LEFT JOIN Curriculums c ON cs.Curriculum_ID = c.Curriculum_ID " +
                 "WHERE sa.User_ID = ? AND sa.Assignment_Type = 'Reviewer'"; // DÒNG QUAN TRỌNG

    if (keyword != null && !keyword.trim().isEmpty()) {
        sql += " AND (s.subject_code LIKE ? OR s.subject_name LIKE ? OR c.curriculum_code LIKE ?)";
    }
    
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
         
        ps.setString(1, reviewerId);
        // ... (phần set Parameter cho keyword tương tự như hàm Designer) ...
        
        // ... (vòng lặp rs.next() và đổ dữ liệu vào CurriculumSubject tương tự như hàm Designer) ...
    } catch (Exception e) { e.printStackTrace(); }
    return list;
}
}