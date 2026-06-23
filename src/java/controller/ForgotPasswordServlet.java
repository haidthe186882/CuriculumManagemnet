package controller;

import dao.UserDAO;
import model.User;
import util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Random;

/**
 * Handles the "Forgot Password" flow:
 *   GET  /forgot-password → show email form
 *   POST /forgot-password → validate email, generate OTP, send via Gmail, forward to OTP page
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String email = req.getParameter("email");

        // 3.1 — Validate email not empty
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập địa chỉ email.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        email = email.trim();

        // 3.2 — Check if email exists in database
        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);

        // 3.3 — If email NOT found → show generic success (security: don't reveal email existence)
        if (user == null) {
            req.setAttribute("success", "Nếu email tồn tại trong hệ thống, mã OTP đã được gửi đến hộp thư của bạn.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        // 3.4 — Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // 3.5 — Store OTP data in session
        HttpSession session = req.getSession(true);
        session.setAttribute("resetOTP", otp);
        session.setAttribute("resetEmail", email);
        session.setAttribute("resetUserId", user.getUserId());
        session.setAttribute("otpCreatedTime", System.currentTimeMillis());
        session.removeAttribute("otpVerified"); // clear any previous verification

        // 3.6 — Send OTP via Gmail SMTP
        try {
            EmailUtil.sendOTP(email, otp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        // 3.7 — Forward to OTP verification page
        req.setAttribute("maskedEmail", maskEmail(email));
        req.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(req, res);
    }

    /**
     * Masks an email for display: "john.doe@gmail.com" → "jo***@gmail.com"
     */
    private String maskEmail(String email) {
        int atIdx = email.indexOf('@');
        if (atIdx <= 2) return "***" + email.substring(atIdx);
        return email.substring(0, 2) + "***" + email.substring(atIdx);
    }
}
