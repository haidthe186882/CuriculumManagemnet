<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Forgot Password — Academic Management System</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <style>
            :root{
                --card-radius:14px;
                --accent:#0b1020;
            }
            *{
                box-sizing:border-box;
                font-family:Inter,system-ui,Segoe UI,Roboto,"Helvetica Neue",Arial
            }
            body{
                min-height:100vh;
                display:flex;
                align-items:center;
                justify-content:center;
                margin:0;
                background:linear-gradient(180deg,#eef6ff 0%, #f3f8ff 100%)
            }
            .page-wrap{
                width:100%;
                max-width:520px;
                padding:40px
            }
            .card-box{
                background:#fff;
                border-radius:var(--card-radius);
                padding:36px 36px 28px;
                box-shadow:0 10px 30px rgba(15,23,42,0.08);
                animation:fadeUp .5s ease-out
            }
            @keyframes fadeUp{
                from{opacity:0;transform:translateY(18px)}
                to{opacity:1;transform:translateY(0)}
            }
            .logo-circle{
                width:72px;height:72px;border-radius:50%;
                background:linear-gradient(180deg,#0b1220,#0b1220);
                display:flex;align-items:center;justify-content:center;
                margin:0 auto;color:#fff;font-size:28px
            }
            .brand-title{
                font-size:22px;font-weight:600;text-align:center;
                margin-top:14px;color:#0b1020
            }
            .brand-sub{
                font-size:14px;color:#6b7280;text-align:center;margin-bottom:24px
            }
            .form-label{
                font-size:14px;color:#111827;margin-bottom:8px;font-weight:600
            }
            .form-control{
                border-radius:10px;border:1px solid #e6eef8;
                padding:12px 14px 12px 40px;background:#fbfdff;
                transition:border-color .2s,box-shadow .2s
            }
            .form-control:focus{
                border-color:#3b82f6;box-shadow:0 0 0 3px rgba(59,130,246,.12);
                outline:none
            }
            .input-icon-wrap{position:relative}
            .input-icon-wrap .icon-left{
                position:absolute;left:12px;top:50%;transform:translateY(-50%);
                color:#9aa4b2;font-size:16px
            }
            .btn-primary-custom{
                background:#0b1020;color:#fff;border-radius:10px;
                padding:12px 16px;border:none;width:100%;font-weight:600;
                font-size:15px;cursor:pointer;transition:opacity .2s
            }
            .btn-primary-custom:hover{opacity:.92}
            .btn-primary-custom:disabled{opacity:.6;cursor:not-allowed}
            .back-link{
                display:flex;align-items:center;gap:6px;
                color:#6b7280;text-decoration:none;font-size:14px;
                margin-top:18px;justify-content:center;transition:color .2s
            }
            .back-link:hover{color:#0b1020}
            .info-box{
                background:#f0f9ff;border:1px solid #bae6fd;
                border-radius:10px;padding:14px 16px;
                font-size:13px;color:#0369a1;margin-bottom:20px;
                display:flex;align-items:start;gap:10px
            }
            .info-box i{font-size:18px;flex-shrink:0;margin-top:1px}
            .footer-note{
                text-align:center;color:#9ca3af;margin-top:18px;font-size:13px
            }
            @media(max-width:576px){
                .page-wrap{padding:18px}
                .card-box{padding:24px}
            }
        </style>
    </head>
    <body>
        <div class="page-wrap">
            <div class="card-box">
                <div class="text-center">
                    <div class="logo-circle"><i class="bi bi-shield-lock" style="font-size:28px"></i></div>
                    <div class="brand-title">Forgot Password</div>
                    <div class="brand-sub">Enter your email to receive a verification code</div>
                </div>

                <!-- Success message -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success d-flex align-items-center" style="border-radius:10px;font-size:14px">
                        <i class="bi bi-check-circle-fill me-2"></i>${success}
                    </div>
                </c:if>

                <!-- Error message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger d-flex align-items-center" style="border-radius:10px;font-size:14px">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
                    </div>
                </c:if>

                <div class="info-box">
                    <i class="bi bi-info-circle-fill"></i>
                    <span>Nhập email đã đăng ký. Chúng tôi sẽ gửi mã OTP 6 số đến hộp thư của bạn.</span>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/forgot-password" id="forgotForm" novalidate>
                    <div class="mb-4">
                        <label class="form-label">Email Address</label>
                        <div class="input-icon-wrap">
                            <i class="icon-left bi bi-envelope"></i>
                            <input type="email" name="email" class="form-control" 
                                   placeholder="Enter your email address" 
                                   value="${param.email}" required id="emailInput">
                        </div>
                    </div>

                    <button type="submit" class="btn-primary-custom" id="submitBtn">
                        <i class="bi bi-send me-2"></i>Send OTP
                    </button>
                </form>

                <a href="${pageContext.request.contextPath}/login" class="back-link">
                    <i class="bi bi-arrow-left"></i> Back to Sign in
                </a>

                <p class="footer-note">&copy; <span id="year">2026</span> Academic Management System</p>
            </div>
        </div>

        <script>
            document.getElementById('year').textContent = new Date().getFullYear();

            // Simple client-side email validation + loading state
            document.getElementById('forgotForm').addEventListener('submit', function(e) {
                var email = document.getElementById('emailInput').value.trim();
                if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    e.preventDefault();
                    document.getElementById('emailInput').style.borderColor = '#ef4444';
                    return;
                }
                var btn = document.getElementById('submitBtn');
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Sending...';
            });

            document.getElementById('emailInput').addEventListener('input', function() {
                this.style.borderColor = '#e6eef8';
            });
        </script>
    </body>
</html>
