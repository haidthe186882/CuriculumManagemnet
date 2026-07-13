/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.DesignDAO;
import model.CurriculumAssignments;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author lo pc
 */
@WebServlet(name = "DesignServlet", urlPatterns = {"/design/*"})
public class DesginServlet extends HttpServlet {

    private final DesignDAO desginDAO = new DesignDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
            default:
                response.sendRedirect(request.getContextPath() + "/design/list");
        }
    }

    /**
     * Hien thi danh sach curriculum ma Designer dang nhap duoc phan cong.
     */
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = requireDesigner(request, response);
        if (loggedUser == null) {
            return;
        }

        String keyword = request.getParameter("keyword");
        List<CurriculumAssignments> assignments =
                desginDAO.getCurriculumsByDesigner(loggedUser.getUserId(), keyword);

        request.setAttribute("assignments", assignments);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/WEB-INF/views/design/list.jsp").forward(request, response);
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
//        String role = user.getRole() != null ? user.getRole().getRoleName() : "";
//        if (!"Designer".equals(role) && !"Admin".equals(role)) {
//            response.sendRedirect(request.getContextPath() + "/curriculum/list");
//            return null;
//        }
        String primaryRole = user.getRole() != null ? user.getRole().getRoleName() : "";
        
        // 1. Quét quyền Designer (Chính, List, Cờ phụ)
        boolean isDesigner = "Designer".equalsIgnoreCase(primaryRole) || user.hasRole("Designer") || user.isDesigner();
        
        // 2. Quét quyền Admin
        boolean isAdmin = "Admin".equalsIgnoreCase(primaryRole) || user.hasRole("Admin");

        // Nếu KHÔNG có bất kỳ quyền nào trong 3 nhóm trên thì mới chặn
        if (!isDesigner && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/curriculum/list");
            return null;
        }
        return user;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Hien thi danh sach Curriculum duoc phan cong cho Designer";
    }// </editor-fold>
}