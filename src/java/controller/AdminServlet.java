package controller;

import dao.UserDAO;
import model.Role;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/*"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // Bắt đầu ghi ra ổ cứng nếu file > 2MB
    maxFileSize = 1024 * 1024 * 10,      // Dung lượng tối đa của 1 file: 10MB
    maxRequestSize = 1024 * 1024 * 50    // Dung lượng tối đa của cả request: 50MB
)
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
            case "add":
                doAdd(req, res);
                break;
            case "update":
                doUpdate(req, res);
                break;
            case "updateStatus":
                doUpdateStatus(req, res);
                break;
            case "bulkUpdateRole":
                doBulkUpdateRole(req, res);
                break;
            case "bulkDeactivate":
                doBulkDeactivate(req, res);
                break;
            case "importUsersExcel":
                doImportUsersExcel(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/admin/users");
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
        String email = req.getParameter("email");
        if (userDAO.emailExists(email)) {
        // Gửi kèm email bị trùng lên URL để báo lỗi cho Admin
        res.sendRedirect(req.getContextPath() + "/admin/users?msg=duplicateEmail&email=" + email);
        return; // Dừng lại, không tạo mới nữa
        }
        u.setFullName(req.getParameter("fullName"));
        u.setEmail(req.getParameter("email"));
        u.setPhoneNumber(req.getParameter("phoneNumber"));
        u.setDepartment(req.getParameter("department"));
        try { u.setRoleId(Integer.parseInt(req.getParameter("roleId"))); } catch (Exception ignored) {}
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
        u.setStatus(req.getParameter("status"));
        try { u.setRoleId(Integer.parseInt(req.getParameter("roleId"))); } catch (Exception ignored) {}
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
    
    private void doBulkUpdateRole(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Lấy danh sách ID của các User được tick chọn (dạng mảng)
        String[] userIds = req.getParameterValues("userIds");
        
        // Lấy các Role muốn áp dụng chung cho nhóm này
        String newRoleId = req.getParameter("bulkRoleId");
        boolean isReviewer = req.getParameter("bulkReviewer") != null;
        boolean isDesigner = req.getParameter("bulkDesigner") != null;

        if (userIds != null && userIds.length > 0) {
            userDAO.bulkUpdateRoles(userIds, newRoleId, isReviewer, isDesigner);
            res.sendRedirect(req.getContextPath() + "/admin/users?msg=bulkSuccess");
        } else {
            res.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }
    
    private void doBulkDeactivate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Lấy danh sách ID của những User được tích chọn trên giao diện
        String[] userIds = req.getParameterValues("userIds");

        if (userIds != null && userIds.length > 0) {
            userDAO.bulkDeactivateUsers(userIds);
            res.sendRedirect(req.getContextPath() + "/admin/users?msg=bulkDeactivateSuccess");
        } else {
            res.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void doImportUsersExcel(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String msg;
        int imported = 0;
        int skipped = 0;

        try {
            jakarta.servlet.http.Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                res.sendRedirect(req.getContextPath() + "/admin/users?msg=importNoFile");
                return;
            }

            try (java.io.InputStream is = filePart.getInputStream()) {
                util.UserExcelHelper.ParseResult result = util.UserExcelHelper.parseUsersExcel(is);
                System.out.println("[IMPORT_USERS] parsed users=" + (result.getUsers() == null ? 0 : result.getUsers().size()));
                if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                    for (String err : result.getErrors()) System.out.println("[IMPORT_USERS][ERROR] " + err);
                    res.sendRedirect(req.getContextPath() + "/admin/users?msg=importExcelErrors&errCount=" + result.getErrors().size());
                    return;
                }


                java.util.List<String> importWarnings = new java.util.ArrayList<>();

                for (model.User u : result.getUsers()) {
                    String roleName = u.getPhoneNumber();
                    if (roleName == null) roleName = "";

                    java.util.List<model.Role> roles = userDAO.getAllRoles();
                    Integer resolvedRoleId = null;
                    for (model.Role r : roles) {
                        // So sánh linh hoạt hơn: Cắt hết dấu cách thừa ở cả 2 bên
                        if (r.getRoleName() != null && r.getRoleName().trim().equalsIgnoreCase(roleName.trim())) {
                            resolvedRoleId = r.getRoleId();
                            break;
                        }
                    }

                    // CHỐT CHẶN 1: BÁO LỖI NẾU SAI TÊN ROLE
                    if (resolvedRoleId == null) {
                        skipped++;
                        importWarnings.add("Email [" + u.getEmail() + "] bị bỏ qua -> Lý do: Tên Role trong Excel là '" + roleName + "' không khớp với hệ thống.");
                        continue;
                    }

                    u.setRoleId(resolvedRoleId);
                    String defaultPassword = "123456";
                    u.setPasswordHash(hashMD5(defaultPassword));

                    // CHỐT CHẶN 2: BÁO LỖI NẾU TRÙNG EMAIL
                    if (userDAO.emailExists(u.getEmail())) {
                        skipped++;
                        importWarnings.add("Email [" + u.getEmail() + "] bị bỏ qua -> Lý do: Email này ĐÃ TỒN TẠI trong Database.");
                    } else {
                        // Thêm vào DB nếu vượt qua mọi chốt chặn
                        boolean ok = userDAO.addUser(u);
                        if (ok) {
                            imported++;
                        } else {
                            skipped++;
                            importWarnings.add("Email [" + u.getEmail() + "] bị lỗi ẩn khi Insert vào Database (Check lại DAO).");
                        }
                    }
                }
                if (!importWarnings.isEmpty()) {
                    System.out.println("====== BÁO CÁO CÁC DÒNG BỊ SKIP KHI IMPORT ======");
                    for (String w : importWarnings) {
                        System.out.println(w);
                    }
                    System.out.println("=================================================");
                }
            }
            
            res.sendRedirect(req.getContextPath() + "/admin/users?msg=importSuccess&imported=" + imported + "&skipped=" + skipped);
        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(req.getContextPath() + "/admin/users?msg=importFail");
        }
    }
}

