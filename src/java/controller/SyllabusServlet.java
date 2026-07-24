package controller;

import dao.SyllabusDAO;
import dao.SubjectDAO;
import dao.DesignDAO;
import dao.CloDAO;
import dao.SessionDAO;
import model.CourseLearningOutcome;
import model.Session;
import model.Syllabus;
import model.SyllabusMaterial;
import model.User;
import util.SyllabusExcelHelper;
import util.SyllabusExcelHelper.SyllabusImportData;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "SyllabusServlet", urlPatterns = {"/syllabus/*"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize       = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize    = 1024 * 1024 * 15   // 15 MB
)
public class SyllabusServlet extends HttpServlet {

    private final SyllabusDAO syllabusDAO = new SyllabusDAO();
    private final SubjectDAO  subjectDAO  = new SubjectDAO();
    private final DesignDAO   designDAO   = new DesignDAO();
    private final CloDAO      cloDAO      = new CloDAO();
    private final SessionDAO  sessionDAO  = new SessionDAO();
    private final dao.CurriculumDAO curriculumDAO = new dao.CurriculumDAO();
    private final dao.PloDAO  ploDAO      = new dao.PloDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":   showList(req, res);   break;
            case "/detail": showDetail(req, res); break;
            case "/clo-mapping": showCloMapping(req, res); break;
            case "/create": showCreate(req, res); break;
            case "/download": downloadSyllabus(req, res); break;
            default: res.sendRedirect(req.getContextPath() + "/syllabus/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // Check both pathInfo and a fallback parameter for multipart requests
        boolean isImport = "/importExcel".equals(pathInfo)
                || "importExcel".equals(req.getParameter("importAction"));

        if (isImport) {
            handleImportExcel(req, res);
            return;
        }

        // Handle file upload (AJAX)
        if ("/uploadFile".equals(pathInfo)) {
            handleUploadFile(req, res);
            return;
        }

        // Handle create action
        String action = req.getParameter("action");
        if (!"create".equals(action)) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }
        if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Build Syllabus object
        Syllabus s = new Syllabus();
        
        String subjectCode = req.getParameter("subjectCode");
        if (subjectCode == null || subjectCode.trim().isEmpty()) {
            req.setAttribute("error", "Syllabus Code is required. Please enter a valid code (e.g. SWT301).");
            req.setAttribute("subjects", subjectDAO.searchSubjects(null, null, null));
            req.getRequestDispatcher("/WEB-INF/views/syllabus/form.jsp").forward(req, res);
            return;
        }
        subjectCode = subjectCode.trim();

        // Neu form duoc mo tu 1 link co san subjectCode (fix cung, vd tu nut "Add
        // Syllabus"), thi subjectCode nguoi dung/gui len (ke ca tu file Excel import)
        // BAT BUOC phai trung voi ma mon da fix cung do. Khac di -> tu choi luon,
        // khong cho import/luu, tranh ghi nham noi dung sang mot Subject khac.
        String lockedSubjectCode = req.getParameter("lockedSubjectCode");
        if (lockedSubjectCode != null && !lockedSubjectCode.trim().isEmpty()
                && !lockedSubjectCode.trim().equalsIgnoreCase(subjectCode)) {
            req.setAttribute("error", "Subject code mismatch: this form is locked to '" + safeStr(lockedSubjectCode.trim())
                    + "', but the submitted/Excel-imported subject code is '" + safeStr(subjectCode)
                    + "'. Please use a syllabus/Excel file that matches the correct subject.");
            req.setAttribute("subjects", subjectDAO.searchSubjects(null, null, null));
            req.setAttribute("prefillSubjectCode", lockedSubjectCode.trim());
            req.setAttribute("lockedSubjectCode", lockedSubjectCode.trim());
            req.getRequestDispatcher("/WEB-INF/views/syllabus/form.jsp").forward(req, res);
            return;
        }

        String subjectId = subjectDAO.findSubjectIdByCodeAny(subjectCode);
        if (subjectId == null) {
            req.setAttribute("error", "Subject code '" + safeStr(subjectCode) + "' does not exist in the system. Please create the subject first or check the code.");
            req.setAttribute("subjects", subjectDAO.searchSubjects(null, null, null));
            req.getRequestDispatcher("/WEB-INF/views/syllabus/form.jsp").forward(req, res);
            return;
        }

        // ===== Che do "CHI LUU MAPPING" cho Syllabus da Approved =====
        // Khi 1 Subject dung chung Syllabus da Approved boi 1 Curriculum khac (tai su
        // dung theo Subject_Code) nhung CHUA co mapping CLO-PLO rieng cho Curriculum
        // dang xet, showCreate() cho mo form o che do nay (xem "mappingOnlyMode").
        // Nhanh xu ly TACH RIENG hoan toan: KHONG dung toi Syllabus/CLO/Session/
        // Material da duyet, CHI ghi/ghi de mapping cho DUNG 1 Curriculum duoc chi dinh.
        String mappingOnlySave = req.getParameter("mappingOnlySave");
        if ("true".equals(mappingOnlySave)) {
            String mappingCurriculumId = req.getParameter("mappingOnlyCurriculumId");
            Syllabus existingForMapping = syllabusDAO.getSyllabusBySubject(subjectId);
            if (existingForMapping == null || mappingCurriculumId == null || mappingCurriculumId.trim().isEmpty()) {
                res.sendRedirect(req.getContextPath() + "/subject/detail?id=" + subjectId
                        + "&error=" + java.net.URLEncoder.encode("Unable to save mapping: missing syllabus or curriculum context.", "UTF-8"));
                return;
            }
            mappingCurriculumId = mappingCurriculumId.trim();
            String mSyllabusId = existingForMapping.getSyllabusId();

            // Khop theo CLO_Code voi CLO HIEN CO (khong xoa/tao lai CLO trong che do nay)
            java.util.Map<String, String> cloCodeToId = new java.util.HashMap<>();
            for (CourseLearningOutcome clo : cloDAO.getCLOsBySyllabus(mSyllabusId)) {
                cloCodeToId.put(clo.getCloCode(), clo.getCloId());
            }

            // Chi xoa mapping cua DUNG Curriculum nay, khong dung tay vao Curriculum khac
            ploDAO.deleteMappingsBySyllabusAndCurriculum(mSyllabusId, mappingCurriculumId);

            for (model.ProgramLearningOutcome plo : ploDAO.getPLOsByCurriculum(mappingCurriculumId)) {
                String[] checkedCodes = req.getParameterValues("mapping." + plo.getPloId());
                if (checkedCodes == null) continue;
                for (String code : checkedCodes) {
                    String cloId = cloCodeToId.get(code == null ? null : code.trim());
                    if (cloId != null) ploDAO.addMapping(cloId, plo.getPloId());
                }
            }

            res.sendRedirect(req.getContextPath() + "/curriculum/detail?id=" + mappingCurriculumId
                    + "&success=" + java.net.URLEncoder.encode("CLO-PLO mapping saved for this curriculum.", "UTF-8"));
            return;
        }

