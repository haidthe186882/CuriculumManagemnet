package dao;

import dal.DBContext;
import model.Subject;
import model.CurriculumSubject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    private Subject mapSubject(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setSubjectId(rs.getString("Subject_ID"));
        s.setSubjectCode(rs.getString("Subject_Code"));
        s.setSubjectName(rs.getString("Subject_Name"));
        s.setEnglishName(rs.getString("English_Name"));
        s.setCredits(rs.getInt("Credits"));
        s.setDescription(rs.getString("Description"));
        try {
            boolean active = rs.getBoolean("Is_Active");
            s.setStatus(active ? "Active" : "Inactive");
        } catch (SQLException ex) {
            try { s.setStatus(rs.getString("Status")); } catch (SQLException ignored) {}
        }
        try { s.setDepartment(rs.getString("Department")); } catch (SQLException ignored) {}
        return s;
    }

    private String resolveMajorId(Connection con, String departmentNameOrCode) throws SQLException {
        if (departmentNameOrCode == null || departmentNameOrCode.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str FROM Majors WHERE Major_Code = ? OR Major_Name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, departmentNameOrCode.trim());
            ps.setString(2, departmentNameOrCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Major_ID_Str");
                }
            }
        }
        String fallbackSql = "SELECT TOP 1 CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str FROM Majors WHERE Is_Active = 1";
        try (PreparedStatement ps = con.prepareStatement(fallbackSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("Major_ID_Str");
            }
        }
        return null;
    }

    /** Tim kiem subject */
    public List<Subject> searchSubjects(String keyword, String department, Integer credits) {
        List<Subject> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, m.Major_Name AS Department " +
            "FROM Subjects s " +
            "LEFT JOIN Majors m ON s.Major_ID = m.Major_ID " +
            "WHERE s.Is_Active=1"
        );
        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (s.Subject_Name LIKE ? OR s.Subject_Code LIKE ?)");
        if (department != null && !department.trim().isEmpty())
            sql.append(" AND m.Major_Name = ?");
        if (credits != null)
            sql.append(" AND s.Credits = ?");
        sql.append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (department != null && !department.trim().isEmpty())
                ps.setString(idx++, department);
            if (credits != null)
                ps.setInt(idx, credits);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSubject(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Lay subject theo ID */
    public Subject getSubjectById(String id) {
        String sql = "SELECT s.*, m.Major_Name AS Department FROM Subjects s LEFT JOIN Majors m ON s.Major_ID = m.Major_ID WHERE s.Subject_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSubject(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Lay danh sach subject trong curriculum (kem semester, mandatory) */
    public List<CurriculumSubject> getSubjectsByCurriculum(String curriculumId) {
        List<CurriculumSubject> list = new ArrayList<>();
        String sql = "SELECT cs.*, s.Subject_Code, s.Subject_Name, s.English_Name, s.Credits, "
                   + "s.Description, s.Is_Active, m.Major_Name AS Department, sy.Syllabus_ID, sy.Status AS Syllabus_Status, "
                   + "(SELECT STUFF((SELECT ', ' + rs.Subject_Code FROM Subject_Prerequisites sp "
                   + " JOIN Subjects rs ON sp.Required_Subject_ID = rs.Subject_ID "
                   + " WHERE sp.Subject_ID = s.Subject_ID FOR XML PATH('')), 1, 2, '')) AS Prerequisite_Codes "
                   + "FROM Curriculum_Subjects cs "
                   + "JOIN Subjects s ON cs.Subject_ID = s.Subject_ID "
                   + "LEFT JOIN Majors m ON s.Major_ID = m.Major_ID "
                   // Chi lay DUY NHAT 1 Syllabus dai dien cho moi Subject (uu tien Status cao nhat)
                   // de tranh nhan doi dong khi 1 Subject lo co > 1 Syllabus dang Is_Active=1
                   // (du lieu cu truoc khi fix trung lap syllabus).
                   + "OUTER APPLY (SELECT TOP 1 sy2.Syllabus_ID, sy2.Status FROM Syllabuses sy2 "
                   + "              WHERE sy2.Subject_ID = s.Subject_ID AND sy2.Is_Active = 1 "
                   + "              ORDER BY sy2.Status DESC, sy2.Syllabus_ID) sy "
                   + "WHERE cs.Curriculum_ID = ? ORDER BY cs.Semester_No, s.Subject_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CurriculumSubject cs = new CurriculumSubject();
                cs.setCurriculumSubjectId(rs.getString("Curriculum_Subject_ID"));
                cs.setCurriculumId(rs.getString("Curriculum_ID"));
                cs.setSubjectId(rs.getString("Subject_ID"));
                cs.setSemesterNo(rs.getInt("Semester_No"));
                cs.setMandatory(rs.getBoolean("Is_Mandatory"));
                Subject s = mapSubject(rs);
                try { s.setSyllabusId(rs.getString("Syllabus_ID")); } catch (SQLException ignored) {}
                try { s.setSyllabusStatusCode(rs.getInt("Syllabus_Status")); } catch (SQLException ignored) {}
                try { s.setPrerequisiteCodes(rs.getString("Prerequisite_Codes")); } catch (SQLException ignored) {}
                cs.setSubject(s);
                list.add(cs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Them subject moi */
    public boolean addSubject(Subject s) {
        String sql = "INSERT INTO Subjects (Subject_ID, Subject_Code, Subject_Name, English_Name, Credits, Description, Major_ID, Is_Active) "
               + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getEnglishName());
            ps.setInt(4, s.getCredits());
            ps.setString(5, s.getDescription());
            String majorId = resolveMajorId(con, s.getDepartment());
            if (majorId != null) {
                ps.setString(6, majorId);
            } else {
                ps.setNull(6, Types.CHAR);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Cap nhat subject */
    public boolean updateSubject(Subject s) {
        String sql = "UPDATE Subjects SET Subject_Name=?, English_Name=?, Credits=?, Description=?, Major_ID=? WHERE Subject_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectName());
            ps.setString(2, s.getEnglishName());
            ps.setInt(3, s.getCredits());
            ps.setString(4, s.getDescription());
            String majorId = resolveMajorId(con, s.getDepartment());
            if (majorId != null) {
                ps.setString(5, majorId);
            } else {
                ps.setNull(5, Types.CHAR);
            }
            ps.setString(6, s.getSubjectId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Them subject vao curriculum */
    public boolean addSubjectToCurriculum(String curriculumId, String subjectId, int semesterNo, boolean isMandatory) {
        String sql = "INSERT INTO Curriculum_Subjects (Curriculum_Subject_ID, Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory) "
                   + "VALUES (NEWID(), ?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.setString(2, subjectId);
            ps.setInt(3, semesterNo);
            ps.setBoolean(4, isMandatory);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Xoa subject khoi curriculum */
    public boolean removeSubjectFromCurriculum(String curriculumSubjectId) {
        String sql = "DELETE FROM Curriculum_Subjects WHERE Curriculum_Subject_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumSubjectId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Lay danh sach departments */
    public List<String> getAllDepartments() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT Major_Name FROM Majors WHERE Is_Active = 1 ORDER BY Major_Name";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("Major_Name"));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Removes all subjects from a curriculum (used during Excel re-import).
     */
    public void removeAllSubjectsFromCurriculum(String curriculumId) {
        String sql = "DELETE FROM Curriculum_Subjects WHERE Curriculum_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a Subject_ID by its code (case-insensitive, active subjects only).
     * Returns null if not found.
     */
    public String findSubjectIdByCode(String subjectCode) {
        String sql = "SELECT Subject_ID FROM Subjects WHERE UPPER(Subject_Code) = UPPER(?) AND Is_Active = 1";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectCode.trim());
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Subject_ID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a Subject_ID by its code regardless of Is_Active status.
     * Used during Excel import so subjects that exist but are inactive
     * can still be linked to a curriculum.
     */
    /**
     * Tao 1 Subject moi (chua tung ton tai trong he thong) khi import Excel
     * gap 1 Subject_Code chua co. Subject nay se can duoc Admin gan Designer +
     * Reviewer va thiet ke Syllabus truoc khi Curriculum chua no co the Publish.
     * Tra ve Subject_ID moi tao, hoac null neu that bai.
     */
    public String createDraftSubject(String subjectCode, String subjectName, String englishName,
                                      int credits, String majorId) {
        String newId = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO Subjects (Subject_ID, Major_ID, Subject_Code, Subject_Name, English_Name, Credits, Description, Is_Active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, NULL, 1)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newId);
            if (majorId != null) ps.setString(2, majorId); else ps.setNull(2, Types.CHAR);
            ps.setString(3, subjectCode.trim());
            ps.setString(4, subjectName != null ? subjectName.trim() : subjectCode.trim());
            ps.setString(5, englishName);
            ps.setInt(6, Math.max(credits, 0));
            int rows = ps.executeUpdate();
            return rows > 0 ? newId : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ghi nhan 1 quan he tien quyet (Subject_Prerequisites) tu ma mon hoc,
     * bo qua neu 1 trong 2 ma khong ton tai hoac da co san. Dung trong
     * luong import Excel (cot PreRequisite). Khong nem loi ra ngoai (best-effort).
     */
    public void addPrerequisiteByCode(String subjectCode, String requiredSubjectCode) {
        if (subjectCode == null || requiredSubjectCode == null) return;
        String code = subjectCode.trim();
        String reqCode = requiredSubjectCode.trim();
        if (code.isEmpty() || reqCode.isEmpty() || "none".equalsIgnoreCase(reqCode)) return;

        String subjectId = findSubjectIdByCodeAny(code);
        String requiredId = findSubjectIdByCodeAny(reqCode);
        if (subjectId == null || requiredId == null) return;

        String checkSql = "SELECT 1 FROM Subject_Prerequisites WHERE Subject_ID = ? AND Required_Subject_ID = ?";
        String insertSql = "INSERT INTO Subject_Prerequisites (Subject_Prerequisite_ID, Subject_ID, Required_Subject_ID) VALUES (?, ?, ?)";
        try (Connection con = new DBContext().getConnection()) {
            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setString(1, subjectId);
                check.setString(2, requiredId);
                if (check.executeQuery().next()) return; // already exists
            }
            try (PreparedStatement ins = con.prepareStatement(insertSql)) {
                ins.setString(1, java.util.UUID.randomUUID().toString());
                ins.setString(2, subjectId);
                ins.setString(3, requiredId);
                ins.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Danh sach cac Subject trong 1 Curriculum ma VAN CHUA "hoan thanh"
     * (chua co Syllabus, hoac Syllabus chua duoc Reviewer Approve).
     * Dung de: (1) chan nut Publish khi danh sach nay khong rong,
     * (2) hien thi cho Admin biet con thieu subject nao.
     */
    public List<Subject> getIncompleteSubjects(String curriculumId) {
        List<Subject> list = new ArrayList<>();
        // "Hoan thanh" cho 1 Curriculum cu the nghia la: co Syllabus DA Approved
        // (Status = 2) VA (neu Curriculum nay co PLO va Syllabus co CLO) thi it
        // nhat 1 cap CLO-PLO da duoc mapping RIENG cho Curriculum nay. Ly do:
        // Subject co the dung chung 1 Syllabus da Approved tu 1 Curriculum khac
        // (tai su dung theo Subject_Code trung nhau khi import Excel) nhung moi
        // Curriculum co bo PLO khac nhau, nen KHONG the coi la xong neu chua
        // mapping lai CLO-PLO cho Curriculum nay.
        String needsMappingExpr =
                "(sy.Status = 2 " +
                "AND EXISTS (SELECT 1 FROM PLOs p2 WHERE p2.Curriculum_ID = cs.Curriculum_ID) " +
                "AND EXISTS (SELECT 1 FROM CLOs c2 WHERE c2.Syllabus_ID = sy.Syllabus_ID) " +
                "AND NOT EXISTS ( " +
                "      SELECT 1 FROM PLO_CLO_Mappings m " +
                "      JOIN CLOs c ON m.CLO_ID = c.CLO_ID " +
                "      JOIN PLOs p ON m.PLO_ID = p.PLO_ID " +
                "      WHERE c.Syllabus_ID = sy.Syllabus_ID AND p.Curriculum_ID = cs.Curriculum_ID))";
        String sql = "SELECT s.*, sy.Syllabus_ID, sy.Status AS Syllabus_Status, " +
                     "  CASE WHEN " + needsMappingExpr + " THEN 1 ELSE 0 END AS Needs_Plo_Mapping " +
                     "FROM Curriculum_Subjects cs " +
                     "JOIN Subjects s ON cs.Subject_ID = s.Subject_ID " +
                     "OUTER APPLY (SELECT TOP 1 sy2.Syllabus_ID, sy2.Status FROM Syllabuses sy2 " +
                     "              WHERE sy2.Subject_ID = s.Subject_ID AND sy2.Is_Active = 1 " +
                     "              ORDER BY sy2.Status DESC, sy2.Syllabus_ID) sy " +
                     "WHERE cs.Curriculum_ID = ? AND (sy.Syllabus_ID IS NULL OR sy.Status <> 2 OR " + needsMappingExpr + ") " +
                     "ORDER BY s.Subject_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curriculumId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject s = mapSubject(rs);
                try { s.setSyllabusId(rs.getString("Syllabus_ID")); } catch (SQLException ignored) {}
                try { s.setSyllabusStatusCode(rs.getInt("Syllabus_Status")); } catch (SQLException ignored) {}
                try { s.setNeedsPloMapping(rs.getInt("Needs_Plo_Mapping") == 1); } catch (SQLException ignored) {}
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String findSubjectIdByCodeAny(String subjectCode) {
        String sql = "SELECT Subject_ID FROM Subjects WHERE UPPER(Subject_Code) = UPPER(?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectCode.trim());
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Subject_ID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addPrerequisite(String subjectId, String requiredSubjectId) {
        if (subjectId == null || requiredSubjectId == null || subjectId.equals(requiredSubjectId)) return false;
        String sql = "INSERT INTO Subject_Prerequisites (Subject_Prerequisite_ID, Subject_ID, Required_Subject_ID) VALUES (NEWID(), ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ps.setString(2, requiredSubjectId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removePrerequisite(String subjectId, String requiredSubjectId) {
        if (subjectId == null || requiredSubjectId == null) return false;
        String sql = "DELETE FROM Subject_Prerequisites WHERE Subject_ID = ? AND Required_Subject_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ps.setString(2, requiredSubjectId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Subject> getPrerequisitesForSubject(String subjectId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT req.Subject_ID, req.Subject_Code, req.Subject_Name, req.Credits "
                   + "FROM Subject_Prerequisites sp "
                   + "JOIN Subjects req ON sp.Required_Subject_ID = req.Subject_ID "
                   + "WHERE sp.Subject_ID = ? AND req.Is_Active = 1 "
                   + "ORDER BY req.Subject_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject req = new Subject();
                    req.setSubjectId(rs.getString("Subject_ID"));
                    req.setSubjectCode(rs.getString("Subject_Code"));
                    req.setSubjectName(rs.getString("Subject_Name"));
                    req.setCredits(rs.getInt("Credits"));
                    list.add(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}