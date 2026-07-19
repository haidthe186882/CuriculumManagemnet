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

    <c:if test="${not empty param.error}">
        <div class="mb-3" style="background: rgba(239,68,68,0.06); border:1px solid rgba(239,68,68,0.18); border-radius:10px; padding: 0.8rem 1rem; color:#b91c1c;">
            <i class="bi bi-exclamation-triangle me-1"></i>${param.error}
        </div>
    </c:if>

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
            </div>
            <c:choose>
                <c:when test="${not empty syllabus}">
                    <a href="${pageContext.request.contextPath}/syllabus/detail?id=${syllabus.syllabusId}" class="btn btn-view btn-action">
                        <i class="bi bi-file-earmark-text me-1"></i>View Syllabus
                    </a>
                    <c:choose>
                        <c:when test="${syllabus.statusCode == 2}">
                            <span class="badge-status badge-approved ms-2"><i class="bi bi-check-circle me-1"></i>Approved</span>
                        </c:when>
                        <c:when test="${syllabus.statusCode == 1}">
                            <span class="badge-status badge-rejected ms-2"><i class="bi bi-hourglass me-1"></i>Pending Review</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge-status badge-draft ms-2"><i class="bi bi-pencil me-1"></i>Draft</span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/syllabus/list?keyword=${subject.subjectCode}" class="btn btn-view btn-action">
                        <i class="bi bi-file-earmark-text me-1"></i>View Syllabuses
                    </a>
                </c:otherwise>
            </c:choose>
            <c:if test="${canAddSyllabus}">
                <c:url var="addSyllabusUrl" value="/syllabus/create">
                    <c:param name="subjectCode" value="${subject.subjectCode}"/>
                    <c:if test="${not empty param.curriculumId}">
                        <c:param name="curriculumId" value="${param.curriculumId}"/>
                    </c:if>
                </c:url>
                <a href="${addSyllabusUrl}" class="btn btn-primary-custom">
                    <i class="bi bi-plus-lg me-1"></i>${not empty syllabus and syllabus.statusCode != 2 ? 'Fill Syllabus Content' : 'Add Syllabus'}
                </a>
            </c:if>
        </div>
    </c:if>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
