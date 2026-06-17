package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

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
        // Delegate authentication to DAO: DB contains hashed password already.
        User user = dao.login(email.trim(), password.trim());

        if (user == null) {
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
            case "Admin":    res.sendRedirect(req.getContextPath() + "/admin/home"); break;
            case "Reviewer": res.sendRedirect(req.getContextPath() + "/review/list"); break;
            case "Designer": res.sendRedirect(req.getContextPath() + "/curriculum/list"); break;
            default:         res.sendRedirect(req.getContextPath() + "/curriculum/list"); break;
        }
    }

    // Authentication now delegated to `UserDAO.login(email, password)`
}
