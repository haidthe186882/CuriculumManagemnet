<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="prerequisite"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subject Prerequisites — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
    <style>
        .prereq-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: rgba(255, 165, 0, 0.08);
            border: 1px solid rgba(255, 165, 0, 0.18);
            color: #ffa500;
            padding: 3px 10px;
            border-radius: 6px;
            font-size: 0.8rem;
            margin-right: 6px;
            margin-bottom: 6px;
            font-weight: 500;
            transition: all 0.2s ease;
        }
        .prereq-badge:hover {
            background: rgba(255, 165, 0, 0.15);
            border-color: rgba(255, 165, 0, 0.3);
        }
        .prereq-badge .btn-remove {
            background: none;
            border: none;
            padding: 0;
            margin: 0;
            color: #ef4444;
            font-size: 0.85rem;
            cursor: pointer;
            display: flex;
            align-items: center;
        }
        .prereq-badge .btn-remove:hover {
            color: #dc2626;
        }
        .empty-prereqs {
            color: #64748b;
            font-size: 0.85rem;
            font-style: italic;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Subject Prerequisites</div>
            <div class="page-subtitle">Manage required courses and study roadmaps</div>
        </div>
    </div>

    <!-- Add Prerequisite Form (Designer / Admin only) -->
    <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.designer}">
        <div class="card-dark p-4 mb-4">
            <h5 class="mb-3" style="font-weight: 600; color: #1f2937;"><i class="bi bi-link-45deg text-primary me-2"></i>Add Prerequisite Link</h5>
            <form method="post" action="${pageContext.request.contextPath}/subject/prerequisites">
                <input type="hidden" name="action" value="addPrereq">
                <div class="row g-3 align-items-end">
                    <div class="col-md-5">
                        <label class="detail-label">Select Subject</label>
                        <select name="subjectId" class="form-control form-control-dark" required>
                            <option value="">-- Choose Target Subject (Learned After) --</option>
                            <c:forEach var="sub" items="${allSubjects}">
                                <option value="${sub.subjectId}">${sub.subjectCode} — ${sub.subjectName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-5">
                        <label class="detail-label">Requires Passing</label>
                        <select name="requiredSubjectId" class="form-control form-control-dark" required>
                            <option value="">-- Choose Prerequisite Subject (Learned Before) --</option>
                            <c:forEach var="sub" items="${allSubjects}">
                                <option value="${sub.subjectId}">${sub.subjectCode} — ${sub.subjectName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-plus-lg me-1"></i>Link</button>
                    </div>
                </div>
            </form>
        </div>
    </c:if>

    <!-- Search Form -->
    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/subject/prerequisites">
            <div class="row g-2">
                <div class="col-md-10">
                    <div class="input-group">
                        <input type="text" name="keyword" class="search-bar form-control border-end-0"
                               placeholder="Search by Subject Code..." value="${keyword}">
                        <span class="input-group-text bg-white border-start-0" style="border-color: var(--border);">
                            <i class="bi bi-search" style="color: var(--muted);"></i>
                        </span>
                    </div>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-search"></i> Search</button>
                </div>
            </div>
        </form>
    </div>

    <!-- Table of Subjects & Prerequisites -->
    <div class="card-dark">
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th style="width: 5%; cursor: default; user-select: none;"></th>
                        <th style="width: 25%; cursor: default; user-select: none;">Syllabus ID</th>
                        <th style="width: 15%; cursor: default; user-select: none;">Subject Code</th>
                        <th style="width: 25%; cursor: default; user-select: none;">Syllabus Name</th>
                        <th style="width: 30%; cursor: default; user-select: none;">All subjects learn after</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty syllabuses}">
                            <tr><td colspan="5" class="text-center py-5 text-muted">No active syllabuses found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="sy" items="${syllabuses}" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><span class="text-muted" style="font-size:0.8rem;">${sy.syllabusId}</span></td>
                                    <td><code style="color:var(--accent);">${sy.subject.subjectCode}</code></td>
                                    <td class="align-middle">${sy.syllabusName}</td>
                                    <td class="align-middle">
                                        <c:choose>
                                            <c:when test="${empty sy.subjectsLearnAfter}">
                                                <span class="empty-prereqs">None</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="req" items="${sy.subjectsLearnAfter}">
                                                    <span class="prereq-badge">
                                                        <code style="color:#d97706; font-size: 0.75rem;">${req.subjectCode}</code>
                                                        <span>${req.subjectName}</span>
                                                        <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.designer}">
                                                            <form method="post" action="${pageContext.request.contextPath}/subject/prerequisites" style="display:inline;" onsubmit="return confirm('Remove prerequisite link: ${req.subjectCode} requires ${sy.subject.subjectCode}?');">
                                                                <input type="hidden" name="action" value="removePrereq">
                                                                <input type="hidden" name="subjectId" value="${req.subjectId}">
                                                                <input type="hidden" name="requiredSubjectId" value="${sy.subject.subjectId}">
                                                                <button type="submit" class="btn-remove" title="Remove Prerequisite"><i class="bi bi-x-circle-fill"></i></button>
                                                            </form>
                                                        </c:if>
                                                    </span>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
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
