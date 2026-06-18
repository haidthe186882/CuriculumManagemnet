<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Curriculum List — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>

<!-- Sidebar -->
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>

<!-- Main -->
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Curriculum Management</div>
            <div class="page-subtitle">Browse and manage training programs</div>
        </div>
        
        <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin'}">
            <a href="${pageContext.request.contextPath}/curriculum/create" class="btn btn-primary-custom">
                <i class="bi bi-plus-lg me-1"></i> New Curriculum
            </a>            
        </c:if>
    </div>

    <!-- Alert -->
    <c:if test="${param.msg == 'created'}">
        <div class="alert alert-success-dark d-flex align-items-center gap-2 mb-3">
            <i class="bi bi-check-circle-fill"></i> Curriculum created successfully.
        </div>
    </c:if>
    <c:if test="${param.msg == 'submitted'}">
        <div class="alert alert-success-dark d-flex align-items-center gap-2 mb-3">
            <i class="bi bi-send-check"></i> Curriculum submitted for review.
        </div>
    </c:if>

    <!-- Stats -->
    <div class="row g-3 mb-4">
        <div class="col-6 col-md-3">
            <div class="stat-card">
                <div class="stat-number">${totalCount}</div>
                <div class="stat-label"><i class="bi bi-book me-1"></i>Total Curriculums</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card">
                <div class="stat-number" style="color:#34d399;">
                    <c:set var="approvedCount" value="0"/>
                    <c:forEach var="c" items="${curriculums}">
                        <c:if test="${c.status == 'Approved'}"><c:set var="approvedCount" value="${approvedCount + 1}"/></c:if>
                    </c:forEach>
                    ${approvedCount}
                </div>
                <div class="stat-label"><i class="bi bi-check-circle me-1"></i>Approved</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card">
                <div class="stat-number" style="color:#fbbf24;">
                    <c:set var="pendingCount" value="0"/>
                    <c:forEach var="c" items="${curriculums}">
                        <c:if test="${c.status == 'Pending'}"><c:set var="pendingCount" value="${pendingCount + 1}"/></c:if>
                    </c:forEach>
                    ${pendingCount}
                </div>
                <div class="stat-label"><i class="bi bi-hourglass me-1"></i>Pending Review</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card">
                <div class="stat-number" style="color:#94a3b8;">
                    <c:set var="draftCount" value="0"/>
                    <c:forEach var="c" items="${curriculums}">
                        <c:if test="${c.status == 'Draft'}"><c:set var="draftCount" value="${draftCount + 1}"/></c:if>
                    </c:forEach>
                    ${draftCount}
                </div>
                <div class="stat-label"><i class="bi bi-pencil-square me-1"></i>Draft</div>
            </div>
        </div>
    </div>

    <!-- Search & Filter -->
    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/curriculum/list">
            <div class="row g-2">
                <div class="col-md-6">
                    <div class="input-group">
                           <span class="input-group-text">
                            <i class="bi bi-search"></i>
                        </span>
                           <input type="text" name="keyword" class="search-bar form-control border-start-0"
                               placeholder="Search by name, code..." value="${keyword}">
                    </div>
                </div>
                <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.role.roleName == 'Reviewer'}">
                    <div class="col-md-3">
                        <select name="status" class="form-select form-select-dark w-100">
                            <option value="">All Status</option>
                            <option value="Draft"    ${selectedStatus=='Draft'    ? 'selected' : ''}>Draft</option>
                            <option value="Pending"  ${selectedStatus=='Pending'  ? 'selected' : ''}>Pending Review</option>
                            <option value="Approved" ${selectedStatus=='Approved' ? 'selected' : ''}>Approved</option>
                            <option value="Archived" ${selectedStatus=='Archived' ? 'selected' : ''}>Archived</option>
                        </select>
                    </div>
                </c:if>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100">
                        <i class="bi bi-search me-1"></i>Search
                    </button>
                </div>
                <div class="col-md-1">
                    <a href="${pageContext.request.contextPath}/curriculum/list" class="btn btn-secondary-custom w-100">
                        <i class="bi bi-x"></i>
                    </a>
                </div>
            </div>
        </form>
    </div>

    <!-- Table -->
    <div class="card-dark">
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Code</th>
                        <th>Curriculum Name</th>
                        <th>Program</th>
                        <th>Credits</th>
                        <th>Version</th>
                        <th>Status</th>
                        <th>Decision Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty curriculums}">
                            <tr>
                                <td colspan="9" class="text-center py-5 text-muted">
                                    <i class="bi bi-inbox display-6 d-block mb-2"></i>
                                    No curriculum found matching your search criteria.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="c" items="${curriculums}" varStatus="st">
                                <tr>
                                    <td class="text-muted">${st.count}</td>
                                    <td><code style="color:var(--accent);background:rgba(255,106,0,0.06);padding:2px 8px;border-radius:4px;">${c.curriculumCode}</code></td>
                                    <td>
                                        <div class="detail-value">${c.curriculumName}</div>
                                        <div class="text-muted" style="font-size:0.78rem;">${c.englishName}</div>
                                    </td>
                                    <td class="text-muted">${c.program.programName}</td>
                                    <td><span class="detail-value">${c.totalCredits}</span>
                                        <span class="text-muted" style="font-size:0.8rem;"> cr</span></td>
                                    <td class="text-muted">${c.version}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.status == 'Approved'}">
                                                <span class="badge-status badge-approved"><i class="bi bi-check-circle me-1"></i>Approved</span>
                                            </c:when>
                                            <c:when test="${c.status == 'Pending'}">
                                                <span class="badge-status badge-pending"><i class="bi bi-hourglass me-1"></i>Pending</span>
                                            </c:when>
                                            <c:when test="${c.status == 'Draft'}">
                                                <span class="badge-status badge-draft"><i class="bi bi-pencil me-1"></i>Draft</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-status badge-archived">${c.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-muted">
                                        <c:if test="${not empty c.decisionDate}">
                                            <fmt:formatDate value="${c.decisionDate}" pattern="dd/MM/yyyy"/>
                                        </c:if>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/curriculum/detail?id=${c.curriculumId}"
                                           class="btn btn-action btn-view">
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
