package controller;

import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("loggedUser");
        String role = user.getRole() != null ? user.getRole().getRoleName() : "";
        switch (role) {
            case "Admin":    res.sendRedirect(req.getContextPath() + "/admin/home"); break;
            case "Reviewer": res.sendRedirect(req.getContextPath() + "/review/list"); break;
            case "Teacher":  res.sendRedirect(req.getContextPath() + "/teacher/home"); break;
            default:         res.sendRedirect(req.getContextPath() + "/curriculum/list"); break;
        }
    }
}
