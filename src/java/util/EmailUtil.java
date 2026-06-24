package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @author Mai Duy An
 * @MSSV HE197000
 * @date 24/6/2026
 */
public class EmailUtil {

    // ===== REPLACE THESE WITH YOUR REAL CREDENTIALS =====
    private static final String SENDER_EMAIL = "maiduyan2005@gmail.com";
    private static final String APP_PASSWORD = "ivvk skek jris nyba";
    // ====================================================

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    /**
     * Sends a 6-digit OTP code to the specified email address.
     *
     * @param toEmail recipient email address
     * @param otp the 6-digit OTP string
     *
     */
    public static void sendOTP(String toEmail, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Academic Management System", "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Password Reset OTP — Academic Management System", "UTF-8");
        message.setContent(buildHtmlBody(otp), "text/html; charset=UTF-8");

        Transport.send(message);
    }

    /**
     * Builds a styled HTML email body containing the OTP code.
     */
    private static String buildHtmlBody(String otp) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background:#f3f8ff;font-family:Inter,Arial,sans-serif'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f3f8ff;padding:40px 0'>"
                + "<tr><td align='center'>"
                + "<table width='480' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:14px;padding:32px;'>"
                + "<tr><td align='center' style='padding-bottom:24px'>"
                + "<div style='width:56px;height:56px;border-radius:50%;background:#0b1020;margin:0 auto;'></div>"
                + "<h2 style='color:#0b1020;margin:14px 0 4px;font-size:20px'>Password Reset</h2>"
                + "<p style='color:#6b7280;font-size:14px;margin:0'>Academic Management System</p>"
                + "</td></tr>"
                + "<tr><td style='padding:0 0 20px'>"
                + "<p style='color:#374151;font-size:14px;line-height:1.6;margin:0'>"
                + "We received a request to reset your password. Use the following OTP code to proceed:</p>"
                + "</td></tr>"
                + "<tr><td align='center' style='padding:0 0 20px'>"
                + "<div style='background:#f0f4ff;border:2px dashed #3b82f6;border-radius:12px;padding:20px 40px;display:inline-block'>"
                + "<span style='font-size:32px;font-weight:700;letter-spacing:8px;color:#0b1020'>"
                + otp + "</span></div>"
                + "</td></tr>"
                + "<tr><td style='padding:0 0 20px'>"
                + "<p style='color:#6b7280;font-size:13px;line-height:1.5;margin:0'>"
                + "This code will expire in <strong>5 minutes</strong>.<br>"
                + "If you did not request a password reset, please ignore this email.</p>"
                + "</td></tr>"
                + "<tr><td style='border-top:1px solid #e5e7eb;padding-top:16px'>"
                + "<p style='color:#9ca3af;font-size:12px;text-align:center;margin:0'>"
                + "© 2026 Academic Management System. All rights reserved.</p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }
}
