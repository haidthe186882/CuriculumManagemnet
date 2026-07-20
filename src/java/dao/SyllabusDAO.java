package dao;

import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CurriculumSubject;
import model.Subject;
import model.Syllabus;
import model.SyllabusMaterial;

public class SyllabusDAO {

    public static final String STATUS_DRAFT = "Draft";
    public static final String STATUS_PENDING_REVIEW = "PendingReview";
    public static final String STATUS_CHANGES_REQUESTED = "ChangesRequested";
    public static final String STATUS_APPROVED_FOR_PUBLISH = "ApprovedForPublish";
    public static final String STATUS_PUBLISHED = "Published";

    private Syllabus mapSyllabus(ResultSet rs) throws SQLException {
        Syllabus syllabus = new Syllabus();
        syllabus.setSyllabusId(rs.getString("Syllabus_ID"));
        syllabus.setSubjectId(rs.getString("Subject_ID"));
        syllabus.setSyllabusName(rs.getString("Syllabus_Name"));
        syllabus.setEnglishName(rs.getString("English_Name"));
        syllabus.setVersion(rs.getString("Version"));
        syllabus.setDescription(rs.getString("Description"));
        syllabus.setTimeAllocation(rs.getString("Time_Allocation"));
        syllabus.setStudentTasks(rs.getString("Student_Tasks"));
        syllabus.setTools(rs.getString("Tools"));
        syllabus.setScoringScale(rs.getString("Scoring_Scale"));
        syllabus.setMinAvgMarkToPass(rs.getDouble("Min_Avg_Mark_To_Pass"));
        syllabus.setDecisionNo(rs.getString("Decision_No"));
        syllabus.setApprovedDate(rs.getDate("Approved_Date"));

        boolean active = false;
        try {
            active = rs.getBoolean("Is_Active");
            syllabus.setActive(active);
        } catch (SQLException ignored) {
        }

        String workflowStatus = null;
        try {
            workflowStatus = rs.getString("Workflow_Status");
        } catch (SQLException ignored) {
        }
        try {
            syllabus.setStatusCode(rs.getInt("Status"));
        } catch (SQLException ignored) {
        }
        if (workflowStatus != null && !workflowStatus.trim().isEmpty()) {
            syllabus.setStatus(workflowStatus);
        } else if (syllabus.getStatus() == null || syllabus.getStatus().trim().isEmpty()) {
            syllabus.setStatus(active ? STATUS_PUBLISHED : STATUS_DRAFT);
        }

        try {
            syllabus.setMaterialUrl(rs.getString("Material_URL"));
        } catch (SQLException ignored) {
        }

        try {
            Subject subject = new Subject();
            subject.setSubjectId(rs.getString("Subject_ID"));
            subject.setSubjectCode(rs.getString("Subject_Code"));
            subject.setSubjectName(rs.getString("Subject_Name"));
            subject.setCredits(rs.getInt("Credits"));
            syllabus.setSubject(subject);
        } catch (SQLException ignored) {
        }
        return syllabus;
    }

    public List<Syllabus> searchSyllabuses(String keyword, String status, boolean activeOnly) {
        return searchSyllabuses(keyword, status, activeOnly, false);
    }

