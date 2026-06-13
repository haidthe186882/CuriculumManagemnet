package controller;

import dao.ProgramDAO;
import model.Program;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProgramServlet", urlPatterns = {"/program/*"})
public class ProgramServlet extends HttpServlet {

    private final ProgramDAO programDAO = new ProgramDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/list";
        switch (path) {
            case "/list": showList(req, res); break;
            case "/create": showForm(req, res); break;
            case "/edit": showForm(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/program/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";
        switch (action) {
            case "create": doCreate(req, res); break;
            case "update": doUpdate(req, res); break;
            case "toggle": doToggle(req, res); break;
            case "delete": doDelete(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/program/list");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Program> programs = programDAO.getAllPrograms();
        req.setAttribute("programs", programs);
        req.getRequestDispatcher("/WEB-INF/views/program/list.jsp").forward(req, res);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) req.setAttribute("program", programDAO.getProgramById(id));
        req.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(req, res);
    }

    private void doCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) return;
        Program p = new Program();
        p.setProgramCode(req.getParameter("programCode"));
        p.setProgramName(req.getParameter("programName"));
        p.setDescription(req.getParameter("description"));
        programDAO.addProgram(p);
        res.sendRedirect(req.getContextPath() + "/program/list?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) return;
        Program p = new Program();
        p.setProgramId(req.getParameter("programId"));
        p.setProgramCode(req.getParameter("programCode"));
        p.setProgramName(req.getParameter("programName"));
        p.setDescription(req.getParameter("description"));
        programDAO.updateProgram(p);
        res.sendRedirect(req.getContextPath() + "/program/list?msg=updated");
    }

    private void doToggle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) return;
        String id = req.getParameter("programId");
        boolean active = "1".equals(req.getParameter("active"));
        programDAO.toggleActive(id, active);
        res.sendRedirect(req.getContextPath() + "/program/list?msg=toggled");
    }

    private void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireAdmin(req, res)) return;
        String id = req.getParameter("programId");
        programDAO.deleteProgram(id);
        res.sendRedirect(req.getContextPath() + "/program/list?msg=deleted");
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("loggedUser") == null) { res.sendRedirect(req.getContextPath() + "/login"); return false; }
        User u = (User) s.getAttribute("loggedUser");
        String role = u.getRole() != null ? u.getRole().getRoleName() : "";
        if (!"Admin".equals(role)) { res.sendRedirect(req.getContextPath() + "/curriculum/list"); return false; }
        return true;
    }
}
