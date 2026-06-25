<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="syllabus"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Syllabus Detail — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">${syllabus.syllabusName}</div>
            <div class="page-subtitle">${syllabus.subject.subjectCode} — ${syllabus.subject.subjectName}</div>
        </div>
        <div class="d-flex gap-2">

            <a href="${pageContext.request.contextPath}/syllabus/list" class="btn btn-secondary-custom">
                <i class="bi bi-arrow-left me-1"></i>Back
            </a>
        </div>
    </div>

    <div class="card-dark p-4">
        <div class="row">
            <div class="col-md-4"><div class="detail-label">Version</div><div class="detail-value">${syllabus.version}</div></div>
            <div class="col-md-4"><div class="detail-label">Status</div><div class="detail-value">${syllabus.status}</div></div>
            <div class="col-md-4"><div class="detail-label">Time Allocation</div><div class="detail-value">${syllabus.timeAllocation}</div></div>
            <div class="col-md-4"><div class="detail-label">Scoring Scale</div><div class="detail-value">${syllabus.scoringScale}</div></div>
            <div class="col-md-4"><div class="detail-label">Min Avg to Pass</div><div class="detail-value">${syllabus.minAvgMarkToPass}</div></div>
            <div class="col-md-4"><div class="detail-label">Decision No</div><div class="detail-value">${syllabus.decisionNo}</div></div>
            <div class="col-12"><div class="detail-label">Description</div><div class="detail-value">${syllabus.description}</div></div>
            <div class="col-12"><div class="detail-label">Student Tasks</div><div class="detail-value">${syllabus.studentTasks}</div></div>
            <div class="col-12"><div class="detail-label">Tools</div><div class="detail-value">${syllabus.tools}</div></div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
