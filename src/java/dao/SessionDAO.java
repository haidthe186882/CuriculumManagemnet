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
}
