<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="design"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Design Assignments — LTMS</title>
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
            <div class="page-title">My Design Assignments</div>
            <div class="page-subtitle">Curriculums assigned to you for design</div>
        </div>
    </div>

    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/design/list">
            <div class="row g-2">
                <div class="col-md-10">
                    <input type="text" name="keyword" class="search-bar form-control w-100"
                           placeholder="Search by curriculum name, code..." value="${keyword}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100">Search</button>
                </div>
            </div>
        </form>
    </div>

    <div class="card-dark">
        <div class="p-3 border-bottom">
            <h6 class="mb-0"><i class="bi bi-pencil-square me-2" style="color:#4fc3f7;"></i>Assigned Curriculums (${assignments.size()})</h6>
        </div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Code</th>
                        <th>Curriculum Name</th>
                        <th>Major</th>
                        <th>Version</th>
                        <th>Credits</th>
                        <th>Status</th>
                        <th>Assigned Date</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty assignments}">
                            <tr>
                                <td colspan="9" class="text-center py-5 text-muted">
                                    <i class="bi bi-inbox display-6 d-block mb-2"></i>
                                    You have not been assigned to any curriculum yet.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${assignments}" varStatus="st">
                                <tr>
                                    <td class="text-muted">${st.count}</td>
                                    <td><code style="color:#4fc3f7;">${a.curriculum.curriculumCode}</code></td>
                                    <td>
                                        <div class="detail-value">${a.curriculum.curriculumName}</div>
                                        <div class="text-muted" style="font-size:0.78rem;">${a.curriculum.englishName}</div>
                                    </td>
                                    <td class="text-muted">${a.curriculum.majorName}</td>
                                    <td class="text-muted">${a.curriculum.version}</td>
                                    <td>${a.curriculum.totalCredits}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.curriculum.status == 1}">
                                                <span class="badge-status badge-approved"><i class="bi bi-check-circle me-1"></i>Approved</span>
                                            </c:when>
                                            <c:when test="${a.curriculum.status == 2}">
                                                <span class="badge-status badge-rejected"><i class="bi bi-hourglass me-1"></i>Pending</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-status badge-draft"><i class="bi bi-pencil me-1"></i>Draft</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-muted"><fmt:formatDate value="${a.assignedDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/curriculum/detail?id=${a.curriculum.curriculumId}&fromDesign=true" class="btn btn-action btn-view">
                                            <i class="bi bi-eye me-1"></i>Open
                                        </a>
                                    </td>
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
