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
	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
	<%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<div style="min-height:100vh;display:flex;align-items:center;justify-content:center;padding:2rem;">
	<div class="card-dark shadow-sm text-center" style="max-width:560px;padding:28px;">
		<h2 style="font-weight:700;color:#111827">Welcome to LTMS</h2>
		<p class="text-muted">Please login to continue</p>
		<div class="d-flex justify-content-center gap-3 mt-3">
			<a class="btn btn-primary-custom" href="${pageContext.request.contextPath}/login">Login</a>
			<a class="btn btn-primary-custom" href="${pageContext.request.contextPath}/login?action=guest">Guest</a>
		</div>
		<p class="mt-4 text-muted" style="font-size:0.9rem">Or, if you already have a session, you will be redirected.</p>
	</div>
</div>
</body>
</html>
