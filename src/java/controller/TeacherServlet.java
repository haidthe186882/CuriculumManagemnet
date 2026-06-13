package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@WebServlet(name = "TeacherServlet", urlPatterns = {"/teacher/*"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 50 * 1024 * 1024)
public class TeacherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User u = (User) session.getAttribute("loggedUser");
        String role = u.getRole() != null ? u.getRole().getRoleName() : "";
        // Allow Admin always; allow Teacher or users with reviewer/designer flags to access teacher pages
        if (!"Admin".equals(role) && !"Teacher".equals(role) && !u.isReviewer() && !u.isDesigner()) {
            resp.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }
        String path = req.getPathInfo();
        if (path == null || "/home".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/teacher/home.jsp").forward(req, resp);
            return;
        }
        if ("/upload".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/teacher/upload.jsp").forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/home");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User u = (User) session.getAttribute("loggedUser");
        String role = u.getRole() != null ? u.getRole().getRoleName() : "";
        if (!"Admin".equals(role) && !"Teacher".equals(role) && !u.isReviewer() && !u.isDesigner()) {
            resp.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }
        String path = req.getPathInfo();
        if ("/upload".equals(path)) {
            Part filePart = req.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String uploads = getServletContext().getRealPath("/uploads/teacher/") + File.separator + u.getUserId();
                File dir = new File(uploads);
                if (!dir.exists()) dir.mkdirs();
                String submitted = Path.of(filePart.getSubmittedFileName()).getFileName().toString();
                Path target = Path.of(uploads, submitted);
                try (InputStream in = filePart.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }
                req.getSession().setAttribute("msg", "File uploaded: " + submitted);
            }
            resp.sendRedirect(req.getContextPath() + "/teacher/upload");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/home");
    }
}
