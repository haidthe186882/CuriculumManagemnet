<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 — Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<div style="min-height:100vh;display:flex;align-items:center;justify-content:center;padding:2rem;">
    <div class="card-dark" style="max-width:720px;text-align:center;padding:48px;">
        <div style="font-size:4.25rem;color:var(--accent);font-weight:700">404</div>
        <h4 style="margin-top:12px;color:#111827">Page not found</h4>
        <p style="color:var(--muted)">The page you are looking for does not exist.</p>
        <a href="${pageContext.request.contextPath}/curriculum/list" class="btn btn-primary-custom mt-3">Go Home</a>
    </div>
</div>
</body>
</html>
