package controller;

import dao.ComboDAO;
import dao.CurriculumDAO;
import model.Combo;
import model.Subject;
import model.Curriculum;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ComboServlet", urlPatterns = {"/combo"})
public class ComboServlet extends HttpServlet {

    private final ComboDAO comboDAO = new ComboDAO();
    private final CurriculumDAO curDAO = new CurriculumDAO(); // Giả sử bạn có DAO này

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                showComboList(request, response);
                break;
            case "detail":
                showComboDetail(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/curriculum/list");
        }
    }

    private void showComboList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String curriculumId = request.getParameter("curriculumId");
        
        // Lấy thông tin Curriculum để hiển thị tiêu đề
        Curriculum curriculum = curDAO.getCurriculumById(curriculumId); 
        List<Combo> comboList = comboDAO.getCombosByCurriculumId(curriculumId);

        request.setAttribute("curriculum", curriculum);
        request.setAttribute("comboList", comboList);
        request.getRequestDispatcher("/WEB-INF/views/combo/list.jsp").forward(request, response);
    }

    private void showComboDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String comboId = request.getParameter("comboId");

        Combo combo = comboDAO.getComboById(comboId);
        List<Subject> subjects = comboDAO.getSubjectsByComboId(comboId);

        request.setAttribute("combo", combo);
        request.setAttribute("subjects", subjects);
        request.getRequestDispatcher("/WEB-INF/views/combo/detail.jsp").forward(request, response);
    }
}