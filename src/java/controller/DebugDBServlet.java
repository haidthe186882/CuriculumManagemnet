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
}
