<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="admin"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Admin Dashboard</div>
            <div class="page-subtitle">Central administration and system settings</div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-md-6 col-lg-3">
            <a href="${pageContext.request.contextPath}/admin/users" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">User Management</div>
                <div class="text-muted mt-2">Create, edit users; assign roles and privileges</div>
            </a>
        </div>
        <div class="col-md-6 col-lg-3">
            <a href="${pageContext.request.contextPath}/program/list" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">Program Management</div>
                <div class="text-muted mt-2">Manage programs and learning outcomes</div>
            </a>
        </div>
        <div class="col-md-6 col-lg-3">
            <a href="${pageContext.request.contextPath}/curriculum/list" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">Curriculum Management</div>
                <div class="text-muted mt-2">Create and maintain curricula and subjects</div>
            </a>
        </div>
        <div class="col-md-6 col-lg-3">
            <a href="${pageContext.request.contextPath}/admin/system" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">System Management</div>
                <div class="text-muted mt-2">System settings, roles and permissions</div>
            </a>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
