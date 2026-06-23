package controller;

import dao.SyllabusDAO;
import dao.SubjectDAO;
import dao.CloDAO;
import dao.SessionDAO;
import model.Syllabus;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "SyllabusServlet", urlPatterns = {"/syllabus/*"})
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
        String action = req.getParameter("action");
        if (!"create".equals(action)) {
            res.sendRedirect(req.getContextPath() + "/syllabus/list");
            return;
        }
        if (!hasRole(req, "Designer", "Admin", "Lecturer")) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Syllabus s = new Syllabus();
        s.setSubjectId(req.getParameter("subjectId"));
        s.setSyllabusName(req.getParameter("syllabusName"));
        s.setEnglishName(req.getParameter("englishName"));
        s.setVersion(req.getParameter("version"));
        s.setDescription(req.getParameter("description"));
        s.setTimeAllocation(req.getParameter("timeAllocation"));
        s.setScoringScale(req.getParameter("scoringScale"));
        try { s.setMinAvgMarkToPass(Double.parseDouble(req.getParameter("minAvgMarkToPass"))); } catch (Exception ignored) {}
        syllabusDAO.addSyllabus(s);
        res.sendRedirect(req.getContextPath() + "/syllabus/list?msg=created");
    }

    private void showList(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String status  = req.getParameter("status");
        User user = getLoggedUser(req);
        boolean activeOnly = user == null;
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
}
