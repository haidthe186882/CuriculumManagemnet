package controller;

import dao.SyllabusDAO;
import dao.SubjectDAO;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "SyllabusServlet", urlPatterns = {"/syllabus/*"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize       = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize    = 1024 * 1024 * 15   // 15 MB
)
public class SyllabusServlet extends HttpServlet {

    private final SyllabusDAO syllabusDAO = new SyllabusDAO();
    private final SubjectDAO  subjectDAO  = new SubjectDAO();
    private final CloDAO      cloDAO      = new CloDAO();
    private final SessionDAO  sessionDAO  = new SessionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":   showList(req, res);   break;
            case "/detail": showDetail(req, res); break;
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
        s.setSubjectId(req.getParameter("subjectId"));
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
        String syllabusId = syllabusDAO.addSyllabusAndGetId(s);

        if (syllabusId != null) {
            // Insert CLOs
            String[] cloCodes = req.getParameterValues("cloCode[]");
            String[] cloDescs = req.getParameterValues("cloDesc[]");
            if (cloCodes != null && cloDescs != null) {
                for (int i = 0; i < cloCodes.length; i++) {
                    if (cloCodes[i] != null && !cloCodes[i].trim().isEmpty()) {
                        CourseLearningOutcome clo = new CourseLearningOutcome();
                        clo.setSyllabusId(syllabusId);
                        clo.setCloCode(cloCodes[i].trim());
                        clo.setDescription(i < cloDescs.length ? cloDescs[i].trim() : "");
                        cloDAO.addCLO(clo);
                    }
                }
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
        }

        res.sendRedirect(req.getContextPath() + "/syllabus/list?msg=created");
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
        sb.append("]");

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
        List<Syllabus> list = syllabusDAO.searchSyllabuses(keyword, status, activeOnly);
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
        User user = getLoggedUser(req);
        if (!s.isActive() && (user == null || hasRole(req, "Student", "Guest"))) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }
        req.setAttribute("syllabus", s);
        req.setAttribute("clos", cloDAO.getCLOsBySyllabus(id));
        req.setAttribute("sessions", sessionDAO.getSessionsBySyllabus(id));
        req.setAttribute("materials", syllabusDAO.getMaterialsBySyllabusId(id));
        req.getRequestDispatcher("/WEB-INF/views/syllabus/detail.jsp").forward(req, res);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("subjects", subjectDAO.searchSubjects(null, null, null));
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
