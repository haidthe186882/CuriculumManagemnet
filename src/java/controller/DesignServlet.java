/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.DesignDAO;
import dao.SyllabusDAO;
import model.SyllabusAssignment;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * Man hinh "Design list": Designer xem cac Subject/Syllabus duoc Admin phan
 * cong cho minh (qua bang Syllabus_Assignments), roi bam "Submit for Review"
 * khi thiet ke xong de chuyen qua cho Reviewer duyet.
 *
 * @author lo pc
 */
@WebServlet(name = "DesignServlet", urlPatterns = {"/design/*"})
public class DesignServlet extends HttpServlet {

    private final DesignDAO designDAO = new DesignDAO();
    private final SyllabusDAO syllabusDAO = new SyllabusDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/list";
        }
        switch (pathInfo) {
            case "/list":
                showList(request, response);
                break;
            case "/submit":
                doSubmitForReview(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/design/list");
        }
    }

    /**
     * Hien thi danh sach Subject/Syllabus ma Designer dang nhap duoc phan cong.
     */
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = requireDesigner(request, response);
        if (loggedUser == null) {
            return;
        }

        String keyword = request.getParameter("keyword");
        List<SyllabusAssignment> assignments =
                designDAO.getAssignmentsByDesigner(loggedUser.getUserId(), keyword);

        request.setAttribute("assignments", assignments);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/WEB-INF/views/design/list.jsp").forward(request, response);
    }

    /**
     * Designer bam "Submit for Review" tren 1 Syllabus: Draft(0) -> PendingReview(1).
     * Chi cho phep neu chinh Designer nay (hoac Admin) da duoc gan vao Syllabus do.
     */
    private void doSubmitForReview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User loggedUser = requireDesigner(request, response);
        if (loggedUser == null) return;

        String syllabusId = request.getParameter("syllabusId");
        String primaryRole = loggedUser.getRole() != null ? loggedUser.getRole().getRoleName() : "";
        boolean isAdmin = "Admin".equalsIgnoreCase(primaryRole) || loggedUser.hasRole("Admin");

        if (syllabusId != null && (isAdmin
                || designDAO.isAssignedToSyllabus(loggedUser.getUserId(), syllabusId, "Designer"))) {
            syllabusDAO.submitForReview(syllabusId);
        }
        response.sendRedirect(request.getContextPath() + "/design/list?msg=submitted");
    }

    /**
     * Bat buoc dang nhap va co Role = Designer (Admin duoc xem cung de tien debug).
     * Tra ve null va da redirect san neu khong dat dieu kien.
     */
    private User requireDesigner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        User user = (User) session.getAttribute("loggedUser");
        String primaryRole = user.getRole() != null ? user.getRole().getRoleName() : "";

        // 1. Quét quyền Designer (Chính, List, Cờ phụ)
        boolean isDesigner = "Designer".equalsIgnoreCase(primaryRole) || user.hasRole("Designer") || user.isDesigner();

        // 2. Quét quyền Admin
        boolean isAdmin = "Admin".equalsIgnoreCase(primaryRole) || user.hasRole("Admin");

        // Nếu KHÔNG có bất kỳ quyền nào trong 2 nhóm trên thì mới chặn
        if (!isDesigner && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/curriculum/list");
            return null;
        }
        return user;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Hien thi danh sach Subject/Syllabus duoc phan cong cho Designer va cho submit review";
    }// </editor-fold>
}