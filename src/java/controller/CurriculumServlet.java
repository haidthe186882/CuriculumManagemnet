package controller;

import dao.CurriculumDAO;
import dao.MajorDAO;
import dao.SubjectDAO;
import dao.ReviewDAO;
import dao.PloDAO;
import dao.PoDAO;
import model.Curriculum;
import model.CurriculumSubject;
import model.Subject;
import model.User;
import util.ExcelHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "CurriculumServlet", urlPatterns = {"/curriculum/*"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class CurriculumServlet extends HttpServlet {

    private final CurriculumDAO curriculumDAO = new CurriculumDAO();
    private final MajorDAO      majorDAO      = new MajorDAO();
    private final SubjectDAO    subjectDAO    = new SubjectDAO();
    private final ReviewDAO     reviewDAO     = new ReviewDAO();
    private final PloDAO        ploDAO        = new PloDAO();
    private final PoDAO         poDAO         = new PoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";

        switch (pathInfo) {
            case "/list":
                showList(req, res);
                break;
            case "/detail":
                showDetail(req, res);
                break;
            case "/create":
                showCreate(req, res);
                break;
            case "/edit":
                showEdit(req, res);
                break;
            case "/po":
                showPO(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/curriculum/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        String action = req.getParameter("action");
        if (action == null)
            action = "";

        if ("/importExcel".equals(pathInfo)) {
            doImportExcel(req, res);
            return;
        }

        switch (action) {
            case "create":
                doCreate(req, res);
                break;
            case "update":
                doUpdate(req, res);
                break;
            case "submit":
                doSubmit(req, res);
                break;
            case "approve":
                doApprove(req, res);
                break;
            case "reject":
                doReject(req, res);
                break;
            case "addPo":
                doAddPo(req, res);
                break;
            case "deletePo":
                doDeletePo(req, res);
                break;
            case "addPlo":
                doAddPlo(req, res);
                break;
            case "deletePlo":
                doDeletePlo(req, res);
                break;
            case "saveMapping":
                doSaveMapping(req, res);
                break;
            case "addSubject":
                doAddSubject(req, res);
                break;
            case "removeSubject":
                doRemoveSubject(req, res);
                break;
            default:
                res.sendRedirect(req.getContextPath() + "/curriculum/list");
        }
    }

    // ===== GET handlers =====

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = getLoggedUser(req);
        String keyword = req.getParameter("keyword");
        String status  = req.getParameter("status"); // Đọc tham số bộ lọc Status từ giao diện (0, 2, 1)
        String majorId = req.getParameter("majorId"); // Đọc tham số bộ lọc Program/Major

        boolean publicOnly = (user == null || isPublicRole(user));

        // Gọi hàm tìm kiếm truyền bộ lọc status và majorId sang cho DAO
        List<Curriculum> list = curriculumDAO.searchCurriculums(keyword, status, majorId, publicOnly);
        req.setAttribute("curriculums", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("selectedMajorId", majorId);
        req.setAttribute("majors", majorDAO.getAllMajors());
        req.setAttribute("totalCount", list.size());
        forward(req, res, "/WEB-INF/views/curriculum/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Curriculum c = curriculumDAO.getCurriculumById(id);
        if (c == null) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }
        req.setAttribute("canEdit", isAllowedToEdit(getLoggedUser(req), id));
        List<CurriculumSubject> subjectsInCurriculum = subjectDAO.getSubjectsByCurriculum(id);

        req.setAttribute("curriculum", c);
        req.setAttribute("subjects", subjectsInCurriculum);
        req.setAttribute("reviews", reviewDAO.getReviewsByCurriculum(id));
        req.setAttribute("plos", ploDAO.getPLOsByCurriculum(id));
        req.setAttribute("pos", poDAO.getPOsByCurriculum(id));
        req.setAttribute("availableSubjects", getAvailableSubjects(subjectsInCurriculum));
        forward(req, res, "/WEB-INF/views/curriculum/detail.jsp");
    }

    private void showPO(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Curriculum c = curriculumDAO.getCurriculumById(id);
        if (c == null) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }
        req.setAttribute("canEdit", isAllowedToEdit(getLoggedUser(req), id));
        req.setAttribute("curriculum", c);
        req.setAttribute("pos", poDAO.getPOsByCurriculum(id));
        req.setAttribute("plos", ploDAO.getPLOsByCurriculum(id));
        req.setAttribute("mappings", poDAO.getPoPloMappings(id));
        forward(req, res, "/WEB-INF/views/curriculum/po.jsp");
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin")) return;
        req.setAttribute("majors", majorDAO.getAllMajors());
        req.setAttribute("isEdit", false);
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String id = req.getParameter("id");
        if (!checkEditPermission(req, res, id)) return;
        req.setAttribute("curriculum", curriculumDAO.getCurriculumById(id));
        req.setAttribute("majors",   majorDAO.getAllMajors());
        req.setAttribute("isEdit", true);
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    // ===== POST handlers =====

    private void doImportExcel(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;

        try {
            Part filePart = req.getPart("excelFile"); 
            if (filePart != null && filePart.getSize() > 0) {
                InputStream fileContent = filePart.getInputStream();

                Curriculum importedData = ExcelHelper.parseCurriculumExcel(fileContent);
                
                // ĐỒNG BỘ: Mặc định dữ liệu vừa bóc tách từ Excel nhận trạng thái tiến trình là 0 (Draft)
                importedData.setStatus(0);
                // Mở kích hoạt Is_Active hiển thị hệ thống mặc định
                importedData.setIsActive(false);

                req.setAttribute("curriculum", importedData);
                req.setAttribute("successMessage",
                        "Imported successfully from Excel! Please verify data and choose Major before saving.");
            } else {
                req.setAttribute("errorMessage", "Please select a valid Excel file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error parsing Excel file: " + e.getMessage());
        }
        req.setAttribute("majors", majorDAO.getAllMajors());
        req.setAttribute("isEdit", false);
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    private void doCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        User user = getLoggedUser(req);
        Curriculum c = buildFromRequest(req);
        c.setCreatedBy(user.getUserId());
        
        // Gọi hàm addCurriculum, DB tự gán Status = 0 (Nhờ Default Constraint) và Is_Active = 1
        curriculumDAO.addCurriculum(c);
        res.sendRedirect(req.getContextPath() + "/curriculum/list?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        Curriculum c = buildFromRequest(req);
        c.setCurriculumId(req.getParameter("curriculumId"));
        curriculumDAO.updateCurriculum(c);
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + c.getCurriculumId() + "&msg=updated");
    }

    private void doSubmit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String id = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, id)) return;
        
        // ĐỒNG BỘ: Sử dụng hàm phê duyệt luồng tiến trình `submitForReview` (Chuyển Status thành 2)
        curriculumDAO.submitForReview(id);
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + id + "&msg=submitted");
    }

    private void doApprove(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Reviewer", "Admin"))
            return;
        User user = getLoggedUser(req);
        String id = req.getParameter("curriculumId");
        String comment = req.getParameter("comment");
        
        // ĐỒNG BỘ: Sử dụng hàm phê duyệt `approveCurriculum` (Chuyển Status thành 1)
        curriculumDAO.approveCurriculum(id);
        reviewDAO.addReview(id, user.getUserId(), "Approved", comment);
        res.sendRedirect(req.getContextPath() + "/curriculum/list?msg=approved");
    }

    private void doReject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Reviewer", "Admin"))
            return;
        User user = getLoggedUser(req);
        String id = req.getParameter("curriculumId");
        String comment = req.getParameter("comment");
        
        // ĐỒNG BỘ: Sử dụng hàm từ chối duyệt `rejectCurriculum` (Đẩy Status quay về 0)
        curriculumDAO.rejectCurriculum(id);
        reviewDAO.addReview(id, user.getUserId(), "Rejected", comment);
        res.sendRedirect(req.getContextPath() + "/curriculum/list?msg=rejected");
    }

    // ===== PO / PLO / Mapping handlers =====

    private void doAddPo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        String poCode = req.getParameter("poCode");
        String description = req.getParameter("description");
        poDAO.addPO(curriculumId, poCode, description);
        res.sendRedirect(req.getContextPath() + "/curriculum/po?id=" + curriculumId + "&msg=poAdded");
    }

    private void doDeletePo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        String poId = req.getParameter("poId");
        poDAO.deletePO(poId);
        res.sendRedirect(req.getContextPath() + "/curriculum/po?id=" + curriculumId + "&msg=poDeleted");
    }

    private void doAddPlo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        String ploCode = req.getParameter("ploCode");
        String description = req.getParameter("description");
        ploDAO.addPLO(curriculumId, ploCode, description);
        res.sendRedirect(req.getContextPath() + "/curriculum/po?id=" + curriculumId + "&msg=ploAdded");
    }

    private void doDeletePlo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        String ploId = req.getParameter("ploId");
        ploDAO.deletePLO(ploId);
        res.sendRedirect(req.getContextPath() + "/curriculum/po?id=" + curriculumId + "&msg=ploDeleted");
    }

    private void doSaveMapping(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        String[] checkedKeys = req.getParameterValues("mapKey"); // mỗi checkbox value="POID_PLOID"
        poDAO.saveMappings(curriculumId, checkedKeys);
        res.sendRedirect(req.getContextPath() + "/curriculum/po?id=" + curriculumId + "&msg=mappingSaved");
    }

    /**
     * Designer them 1 Subject vao Curriculum dang design (chi cho phep khi
     * curriculum con o trang thai Draft - Status = 0).
     */
    private void doAddSubject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");

        Curriculum c = curriculumDAO.getCurriculumById(curriculumId);
        if (c == null) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }
        if (c.getStatus() != 0) {
            // Curriculum da Submit/Approved - khong cho sua noi dung mon hoc nua
            res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + curriculumId + "&msg=lockedForEdit");
            return;
        }

        String subjectId = req.getParameter("subjectId");
        int semesterNo = 1;
        try { semesterNo = Integer.parseInt(req.getParameter("semesterNo")); } catch (Exception ignored) {}
        boolean isMandatory = req.getParameter("isMandatory") != null;

        boolean ok = (subjectId != null && !subjectId.isEmpty())
                && subjectDAO.addSubjectToCurriculum(curriculumId, subjectId, semesterNo, isMandatory);

        String msg = ok ? "subjectAdded" : "subjectAddFailed";
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + curriculumId + "&msg=" + msg);
    }

    /**
     * Designer go 1 Subject khoi Curriculum dang design (chi cho phep khi
     * curriculum con o trang thai Draft - Status = 0).
     */
    private void doRemoveSubject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Designer", "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");

        Curriculum c = curriculumDAO.getCurriculumById(curriculumId);
        if (c == null) {
            res.sendRedirect(req.getContextPath() + "/curriculum/list");
            return;
        }
        if (c.getStatus() != 0) {
            res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + curriculumId + "&msg=lockedForEdit");
            return;
        }

        String curriculumSubjectId = req.getParameter("curriculumSubjectId");
        subjectDAO.removeSubjectFromCurriculum(curriculumSubjectId);
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + curriculumId + "&msg=subjectRemoved");
    }

    /**
     * Danh sach Subject (dang Active) chua duoc gan vao curriculum nay -
     * dung de do vao dropdown "Add Subject".
     */
    private List<Subject> getAvailableSubjects(List<CurriculumSubject> subjectsInCurriculum) {
        Set<String> usedIds = new HashSet<>();
        for (CurriculumSubject cs : subjectsInCurriculum) {
            usedIds.add(cs.getSubjectId());
        }
        List<Subject> all = subjectDAO.searchSubjects(null, null, null);
        List<Subject> available = new ArrayList<>();
        for (Subject s : all) {
            if (!usedIds.contains(s.getSubjectId())) {
                available.add(s);
            }
        }
        return available;
    }

    // ===== Helpers =====

    private Curriculum buildFromRequest(HttpServletRequest req) {
        Curriculum c = new Curriculum();
        c.setMajorId(req.getParameter("majorId"));
        c.setCurriculumCode(req.getParameter("curriculumCode"));
        c.setCurriculumName(req.getParameter("curriculumName"));
        c.setEnglishName(req.getParameter("englishName"));
        c.setDescription(req.getParameter("description"));
        try {
            c.setTotalCredits(Integer.parseInt(req.getParameter("totalCredits")));
        } catch (Exception ignored) {
        }
        c.setVersion(req.getParameter("version"));
        c.setDecisionNo(req.getParameter("decisionNo"));
        try {
            String dateParam = req.getParameter("decisionDate");
            if (dateParam != null && !dateParam.isEmpty()) {
                c.setDecisionDate(java.sql.Date.valueOf(dateParam));
            }
        } catch (Exception ignored) {}
        return c;
    }

    private User getLoggedUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? (User) s.getAttribute("loggedUser") : null;
    }

    private boolean isPublicRole(User u) {
        String role = "";
        if (u != null && u.getRole() != null) {
            role = u.getRole().toString();
        }
        return role.equals("Guest") || role.equals("Student");
    }

    private boolean requireRole(HttpServletRequest req, HttpServletResponse res, String... roles)
            throws IOException {
        User user = getLoggedUser(req);
        if (user == null) { res.sendRedirect(req.getContextPath() + "/login"); return false; }
        
        String userRole = "";
        if (user.getRole() != null) {
            userRole = user.getRole().toString();
        }
        
//        for (String r : roles) if (r.equals(userRole)) return true;
       for (String r : roles) {
            if (r.equalsIgnoreCase(userRole)) return true; 
            if (user.hasRole(r)) return true;
            if ("Designer".equalsIgnoreCase(r) && (user.isDesigner() || user.hasRole("Designer"))) return true;
            if ("Reviewer".equalsIgnoreCase(r) && (user.isReviewer() || user.hasRole("Reviewer"))) return true;
        }
        res.sendRedirect(req.getContextPath() + "/curriculum/list");
        return false;
    }

    private void forward(HttpServletRequest req, HttpServletResponse res, String path)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, res);
    }

    private void doAssign(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Chỉ Admin mới có quyền phân công
        if (!requireRole(req, res, "Admin")) {
            return;
        }

        String curriculumId = req.getParameter("curriculumId");
        String designerId = req.getParameter("designerId");
        String reviewerId = req.getParameter("reviewerId");
        User admin = getLoggedUser(req);

        curriculumDAO.assignCurriculumRoles(curriculumId, designerId, reviewerId, admin.getUserId());
        res.sendRedirect(req.getContextPath() + "/curriculum/list?msg=assigned");
    }
    
    // Kiểm tra logic nội bộ xem User có quyền Edit Curriculum này không
    private boolean isAllowedToEdit(User user, String curriculumId) {
        if (user == null) return false;
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "";
        
        // Admin thì luôn được sửa mọi thứ
        boolean isAdmin = "Admin".equalsIgnoreCase(roleName) || user.hasRole("Admin");
        if (isAdmin) return true;
        
        // Designer thì PHẢI được Assign mới được sửa
        boolean isDesigner = "Designer".equalsIgnoreCase(roleName) || user.hasRole("Designer") || user.isDesigner();
        if (isDesigner) {
            return curriculumDAO.checkAssignment(curriculumId, user.getUserId(), "Designer");
        }
        return false;
    }

    // Hàm gọi để chặn hoặc đá văng ra ngoài nếu cố tình vi phạm
    private boolean checkEditPermission(HttpServletRequest req, HttpServletResponse res, String curriculumId) throws IOException {
        User user = getLoggedUser(req);
        if (isAllowedToEdit(user, curriculumId)) {
            return true;
        }
        res.sendRedirect(req.getContextPath() + "/curriculum/list");
        return false;
    }
}

