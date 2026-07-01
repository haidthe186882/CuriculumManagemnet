package dao;

import dal.DBContext;
import model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public List<Session> getSessionsBySyllabus(String syllabusId) {
        List<Session> list = new ArrayList<>();
        String sql = "SELECT Session_ID, Syllabus_ID, Session_No, Topic, Learning_Teaching_Type, LO, ITU, Student_Materials, Student_Tasks, URLs "
                   + "FROM Sessions WHERE Syllabus_ID = ? ORDER BY Session_No";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, syllabusId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Session s = new Session();
                    s.setSessionId(rs.getString("Session_ID"));
                    s.setSyllabusId(rs.getString("Syllabus_ID"));
                    s.setSessionNo(rs.getInt("Session_No"));
                    s.setTopic(rs.getString("Topic"));
                    s.setLearningTeachingType(rs.getString("Learning_Teaching_Type"));
                    s.setLo(rs.getString("LO"));
                    s.setItu(rs.getString("ITU"));
                    s.setStudentMaterials(rs.getString("Student_Materials"));
                    s.setStudentTasks(rs.getString("Student_Tasks"));
                    s.setUrls(rs.getString("URLs"));
                    list.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Insert a single session */
    public boolean addSession(Session session) {
        String sql = "INSERT INTO Sessions (Session_ID, Syllabus_ID, Session_No, Topic, Learning_Teaching_Type, LO, ITU, Student_Materials, Student_Tasks, URLs) "
                   + "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, session.getSyllabusId());
            ps.setInt(2, session.getSessionNo());
            ps.setString(3, session.getTopic());
            ps.setString(4, session.getLearningTeachingType());
            ps.setString(5, session.getLo());
            ps.setString(6, session.getItu());
            ps.setString(7, session.getStudentMaterials());
            ps.setString(8, session.getStudentTasks());
            ps.setString(9, session.getUrls());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Bulk insert sessions for a syllabus */
    public int addSessions(String syllabusId, List<Session> sessions) {
        int count = 0;
        for (Session session : sessions) {
            session.setSyllabusId(syllabusId);
            if (addSession(session)) count++;
        }
        return count;
    }
}