        // Chan sua khi Syllabus da Submit for Review hoac da Approved (phai qua
        // Reject cua Reviewer de dua ve Draft truoc thi moi sua tiep duoc).
        Syllabus existingCheck = syllabusDAO.getSyllabusBySubject(subjectId);
        if (existingCheck != null && existingCheck.getStatusCode() != Syllabus.STATUS_DRAFT) {
            String reason = existingCheck.getStatusCode() == Syllabus.STATUS_PENDING_REVIEW
                    ? "This syllabus has been submitted and is pending review. It cannot be edited until the Reviewer sends it back."
                    : "This syllabus has already been approved and is locked from further edits.";
            res.sendRedirect(req.getContextPath() + "/subject/detail?id=" + subjectId
                    + "&error=" + java.net.URLEncoder.encode(reason, "UTF-8"));
            return;
        }
        s.setSubjectId(subjectId);
        s.setSyllabusName(req.getParameter("syllabusName"));
        s.setEnglishName(req.getParameter("englishName"));
        s.setVersion(req.getParameter("version"));
        s.setDescription(req.getParameter("description"));
        s.setTimeAllocation(req.getParameter("timeAllocation"));
        s.setStudentTasks(req.getParameter("studentTasks"));
        s.setTools(req.getParameter("tools"));
        s.setScoringScale(req.getParameter("scoringScale"));
        s.setDecisionNo(req.getParameter("decisionNo"));
        try { s.setMinAvgMarkToPass(Double.parseDouble(req.getParameter("minAvgMarkToPass"))); } catch (Exception ignored) {}
        try {
            String dateStr = req.getParameter("approvedDate");
            if (dateStr != null && !dateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                s.setApprovedDate(new java.sql.Date(sdf.parse(dateStr).getTime()));
            }
        } catch (Exception ignored) {}

        // Insert syllabus and get the generated ID
        // Neu Subject nay DA CO san 1 Syllabus active (vi du: syllabus rong duoc tao
        // tu dong khi Admin import Excel cho Subject moi), thi UPDATE ngay tren
        // Syllabus_ID cu, KHONG insert dong moi -> tranh mo coi cac
        // Syllabus_Assignments/Reviews da duoc Admin gan cho Syllabus do.
        String existingSyllabusId = syllabusDAO.getActiveSyllabusIdBySubject(subjectId);
        String syllabusId;
        // Truoc khi xoa CLOs cu (se lam CLO_ID doi khac hoan toan), luu lai mapping
        // CLO-PLO CUA CAC CURRICULUM KHONG XUAT HIEN trong form dang submit (form
        // Create/Edit chi hien 1 Curriculum tai 1 thoi diem - xem showCreate).
        // Neu khong lam vay, luu Syllabus trong ngu canh Curriculum A se xoa mat
        // mapping cua Curriculum B dung chung Subject nay.
        List<String[]> preservedMappings = new java.util.ArrayList<>(); // {PLO_ID, CLO_Code}
        String[] formCurriculumIds = req.getParameterValues("mappingCurriculumIds[]");
        java.util.Set<String> formCurriculumIdSet = new java.util.HashSet<>();
        if (formCurriculumIds != null) {
            for (String cid : formCurriculumIds) if (cid != null) formCurriculumIdSet.add(cid.trim());
        }
        if (existingSyllabusId != null) {
            for (String[] row : ploDAO.getAllCloCodePloMappings(existingSyllabusId)) {
                String ploId = row[0], cloCode = row[1], curriculumId = row[2];
                if (curriculumId == null || !formCurriculumIdSet.contains(curriculumId)) {
                    preservedMappings.add(new String[]{ploId, cloCode});
                }
            }
        }
        if (existingSyllabusId != null) {
            syllabusId = existingSyllabusId;
            syllabusDAO.updateSyllabusContent(syllabusId, s);
            // Xoa noi dung cu de tranh trung lap khi Designer luu lai (form hien chua ho tro prefill)
            // Phai xoa PLO_CLO_Mappings TRUOC khi xoa CLOs (FK khong co ON DELETE CASCADE)
            ploDAO.deleteMappingsBySyllabus(syllabusId);
            cloDAO.deleteCLOsBySyllabus(syllabusId);
            sessionDAO.deleteSessionsBySyllabus(syllabusId);
            syllabusDAO.deleteMaterialsBySyllabus(syllabusId);
        } else {
            syllabusId = syllabusDAO.addSyllabusAndGetId(s);
        }

