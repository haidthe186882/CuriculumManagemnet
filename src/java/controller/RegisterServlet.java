package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullname = req.getParameter("fullname");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        if (fullname == null || email == null || password == null || confirm == null ||
            fullname.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirm)) {
            req.setAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.emailExists(email.trim())) {
            req.setAttribute("error", "Email đã được sử dụng.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        User u = new User();
        u.setFullName(fullname.trim());
        u.setEmail(email.trim());
        // hash password with bcrypt
        String bcrypt = BCrypt.hashpw(password.trim(), BCrypt.gensalt(12));
        u.setPasswordHash(bcrypt);
        u.setRoleId(5); // default to 'Student' per seed data

        boolean ok = dao.addUser(u);
        if (!ok) {
            req.setAttribute("error", "Đăng ký thất bại. Vui lòng thử lại sau.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // Tự động đăng nhập sau khi đăng ký
        User created = dao.getUserByEmail(u.getEmail());
        if (created != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", created);
            session.setMaxInactiveInterval(30 * 60);
        }

        resp.sendRedirect(req.getContextPath() + "/curriculum/list");
    }
}
