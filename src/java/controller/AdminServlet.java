package controller;

import dao.UserDAO;
import model.Role;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/*"})
public class AdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireAdmin(req, res)) return;
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/home";
        switch (pathInfo) {
            case "/users": showUsers(req, res); break;
            case "/home":
                req.getRequestDispatcher("/WEB-INF/views/admin/home.jsp").forward(req, res);
                break;
            default: res.sendRedirect(req.getContextPath() + "/admin/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireAdmin(req, res)) return;
        String action = req.getParameter("action");
        if (action == null) action = "";
        switch (action) {
            case "add":          doAdd(req, res); break;
            case "update":       doUpdate(req, res); break;
            case "updateStatus": doUpdateStatus(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void showUsers(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String status  = req.getParameter("status");
        String roleId = req.getParameter("roleId");
        List<User> users = userDAO.getAllUsers(keyword, status,roleId);
        List<Role> allRoles = userDAO.getAllRoles();
        List<Role> filteredRoles = allRoles.stream().filter(r -> r.getRoleName().equals("Teacher") || 
                r.getRoleName().equals("Student")).collect(Collectors.toList());
        req.setAttribute("users", users);
        req.setAttribute("roles", filteredRoles);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("selectedRole",roleId);
        req.setAttribute("roles", userDAO.getAllRoles());
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, res);
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User u = new User();
        u.setFullName(req.getParameter("fullName"));
        u.setEmail(req.getParameter("email"));
        u.setPhoneNumber(req.getParameter("phoneNumber"));
        u.setDepartment(req.getParameter("department"));
        try { u.setRoleId(Integer.parseInt(req.getParameter("roleId"))); } catch (Exception ignored) {}
//        u.setReviewer(req.getParameter("isReviewer") != null && req.getParameter("isReviewer").equals("on"));
//        u.setDesigner(req.getParameter("isDesigner") != null && req.getParameter("isDesigner").equals("on"));
        u.setReviewer(req.getParameter("isReviewer") != null);
        u.setDesigner(req.getParameter("isDesigner") != null);
        String pwd = req.getParameter("password");
        if (pwd == null || pwd.trim().isEmpty()) pwd = "123456";
        u.setPasswordHash(hashMD5(pwd.trim()));
        userDAO.addUser(u);
        res.sendRedirect(req.getContextPath() + "/admin/users?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User u = new User();
        u.setUserId(req.getParameter("userId"));
        u.setFullName(req.getParameter("fullName"));
        u.setPhoneNumber(req.getParameter("phoneNumber"));
        u.setDepartment(req.getParameter("department"));
        u.setStatus(req.getParameter("status"));
        try { u.setRoleId(Integer.parseInt(req.getParameter("roleId"))); } catch (Exception ignored) {}
//        u.setReviewer(req.getParameter("isReviewer") != null && req.getParameter("isReviewer").equals("on"));
//        u.setDesigner(req.getParameter("isDesigner") != null && req.getParameter("isDesigner").equals("on"));
        u.setReviewer(req.getParameter("isReviewer") != null);
        u.setDesigner(req.getParameter("isDesigner") != null);
        userDAO.updateUser(u);
        res.sendRedirect(req.getContextPath() + "/admin/users?msg=updated");
    }

    private void doUpdateStatus(HttpServletRequest req, HttpServletResponse res) throws IOException {
        userDAO.updateStatus(req.getParameter("userId"), req.getParameter("status"));
        res.sendRedirect(req.getContextPath() + "/admin/users?msg=status");
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        User user = (User) session.getAttribute("loggedUser");
        String role = user.getRole() != null ? user.getRole().getRoleName() : "";
        if (!"Admin".equals(role)) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return false;
        }
        return true;
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
