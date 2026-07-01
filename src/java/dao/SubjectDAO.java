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
                   + "s.Description, s.Is_Active, m.Major_Name AS Department, sy.Syllabus_ID "
                   + "FROM Curriculum_Subjects cs "
                   + "JOIN Subjects s ON cs.Subject_ID = s.Subject_ID "
                   + "LEFT JOIN Majors m ON s.Major_ID = m.Major_ID "
                   + "LEFT JOIN Syllabuses sy ON s.Subject_ID = sy.Subject_ID AND sy.Is_Active = 1 "
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
                s.setPrerequisites(getPrerequisitesForSubject(s.getSubjectId()));
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

    public List<Subject> getAllSubjectsWithPrerequisites() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT s.Subject_ID, s.Subject_Code, s.Subject_Name, s.Credits, s.Description, " +
                     "req.Subject_ID AS Req_ID, req.Subject_Code AS Req_Code, req.Subject_Name AS Req_Name " +
                     "FROM Subjects s " +
                     "LEFT JOIN Subject_Prerequisites sp ON s.Subject_ID = sp.Subject_ID " +
                     "LEFT JOIN Subjects req ON sp.Required_Subject_ID = req.Subject_ID " +
                     "WHERE s.Is_Active = 1 " +
                     "ORDER BY s.Subject_Code";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Subject current = null;
            while (rs.next()) {
                String subId = rs.getString("Subject_ID");
                if (current == null || !current.getSubjectId().equals(subId)) {
                    current = new Subject();
                    current.setSubjectId(subId);
                    current.setSubjectCode(rs.getString("Subject_Code"));
                    current.setSubjectName(rs.getString("Subject_Name"));
                    current.setCredits(rs.getInt("Credits"));
                    current.setDescription(rs.getString("Description"));
                    list.add(current);
                }
                String reqId = rs.getString("Req_ID");
                if (reqId != null) {
                    Subject req = new Subject();
                    req.setSubjectId(reqId);
                    req.setSubjectCode(rs.getString("Req_Code"));
                    req.setSubjectName(rs.getString("Req_Name"));
                    current.getPrerequisites().add(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
        String sql = "SELECT req.Subject_ID, req.Subject_Code, req.Subject_Name, req.Credits " +
                     "FROM Subject_Prerequisites sp " +
                     "JOIN Subjects req ON sp.Required_Subject_ID = req.Subject_ID " +
                     "WHERE sp.Subject_ID = ? AND req.Is_Active = 1 " +
                     "ORDER BY req.Subject_Code";
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