    public List<Syllabus> searchSyllabuses(String keyword, String status, boolean activeOnly, boolean approvedOnly) {
        List<Syllabus> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID WHERE 1=1");
        if (activeOnly) {
            sql.append(" AND sy.Is_Active = 1");
        }
        if (approvedOnly) {
            sql.append(" AND sy.Status = 2");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (sy.Syllabus_Name LIKE ? OR s.Subject_Code LIKE ? OR s.Subject_Name LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND (sy.Workflow_Status = ? OR CAST(sy.Status AS NVARCHAR(20)) = ?)");
        }
        sql.append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status.trim());
                ps.setString(index++, status.trim());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapSyllabus(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Syllabus getSyllabusById(String id) {
        String sql = "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID WHERE sy.Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapSyllabus(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Syllabus getSyllabusBySubject(String subjectId) {
        String sql = "SELECT TOP 1 sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "WHERE sy.Subject_ID = ? AND sy.Is_Active = 1 ORDER BY sy.Status DESC, sy.Syllabus_ID";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapSyllabus(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addSyllabus(Syllabus syllabus) {
        String workflowStatus = normalizeWorkflowStatus(syllabus.getStatus(), STATUS_DRAFT);
        int legacyStatus = mapWorkflowStatusToLegacyCode(workflowStatus);
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, "
                + "Description, Time_Allocation, Student_Tasks, Tools, Scoring_Scale, Min_Avg_Mark_To_Pass, "
                + "Decision_No, Approved_Date, Status, Workflow_Status, Is_Active) "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabus.getSubjectId());
            ps.setString(2, syllabus.getSyllabusName());
            ps.setString(3, syllabus.getEnglishName());
            ps.setString(4, syllabus.getVersion());
            ps.setString(5, syllabus.getDescription());
            ps.setString(6, syllabus.getTimeAllocation());
            ps.setString(7, syllabus.getStudentTasks());
            ps.setString(8, syllabus.getTools());
            ps.setString(9, syllabus.getScoringScale());
            ps.setDouble(10, syllabus.getMinAvgMarkToPass());
            ps.setString(11, syllabus.getDecisionNo());
            ps.setDate(12, syllabus.getApprovedDate() != null ? new java.sql.Date(syllabus.getApprovedDate().getTime()) : null);
            ps.setInt(13, legacyStatus);
            ps.setString(14, workflowStatus);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(String syllabusId, String status) {
        String workflowStatus = normalizeWorkflowStatus(status, STATUS_DRAFT);
        int legacyStatus = mapWorkflowStatusToLegacyCode(workflowStatus);
        // Is_Active nghia la "day la ban ghi Syllabus hien hanh cua Subject" (dung boi
        // getActiveSyllabusIdBySubject/getSyllabusBySubject...), KHONG PHAI "da Published".
        // Neu gan theo Published thi ngay sau khi Submit for Review, Syllabus se "bien mat"
        // khoi moi truy van tim syllabus hien hanh cua subject -> gay ra hang loat loi day
        // chuyen (assign lai bi tao trung, khong tim thay syllabus...).
        boolean active = true;
        String sql = "UPDATE Syllabuses SET Status = ?, Workflow_Status = ?, Is_Active = ? WHERE Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legacyStatus);
            ps.setString(2, workflowStatus);
            ps.setBoolean(3, active);
            ps.setString(4, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String createEmptySyllabus(String subjectId, String syllabusName) {
        String newId = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, Version, Status, Workflow_Status, Is_Active) "
                + "VALUES (?, ?, ?, 'v1.0', ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newId);
            ps.setString(2, subjectId);
            ps.setString(3, syllabusName);
            ps.setInt(4, Syllabus.STATUS_DRAFT);
            ps.setString(5, STATUS_DRAFT);
            int rows = ps.executeUpdate();
            return rows > 0 ? newId : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getActiveSyllabusIdBySubject(String subjectId) {
        String sql = "SELECT TOP 1 Syllabus_ID FROM Syllabuses WHERE Subject_ID = ? AND Is_Active = 1 ORDER BY Status DESC, Syllabus_ID";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateWorkflowStatus(String syllabusId, int statusCode) {
        String workflowStatus = mapLegacyCodeToWorkflowStatus(statusCode);
        // Xem giai thich trong updateStatus(): Is_Active phai luon la true o day,
        // khong duoc gan theo Published, neu khong cac truy van tim "syllabus hien
        // hanh cua subject" se khong thay Syllabus ngay sau khi doi trang thai.
        boolean active = true;
        String sql = "UPDATE Syllabuses SET Status = ?, Workflow_Status = ?, Is_Active = ? WHERE Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, statusCode);
            ps.setString(2, workflowStatus);
            ps.setBoolean(3, active);
            ps.setString(4, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSyllabusContent(String syllabusId, Syllabus syllabus) {
        // QUAN TRONG: luu noi dung (tao moi hoac Designer sua lai sau khi bi Reject)
        // KHONG duoc tu dong chuyen sang PendingReview - phai giu o Draft cho den khi
        // chinh Designer bam "Submit for Review" (design/list -> submitForReview()).
        // Neu khong Reviewer se thay Syllabus ngay ca khi Designer chua lam gi ca.
        String workflowStatus = STATUS_DRAFT;
        // Is_Active=1 (KHONG phai 0): day la ban ghi Syllabus dang dung cho Subject nay,
        // can duoc getActiveSyllabusIdBySubject()/getSyllabusBySubject() tim thay - neu de
        // 0 se bi coi la "khong co syllabus nao" va tao nham 1 Syllabus TRUNG LAP o lan assign/save sau.
        String sql = "UPDATE Syllabuses SET Syllabus_Name=?, English_Name=?, Version=?, Description=?, "
                + "Time_Allocation=?, Student_Tasks=?, Tools=?, Scoring_Scale=?, Min_Avg_Mark_To_Pass=?, "
                + "Decision_No=?, Approved_Date=?, Status=?, Workflow_Status=?, Is_Active=1 WHERE Syllabus_ID=?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabus.getSyllabusName());
            ps.setString(2, syllabus.getEnglishName());
            ps.setString(3, syllabus.getVersion());
            ps.setString(4, syllabus.getDescription());
            ps.setString(5, syllabus.getTimeAllocation());
            ps.setString(6, syllabus.getStudentTasks());
            ps.setString(7, syllabus.getTools());
            ps.setString(8, syllabus.getScoringScale());
            ps.setDouble(9, syllabus.getMinAvgMarkToPass());
            ps.setString(10, syllabus.getDecisionNo());
            ps.setDate(11, syllabus.getApprovedDate() != null ? new java.sql.Date(syllabus.getApprovedDate().getTime()) : null);
            ps.setInt(12, Syllabus.STATUS_DRAFT);
            ps.setString(13, workflowStatus);
            ps.setString(14, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean submitForReview(String syllabusId) {
        return updateWorkflowStatus(syllabusId, Syllabus.STATUS_PENDING_REVIEW);
    }

    public List<SyllabusMaterial> getMaterialsBySyllabusId(String syllabusId) {
        List<SyllabusMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM Materials WHERE Syllabus_ID = ? ORDER BY Is_Main_Material DESC";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SyllabusMaterial material = new SyllabusMaterial();
                material.setMaterialId(rs.getString("Material_ID"));
                material.setSyllabusId(rs.getString("Syllabus_ID"));
                material.setMaterialDescription(rs.getString("Material_Description"));
                material.setAuthor(rs.getString("Author"));
                material.setPublisher(rs.getString("Publisher"));
                material.setPublishedDate(rs.getDate("Published_Date"));
                material.setEdition(rs.getString("Edition"));
                material.setIsbn(rs.getString("ISBN"));
                material.setMainMaterial(rs.getBoolean("Is_Main_Material"));
                material.setHardCopy(rs.getBoolean("Is_Hard_Copy"));
                material.setOnline(rs.getBoolean("Is_Online"));
                material.setLink(rs.getString("Link"));
                material.setNotes(rs.getString("Notes"));
                try {
                    material.setFilePath(rs.getString("Download_Link"));
                } catch (SQLException ignored) {
                }
                list.add(material);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteMaterialsBySyllabus(String syllabusId) {
        String sql = "DELETE FROM Materials WHERE Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String addSyllabusAndGetId(Syllabus syllabus) {
        // Moi tao xong PHAI o trang thai Draft - khong duoc gui thang cho Reviewer.
        // Chi khi Designer bam "Submit for Review" (xem submitForReview()) thi moi
        // chuyen sang PendingReview de Reviewer thay duoc.
        String workflowStatus = normalizeWorkflowStatus(syllabus.getStatus(), STATUS_DRAFT);
        int legacyStatus = mapWorkflowStatusToLegacyCode(workflowStatus);
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, "
                + "Description, Time_Allocation, Student_Tasks, Tools, Scoring_Scale, Min_Avg_Mark_To_Pass, "
                + "Decision_No, Approved_Date, Status, Workflow_Status, Is_Active) "
                + "OUTPUT INSERTED.Syllabus_ID VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabus.getSubjectId());
            ps.setString(2, syllabus.getSyllabusName());
            ps.setString(3, syllabus.getEnglishName());
            ps.setString(4, syllabus.getVersion());
            ps.setString(5, syllabus.getDescription());
            ps.setString(6, syllabus.getTimeAllocation());
            ps.setString(7, syllabus.getStudentTasks());
            ps.setString(8, syllabus.getTools());
            ps.setString(9, syllabus.getScoringScale());
            ps.setDouble(10, syllabus.getMinAvgMarkToPass());
            ps.setString(11, syllabus.getDecisionNo());
            ps.setDate(12, syllabus.getApprovedDate() != null ? new java.sql.Date(syllabus.getApprovedDate().getTime()) : null);
            ps.setInt(13, legacyStatus);
            ps.setString(14, workflowStatus);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addMaterial(SyllabusMaterial material) {
        String sql = "INSERT INTO Materials (Material_ID, Syllabus_ID, Material_Description, Author, Publisher, "
                + "Published_Date, Edition, ISBN, Is_Main_Material, Is_Hard_Copy, Is_Online, Link, Notes, Download_Link) "
                + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, material.getSyllabusId());
            ps.setString(2, material.getMaterialDescription());
            ps.setString(3, material.getAuthor());
            ps.setString(4, material.getPublisher());
            ps.setDate(5, material.getPublishedDate() != null ? new java.sql.Date(material.getPublishedDate().getTime()) : null);
            ps.setString(6, material.getEdition());
            ps.setString(7, material.getIsbn());
            ps.setBoolean(8, material.isMainMaterial());
            ps.setBoolean(9, material.isHardCopy());
            ps.setBoolean(10, material.isOnline());
            ps.setString(11, material.getLink());
            ps.setString(12, material.getNotes());
            ps.setString(13, material.getFilePath());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addMaterials(String syllabusId, List<SyllabusMaterial> materials) {
        int count = 0;
        for (SyllabusMaterial material : materials) {
            material.setSyllabusId(syllabusId);
            if (addMaterial(material)) {
                count++;
            }
        }
        return count;
    }

    public List<Syllabus> getSyllabusesWithSubjectsLearnAfter(String keyword) {
        List<Syllabus> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sy.Syllabus_ID, s.Subject_Code, sy.Syllabus_Name, s.Subject_ID, "
                + "after_sub.Subject_ID AS After_Sub_ID, after_sub.Subject_Code AS After_Sub_Code, after_sub.Subject_Name AS After_Sub_Name "
                + "FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "LEFT JOIN Subject_Prerequisites sp ON s.Subject_ID = sp.Required_Subject_ID "
                + "LEFT JOIN Subjects after_sub ON sp.Subject_ID = after_sub.Subject_ID AND after_sub.Is_Active = 1 "
                + "WHERE sy.Is_Active = 1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND s.Subject_Code LIKE ? ");
        }
        sql.append("ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                Syllabus current = null;
                while (rs.next()) {
                    String syllabusId = rs.getString("Syllabus_ID");
                    if (current == null || !current.getSyllabusId().equals(syllabusId)) {
                        current = new Syllabus();
                        current.setSyllabusId(syllabusId);
                        current.setSyllabusName(rs.getString("Syllabus_Name"));

                        Subject subject = new Subject();
                        subject.setSubjectId(rs.getString("Subject_ID"));
                        subject.setSubjectCode(rs.getString("Subject_Code"));
                        current.setSubject(subject);

                        list.add(current);
                    }
                    String afterSubjectId = rs.getString("After_Sub_ID");
                    if (afterSubjectId != null) {
                        Subject required = new Subject();
                        required.setSubjectId(afterSubjectId);
                        required.setSubjectCode(rs.getString("After_Sub_Code"));
                        required.setSubjectName(rs.getString("After_Sub_Name"));
                        current.getSubjectsLearnAfter().add(required);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Syllabus findExistingSyllabusBySubjectAny(String subjectId) {
        String sql = "SELECT TOP 1 sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                + "WHERE sy.Subject_ID = ? ORDER BY sy.Status DESC, sy.Syllabus_ID";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapSyllabus(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean approveForPublish(String syllabusId) {
        return updateWorkflowStatus(syllabusId, STATUS_APPROVED_FOR_PUBLISH, true);
    }

    public boolean requestChanges(String syllabusId) {
        return updateWorkflowStatus(syllabusId, STATUS_CHANGES_REQUESTED, true);
    }

    public boolean publishSyllabus(String syllabusId) {
        return updateWorkflowStatus(syllabusId, STATUS_PUBLISHED, true);
    }

    public boolean isUserAssignedAsReviewer(String syllabusId, String userId) {
        return hasAssignment(syllabusId, userId, "Reviewer");
    }

    public boolean isUserAssignedAsDesigner(String syllabusId, String userId) {
        return hasAssignment(syllabusId, userId, "Designer");
    }

    public List<Syllabus> getSyllabusesByWorkflowStatus(String workflowStatus, String keyword) {
        List<Syllabus> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits FROM Syllabuses sy "
                + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID WHERE sy.Workflow_Status = ?");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (sy.Syllabus_Name LIKE ? OR s.Subject_Code LIKE ? OR s.Subject_Name LIKE ?)");
        }
        sql.append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, workflowStatus);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapSyllabus(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean assignSyllabusRoles(String curriculumId, String subjectId, String designerId, String reviewerId) {
        String syllabusId = ensureSyllabusExistsForSubject(subjectId);
        if (syllabusId == null) {
            return false;
        }
        boolean designerOk = upsertAssignment(syllabusId, designerId, "Designer");
        boolean reviewerOk = upsertAssignment(syllabusId, reviewerId, "Reviewer");
        return designerOk && reviewerOk;
    }

    public List<CurriculumSubject> getAssignedSubjectsForDesigner(String designerId, String keyword) {
        List<CurriculumSubject> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sa.Assignment_ID, sa.Syllabus_ID, syl.Subject_ID, syl.Workflow_Status, "
                + "s.Subject_Code, s.Subject_Name, s.Credits, cs.Curriculum_ID, cs.Semester_No, "
                + "c.Curriculum_Code, c.Curriculum_Name "
                + "FROM Syllabus_Assignments sa "
                + "JOIN Syllabuses syl ON sa.Syllabus_ID = syl.Syllabus_ID "
                + "JOIN Subjects s ON syl.Subject_ID = s.Subject_ID "
                + "LEFT JOIN Curriculum_Subjects cs ON s.Subject_ID = cs.Subject_ID "
                + "LEFT JOIN Curriculums c ON cs.Curriculum_ID = c.Curriculum_ID "
                + "WHERE sa.User_ID = ? AND sa.Assignment_Type = 'Designer'");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (s.Subject_Code LIKE ? OR s.Subject_Name LIKE ? OR c.Curriculum_Code LIKE ?)");
        }
        sql.append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, designerId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAssignedSubject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CurriculumSubject> getAssignedSubjectsForReviewer(String reviewerId, String keyword) {
        List<CurriculumSubject> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sa.Assignment_ID, sa.Syllabus_ID, syl.Subject_ID, syl.Workflow_Status, "
                + "s.Subject_Code, s.Subject_Name, s.Credits, "
                + "(SELECT TOP 1 cs.Curriculum_ID FROM Curriculum_Subjects cs WHERE cs.Subject_ID = s.Subject_ID) AS Curriculum_ID, "
                + "(SELECT TOP 1 cs.Semester_No FROM Curriculum_Subjects cs WHERE cs.Subject_ID = s.Subject_ID) AS Semester_No, "
                + "(SELECT TOP 1 c.Curriculum_Code FROM Curriculum_Subjects cs JOIN Curriculums c ON cs.Curriculum_ID = c.Curriculum_ID WHERE cs.Subject_ID = s.Subject_ID) AS Curriculum_Code, "
                + "(SELECT TOP 1 c.Curriculum_Name FROM Curriculum_Subjects cs JOIN Curriculums c ON cs.Curriculum_ID = c.Curriculum_ID WHERE cs.Subject_ID = s.Subject_ID) AS Curriculum_Name "
                + "FROM Syllabus_Assignments sa "
                + "JOIN Syllabuses syl ON sa.Syllabus_ID = syl.Syllabus_ID "
                + "JOIN Subjects s ON syl.Subject_ID = s.Subject_ID "
                + "WHERE sa.User_ID = ? AND sa.Assignment_Type = 'Reviewer' AND syl.Workflow_Status = ?");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (s.Subject_Code LIKE ? OR s.Subject_Name LIKE ? OR c.Curriculum_Code LIKE ?)");
        }
        sql.append(" ORDER BY s.Subject_Code");
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, reviewerId);
            ps.setString(2, STATUS_PENDING_REVIEW);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(3, kw);
                ps.setString(4, kw);
                ps.setString(5, kw);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAssignedSubject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private CurriculumSubject mapAssignedSubject(ResultSet rs) throws SQLException {
        CurriculumSubject assigned = new CurriculumSubject();
        assigned.setCurriculumSubjectId(rs.getString("Assignment_ID"));
        assigned.setCurriculumId(rs.getString("Curriculum_ID"));
        assigned.setSubjectId(rs.getString("Subject_ID"));
        assigned.setSyllabusId(rs.getString("Syllabus_ID"));
        assigned.setSyllabusStatus(rs.getString("Workflow_Status"));
        assigned.setSemesterNo(rs.getInt("Semester_No"));

        Subject subject = new Subject();
        subject.setSubjectId(rs.getString("Subject_ID"));
        subject.setSubjectCode(rs.getString("Subject_Code"));
        subject.setSubjectName(rs.getString("Subject_Name"));
        subject.setCredits(rs.getInt("Credits"));
        assigned.setSubject(subject);

        model.Curriculum curriculum = new model.Curriculum();
        curriculum.setCurriculumId(rs.getString("Curriculum_ID"));
        curriculum.setCurriculumCode(rs.getString("Curriculum_Code"));
        curriculum.setCurriculumName(rs.getString("Curriculum_Name"));
        assigned.setCurriculum(curriculum);
        return assigned;
    }

    private boolean updateWorkflowStatus(String syllabusId, String workflowStatus, boolean isActive) {
        String sql = "UPDATE Syllabuses SET Status = ?, Workflow_Status = ?, Is_Active = ? WHERE Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mapWorkflowStatusToLegacyCode(workflowStatus));
            ps.setString(2, workflowStatus);
            ps.setBoolean(3, isActive);
            ps.setString(4, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasAssignment(String syllabusId, String userId, String assignmentType) {
        String sql = "SELECT COUNT(*) FROM Syllabus_Assignments WHERE Syllabus_ID = ? AND User_ID = ? AND Assignment_Type = ?";
        try (Connection con = new DBContext().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            ps.setString(2, userId);
            ps.setString(3, assignmentType);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String ensureSyllabusExistsForSubject(String subjectId) {
        Syllabus existing = findExistingSyllabusBySubjectAny(subjectId);
        if (existing != null) {
            return existing.getSyllabusId();
        }
        return createPlaceholderSyllabus(subjectId);
    }

    private String createPlaceholderSyllabus(String subjectId) {
        String sql = "INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, Status, Workflow_Status, Is_Active) "
                + "OUTPUT INSERTED.Syllabus_ID VALUES (NEWID(), ?, ?, ?, ?, 1)";
        String subjectLabel = "Draft";
        try (Connection con = new DBContext().getConnection()) {
            try (PreparedStatement subjectPs = con.prepareStatement(
                    "SELECT Subject_Code, Subject_Name FROM Subjects WHERE Subject_ID = ?")) {
                subjectPs.setString(1, subjectId);
                ResultSet subjectRs = subjectPs.executeQuery();
                if (subjectRs.next()) {
                    String code = subjectRs.getString("Subject_Code");
                    String name = subjectRs.getString("Subject_Name");
                    if (code != null && !code.trim().isEmpty()) {
                        subjectLabel = code.trim();
                    }
                    if (name != null && !name.trim().isEmpty()) {
                        subjectLabel = subjectLabel + " - " + name.trim();
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, subjectId);
                ps.setString(2, "Draft syllabus for " + subjectLabel);
                ps.setInt(3, Syllabus.STATUS_DRAFT);
                ps.setString(4, STATUS_DRAFT);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean upsertAssignment(String syllabusId, String userId, String assignmentType) {
        if (userId == null || userId.trim().isEmpty()) {
            return true;
        }
        String updateSql = "UPDATE Syllabus_Assignments SET User_ID = ? WHERE Syllabus_ID = ? AND Assignment_Type = ?";
        String insertSql = "INSERT INTO Syllabus_Assignments (Assignment_ID, Syllabus_ID, User_ID, Assignment_Type) VALUES (NEWID(), ?, ?, ?)";
        try (Connection con = new DBContext().getConnection()) {
            try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                updatePs.setString(1, userId);
                updatePs.setString(2, syllabusId);
                updatePs.setString(3, assignmentType);
                if (updatePs.executeUpdate() > 0) {
                    return true;
                }
            }
            try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                insertPs.setString(1, syllabusId);
                insertPs.setString(2, userId);
                insertPs.setString(3, assignmentType);
                return insertPs.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String normalizeWorkflowStatus(String status, String defaultStatus) {
        if (status == null || status.trim().isEmpty()) {
            return defaultStatus;
        }
        String normalized = status.trim();
        if ("Approved".equalsIgnoreCase(normalized) || "Active".equalsIgnoreCase(normalized)) {
            return STATUS_PUBLISHED;
        }
        if ("Rejected".equalsIgnoreCase(normalized)) {
            return STATUS_CHANGES_REQUESTED;
        }
        return normalized;
    }

    private int mapWorkflowStatusToLegacyCode(String workflowStatus) {
        if (workflowStatus == null) {
            return Syllabus.STATUS_DRAFT;
        }
        switch (workflowStatus) {
            case STATUS_PENDING_REVIEW:
                return Syllabus.STATUS_PENDING_REVIEW;
            case STATUS_APPROVED_FOR_PUBLISH:
            case STATUS_PUBLISHED:
                return Syllabus.STATUS_APPROVED;
            default:
                return Syllabus.STATUS_DRAFT;
        }
    }

    private String mapLegacyCodeToWorkflowStatus(int statusCode) {
        switch (statusCode) {
            case Syllabus.STATUS_PENDING_REVIEW:
                return STATUS_PENDING_REVIEW;
            case Syllabus.STATUS_APPROVED:
                return STATUS_APPROVED_FOR_PUBLISH;
            default:
                return STATUS_DRAFT;
        }
    }
}