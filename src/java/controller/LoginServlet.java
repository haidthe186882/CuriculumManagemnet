package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Please enter both email and password.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email.trim());

        if (user == null) {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
            return;
        }

        String storedHash = user.getPasswordHash();
        boolean ok = false;

        if (storedHash != null && (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$"))) {
            // bcrypt
            ok = BCrypt.checkpw(password, storedHash);
        } else if (storedHash.equals(password)) {
            // stored plaintext (legacy/mistake) -> accept and migrate to bcrypt
            ok = true;
            try {
                String b = BCrypt.hashpw(password, BCrypt.gensalt(12));
                dao.updatePassword(user.getUserId(), b);
            } catch (Exception ignored) {}
        } else {
            // fallback MD5
            String md5 = hashMD5(password.trim());
            if (md5.equalsIgnoreCase(storedHash)) {
                ok = true;
                // migrate to bcrypt
                try {
                    String b = BCrypt.hashpw(password, BCrypt.gensalt(12));
                    dao.updatePassword(user.getUserId(), b);
                } catch (Exception ignored) {}
            }
        }

        if (!ok) {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("loggedUser", user);
        session.setMaxInactiveInterval(30 * 60); // 30 min timeout (NFR 4.2.4)

        // Redirect theo role
        String role = user.getRole() != null ? user.getRole().getRoleName() : "";
        switch (role) {
            case "Admin":    res.sendRedirect(req.getContextPath() + "/admin/users"); break;
            case "Reviewer": res.sendRedirect(req.getContextPath() + "/review/list"); break;
            case "Designer": res.sendRedirect(req.getContextPath() + "/curriculum/list"); break;
            default:         res.sendRedirect(req.getContextPath() + "/curriculum/list"); break;
        }
    }

    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return input; }
    }
}
