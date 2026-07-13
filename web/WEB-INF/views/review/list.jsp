<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="review"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review List — LTMS</title>
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
            <div class="page-title">Subject Review</div>
            <div class="page-subtitle">Pending subject approvals and review history</div>
        </div>
        <div class="mb-3">
            <c:if test="${sessionScope.loggedUser.role.roleName == 'Admin'}">
                <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back To Admin Dashboard
                </a>
            </c:if>
        </div>
    </div>

    <c:if test="${param.msg == 'approved'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Subject approved. It now counts as completed in every curriculum that uses it.</div>
    </c:if>
    <c:if test="${param.msg == 'rejected'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-x-circle me-1"></i>Subject sent back to the Designer for revision.</div>
    </c:if>

    <div class="card-dark mb-4">
        <div class="p-3 border-bottom">
            <h6 class="mb-0"><i class="bi bi-hourglass me-2" style="color:#fbbf24;"></i>Pending Subjects (${pendingSyllabuses.size()})</h6>
        </div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead><tr><th>Subject Code</th><th>Subject Name</th><th>Comment</th><th style="width:280px;">Actions</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pendingSyllabuses}">
                            <tr><td colspan="4" class="text-center py-4 text-muted">No subjects pending review.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${pendingSyllabuses}">
                                <tr>
                                    <td><code style="color:#4fc3f7;">${a.subjectCode}</code></td>
                                    <td>${a.subjectName}</td>
                                    <td style="min-width:220px;">
                                        <input type="text" form="cmt-${a.syllabusId}" name="comment"
                                               class="form-control form-control-sm" placeholder="Optional comment">
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/subject/detail?id=${a.subjectId}"
                                           class="btn btn-action btn-view mb-1"><i class="bi bi-eye me-1"></i>View</a>

                                        <form id="cmt-${a.syllabusId}" method="post"
                                              action="${pageContext.request.contextPath}/review/approve" class="d-inline">
                                            <input type="hidden" name="syllabusId" value="${a.syllabusId}">
                                            <button type="submit" class="btn btn-action btn-approve">
                                                <i class="bi bi-check-lg me-1"></i>Approve
                                            </button>
                                        </form>
                                        <button type="submit" form="cmt-${a.syllabusId}"
                                                formaction="${pageContext.request.contextPath}/review/reject"
                                                class="btn btn-action btn-reject">
                                            <i class="bi bi-x-lg me-1"></i>Reject
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/review/list">
            <div class="row g-2">
                <div class="col-md-10">
                    <input type="text" name="keyword" class="search-bar form-control w-100"
                           placeholder="Search review history by subject, reviewer..." value="${keyword}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100">Search</button>
                </div>
            </div>
        </form>
    </div>

    <div class="card-dark">
    <div class="p-3 border-bottom"><h6 class="mb-0">Review History</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead><tr><th>Date</th><th>Subject</th><th>Reviewer</th><th>Status</th><th>Comment</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty reviews}">
                            <tr><td colspan="5" class="text-center py-4 text-muted">No review records.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="rv" items="${reviews}">
                                <tr>
                                    <td><fmt:formatDate value="${rv.reviewDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><code style="color:#4fc3f7;">${rv.subjectCode}</code> ${rv.subjectName}</td>
                                    <td>${rv.reviewer.fullName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${rv.status == 'Approved'}"><span class="badge-status badge-approved">${rv.status}</span></c:when>
                                            <c:otherwise><span class="badge-status badge-rejected">${rv.status}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${rv.comment}</td>
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
