package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * @author Mai Duy An
 * @MSSV HE197000
 * @date 24/6/2026
 *
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("guest".equalsIgnoreCase(action)) {
            User guestUser = new User();
            guestUser.setUserId("guest-id");
            guestUser.setFullName("Guest User");
            guestUser.setEmail("guest@ltms.com");
            guestUser.setIsActive("Active");
            guestUser.addRole(new model.Role(5, "Guest"));
            guestUser.setRoleId(5);

            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", guestUser);
            session.setMaxInactiveInterval(30 * 60);
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }

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
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
            return;
        }

//        UserDAO dao = new UserDAO();
//        // DB stores password hash; the form value is compared as-is (plain hash_placeholder).
//        User user = dao.login(email.trim(), password.trim());
        UserDAO dao = new UserDAO();
        String hashedPassword = hashMD5(password.trim()); //
        User user = dao.login(email.trim(), hashedPassword);
        if (user == null) {
            user = dao.login(email.trim(), password.trim());
        }
        // Hash mật khẩu bằng MD5 trước khi so sánh với DB
        String hashedPassword = hashMD5(password.trim());
        User user = dao.login(email.trim(), hashedPassword);

        if (user == null) {
            req.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
            return;
        }

        // Store user (with loaded roles) in session
        HttpSession session = req.getSession(true);
        session.setAttribute("loggedUser", user);
        session.setMaxInactiveInterval(30 * 60); // 30 min timeout

        // Redirect based on primary role (User_Roles table may assign multiple roles
        // we use the first one returned, priority: Admin > Designer > Reviewer > Teacher > Student)
        if (user.hasRole("Admin")) {
            res.sendRedirect(req.getContextPath() + "/admin/users");
        } else if (user.hasRole("Reviewer") || user.isReviewer()) {
            res.sendRedirect(req.getContextPath() + "/review/list");
        } else if (user.hasRole("Designer") || user.isDesigner()) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
        } else {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
        }
    }
    

    /**
     * Mã hóa chuỗi đầu vào bằng thuật toán MD5.
     * @param input Chuỗi cần mã hóa
     */
    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return input; }//
    }
}
