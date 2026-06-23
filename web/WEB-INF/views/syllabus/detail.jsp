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

    <!-- CLO Table Card -->
    <div class="card-dark mt-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Course Learning Outcomes (CLOs)</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">#</th>
                        <th style="width: 200px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">CLO Name</th>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">CLO Description</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty clos}">
                            <tr><td colspan="3" class="text-center py-4 text-muted">No CLOs defined for this syllabus yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="clo" items="${clos}" varStatus="st">
                                <tr>
                                    <td style="padding: 0.8rem 0.9rem;">${st.count}</td>
                                    <td style="padding: 0.8rem 0.9rem;"><strong style="color: #111827;">${clo.cloCode}</strong></td>
                                    <td style="padding: 0.8rem 0.9rem;">${clo.description}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