        if (syllabusId != null) {
            // Insert CLOs
            String[] cloCodes = req.getParameterValues("cloCode[]");
            String[] cloDescs = req.getParameterValues("cloDesc[]");
            java.util.Map<String, String> cloCodeToId = new java.util.HashMap<>();
            if (cloCodes != null && cloDescs != null) {
                for (int i = 0; i < cloCodes.length; i++) {
                    if (cloCodes[i] != null && !cloCodes[i].trim().isEmpty()) {
                        String code = cloCodes[i].trim();
                        CourseLearningOutcome clo = new CourseLearningOutcome();
                        clo.setSyllabusId(syllabusId);
                        clo.setCloCode(code);
                        clo.setDescription(i < cloDescs.length ? cloDescs[i].trim() : "");
                        if (cloDAO.addCLO(clo)) {
                            cloCodeToId.put(code, clo.getCloId());
                        }
                    }
                }
            }

            // Luu mapping CLO - PLO (tab "Mapping" tren form). Checkbox duoc dat ten
            // "mapping.<PLO_ID>" voi value la CLO_Code duoc tick, dua theo bo PLO cua
            // TAT CA Curriculum co dung Subject nay (subject co the dung chung nhieu
            // Curriculum, moi Curriculum co bo PLO rieng).
            if (!cloCodeToId.isEmpty()) {
                List<model.Curriculum> curriculumsForMapping = curriculumDAO.getCurriculumsBySubject(subjectId);
                for (model.Curriculum c : curriculumsForMapping) {
                    List<model.ProgramLearningOutcome> plos = ploDAO.getPLOsByCurriculum(c.getCurriculumId());
                    for (model.ProgramLearningOutcome plo : plos) {
                        String[] checkedCodes = req.getParameterValues("mapping." + plo.getPloId());
                        if (checkedCodes == null) continue;
                        for (String code : checkedCodes) {
                            String cloId = cloCodeToId.get(code == null ? null : code.trim());
                            if (cloId != null) ploDAO.addMapping(cloId, plo.getPloId());
                        }
                    }
                }
            }

            // Ghi lai mapping cua cac Curriculum KHONG co trong form nay (da luu tam
            // o preservedMappings phia tren, truoc khi CLOs cu bi xoa). Khop lai theo
            // CLO_Code voi CLO moi vua tao (gia dinh Designer khong doi CLO_Code giua
            // cac lan luu - day la cach duy nhat de "noi lai" vi CLO_ID da doi het).
            for (String[] pm : preservedMappings) {
                String ploId = pm[0], cloCode = pm[1];
                String newCloId = cloCodeToId.get(cloCode);
                if (newCloId != null) ploDAO.addMapping(newCloId, ploId);
            }

            // Insert Sessions
            String[] sessionNos = req.getParameterValues("sessionNo[]");
            String[] sessionTopics = req.getParameterValues("sessionTopic[]");
            String[] sessionTypes = req.getParameterValues("sessionType[]");
            String[] sessionLOs = req.getParameterValues("sessionLO[]");
            String[] sessionITUs = req.getParameterValues("sessionITU[]");
            String[] sessionMats = req.getParameterValues("sessionMaterial[]");
            String[] sessionTasks = req.getParameterValues("sessionTask[]");
            String[] sessionURLs = req.getParameterValues("sessionURL[]");

            if (sessionNos != null && sessionTopics != null) {
                for (int i = 0; i < sessionNos.length; i++) {
                    String topic = i < sessionTopics.length ? sessionTopics[i].trim() : "";
                    if (topic.isEmpty() && (sessionNos[i] == null || sessionNos[i].trim().isEmpty())) continue;

                    Session sess = new Session();
                    sess.setSyllabusId(syllabusId);
                    try { sess.setSessionNo(Integer.parseInt(sessionNos[i].trim())); }
                    catch (Exception e) { sess.setSessionNo(i + 1); }
                    sess.setTopic(topic);
                    sess.setLearningTeachingType(i < sessionTypes.length ? safeStr(sessionTypes[i]) : "");
                    sess.setLo(i < sessionLOs.length ? safeStr(sessionLOs[i]) : "");
                    sess.setItu(i < sessionITUs.length ? safeStr(sessionITUs[i]) : "");
                    sess.setStudentMaterials(i < sessionMats.length ? safeStr(sessionMats[i]) : "");
                    sess.setStudentTasks(i < sessionTasks.length ? safeStr(sessionTasks[i]) : "");
                    sess.setUrls(i < sessionURLs.length ? safeStr(sessionURLs[i]) : "");
                    sessionDAO.addSession(sess);
                }
            }

            // Insert Materials
            String[] matDescs = req.getParameterValues("matDesc[]");
            String[] matAuthors = req.getParameterValues("matAuthor[]");
            String[] matPublishers = req.getParameterValues("matPublisher[]");
            String[] matEditions = req.getParameterValues("matEdition[]");
            String[] matIsbns = req.getParameterValues("matIsbn[]");
            String[] matLinks = req.getParameterValues("matLink[]");
            String[] matNotes = req.getParameterValues("matNotes[]");
            String[] matMain = req.getParameterValues("matMain[]");

            if (matDescs != null) {
                for (int i = 0; i < matDescs.length; i++) {
                    if (matDescs[i] == null || matDescs[i].trim().isEmpty()) continue;

                    SyllabusMaterial m = new SyllabusMaterial();
                    m.setSyllabusId(syllabusId);
                    m.setMaterialDescription(matDescs[i].trim());
                    m.setAuthor(i < matAuthors.length ? safeStr(matAuthors[i]) : "");
                    m.setPublisher(i < matPublishers.length ? safeStr(matPublishers[i]) : "");
                    m.setEdition(i < matEditions.length ? safeStr(matEditions[i]) : "");
                    m.setIsbn(i < matIsbns.length ? safeStr(matIsbns[i]) : "");
                    m.setLink(i < matLinks.length ? safeStr(matLinks[i]) : "");
                    m.setNotes(i < matNotes.length ? safeStr(matNotes[i]) : "");
                    // Check if this material index is flagged as main
                    m.setMainMaterial(matMain != null && i < matMain.length && "on".equals(matMain[i]));
                    m.setOnline(m.getLink() != null && !m.getLink().isEmpty());
                    syllabusDAO.addMaterial(m);
                }
            }

            // Insert Documents (uploaded files from Documents tab)
            String[] docFilePaths = req.getParameterValues("docFilePath[]");
            String[] docOrigNames = req.getParameterValues("docOrigName[]");
            String[] docDescriptions = req.getParameterValues("docDescription[]");

            if (docFilePaths != null) {
                for (int i = 0; i < docFilePaths.length; i++) {
                    String fp = safeStr(docFilePaths[i]);
                    if (fp.isEmpty()) continue;

                    SyllabusMaterial m = new SyllabusMaterial();
                    m.setSyllabusId(syllabusId);
                    m.setFilePath(fp);
                    String origName = (docOrigNames != null && i < docOrigNames.length) ? safeStr(docOrigNames[i]) : "";
                    m.setOriginalFileName(origName);
                    m.setMaterialDescription(origName.isEmpty() ? "Uploaded Document" : origName);
                    if (docDescriptions != null && i < docDescriptions.length) {
                        String desc = safeStr(docDescriptions[i]);
                        if (!desc.isEmpty()) m.setMaterialDescription(desc);
                    }
                    m.setOnline(true);
                    syllabusDAO.addMaterial(m);
                }
            }
        }

