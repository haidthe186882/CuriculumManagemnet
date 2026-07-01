<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="subject"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${subject.subjectCode} — LTMS</title>
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
            <div class="page-title">${subject.subjectName}</div>
            <div class="page-subtitle"><code style="color:#4fc3f7;">${subject.subjectCode}</code></div>
        </div>
        <a href="${pageContext.request.contextPath}/subject/list" class="btn btn-secondary-custom">
            <i class="bi bi-arrow-left me-1"></i>Back
        </a>
    </div>

    <c:if test="${subject == null}">
        <div class="card-dark p-4 text-center" style="color:#64748b;">Subject not found.</div>
    </c:if>
    <c:if test="${subject != null}">
        <div class="card-dark p-4" style="max-width:700px;">
            <div class="row">
                <div class="col-md-6"><div class="detail-label">English Name</div><div class="detail-value">${subject.englishName}</div></div>
                <div class="col-md-3"><div class="detail-label">Credits</div><div class="detail-value">${subject.credits}</div></div>
                <div class="col-md-3"><div class="detail-label">Department</div><div class="detail-value">${subject.department}</div></div>
                <div class="col-md-3"><div class="detail-label">Status</div><div class="detail-value">${subject.status}</div></div>
                <div class="col-12"><div class="detail-label">Description</div><div class="detail-value">${subject.description}</div></div>
                <div class="col-12 mt-2">
                    <div class="detail-label">Prerequisites</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${empty prerequisites}">
                                <span class="text-muted" style="font-style:italic;">None</span>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="req" items="${prerequisites}">
                                    <span class="badge bg-secondary me-2 p-2" style="font-size:0.85rem; font-weight:500;">
                                        <code style="color:#fff;">${req.subjectCode}</code> — ${req.subjectName}
                                    </span>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <c:choose>
                <c:when test="${not empty syllabus}">
                    <a href="${pageContext.request.contextPath}/syllabus/detail?id=${syllabus.syllabusId}" class="btn btn-view btn-action">
                        <i class="bi bi-file-earmark-text me-1"></i>View Syllabus
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/syllabus/list?keyword=${subject.subjectCode}" class="btn btn-view btn-action">
                        <i class="bi bi-file-earmark-text me-1"></i>View Syllabuses
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
