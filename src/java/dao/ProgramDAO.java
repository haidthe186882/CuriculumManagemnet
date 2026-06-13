package dao;

import dal.DBContext;
import model.Program;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramDAO {

    public List<Program> getAllPrograms() {
        List<Program> list = new ArrayList<>();
        String sql = "SELECT * FROM Programs WHERE Is_Active=1 ORDER BY Program_Name";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Program p = new Program();
                p.setProgramId(rs.getString("Program_ID"));
                p.setProgramCode(rs.getString("Program_Code"));
                p.setProgramName(rs.getString("Program_Name"));
                p.setDescription(rs.getString("Description"));
                try { p.setStatus(rs.getString("Status")); } catch (SQLException ignored) {}
                list.add(p);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Program getProgramById(String id) {
        String sql = "SELECT * FROM Programs WHERE Program_ID = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Program p = new Program();
                p.setProgramId(rs.getString("Program_ID"));
                p.setProgramCode(rs.getString("Program_Code"));
                p.setProgramName(rs.getString("Program_Name"));
                p.setDescription(rs.getString("Description"));
                try { p.setStatus(rs.getString("Status")); } catch (SQLException ignored) {}
                return p;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean addProgram(Program p) {
        String sql = "INSERT INTO Programs (Program_ID, Program_Code, Program_Name, Description, Is_Active) "
               + "VALUES (NEWID(), ?, ?, ?, 1)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getProgramCode());
            ps.setString(2, p.getProgramName());
            ps.setString(3, p.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateProgram(Program p) {
        String sql = "UPDATE Programs SET Program_Code=?, Program_Name=?, Description=? WHERE Program_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getProgramCode());
            ps.setString(2, p.getProgramName());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getProgramId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean toggleActive(String programId, boolean isActive) {
        String sql = "UPDATE Programs SET Is_Active=? WHERE Program_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, isActive ? 1 : 0);
            ps.setString(2, programId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteProgram(String programId) {
        String sql = "DELETE FROM Programs WHERE Program_ID=?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, programId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
