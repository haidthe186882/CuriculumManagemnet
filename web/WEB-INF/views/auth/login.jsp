<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { font-family: 'Inter', sans-serif; }
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #0f2027, #203a43, #2c5364);
            display: flex; align-items: center; justify-content: center;
        }
        .login-card {
            background: rgba(255,255,255,0.05);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: 20px;
            padding: 2.5rem;
            width: 100%; max-width: 440px;
            box-shadow: 0 25px 50px rgba(0,0,0,0.4);
        }
        .brand-logo { font-size: 2rem; font-weight: 700; color: #fff; letter-spacing: -1px; }
        .brand-logo span { color: #4fc3f7; }
        .subtitle { color: rgba(255,255,255,0.6); font-size: 0.9rem; margin-top: 4px; }
        .form-label { color: rgba(255,255,255,0.8); font-size: 0.85rem; font-weight: 500; }
        .form-control {
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: 10px; color: #fff; padding: 0.75rem 1rem;
            transition: all 0.2s;
        }
        .form-control:focus {
            background: rgba(255,255,255,0.12);
            border-color: #4fc3f7; box-shadow: 0 0 0 3px rgba(79,195,247,0.15); color: #fff;
        }
        .form-control::placeholder { color: rgba(255,255,255,0.35); }
        .btn-login {
            background: linear-gradient(135deg, #4fc3f7, #0288d1);
            border: none; border-radius: 10px; padding: 0.75rem;
            font-weight: 600; font-size: 1rem; color: #fff; width: 100%;
            transition: transform 0.15s, box-shadow 0.15s;
        }
        .btn-login:hover { transform: translateY(-1px); box-shadow: 0 8px 20px rgba(79,195,247,0.4); color:#fff; }
        .divider { border-color: rgba(255,255,255,0.1); }
        .alert-danger { background: rgba(220,53,69,0.15); border: 1px solid rgba(220,53,69,0.3); border-radius: 10px; color: #ff8a80; }
        .input-group-text { background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.15); color: rgba(255,255,255,0.5); border-radius: 10px 0 0 10px; }
        .input-group .form-control { border-radius: 0 10px 10px 0; }
        .form-check-label { color: rgba(255,255,255,0.6); font-size: 0.85rem; }
        .forgot-link { color: #4fc3f7; font-size: 0.85rem; text-decoration: none; }
        .forgot-link:hover { color: #81d4fa; }
    </style>
</head>
<body>
<div class="login-card">
    <div class="text-center mb-4">
        <div class="brand-logo">LT<span>MS</span></div>
        <div class="subtitle">Learning Tutorial Management System</div>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger d-flex align-items-center gap-2 mb-3" role="alert">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <span>${error}</span>
        </div>
    </c:if>
    <c:if test="${param.msg == 'timeout'}">
        <div class="alert alert-danger d-flex align-items-center gap-2 mb-3">
            <i class="bi bi-clock"></i> Session expired. Please login again.
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
        <div class="mb-3">
            <label class="form-label" for="email">Email Address</label>
            <div class="input-group">
                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="yourname@fpt.edu.vn" value="${param.email}" required>
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label" for="password">Password</label>
            <div class="input-group">
                <span class="input-group-text"><i class="bi bi-lock"></i></span>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required>
            </div>
        </div>
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" id="remember">
                <label class="form-check-label" for="remember">Remember me</label>
            </div>
            <a href="#" class="forgot-link">Forgot Password?</a>
        </div>
        <button type="submit" class="btn btn-login">
            <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
        </button>
        <div class="mt-3 text-center">
            <span style="color:rgba(255,255,255,0.8);">Chưa có tài khoản?</span>
            <a href="${pageContext.request.contextPath}/register" class="ms-2" style="color:#4fc3f7; text-decoration:underline;">Đăng ký</a>
        </div>
    </form>

    <hr class="divider my-4">
    <p class="text-center mb-0" style="color:rgba(255,255,255,0.4);font-size:0.8rem;">
        &copy; 2024 LTMS — FPT University
    </p>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
