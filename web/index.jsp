<%@ page contentType="text/html;charset=UTF-8" language="java" import="model.User" %>
<%
	User u = null;
	if (session != null) {
		Object o = session.getAttribute("loggedUser");
		if (o instanceof model.User) u = (User) o;
	}
	if (u != null) {
		response.sendRedirect(request.getContextPath() + "/dashboard");
		return;
	}
%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Welcome — LTMS</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<style>body{display:flex;align-items:center;justify-content:center;height:100vh;background:#f8fafc} .card{padding:2rem;max-width:520px;border-radius:12px}</style>
</head>
<body>
<div class="card shadow-sm text-center">
	<h2>Welcome to LTMS</h2>
	<p class="text-muted">Please login or register to continue</p>
	<div class="d-flex justify-content-center gap-3 mt-3">
		<a class="btn btn-primary" href="${pageContext.request.contextPath}/login">Login</a>
		<a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/register">Register</a>
	</div>
	<p class="mt-4 text-muted" style="font-size:0.9rem">Or, if you already have a session, you will be redirected.</p>
</div>
</body>
</html>
