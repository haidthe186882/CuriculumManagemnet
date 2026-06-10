<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { font-family: Inter, sans-serif; background: #f5f7fa; padding:60px 0; }
        .card { width:100%; max-width:640px; border-radius:12px; padding:2rem; margin: 0 auto; }
        .btn-primary { width:100% }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-12 col-md-6">
            <div class="card shadow-sm">
                <div class="p-4">
                    <h3 class="mb-3">Đăng ký tài khoản</h3>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/register" novalidate>
        <div class="mb-3">
            <label class="form-label">Họ tên</label>
            <input name="fullname" class="form-control" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Mật khẩu</label>
            <input type="password" name="password" class="form-control" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Xác nhận mật khẩu</label>
            <input type="password" name="confirmPassword" class="form-control" required />
        </div>
        <button type="submit" class="btn btn-primary">Đăng ký</button>
        <p class="mt-3">Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></p>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
