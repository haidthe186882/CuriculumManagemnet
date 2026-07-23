<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Academic Management System — Sign in</title>
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
            .login-wrap{
                width:100%;
                max-width:540px;
                padding:24px;
            }
            .login-card{
                background:#fff;
                border-radius:var(--card-radius);
                padding:36px 36px 30px;
                box-shadow:0 15px 45px rgba(15,23,42,0.06), 0 0 1px rgba(0,0,0,0.08);
                border:1px solid #e2e8f0;
            }
            .back-welcome-link {
                color: #64748b;
                font-size: 13.5px;
                font-weight: 600;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: all 0.2s ease;
            }
            .back-welcome-link:hover {
                color: var(--primary-orange);
            }
            .logo-circle{
                width:72px;
                height:72px;
                border-radius:50%;
                background:linear-gradient(135deg, #f97316, #ea580c);
                display:flex;
                align-items:center;
                justify-content:center;
                margin:0 auto;
                color:#fff;
                font-size:30px;
                box-shadow:0 8px 20px rgba(249, 93, 0, 0.25);
            }
            .brand-title{
                font-size:22px;
                font-weight:800;
                text-align:center;
                margin-top:16px;
                color:#0f172a
            }
            .brand-sub{
                font-size:14px;
                color:#64748b;
                text-align:center;
                margin-bottom:24px
            }
            .form-label{
                font-size:14px;
                color:#1e293b;
                margin-bottom:8px;
                font-weight:600
            }
            .form-control{
                border-radius:12px;
                border:1px solid #e2e8f0;
                padding:12px 14px;
                background:#f8fafc;
                font-size:14px;
                transition:all 0.2s ease;
            }
            .form-control:focus{
                background:#fff;
                border-color:var(--primary-orange);
                box-shadow:0 0 0 3px rgba(249, 93, 0, 0.15);
            }
            .input-with-icon{
                position:relative
            }
            .input-with-icon .bi-left-icon{
                position:absolute;
                left:14px;
                top:50%;
                transform:translateY(-50%);
                color:#94a3b8
            }
            .input-with-icon .bi-right-icon{
                position:absolute;
                right:14px;
                top:50%;
                transform:translateY(-50%);
                color:#94a3b8
            }
            .input-with-icon input{
                padding-left:42px
            }
            .password-toggle{
                position:absolute;
                right:14px;
                top:50%;
                transform:translateY(-50%);
                color:#94a3b8;
                cursor:pointer
            }
            .controls{
                display:flex;
                align-items:center;
                justify-content:flex-end;
                margin-top:10px;
                margin-bottom:20px
            }
            .forgot-link {
                color: #64748b;
                text-decoration: none;
                font-size: 13.5px;
                font-weight: 600;
                transition: color 0.2s ease;
            }
            .forgot-link:hover {
                color: var(--primary-orange);
            }
            .btn-signin{
                background:var(--primary-orange);
                color:#fff;
                border-radius:12px;
                height:48px;
                border:none;
                width:100%;
                font-weight:700;
                font-size:1rem;
                box-shadow:0 6px 18px rgba(249, 93, 0, 0.3);
                transition:all 0.3s ease;
            }
            .btn-signin:hover{
                background:var(--hover-orange);
                box-shadow:0 10px 25px rgba(249, 93, 0, 0.45);
                transform:translateY(-1px);
            }
            .footer-note{
                text-align:center;
                color:#94a3b8;
                margin-top:20px;
                font-size:13px
            }
            @media (max-width:576px){
                .login-wrap{
                    padding:16px
                }
                .login-card{
                    padding:24px 20px
                }
            }
        </style>
    </head>
    <body>
        <div class="login-wrap">
            <div class="login-card">
                <div class="mb-3 text-start">
                    <a href="${pageContext.request.contextPath}/" class="back-welcome-link">
                        <i class="bi bi-arrow-left"></i> Back to Welcome Page
                    </a>
                </div>

                <div class="text-center">
                    <div class="logo-circle"><i class="bi bi-mortarboard-fill"></i></div>
                    <div class="brand-title">Academic Management System</div>
                    <div class="brand-sub">Sign in to continue</div>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" style="border-radius:12px; font-size:14px;">${error}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
                    <div class="mb-3">
                        <label class="form-label">Email or Username</label>
                        <div class="input-with-icon">
                            <i class="bi-left-icon bi-envelope"></i>
                            <input type="text" name="email" class="form-control" placeholder="Enter email or username" value="${param.email}">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Password</label>
                        <div class="input-with-icon" style="position:relative">
                            <i class="bi-left-icon bi-lock"></i>
                            <input type="password" name="password" id="password" class="form-control" placeholder="Enter password">
                            <i class="bi-right-icon bi-eye password-toggle" id="togglePwd"></i>
                        </div>
                    </div>

                    <div class="controls">
                        <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-link">Forgot password?</a>
                    </div>

                    <button type="submit" class="btn-signin">Sign in</button>
                </form>

                <div class="footer-note">Don't have an account? Contact your administrator.</div>
                <p class="footer-note">© <span id="year">2026</span> Academic Management System</p>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('togglePwd').addEventListener('click', function () {
                var p = document.getElementById('password');
                if (p.type === 'password') {
                    p.type = 'text';
                    this.classList.remove('bi-eye');
                    this.classList.add('bi-eye-slash');
                } else {
                    p.type = 'password';
                    this.classList.remove('bi-eye-slash');
                    this.classList.add('bi-eye');
                }
            });
            document.getElementById('year').textContent = new Date().getFullYear();
        </script>
    </body>
</html>
