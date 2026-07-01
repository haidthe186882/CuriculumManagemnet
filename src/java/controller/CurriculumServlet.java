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
import util.ExcelHelper.ImportResult;
import util.ExcelHelper.PloRow;
import util.ExcelHelper.SubjectRow;

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
    private final MajorDAO majorDAO = new MajorDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final dao.UserDAO userDAO = new dao.UserDAO();//

    private final PloDAO ploDAO = new PloDAO();
    private final PoDAO poDAO = new PoDAO();

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
        req.setAttribute("designers", userDAO.getUsersByRole("Designer"));
        req.setAttribute("reviewers", userDAO.getUsersByRole("Reviewer"));
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
        req.setAttribute("mappings", poDAO.getPoPloMappings(id));
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
        // Chỉ Admin được tạo Curriculum mới — Designer chỉ thiết kế Subject.
        if (!requireRole(req, res, "Admin")) return;
        req.setAttribute("majors", majorDAO.getAllMajors());
        req.setAttribute("isEdit", false);
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!requireRole(req, res, "Admin"))
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

        String curriculumId = req.getParameter("curriculumId");
        boolean editMode = curriculumId != null && !curriculumId.trim().isEmpty();

        // Chỉ Admin được tạo/sửa Curriculum bằng Excel — Designer chỉ thiết kế Subject.
        if (!requireRole(req, res, "Admin")) return;

        Curriculum baseCurriculum = null;
        if (editMode) {
            if (!checkEditPermission(req, res, curriculumId)) return;
            baseCurriculum = curriculumDAO.getCurriculumById(curriculumId);
            if (baseCurriculum == null) {
                res.sendRedirect(req.getContextPath() + "/curriculum/list");
                return;
            }
        }

        try {
            Part filePart = req.getPart("excelFile");
            if (filePart != null && filePart.getSize() > 0) {
                InputStream fileContent = filePart.getInputStream();

                // Parse all 3 sheets at once
                ImportResult result = ExcelHelper.parseFullExcel(fileContent);
                Curriculum importedData = result.curriculum;

                if (editMode) {
                    // Edit mode: keep Code, Major, Status, IsActive — refresh content fields only
                    baseCurriculum.setCurriculumName(importedData.getCurriculumName());
                    baseCurriculum.setEnglishName(importedData.getEnglishName());
                    baseCurriculum.setDescription(importedData.getDescription());
                    baseCurriculum.setDecisionNo(importedData.getDecisionNo());
                    baseCurriculum.setDecisionDate(importedData.getDecisionDate());
                    baseCurriculum.setTotalCredits(importedData.getTotalCredits());
                    if (importedData.getVersion() != null && !importedData.getVersion().isEmpty()) {
                        baseCurriculum.setVersion(importedData.getVersion());
                    }

                    // Re-import PLOs if any found in the file
                    if (!result.plos.isEmpty()) {
                        ploDAO.deletePLOsByCurriculum(curriculumId);
                        for (PloRow pr : result.plos) {
                            ploDAO.addPLO(curriculumId, pr.ploCode, pr.description);
                        }
                    }

                    // Re-import POs if any found in the file
                    if (!result.pos.isEmpty()) {
                        poDAO.deletePOsByCurriculum(curriculumId);
                        for (util.ExcelHelper.PoRow pr : result.pos) {
                            poDAO.addPO(curriculumId, pr.poCode, pr.description);
                        }
                    }

                    // Re-import PO-PLO mapping matrix if any found in the file
                    if (!result.mappingPairs.isEmpty()) {
                        poDAO.addMappingsByCode(curriculumId, result.mappingPairs);
                    }

                    // Re-import subjects if any found in the file
                    if (!result.subjects.isEmpty()) {
                        subjectDAO.removeAllSubjectsFromCurriculum(curriculumId);
                        for (SubjectRow sr : result.subjects) {
                            String sid = subjectDAO.findSubjectIdByCodeAny(sr.subjectCode);
                            if (sid != null) {
                                subjectDAO.addSubjectToCurriculum(curriculumId, sid, sr.semesterNo, true);
                            }
                        }
                    }

                    req.setAttribute("curriculum", baseCurriculum);
                    int ploCount = result.plos.size();
                    int subjCount = result.subjects.isEmpty() ? 0 :
                            (int) result.subjects.stream().filter(s -> subjectDAO.findSubjectIdByCodeAny(s.subjectCode) != null).count();
                    req.setAttribute("successMessage",
                            "Re-imported from Excel: " + ploCount + " PLO(s), " + result.pos.size() + " PO(s) and "
                            + subjCount + " subject(s) refreshed. Verify then save.");
                } else {
                    // Create mode: new curriculum from Excel
                    importedData.setStatus(0);
                    importedData.setIsActive(false);
                    // Stash PLO, PO, Mapping & subject rows in session so they can be saved after Create
                    req.getSession().setAttribute("pendingImportPlos", result.plos);
                    req.getSession().setAttribute("pendingImportPos", result.pos);
                    req.getSession().setAttribute("pendingImportMappings", result.mappingPairs);
                    req.getSession().setAttribute("pendingImportSubjects", result.subjects);
                    req.setAttribute("curriculum", importedData);
                    req.setAttribute("successMessage",
                            "Imported from Excel: " + result.plos.size() + " PLO(s), " + result.pos.size() + " PO(s) and "
                            + result.subjects.size() + " subject(s) detected. Choose Major and click Create to save.");
                }
            } else {
                req.setAttribute("errorMessage", "Please select a valid Excel file.");
                if (editMode) req.setAttribute("curriculum", baseCurriculum);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error parsing Excel file: " + e.getMessage());
            if (editMode) req.setAttribute("curriculum", baseCurriculum);
        }

        req.setAttribute("majors", majorDAO.getAllMajors());
        req.setAttribute("isEdit", editMode);
        forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
    }

    @SuppressWarnings("unchecked")
    private void doCreate(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        // Chỉ Admin được tạo Curriculum mới — Designer chỉ thiết kế Subject.
        if (!requireRole(req, res, "Admin"))
            return;
        User user = getLoggedUser(req);
        Curriculum c = buildFromRequest(req);
        c.setCreatedBy(user.getUserId());

        if (c.getCurriculumCode() == null || c.getCurriculumCode().trim().isEmpty()
                || curriculumDAO.checkCurriculumCodeExists(c.getCurriculumCode().trim())) {
            req.setAttribute("errorMessage", "Could not create curriculum. The Curriculum Code \""
                    + c.getCurriculumCode() + "\" is empty or already exists, please choose another one.");
            req.setAttribute("curriculum", c);
            req.setAttribute("majors", majorDAO.getAllMajors());
            req.setAttribute("isEdit", false);
            forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
            return;
        }

        String newId = curriculumDAO.addCurriculumAndReturnId(c);

        if (newId == null) {
            // Thực sự lưu thất bại (lỗi DB/ràng buộc) — báo lỗi rõ ràng, KHÔNG redirect "created" giả.
            req.setAttribute("errorMessage", "Could not create curriculum. Please check the required fields and try again.");
            req.setAttribute("curriculum", c);
            req.setAttribute("majors", majorDAO.getAllMajors());
            req.setAttribute("isEdit", false);
            forward(req, res, "/WEB-INF/views/curriculum/form.jsp");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null) {
            List<PloRow> pendingPlos = (List<PloRow>) session.getAttribute("pendingImportPlos");
            List<util.ExcelHelper.PoRow> pendingPos = (List<util.ExcelHelper.PoRow>) session.getAttribute("pendingImportPos");
            List<String[]> pendingMappings = (List<String[]>) session.getAttribute("pendingImportMappings");
            List<SubjectRow> pendingSubjects = (List<SubjectRow>) session.getAttribute("pendingImportSubjects");
            if (pendingPlos != null) {
                for (PloRow pr : pendingPlos) {
                    ploDAO.addPLO(newId, pr.ploCode, pr.description);
                }
                session.removeAttribute("pendingImportPlos");
            }
            if (pendingPos != null) {
                for (util.ExcelHelper.PoRow pr : pendingPos) {
                    poDAO.addPO(newId, pr.poCode, pr.description);
                }
                session.removeAttribute("pendingImportPos");
            }
            if (pendingMappings != null) {
                poDAO.addMappingsByCode(newId, pendingMappings);
                session.removeAttribute("pendingImportMappings");
            }
            if (pendingSubjects != null) {
                for (SubjectRow sr : pendingSubjects) {
                    String sid = subjectDAO.findSubjectIdByCodeAny(sr.subjectCode);
                    if (sid != null) {
                        subjectDAO.addSubjectToCurriculum(newId, sid, sr.semesterNo, true);
                    }
                }
                session.removeAttribute("pendingImportSubjects");
            }
        }

        // Admin tạo Curriculum thì duyệt và kích hoạt luôn, không cần qua vòng Review.
        curriculumDAO.approveCurriculum(newId);
        curriculumDAO.toggleActive(newId, true);

        res.sendRedirect(req.getContextPath() + "/curriculum/list?msg=created");
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
            return;
        
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        Curriculum c = buildFromRequest(req);
        c.setCurriculumId(req.getParameter("curriculumId"));
        curriculumDAO.updateCurriculum(c);
        res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + c.getCurriculumId() + "&msg=updated");
    }

    private void doSubmit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
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
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        String poCode = req.getParameter("poCode");
        String description = req.getParameter("description");
        poDAO.addPO(curriculumId, poCode, description);
        res.sendRedirect(returnUrl(req, curriculumId, "poAdded"));
    }

    private void doDeletePo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        String poId = req.getParameter("poId");
        poDAO.deletePO(poId);
        res.sendRedirect(returnUrl(req, curriculumId, "poDeleted"));
    }

    private void doAddPlo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        String ploCode = req.getParameter("ploCode");
        String description = req.getParameter("description");
        ploDAO.addPLO(curriculumId, ploCode, description);
        res.sendRedirect(returnUrl(req, curriculumId, "ploAdded"));
    }

    private void doDeletePlo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        String ploId = req.getParameter("ploId");
        ploDAO.deletePLO(ploId);
        res.sendRedirect(returnUrl(req, curriculumId, "ploDeleted"));
    }

    private void doSaveMapping(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;
        String[] checkedKeys = req.getParameterValues("mapKey"); // mỗi checkbox value="POID_PLOID"
        poDAO.saveMappings(curriculumId, checkedKeys);
        res.sendRedirect(returnUrl(req, curriculumId, "mappingSaved"));
    }

    /**
     * Cac form PO/PLO/Mapping co the duoc submit tu trang /curriculum/po
     * hoac tu trang /curriculum/detail (embed san bang PO/PLO/Mapping).
     * Dung hidden field "returnTo" de biet quay lai trang nao sau khi luu.
     */
    private String returnUrl(HttpServletRequest req, String curriculumId, String msg) {
        String returnTo = req.getParameter("returnTo");
        String path = "detail".equals(returnTo) ? "/curriculum/detail" : "/curriculum/po";
        return req.getContextPath() + path + "?id=" + curriculumId + "&msg=" + msg;
    }

    /**
     * Designer them 1 Subject vao Curriculum dang design (chi cho phep khi
     * curriculum con o trang thai Draft - Status = 0).
     */
    private void doAddSubject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;

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
        if (!requireRole(req, res, "Admin"))
            return;
        String curriculumId = req.getParameter("curriculumId");
        if (!checkEditPermission(req, res, curriculumId)) return;

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

        // Chỉ Admin được sửa cấu trúc Curriculum (PO/PLO/Subject-linking/Submit).
        // Designer giờ chỉ thiết kế Subject (qua trang /subject), không sửa Curriculum nữa.
        return "Admin".equalsIgnoreCase(roleName) || user.hasRole("Admin");
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