        res.sendRedirect(req.getContextPath() + "/syllabus/list?msg=created");
    }
    /* ====== Upload File – AJAX endpoint returning JSON ====== */
    private void handleUploadFile(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();

        try {
            if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
                out.print("{\"error\":\"Access denied\"}");
                out.flush();
                return;
            }

            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                out.print("{\"error\":\"No file uploaded\"}");
                out.flush();
                return;
            }

            // Get original file name
            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            // Validate file extension
            String lowerName = originalName.toLowerCase();
            String[] allowedExts = {".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".txt", ".zip", ".rar"};
            boolean validExt = false;
            for (String ext : allowedExts) {
                if (lowerName.endsWith(ext)) { validExt = true; break; }
            }
            if (!validExt) {
                out.print("{\"error\":\"File type not supported. Allowed: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, TXT, ZIP, RAR\"}");
                out.flush();
                return;
            }

            // Validate file size (max 10MB)
            if (filePart.getSize() > 10 * 1024 * 1024) {
                out.print("{\"error\":\"File too large. Maximum size is 10MB.\"}");
                out.flush();
                return;
            }

            // Create upload directory
            String uploadDir = getServletContext().getRealPath("/uploads/documents");
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate unique filename: timestamp_uuid_originalname
            String safeFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8)
                    + "_" + originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path targetPath = Paths.get(uploadDir, safeFileName);

            // Save file
            try (InputStream is = filePart.getInputStream()) {
                Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return JSON response
            String filePath = "/uploads/documents/" + safeFileName;
            long fileSize = filePart.getSize();
            String sizeDisplay;
            if (fileSize < 1024) sizeDisplay = fileSize + " B";
            else if (fileSize < 1024 * 1024) sizeDisplay = String.format("%.1f KB", fileSize / 1024.0);
            else sizeDisplay = String.format("%.1f MB", fileSize / (1024.0 * 1024));

            out.print("{\"success\":true,\"filePath\":\"" + escapeJson(filePath)
                    + "\",\"originalName\":\"" + escapeJson(originalName)
                    + "\",\"fileSize\":" + fileSize
                    + ",\"fileSizeDisplay\":\"" + escapeJson(sizeDisplay) + "\"}");
            out.flush();
        } catch (Throwable e) {
            e.printStackTrace();
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            out.print("{\"error\":\"" + escapeJson(msg) + "\"}");
            out.flush();
        }
    }

    /* ====== Import Excel – AJAX endpoint returning JSON ====== */
    private void handleImportExcel(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();

        try {
            if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
                out.print("{\"error\":\"Access denied\"}");
                out.flush();
                return;
            }

            Part filePart = req.getPart("excelFile");
            if (filePart == null || filePart.getSize() == 0) {
                out.print("{\"error\":\"No file uploaded\"}");
                out.flush();
                return;
            }

            try (InputStream is = filePart.getInputStream()) {
                SyllabusImportData data = SyllabusExcelHelper.parseSyllabusExcel(is);
                String json = toJson(data);
                out.print(json);
                out.flush();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            out.print("{\"error\":\"" + escapeJson(msg) + "\"}");
            out.flush();
        }
    }

    /* ====== build JSON from import data ====== */
    private String toJson(SyllabusImportData data) {
        StringBuilder sb = new StringBuilder("{");
        Syllabus s = data.getSyllabus();

        sb.append("\"subjectCode\":").append(jsonStr(data.getSubjectCode())).append(",");
        sb.append("\"syllabusName\":").append(jsonStr(s.getSyllabusName())).append(",");
        sb.append("\"englishName\":").append(jsonStr(s.getEnglishName())).append(",");
        sb.append("\"version\":").append(jsonStr(s.getVersion())).append(",");
        sb.append("\"description\":").append(jsonStr(s.getDescription())).append(",");
        sb.append("\"timeAllocation\":").append(jsonStr(s.getTimeAllocation())).append(",");
        sb.append("\"studentTasks\":").append(jsonStr(s.getStudentTasks())).append(",");
        sb.append("\"tools\":").append(jsonStr(s.getTools())).append(",");
        sb.append("\"scoringScale\":").append(jsonStr(s.getScoringScale())).append(",");
        sb.append("\"minAvgMarkToPass\":").append(s.getMinAvgMarkToPass()).append(",");
        sb.append("\"decisionNo\":").append(jsonStr(s.getDecisionNo())).append(",");
        sb.append("\"approvedDate\":").append(jsonStr(
            s.getApprovedDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(s.getApprovedDate()) : "")).append(",");

        // CLOs
        sb.append("\"clos\":[");
        for (int i = 0; i < data.getClos().size(); i++) {
            if (i > 0) sb.append(",");
            CourseLearningOutcome clo = data.getClos().get(i);
            sb.append("{\"code\":").append(jsonStr(clo.getCloCode()))
              .append(",\"description\":").append(jsonStr(clo.getDescription())).append("}");
        }
        sb.append("],");

        // Sessions
        sb.append("\"sessions\":[");
        for (int i = 0; i < data.getSessions().size(); i++) {
            if (i > 0) sb.append(",");
            Session sess = data.getSessions().get(i);
            sb.append("{\"no\":").append(sess.getSessionNo())
              .append(",\"topic\":").append(jsonStr(sess.getTopic()))
              .append(",\"type\":").append(jsonStr(sess.getLearningTeachingType()))
              .append(",\"lo\":").append(jsonStr(sess.getLo()))
              .append(",\"itu\":").append(jsonStr(sess.getItu()))
              .append(",\"materials\":").append(jsonStr(sess.getStudentMaterials()))
              .append(",\"tasks\":").append(jsonStr(sess.getStudentTasks()))
              .append(",\"urls\":").append(jsonStr(sess.getUrls())).append("}");
        }
        sb.append("],");

        // Materials
        sb.append("\"materials\":[");
        for (int i = 0; i < data.getMaterials().size(); i++) {
            if (i > 0) sb.append(",");
            SyllabusMaterial m = data.getMaterials().get(i);
            sb.append("{\"description\":").append(jsonStr(m.getMaterialDescription()))
              .append(",\"author\":").append(jsonStr(m.getAuthor()))
              .append(",\"publisher\":").append(jsonStr(m.getPublisher()))
              .append(",\"edition\":").append(jsonStr(m.getEdition()))
              .append(",\"isbn\":").append(jsonStr(m.getIsbn()))
              .append(",\"isMain\":").append(m.isMainMaterial())
              .append(",\"isHardCopy\":").append(m.isHardCopy())
              .append(",\"isOnline\":").append(m.isOnline())
              .append(",\"link\":").append(jsonStr(m.getLink()))
              .append(",\"notes\":").append(jsonStr(m.getNotes())).append("}");
        }
        sb.append("],");

        // CLO-PLO Mapping (tu sheet "CLO-PLO Mapping" trong file Excel, dung de FE
        // tu dong tick san checkbox trong tab "Mapping")
        sb.append("\"cloPloMapping\":{");
        boolean firstEntry = true;
        for (java.util.Map.Entry<String, List<String>> e : data.getCloPloMapping().entrySet()) {
            if (!firstEntry) sb.append(",");
            firstEntry = false;
            sb.append(jsonStr(e.getKey())).append(":[");
            List<String> ploCodes = e.getValue();
            for (int i = 0; i < ploCodes.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(jsonStr(ploCodes.get(i)));
            }
            sb.append("]");
        }
        sb.append("}");

        sb.append("}");
        return sb.toString();
    }

    /* ====== existing handlers ====== */
    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String status  = req.getParameter("status");
        User user = getLoggedUser(req);
        boolean activeOnly = (user == null || hasRole(req, "Student", "Guest"));
        // Chi Admin duoc xem Syllabus con dang Draft/Pending Review (chua hoan thanh).
        // Tat ca role khac (Designer, Reviewer, Lecturer, Student, Guest...) trong man hinh
        // danh sach chung nay chi thay Syllabus da Approved. Designer/Reviewer van xem duoc
        // ban nhap cua rieng minh qua "My Assignments" / "Review" (khong bi anh huong).
        boolean isAdmin = hasRole(req, "Admin");
        boolean approvedOnly = !isAdmin;
        List<Syllabus> list = syllabusDAO.searchSyllabuses(keyword, status, activeOnly, approvedOnly);
        req.setAttribute("syllabuses", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher("/WEB-INF/views/syllabus/list.jsp").forward(req, res);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Syllabus s = syllabusDAO.getSyllabusById(id);
        if (s == null) { res.sendRedirect(req.getContextPath() + "/syllabus/list"); return; }
        if (!canViewSyllabus(req, s)) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }
        req.setAttribute("syllabus", s);
        req.setAttribute("clos", cloDAO.getCLOsBySyllabus(id));
        req.setAttribute("sessions", sessionDAO.getSessionsBySyllabus(id));
        req.setAttribute("downloadMaterials", syllabusDAO.getDownloadableMaterialsBySyllabusId(id));
        req.getRequestDispatcher("/WEB-INF/views/syllabus/detail.jsp").forward(req, res);
    }

    /**
     * Trang "View mapping of CLOs to PLOs": voi moi Curriculum co dung Subject
     * cua Syllabus nay, hien 1 bang mapping CLO x PLO rieng (PLO thuoc ve tung
     * Curriculum nen khong the gop chung 1 bang duy nhat).
     */
    private void showCloMapping(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Syllabus s = syllabusDAO.getSyllabusById(id);
        if (s == null) { res.sendRedirect(req.getContextPath() + "/syllabus/list"); return; }
        if (!canViewSyllabus(req, s)) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }

        List<model.CourseLearningOutcome> clos = cloDAO.getCLOsBySyllabus(id);
        List<model.Curriculum> curriculums = curriculumDAO.getCurriculumsBySubject(s.getSubjectId());
        List<model.CloPloMappingTable> mappingTables = new java.util.ArrayList<>();
        for (model.Curriculum c : curriculums) {
            List<model.ProgramLearningOutcome> plos = ploDAO.getPLOsByCurriculum(c.getCurriculumId());
            java.util.Set<String> checkedPairs = ploDAO.getCheckedCloPloPairs(id, c.getCurriculumId());

            java.util.Map<String, java.util.Map<String, Boolean>> matrix = new java.util.HashMap<>();
            for (model.CourseLearningOutcome clo : clos) {
                java.util.Map<String, Boolean> row = new java.util.HashMap<>();
                for (model.ProgramLearningOutcome plo : plos) {
                    row.put(plo.getPloId(), checkedPairs.contains(clo.getCloId() + "|" + plo.getPloId()));
                }
                matrix.put(clo.getCloId(), row);
            }

            model.CloPloMappingTable table = new model.CloPloMappingTable(
                    c.getCurriculumId(), c.getCurriculumCode(), c.getCurriculumName(), plos, checkedPairs);
            table.setMatrix(matrix);
            mappingTables.add(table);
        }

        req.setAttribute("syllabus", s);
        req.setAttribute("clos", clos);
        req.setAttribute("mappingTables", mappingTables);
        req.getRequestDispatcher("/WEB-INF/views/syllabus/clo-mapping.jsp").forward(req, res);
    }

    /**
     * Chi Admin, hoac chinh Designer/Reviewer duoc gan vao Syllabus nay, moi
     * duoc xem khi Syllabus chua Approved. Syllabus da Approved thi ai cung xem duoc.
     */
    private boolean canViewSyllabus(HttpServletRequest req, Syllabus s) {
        if (s.isApproved() || hasRole(req, "Admin")) return true;
        User user = getLoggedUser(req);
        return user != null && (
                designDAO.isAssignedToSyllabus(user.getUserId(), s.getSyllabusId(), "Designer") ||
                designDAO.isAssignedToSyllabus(user.getUserId(), s.getSyllabusId(), "Reviewer"));
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("subjects", subjectDAO.searchSubjects(null, null, null));
        String prefillCode = req.getParameter("subjectCode");
        if (prefillCode != null && !prefillCode.trim().isEmpty()) {
            prefillCode = prefillCode.trim();
            // Fix cung ma mon: khi vao trang qua link co san subjectCode (vd tu nut
            // "Add Syllabus" o trang Subject detail), khoa luon o Subject Code lai,
            // khong cho doi sang mon khac (kho ca tren UI lan khi file Excel import
            // co ma mon khac se bi tu choi - xem importExcel() o JS va check server-side ben duoi).
            req.setAttribute("prefillSubjectCode", prefillCode);
            req.setAttribute("lockedSubjectCode", prefillCode);

            String subjectId = subjectDAO.findSubjectIdByCodeAny(prefillCode);
            if (subjectId != null) {
                Syllabus existing = syllabusDAO.getSyllabusBySubject(subjectId);
                String curriculumIdParamForLockCheck = req.getParameter("curriculumId");
                boolean mappingOnlyMode = false;
                if (existing != null && existing.getStatusCode() != Syllabus.STATUS_DRAFT) {
                    // Truong hop dac biet: Syllabus DA APPROVED nhung dang duoc mo tu 1
                    // Curriculum khac (curriculumId tren URL) ma CHUA co mapping CLO-PLO
                    // rieng cho Curriculum do (Subject dung chung Syllabus nay giua nhieu
                    // Curriculum). Cho phep mo form o che do "CHI SUA MAPPING" thay vi khoa
                    // han - Designer khong duoc dung vao noi dung Syllabus da duyet, chi
                    // duoc tick mapping CLO-PLO cho dung Curriculum nay (xem doPost o tren,
                    // nhanh "mappingOnlySave").
                    if (existing.getStatusCode() == Syllabus.STATUS_APPROVED
                            && curriculumIdParamForLockCheck != null && !curriculumIdParamForLockCheck.trim().isEmpty()) {
                        String cid = curriculumIdParamForLockCheck.trim();
                        boolean hasMappingForThisCurriculum = false;
                        for (String[] row : ploDAO.getAllCloCodePloMappings(existing.getSyllabusId())) {
                            if (cid.equalsIgnoreCase(row[2])) { hasMappingForThisCurriculum = true; break; }
                        }
                        if (!hasMappingForThisCurriculum) mappingOnlyMode = true;
                    }
                    if (!mappingOnlyMode) {
                        // Dang Pending Review, hoac da Approved va khong o truong hop dac biet
                        // tren -> khoa, khong cho vao sua
                        String reason = existing.getStatusCode() == Syllabus.STATUS_PENDING_REVIEW
                                ? "This syllabus has been submitted and is pending review. It cannot be edited until the Reviewer sends it back."
                                : "This syllabus has already been approved and is locked from further edits.";
                        res.sendRedirect(req.getContextPath() + "/subject/detail?id=" + subjectId
                                + "&error=" + java.net.URLEncoder.encode(reason, "UTF-8"));
                        return;
                    }
                    req.setAttribute("mappingOnlyMode", true);
                    req.setAttribute("mappingOnlyCurriculumId", curriculumIdParamForLockCheck.trim());
                }

                // Prefill: Syllabus (Draft) da ton tai san -> nap lai CLO/Session/Material/
                // thong tin co ban de dien san vao form, KHONG de form trong nhu truoc day
                // (truoc day bam vao sua se mat het du lieu cu, vi luc luu se ghi de bang du
                // lieu rong). Tai su dung lai dung JSON shape va ham fillFormFromImport() ben
                // JS von dang dung cho tinh nang "Load Data From Excel", chi khac la du lieu
                // lay tu DB thay vi tu file Excel.
                if (existing != null) {
                    util.SyllabusExcelHelper.SyllabusImportData prefillData = new util.SyllabusExcelHelper.SyllabusImportData();
                    prefillData.setSubjectCode(prefillCode);
                    prefillData.setSyllabus(existing);
                    prefillData.setClos(cloDAO.getCLOsBySyllabus(existing.getSyllabusId()));
                    prefillData.setSessions(sessionDAO.getSessionsBySyllabus(existing.getSyllabusId()));
                    prefillData.setMaterials(syllabusDAO.getMaterialsBySyllabusId(existing.getSyllabusId()));

                    // CLO-PLO mapping da tick san. Dung CLO_Code/PLO_Code (khong phai ID) de
                    // khop dung voi cach fillFormFromImport() dang hoat dong ben JS (ham nay
                    // von dung cho Excel import, gio tai su dung lai cho prefill tu DB).
                    java.util.Map<String, List<String>> checkedMap = new java.util.LinkedHashMap<>();
                    java.util.Map<String, String> ploIdToCode = new java.util.HashMap<>();
                    for (model.Curriculum c : curriculumDAO.getCurriculumsBySubject(subjectId)) {
                        for (model.ProgramLearningOutcome plo : ploDAO.getPLOsByCurriculum(c.getCurriculumId())) {
                            ploIdToCode.put(plo.getPloId(), plo.getPloCode());
                        }
                    }
                    for (String[] row : ploDAO.getAllCloCodePloMappings(existing.getSyllabusId())) {
                        // row = {PLO_ID, CLO_Code, Curriculum_ID}
                        String ploCode = ploIdToCode.get(row[0]);
                        String cloCode = row[1];
                        if (ploCode == null || cloCode == null) continue;
                        checkedMap.computeIfAbsent(cloCode, k -> new java.util.ArrayList<>()).add(ploCode);
                    }
                    prefillData.setCloPloMapping(checkedMap);

                    req.setAttribute("prefillJson", toJson(prefillData).replace("</", "<\\/"));
                }

                // Nap danh sach Curriculum (+ bo PLO cua tung Curriculum) co dung Subject
                // nay, de tab "Mapping" tren form co du lieu de ve bang tick CLO x PLO.
                // Neu nguoi dung vao tu 1 Curriculum cu the (curriculumId tren URL, vd bam
                // vao Subject tu trong trang Curriculum detail), CHI hien mapping cua dung
                // Curriculum do trong luc tao/sua (tranh roi mat, tap trung dung ngu canh).
                // Sau khi luu xong, xem lai qua "View mapping of CLOs to PLOs" se thay DAY DU
                // tat ca Curriculum co dung Subject nay (xem showCloMapping ben duoi).
                String curriculumIdParam = req.getParameter("curriculumId");
                List<model.Curriculum> curriculums = curriculumDAO.getCurriculumsBySubject(subjectId);
                if (curriculumIdParam != null && !curriculumIdParam.trim().isEmpty()) {
                    final String cid = curriculumIdParam.trim();
                    List<model.Curriculum> filtered = new java.util.ArrayList<>();
                    for (model.Curriculum c : curriculums) {
                        if (cid.equalsIgnoreCase(c.getCurriculumId())) filtered.add(c);
                    }
                    // Neu curriculumId tren URL khong khop curriculum nao thuc su co mon nay
                    // (du lieu bi lech), fallback ve curriculum dau tien (xem giai thich ben duoi)
                    // thay vi hien het tat ca.
                    curriculums = !filtered.isEmpty() ? filtered
                            : (curriculums.isEmpty() ? curriculums : curriculums.subList(0, 1));
                } else if (curriculums.size() > 1) {
                    // Khong co curriculumId tren URL (vd vao tu Syllabuses > Create Syllabus
                    // roi tu go/chon Subject Code) nhung mon nay lai thuoc nhieu Curriculum:
                    // CHI hien 1 bang mapping (curriculum dau tien) luc tao/sua de tap trung
                    // dung ngu canh, tranh roi mat vi phai tick nhieu bang cung luc. Xem DAY DU
                    // tat ca Curriculum sau khi luu, qua man "View mapping of CLOs to PLOs"
                    // (showCloMapping ben duoi).
                    curriculums = curriculums.subList(0, 1);
                }
                List<model.CloPloMappingTable> mappingCurricula = new java.util.ArrayList<>();
                for (model.Curriculum c : curriculums) {
                    List<model.ProgramLearningOutcome> plos = ploDAO.getPLOsByCurriculum(c.getCurriculumId());
                    mappingCurricula.add(new model.CloPloMappingTable(
                            c.getCurriculumId(), c.getCurriculumCode(), c.getCurriculumName(), plos, null));
                }
                req.setAttribute("mappingCurricula", mappingCurricula);
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/syllabus/form.jsp").forward(req, res);
    }

    private void downloadSyllabus(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = getLoggedUser(req);
        if (user == null || "Guest".equals(user.getRole() != null ? user.getRole().getRoleName() : "")) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String id = req.getParameter("id");
        Syllabus s = syllabusDAO.getSyllabusById(id);
        if (s == null) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }

        String format = req.getParameter("format");
        if (format == null) format = "excel";

        res.setCharacterEncoding("UTF-8");
        
        if ("word".equalsIgnoreCase(format)) {
            res.setContentType("application/msword;charset=UTF-8");
            res.setHeader("Content-Disposition", "attachment; filename=\"Syllabus_" + s.getSubject().getSubjectCode() + ".doc\"");
            try (PrintWriter out = res.getWriter()) {
                out.println("<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>");
                out.println("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
                out.println("<style>");
                out.println("body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; }");
                out.println("h1 { color: #0288d1; border-bottom: 2px solid #0288d1; padding-bottom: 5px; }");
                out.println(".section-title { font-weight: bold; font-size: 1.2em; margin-top: 20px; color: #0288d1; }");
                out.println(".content { margin-bottom: 15px; background: #f9f9f9; padding: 10px; border-left: 3px solid #ccc; }");
                out.println("table { border-collapse: collapse; width: 100%; margin-top: 10px; }");
                out.println("th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }");
                out.println("th { background-color: #f7f7f7; font-weight: bold; }");
                out.println("</style></head><body>");
                
                out.println("<h1>Syllabus: " + s.getSyllabusName() + "</h1>");
                out.println("<p><b>Subject:</b> " + s.getSubject().getSubjectCode() + " - " + s.getSubject().getSubjectName() + "</p>");
                
                out.println("<div class='section-title'>General Information</div>");
                out.println("<table>");
                out.println("  <tr><th>Field</th><th>Value</th></tr>");
                out.println("  <tr><td><b>Version</b></td><td>" + s.getVersion() + "</td></tr>");
                out.println("  <tr><td><b>Status</b></td><td>" + (s.getStatus() != null ? s.getStatus() : "") + "</td></tr>");
                out.println("  <tr><td><b>Time Allocation</b></td><td>" + (s.getTimeAllocation() != null ? s.getTimeAllocation() : "") + "</td></tr>");
                out.println("  <tr><td><b>Scoring Scale</b></td><td>" + (s.getScoringScale() != null ? s.getScoringScale() : "") + "</td></tr>");
                out.println("  <tr><td><b>Min Avg to Pass</b></td><td>" + s.getMinAvgMarkToPass() + "</td></tr>");
                out.println("  <tr><td><b>Decision No</b></td><td>" + (s.getDecisionNo() != null ? s.getDecisionNo() : "") + "</td></tr>");
                out.println("</table>");
                
                out.println("<div class='section-title'>Description</div>");
                out.println("<div class='content'>" + (s.getDescription() != null ? s.getDescription().replace("\n", "<br>") : "") + "</div>");
                
                out.println("<div class='section-title'>Student Tasks</div>");
                out.println("<div class='content'>" + (s.getStudentTasks() != null ? s.getStudentTasks().replace("\n", "<br>") : "") + "</div>");
                
                out.println("<div class='section-title'>Tools</div>");
                out.println("<div class='content'>" + (s.getTools() != null ? s.getTools().replace("\n", "<br>") : "") + "</div>");
                
                out.println("</body></html>");
            }
        } else if ("csv".equalsIgnoreCase(format)) {
            res.setContentType("text/csv;charset=UTF-8");
            res.setHeader("Content-Disposition", "attachment; filename=\"Syllabus_" + s.getSubject().getSubjectCode() + ".csv\"");
            try (PrintWriter out = res.getWriter()) {
                out.write('\ufeff'); // UTF-8 BOM
                out.println("Field,Detail");
                out.println("Subject Code,\"" + s.getSubject().getSubjectCode().replace("\"", "\"\"") + "\"");
                out.println("Subject Name,\"" + s.getSubject().getSubjectName().replace("\"", "\"\"") + "\"");
                out.println("Version,\"" + s.getVersion().replace("\"", "\"\"") + "\"");
                out.println("Status,\"" + (s.getStatus() != null ? s.getStatus().replace("\"", "\"\"") : "") + "\"");
                out.println("Time Allocation,\"" + (s.getTimeAllocation() != null ? s.getTimeAllocation().replace("\"", "\"\"") : "") + "\"");
                out.println("Scoring Scale,\"" + (s.getScoringScale() != null ? s.getScoringScale().replace("\"", "\"\"") : "") + "\"");
                out.println("Min Avg to Pass,\"" + s.getMinAvgMarkToPass() + "\"");
                out.println("Decision No,\"" + (s.getDecisionNo() != null ? s.getDecisionNo().replace("\"", "\"\"") : "") + "\"");
                out.println("Description,\"" + (s.getDescription() != null ? s.getDescription().replace("\"", "\"\"").replace("\n", " ").replace("\r", "") : "") + "\"");
                out.println("Student Tasks,\"" + (s.getStudentTasks() != null ? s.getStudentTasks().replace("\"", "\"\"").replace("\n", " ").replace("\r", "") : "") + "\"");
                out.println("Tools,\"" + (s.getTools() != null ? s.getTools().replace("\"", "\"\"").replace("\n", " ").replace("\r", "") : "") + "\"");
            }
        } else {
            // Default to Excel
            res.setContentType("application/vnd.ms-excel;charset=UTF-8");
            res.setHeader("Content-Disposition", "attachment; filename=\"Syllabus_" + s.getSubject().getSubjectCode() + ".xls\"");
            try (PrintWriter out = res.getWriter()) {
                out.println("<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:x='urn:schemas-microsoft-com:office:excel' xmlns='http://www.w3.org/TR/REC-html40'>");
                out.println("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
                out.println("<style>");
                out.println("table { border-collapse: collapse; width: 100%; }");
                out.println("th, td { border: 1px solid #000; padding: 8px; text-align: left; }");
                out.println("th { background-color: #f2f2f2; font-weight: bold; }");
                out.println("</style></head><body>");
                
                out.println("<h2>Syllabus: " + s.getSyllabusName() + "</h2>");
                out.println("<table>");
                out.println("  <tr><th>Field</th><th>Detail</th></tr>");
                out.println("  <tr><td><b>Subject Code</b></td><td>" + s.getSubject().getSubjectCode() + "</td></tr>");
                out.println("  <tr><td><b>Subject Name</b></td><td>" + s.getSubject().getSubjectName() + "</td></tr>");
                out.println("  <tr><td><b>Version</b></td><td>" + s.getVersion() + "</td></tr>");
                out.println("  <tr><td><b>Status</b></td><td>" + (s.getStatus() != null ? s.getStatus() : "") + "</td></tr>");
                out.println("  <tr><td><b>Time Allocation</b></td><td>" + (s.getTimeAllocation() != null ? s.getTimeAllocation() : "") + "</td></tr>");
                out.println("  <tr><td><b>Scoring Scale</b></td><td>" + (s.getScoringScale() != null ? s.getScoringScale() : "") + "</td></tr>");
                out.println("  <tr><td><b>Min Avg to Pass</b></td><td>" + s.getMinAvgMarkToPass() + "</td></tr>");
                out.println("  <tr><td><b>Decision No</b></td><td>" + (s.getDecisionNo() != null ? s.getDecisionNo() : "") + "</td></tr>");
                out.println("  <tr><td><b>Description</b></td><td>" + (s.getDescription() != null ? s.getDescription().replace("\n", "<br>") : "") + "</td></tr>");
                out.println("  <tr><td><b>Student Tasks</b></td><td>" + (s.getStudentTasks() != null ? s.getStudentTasks().replace("\n", "<br>") : "") + "</td></tr>");
                out.println("  <tr><td><b>Tools</b></td><td>" + (s.getTools() != null ? s.getTools().replace("\n", "<br>") : "") + "</td></tr>");
                out.println("</table>");
                
                out.println("</body></html>");
            }
        }
    }

    /* ====== utilities ====== */
    private User getLoggedUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? (User) s.getAttribute("loggedUser") : null;
    }

    private boolean hasRole(HttpServletRequest req, String... roles) {
        User user = getLoggedUser(req);
        if (user == null) return false;
        String userRole = user.getRole() != null ? user.getRole().getRoleName() : "";
//        for (String r : roles) if (r.equals(userRole)) return true;
        for (String r : roles) {
            if (r.equalsIgnoreCase(userRole)) return true;
            if (user.hasRole(r)) return true;
            if ("Designer".equalsIgnoreCase(r) && (user.isDesigner() || user.hasRole("Designer"))) return true;
            if ("Reviewer".equalsIgnoreCase(r) && (user.isReviewer() || user.hasRole("Reviewer"))) return true;
        }
        return false;
    }

    private String jsonStr(String val) {
        if (val == null) return "\"\"";
        return "\"" + escapeJson(val) + "\"";
    }

    private String escapeJson(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String safeStr(String val) {
        return val != null ? val.trim() : "";
    }
}