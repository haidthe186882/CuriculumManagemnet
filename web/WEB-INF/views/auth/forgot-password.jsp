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
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
        <style>
            :root{
                --card-radius:24px;
                --primary-orange:#f95d00;
                --hover-orange:#ea5200;
            }
            *{
                box-sizing:border-box;
                font-family:'Inter',system-ui,Segoe UI,Roboto,"Helvetica Neue",Arial
            }
            body{
                min-height:100vh;
                display:flex;
                align-items:center;
                justify-content:center;
                margin:0;
                background:#f0f4f9;
            }
            .page-wrap{
                width:100%;
                max-width:520px;
                padding:24px
            }
            .card-box{
                background:#fff;
                border-radius:var(--card-radius);
                padding:36px 36px 30px;
                box-shadow:0 15px 45px rgba(15,23,42,0.06), 0 0 1px rgba(0,0,0,0.08);
                border:1px solid #e2e8f0;
                animation:fadeUp .5s ease-out
            }
            @keyframes fadeUp{
                from{opacity:0;transform:translateY(18px)}
                to{opacity:1;transform:translateY(0)}
            }
            .logo-circle{
                width:72px;height:72px;border-radius:50%;
                background:linear-gradient(135deg, #f97316, #ea580c);
                display:flex;align-items:center;justify-content:center;
                margin:0 auto;color:#fff;font-size:30px;
                box-shadow:0 8px 20px rgba(249, 93, 0, 0.25);
            }
            .brand-title{
                font-size:22px;font-weight:800;text-align:center;
                margin-top:16px;color:#0f172a
            }
            .brand-sub{
                font-size:14px;color:#64748b;text-align:center;margin-bottom:24px
            }
            .form-label{
                font-size:14px;color:#1e293b;margin-bottom:8px;font-weight:600
            }
            .form-control{
                border-radius:12px;border:1px solid #e2e8f0;
                padding:12px 14px 12px 42px;background:#f8fafc;
                font-size:14px;
                transition:all .2s ease
            }
            .form-control:focus{
                background:#fff;
                border-color:var(--primary-orange);
                box-shadow:0 0 0 3px rgba(249, 93, 0, 0.15);
                outline:none
            }
            .input-icon-wrap{position:relative}
            .input-icon-wrap .icon-left{
                position:absolute;left:14px;top:50%;transform:translateY(-50%);
                color:#94a3b8;font-size:16px
            }
            .btn-primary-custom{
                background:var(--primary-orange);color:#fff;border-radius:12px;
                height:48px;border:none;width:100%;font-weight:700;
                font-size:1rem;cursor:pointer;
                box-shadow:0 6px 18px rgba(249, 93, 0, 0.3);
                transition:all .3s ease
            }
            .btn-primary-custom:hover{
                background:var(--hover-orange);
                box-shadow:0 10px 25px rgba(249, 93, 0, 0.45);
                transform:translateY(-1px);
            }
            .btn-primary-custom:disabled{opacity:.6;cursor:not-allowed}
            .back-link{
                display:flex;align-items:center;gap:6px;
                color:#64748b;text-decoration:none;font-size:13.5px;
                font-weight:600;
                margin-top:20px;justify-content:center;transition:color .2s
            }
            .back-link:hover{color:var(--primary-orange)}
            @media(max-width:576px){
                .page-wrap{padding:16px}
                .card-box{padding:24px 20px}
            }
        </style>
    </head>
    <body>
        <div class="page-wrap">
            <div class="card-box">
                <div class="text-center">
                    <div class="logo-circle"><i class="bi bi-shield-lock"></i></div>
                    <div class="brand-title">Forgot Password</div>
                    <div class="brand-sub">Enter your email to receive a verification code</div>
                </div>

                <!-- Success message -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success d-flex align-items-center" style="border-radius:12px;font-size:14px">
                        <i class="bi bi-check-circle-fill me-2"></i>${success}
                    </div>
                </c:if>

                <!-- Error message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger d-flex align-items-center" style="border-radius:12px;font-size:14px">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
                    </div>
                </c:if>

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
            </div>
        </div>

        <script>
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
                this.style.borderColor = '#e2e8f0';
            });
        </script>
    </body>
</html>
