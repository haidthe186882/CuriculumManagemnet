package controller;

import dal.DBContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "DebugDBServlet", urlPatterns = {"/__debug/db"})
public class DebugDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) email = "admin@fpt.edu.vn";
        try (PrintWriter out = resp.getWriter()) {
            out.println("Checking DB connection and user record for: " + email);
            try (Connection con = new DBContext().getConnection()) {
                out.println("DB connection OK: " + (con != null));
                try (Statement st = con.createStatement();
                     ResultSet rsDb = st.executeQuery("SELECT DB_NAME() AS CurrentDB")) {
                    if (rsDb.next()) {
                        out.println("Current DB: " + rsDb.getString("CurrentDB"));
                    }
                }

                printCount(out, con, "Users", "SELECT COUNT(*) FROM Users");
                printCount(out, con, "Curriculums", "SELECT COUNT(*) FROM Curriculums");
                printCount(out, con, "Curriculums active", "SELECT COUNT(*) FROM Curriculums WHERE Is_Active = 1");
                printCount(out, con, "Curriculums approved", "SELECT COUNT(*) FROM Curriculums WHERE Status = 1");
                printCount(out, con, "Subjects", "SELECT COUNT(*) FROM Subjects");
                printCount(out, con, "Subjects active", "SELECT COUNT(*) FROM Subjects WHERE Is_Active = 1");
                printCount(out, con, "Syllabuses", "SELECT COUNT(*) FROM Syllabuses");
                printCount(out, con, "Syllabuses active", "SELECT COUNT(*) FROM Syllabuses WHERE Is_Active = 1");

                String sql = "SELECT Email, Password_Hash, Is_Active FROM Users WHERE Email = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            out.println("Found row:");
                            out.println("Email: " + rs.getString("Email"));
                            out.println("Password_Hash: " + rs.getString("Password_Hash"));
                            try {
                                boolean active = rs.getBoolean("Is_Active");
                                out.println("Is_Active: " + active);
                            } catch (Exception ex) {
                                out.println("Is_Active: (unknown)");
                            }
                        } else {
                            out.println("No user row found for that email.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            resp.getWriter().println("ERROR: " + e.getMessage());
        }
    }

    private void printCount(PrintWriter out, Connection con, String label, String sql) {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                out.println(label + ": " + rs.getInt(1));
            }
        } catch (Exception ex) {
            out.println(label + ": (error: " + ex.getMessage() + ")");
        }
    }
}
