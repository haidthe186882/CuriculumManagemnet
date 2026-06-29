package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        
        if ("guest-id".equals(loggedUser.getUserId())) {
            req.setAttribute("isGuest", true);
        }

        UserDAO userDAO = new UserDAO();
        User dbUser = userDAO.getUserById(loggedUser.getUserId());
        if (dbUser == null) {
            dbUser = loggedUser;
        }

        req.setAttribute("user", dbUser);
        
        String success = req.getParameter("success");
        if ("profileUpdated".equals(success)) {
            req.setAttribute("successMessage", "Cập nhật họ và tên thành công!");
        } else if ("passwordUpdated".equals(success)) {
            req.setAttribute("successMessage", "Thay đổi mật khẩu thành công!");
        }

        req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        if ("guest-id".equals(loggedUser.getUserId())) {
            req.setAttribute("error", "Tài khoản khách không thể sửa đổi thông tin!");
            req.setAttribute("user", loggedUser);
            req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
            return;
        }

        String action = req.getParameter("action");
        UserDAO userDAO = new UserDAO();
        User dbUser = userDAO.getUserById(loggedUser.getUserId());

        if ("updateProfile".equals(action)) {
            String fullName = req.getParameter("fullName");
            if (fullName == null || fullName.trim().isEmpty()) {
                req.setAttribute("error", "Họ và tên không được để trống!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
                return;
            }

            boolean success = userDAO.updateProfileName(loggedUser.getUserId(), fullName.trim());
            if (success) {
                loggedUser.setFullName(fullName.trim());
                session.setAttribute("loggedUser", loggedUser);
                res.sendRedirect(req.getContextPath() + "/profile?success=profileUpdated");
            } else {
                req.setAttribute("error", "Cập nhật thông tin thất bại. Vui lòng thử lại!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
            }
        } else if ("changePassword".equals(action)) {
            String oldPassword = req.getParameter("oldPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (oldPassword == null || newPassword == null || confirmPassword == null ||
                oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                req.setAttribute("errorPassword", "Vui lòng nhập đầy đủ thông tin mật khẩu!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("errorPassword", "Mật khẩu mới và Xác nhận mật khẩu không khớp!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
                return;
            }

            if (newPassword.length() < 6) {
                req.setAttribute("errorPassword", "Mật khẩu mới phải từ 6 ký tự trở lên!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
                return;
            }

            String oldHashed = hashMD5(oldPassword);
            if (dbUser == null || !oldHashed.equals(dbUser.getPasswordHash())) {
                req.setAttribute("errorPassword", "Mật khẩu cũ không chính xác!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
                return;
            }

            String newHashed = hashMD5(newPassword);
            boolean success = userDAO.updatePassword(loggedUser.getUserId(), newHashed);
            if (success) {
                res.sendRedirect(req.getContextPath() + "/profile?success=passwordUpdated");
            } else {
                req.setAttribute("errorPassword", "Thay đổi mật khẩu thất bại. Vui lòng thử lại!");
                req.setAttribute("user", dbUser != null ? dbUser : loggedUser);
                req.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(req, res);
            }
        } else {
            res.sendRedirect(req.getContextPath() + "/profile");
        }
    }

    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
