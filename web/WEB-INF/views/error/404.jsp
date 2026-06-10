<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 — Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>body{background:#0f1117;color:#e2e8f0;min-height:100vh;display:flex;align-items:center;justify-content:center;}</style>
</head>
<body>
    <div class="text-center">
        <h1 style="font-size:5rem;color:#4fc3f7;">404</h1>
        <p class="text-secondary">The page you are looking for does not exist.</p>
        <a href="${pageContext.request.contextPath}/curriculum/list" class="btn btn-primary mt-3">Go Home</a>
    </div>
</body>
</html>
