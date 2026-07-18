package controller;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import model.TeacherMaterial;
import dao.TeacherMaterialDAO;
import dao.SyllabusDAO;
import jakarta.servlet.ServletException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
@WebServlet(name = "TeacherServlet", urlPatterns = {"/teacher/*"})
public class TeacherServlet extends HttpServlet {

    private final TeacherMaterialDAO materialDAO = new TeacherMaterialDAO();
    private final SyllabusDAO syllabusDAO = new SyllabusDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = requireTeacher(req, resp);
        if (u == null) {
            return;
        }

        String path = req.getPathInfo();
        
        switch (path) {
            case "/upload":
                showUploadList(req, resp, u);
                break;
            case "/edit":
                showEditForm(req, resp, u);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/curriculum/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User u = requireTeacher(req, resp);
        if (u == null) {
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "create":
                doCreate(req, resp, u);
                break;
            case "update":
                doUpdate(req, resp, u);
                break;
            case "delete":
                doDelete(req, resp, u);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/teacher/upload");
        }
    }

    // ===== GET handlers =====
    private void showUploadList(HttpServletRequest req, HttpServletResponse resp, User u)
            throws ServletException, IOException {
        System.out.println("[TeacherServlet] showUploadList - userId: " + u.getUserId() + ", name: " + u.getFullName());
        List<TeacherMaterial> materials = materialDAO.getMaterialsByUser(u.getUserId());
        System.out.println("[TeacherServlet] showUploadList - materials count: " + materials.size());
        req.setAttribute("materials", materials);
        req.setAttribute("syllabuses", syllabusDAO.searchSyllabuses(null, null, true));
        req.getRequestDispatcher("/WEB-INF/views/teacher/upload.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp, User u)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            TeacherMaterial tm = materialDAO.getMaterialById(idStr.trim());
            // chỉ cho phép chỉnh sửa tài liệu của chính mình
            if (tm != null && tm.getUserId().equals(u.getUserId())) {
                req.setAttribute("editMaterial", tm);
            }
        }
        req.setAttribute("syllabuses", syllabusDAO.searchSyllabuses(null, null, true));
        req.setAttribute("materials", materialDAO.getMaterialsByUser(u.getUserId()));
        req.getRequestDispatcher("/WEB-INF/views/teacher/upload.jsp").forward(req, resp);
    }

    // ===== POST handlers =====
    private void doCreate(HttpServletRequest req, HttpServletResponse resp, User u) throws ServletException, IOException {
        TeacherMaterial tm = new TeacherMaterial();
        tm.setUserId(u.getUserId());
        tm.setMaterialName(req.getParameter("materialName"));
        tm.setSyllabusId(req.getParameter("syllabusId"));
        tm.setMaterialType(req.getParameter("materialType"));
        tm.setDescription(req.getParameter("description"));
        tm.setMaterialUrl(req.getParameter("materialUrl"));
        tm.setDownloadLink(processUploadFile(req));

        boolean ok = materialDAO.addMaterial(tm);
        if (ok) {
            req.getSession().setAttribute("msg", "Material uploaded successfully!");
        } else {
            req.getSession().setAttribute("error", "Could not upload material. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/upload");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, User u) throws ServletException, IOException {
        TeacherMaterial tm = new TeacherMaterial();
        tm.setUserId(u.getUserId());
        tm.setMaterialId(req.getParameter("materialId"));
        tm.setSyllabusId(req.getParameter("syllabusId"));
        tm.setMaterialName(req.getParameter("materialName"));
        tm.setMaterialType(req.getParameter("materialType"));
        tm.setDescription(req.getParameter("description"));
        tm.setMaterialUrl(req.getParameter("materialUrl"));

        // Handle file upload, keep existing if new not provided
        String newUpload = processUploadFile(req);
        if (newUpload != null) {
            tm.setDownloadLink(newUpload);
        } else {
            TeacherMaterial existing = materialDAO.getMaterialById(tm.getMaterialId());
            if (existing != null) {
                tm.setDownloadLink(existing.getDownloadLink());
            }
        }

        materialDAO.updateMaterial(tm);
        req.getSession().setAttribute("msg", "Material updated successfully!");
        resp.sendRedirect(req.getContextPath() + "/teacher/upload");
    }

    private void doDelete(HttpServletRequest req, HttpServletResponse resp, User u) throws IOException {
        String idStr = req.getParameter("materialId");
        if (idStr != null && !idStr.trim().isEmpty()) {
            materialDAO.deleteMaterial(idStr.trim(), u.getUserId());
            req.getSession().setAttribute("msg", "Material deleted successfully!");
        }
        resp.sendRedirect(req.getContextPath() + "/teacher/upload");
    }

    // ===== Helper =====
    private String processUploadFile(HttpServletRequest req) throws ServletException, IOException {
        Part filePart = req.getPart("uploadFile");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (!fileName.isEmpty()) {
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator
                        + "teacher_materials";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists())
                    uploadDir.mkdirs();

                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                filePart.write(uploadPath + File.separator + uniqueFileName);
                return "/uploads/teacher_materials/" + uniqueFileName;
            }
        }
        return null;
    }

    private User requireTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        User u = (User) session.getAttribute("loggedUser");
        String role = u.getRole() != null ? u.getRole().getRoleName() : "";
        if (!"Admin".equals(role) && !"Teacher".equals(role) && !u.isReviewer() && !u.isDesigner()) {
            resp.sendRedirect(req.getContextPath() + "/curriculum/list");
            return null;
        }
        return u;
    }
}
