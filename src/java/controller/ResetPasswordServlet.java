package controller;

import dao.UserDAO;
import util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;

/**
 * @author Mai Duy An
 * @MSSV HE197000
 * @date 24/6/2026
 * 
 */
@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("verify".equals(action)) {
            verifyOTP(req, res);
        } else if ("reset".equals(action)) {
            resetPassword(req, res);
        } else if ("resend".equals(action)) {
            resendOTP(req, res);
        } else {
            res.sendRedirect(req.getContextPath() + "/forgot-password");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Direct GET access is not allowed — redirect to forgot-password
        res.sendRedirect(req.getContextPath() + "/forgot-password");
    }

    // ---------------------------------------------------------------
    //  Step 5 — Verify OTP
    // ---------------------------------------------------------------
    private void verifyOTP(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // 5.1 — Check session exists
        if (session == null || session.getAttribute("resetOTP") == null) {
            req.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        String storedOTP     = (String) session.getAttribute("resetOTP");
        Long   otpCreatedTime = (Long) session.getAttribute("otpCreatedTime");
        String email         = (String) session.getAttribute("resetEmail");

        // 5.2 — Check OTP not expired (5 minutes)
        if (otpCreatedTime == null || (System.currentTimeMillis() - otpCreatedTime) > OTP_EXPIRY_MS) {
            // Clear OTP data
            clearResetSession(session);
            req.setAttribute("error", "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        // 5.3 — Compare OTP
        String inputOTP = req.getParameter("otp");
        if (inputOTP == null || !inputOTP.trim().equals(storedOTP)) {
            req.setAttribute("error", "Mã OTP không đúng. Vui lòng thử lại.");
            req.setAttribute("maskedEmail", maskEmail(email));
            req.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(req, res);
            return;
        }

        // 5.4 — OTP correct → mark verified
        session.setAttribute("otpVerified", true);
        req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, res);
    }

    // ---------------------------------------------------------------
    //  Step 7 — Reset Password
    // ---------------------------------------------------------------
    private void resetPassword(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // 7.1 — Check otpVerified flag
        if (session == null || session.getAttribute("otpVerified") == null) {
            req.setAttribute("error", "Phiên làm việc không hợp lệ. Vui lòng thực hiện lại.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        String userId      = (String) session.getAttribute("resetUserId");
        String newPassword  = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // 7.2 — Validate password length ≥ 6
        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, res);
            return;
        }

        // 7.3 — Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, res);
            return;
        }

        // 7.4 — Hash MD5 mật khẩu mới rồi lưu vào DB
        UserDAO dao = new UserDAO();
        String hashedPassword = hashMD5(newPassword);
        boolean updated = dao.updatePassword(userId, hashedPassword);

        if (!updated) {
            req.setAttribute("error", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, res);
            return;
        }

        // 7.5 — Clear all reset data from session
        clearResetSession(session);

        // 7.6 — Forward to login with success message
        req.setAttribute("successMessage", "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập.");
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
    }

    // ---------------------------------------------------------------
    //  Resend OTP
    // ---------------------------------------------------------------
    private void resendOTP(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("resetEmail") == null) {
            req.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, res);
            return;
        }

        String email = (String) session.getAttribute("resetEmail");

        // Generate new OTP
        String newOtp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("resetOTP", newOtp);
        session.setAttribute("otpCreatedTime", System.currentTimeMillis());
        session.removeAttribute("otpVerified");

        // Send new OTP
        try {
            EmailUtil.sendOTP(email, newOtp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau.");
            req.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(req, res);
            return;
        }

        req.setAttribute("success", "Mã OTP mới đã được gửi đến email của bạn.");
        req.setAttribute("maskedEmail", maskEmail(email));
        req.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(req, res);
    }

    // ---------------------------------------------------------------
    //  Helpers
    // ---------------------------------------------------------------
    private void clearResetSession(HttpSession session) {
        session.removeAttribute("resetOTP");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetUserId");
        session.removeAttribute("otpCreatedTime");
        session.removeAttribute("otpVerified");
    }

    private String maskEmail(String email) {
        if (email == null) return "";
        int atIdx = email.indexOf('@');
        if (atIdx <= 2) return "***" + email.substring(atIdx);
        return email.substring(0, 2) + "***" + email.substring(atIdx);
    }

    /**
     * Mã hóa chuỗi đầu vào bằng thuật toán MD5.
     * @param input Chuỗi cần mã hóa
     * @return Chuỗi hex MD5 (32 ký tự), hoặc chuỗi gốc nếu có lỗi
     */
    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
