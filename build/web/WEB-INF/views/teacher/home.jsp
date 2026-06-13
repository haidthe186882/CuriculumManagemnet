<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Home — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Teacher Dashboard</div>
            <div class="page-subtitle">Access your teaching resources and curriculum tools</div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/curriculum/list" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">Curriculum</div>
                <div class="text-muted mt-2">View and edit curricula you manage</div>
            </a>
        </div>
        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/syllabus/list" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">Syllabus</div>
                <div class="text-muted mt-2">Manage syllabuses and learning outcomes</div>
            </a>
        </div>
        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/teacher/upload" class="card-dark p-4 d-block text-decoration-none">
                <div class="h5">Upload Material</div>
                <div class="text-muted mt-2">Upload teaching materials (documents, slides)</div>
            </a>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
