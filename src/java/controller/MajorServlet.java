package controller;

import dao.MajorDAO;
import model.Major;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "MajorServlet", urlPatterns = {"/major/*"})
public class MajorServlet extends HttpServlet {

    private final MajorDAO majorDAO = new MajorDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) {
            path = "/list";
        }
        switch (path) {
            case "/list":
                showList(req, res);
                break;
            case "/create":
                showForm(req, res);
                break;
            case "/edit":
                showForm(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/major/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                doCreate(req, res);
                break;
            case "update":
                doUpdate(req, res);
                break;
            case "toggle":
                doToggle(req, res);
                break;
            case "delete":
                doDelete(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/major/list");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Major> majors = majorDAO.getAllMajors();
        req.setAttribute("majors", majors);
        req.getRequestDispatcher("/WEB-INF/views/major/list.jsp").forward(req, res);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) {
            req.setAttribute("major", majorDAO.getMajorById(id));
        }
        req.getRequestDispatcher("/WEB-INF/views/major/form.jsp").forward(req, res);
    }

    private void doCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) {
            return;
        }
        Major m = new Major();
        m.setMajorCode(req.getParameter("majorCode"));
        m.setMajorName(req.getParameter("majorName"));
        m.setDescription(req.getParameter("description"));
        majorDAO.addMajor(m);
        res.sendRedirect(req.getContextPath() + "/major/list?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) {
            return;
        }
        Major m = new Major();
        m.setMajorId(req.getParameter("majorId"));
        m.setMajorCode(req.getParameter("majorCode"));
        m.setMajorName(req.getParameter("majorName"));
        m.setDescription(req.getParameter("description"));
        majorDAO.updateMajor(m);
        res.sendRedirect(req.getContextPath() + "/major/list?msg=updated");
    }

    private void doToggle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) {
            return;
        }
        String id = req.getParameter("majorId");
        boolean active = "1".equals(req.getParameter("active"));
        majorDAO.toggleActive(id, active);
        res.sendRedirect(req.getContextPath() + "/major/list?msg=toggled");
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) {
            return;
        }
        String id = req.getParameter("majorId");
        majorDAO.deleteMajor(id);
        res.sendRedirect(req.getContextPath() + "/major/list?msg=deleted");
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        User u = (User) s.getAttribute("loggedUser");
        String role = u.getRole() != null ? u.getRole().getRoleName() : "";
        if (!"Admin".equals(role)) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return false;
        }
        return true;
    }
}