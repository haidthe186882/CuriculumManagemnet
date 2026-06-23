package dao;

import dal.DBContext;
import model.Syllabus;
import model.Subject;
import model.SyllabusMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            boolean active = rs.getBoolean("Is_Active");
            s.setActive(active);
            s.setStatus(active ? "Approved" : "Draft");
        } catch (SQLException ignored) {}
        try {
            s.setMaterialUrl(rs.getString("Material_URL"));
        } catch (SQLException ignored) {}
        // join Subject
        try {
            Subject sub = new Subject();
            sub.setSubjectId(rs.getString("Subject_ID"));
            sub.setSubjectCode(rs.getString("Subject_Code"));
            sub.setSubjectName(rs.getString("Subject_Name"));
            sub.setCredits(rs.getInt("Credits"));
            s.setSubject(sub);
        } catch (SQLException ignored) {}
        return s;
    }

    /** Tim kiem syllabus */
    public List<Syllabus> searchSyllabuses(String keyword, String status, boolean activeOnly) {
        List<Syllabus> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits, m.Link AS Material_URL FROM Syllabuses sy "
          + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
          + "LEFT JOIN Materials m ON sy.Syllabus_ID = m.Syllabus_ID AND m.Is_Main_Material = 1 WHERE 1=1");
        if (activeOnly) sql.append(" AND sy.Is_Active=1");
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
            while (rs.next()) list.add(mapSyllabus(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Lay syllabus theo ID */
    public Syllabus getSyllabusById(String id) {
        String sql = "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits, m.Link AS Material_URL FROM Syllabuses sy "
                   + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                   + "LEFT JOIN Materials m ON sy.Syllabus_ID = m.Syllabus_ID AND m.Is_Main_Material = 1 "
                   + "WHERE sy.Syllabus_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSyllabus(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Lay syllabus theo subject */
    public Syllabus getSyllabusBySubject(String subjectId) {
        String sql = "SELECT sy.*, s.Subject_Code, s.Subject_Name, s.Credits, m.Link AS Material_URL FROM Syllabuses sy "
                   + "JOIN Subjects s ON sy.Subject_ID = s.Subject_ID "
                   + "LEFT JOIN Materials m ON sy.Syllabus_ID = m.Syllabus_ID AND m.Is_Main_Material = 1 "
                   + "WHERE sy.Subject_ID = ? AND sy.Is_Active=1";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSyllabus(rs);
        } catch (Exception e) { e.printStackTrace(); }
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
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Cap nhat trang thai */
    public boolean updateStatus(String syllabusId, String status) {
        boolean activeVal = "Approved".equalsIgnoreCase(status) || "Active".equalsIgnoreCase(status) || "1".equals(status);
        String sql = "UPDATE Syllabuses SET Is_Active=? WHERE Syllabus_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activeVal);
            ps.setString(2, syllabusId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
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
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
