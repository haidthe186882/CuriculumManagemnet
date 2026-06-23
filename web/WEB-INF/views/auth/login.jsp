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
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <style>
            :root{
                --card-radius:14px;
                --accent:#0b1020;
                --accent-2:#0f1724
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
            .login-wrap{
                width:100%;
                max-width:760px;
                padding:40px
            }
            .login-card{
                background:#fff;
                border-radius:var(--card-radius);
                padding:36px 36px 28px;
                box-shadow:0 10px 30px rgba(15,23,42,0.08)
            }
            .logo-circle{
                width:72px;
                height:72px;
                border-radius:50%;
                background:linear-gradient(180deg,#0b1220,#0b1220);
                display:flex;
                align-items:center;
                justify-content:center;
                margin:0 auto;
                color:#fff;
                font-size:28px
            }
            .brand-title{
                font-size:22px;
                font-weight:600;
                text-align:center;
                margin-top:14px;
                color:#0b1020
            }
            .brand-sub{
                font-size:14px;
                color:#6b7280;
                text-align:center;
                margin-bottom:20px
            }
            .form-label{
                font-size:14px;
                color:#111827;
                margin-bottom:8px;
                font-weight:600
            }
            .form-control{
                border-radius:10px;
                border:1px solid #e6eef8;
                padding:12px 14px;
                background:#fbfdff
            }
            .input-with-icon{
                position:relative
            }
            .input-with-icon .bi-left-icon{
                position:absolute;
                left:12px;
                top:50%;
                transform:translateY(-50%);
                color:#9aa4b2
            }
            .input-with-icon .bi-right-icon{
                position:absolute;
                right:12px;
                top:50%;
                transform:translateY(-50%);
                color:#9aa4b2
            }

            .input-with-icon input{
                padding-left:38px
            }
            .password-toggle{
                position:absolute;
                right:10px;
                top:50%;
                transform:translateY(-50%);
                color:#9aa4b2;
                cursor:pointer
            }
            .controls{
                display:flex;
                align-items:center;
                justify-content:space-between;
                margin-top:8px;
                margin-bottom:18px
            }
            .btn-signin{
                background:#0b1020;
                color:#fff;
                border-radius:10px;
                padding:12px 16px;
                border:none;
                width:100%;
                font-weight:600
            }
            .btn-signin:hover{
                opacity:0.95
            }
            .footer-note{
                text-align:center;
                color:#9ca3af;
                margin-top:18px;
                font-size:13px
            }
            @media (max-width:576px){
                .login-wrap{
                    padding:18px
                }
                .login-card{
                    padding:24px
                }
            }
        </style>
    </head>
    <body>
        <div class="login-wrap">
            <div class="login-card">
                <div class="text-center">
                    <div class="logo-circle"><i class="bi bi-mortarboard-fill" style="font-size:28px"></i></div>
                    <div class="brand-title">Academic Management System</div>
                    <div class="brand-sub">Sign in to continue</div>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success d-flex align-items-center" style="border-radius:10px;font-size:14px">
                        <i class="bi bi-check-circle-fill me-2"></i>${successMessage}
                    </div>
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
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="remember" name="remember">
                            <label class="form-check-label" for="remember">Remember me</label>
                        </div>
                        <a href="${pageContext.request.contextPath}/forgot-password" style="color:#374151;text-decoration:none">Forgot password?</a>
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
