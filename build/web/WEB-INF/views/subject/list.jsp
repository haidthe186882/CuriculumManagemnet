<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="subject"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subject List — LTMS</title>
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
            <div class="page-title">Subject Management</div>
            <div class="page-subtitle">Browse and manage subjects</div>
        </div>
        <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin'}">
            <a href="${pageContext.request.contextPath}/subject/create" class="btn btn-primary-custom">
                <i class="bi bi-plus-lg me-1"></i>New Subject
            </a>
        </c:if>
    </div>

    <c:if test="${param.msg == 'created' or param.msg == 'updated'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Operation successful.</div>
    </c:if>

    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/subject/list">
            <div class="row g-2">
                <div class="col-md-5">
                    <input type="text" name="keyword" class="search-bar form-control w-100"
                           placeholder="Search by code or name..." value="${keyword}">
                </div>
                <div class="col-md-3">
                    <select name="department" class="form-select form-select-dark w-100">
                        <option value="">All Departments</option>
                        <c:forEach var="d" items="${departments}">
                            <option value="${d}" ${department == d ? 'selected' : ''}>${d}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <input type="number" name="credits" class="form-control form-control-dark w-100"
                           placeholder="Credits" value="${param.credits}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-search"></i> Search</button>
                </div>
            </div>
        </form>
    </div>

    <div class="card-dark">
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr><th>#</th><th>Code</th><th>Subject Name</th><th>Department</th><th>Credits</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty subjects}">
                            <tr><td colspan="6" class="text-center py-5 text-muted">No subjects found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="s" items="${subjects}" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><code style="color:#4fc3f7;">${s.subjectCode}</code></td>
                                    <td>
                                        <div class="detail-value">${s.subjectName}</div>
                                        <div class="text-muted" style="font-size:0.78rem;">${s.englishName}</div>
                                    </td>
                                    <td class="text-muted">${s.department}</td>
                                    <td>${s.credits}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/subject/detail?id=${s.subjectId}" class="btn btn-action btn-view">
                                            <i class="bi bi-eye me-1"></i>View
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
