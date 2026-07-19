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
            <div class="page-title">Syllabuses Review</div>
            <div class="page-subtitle">Pending approvals and review history</div>
        </div>
    </div>

    <c:if test="${param.msg == 'approved'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Syllabus approved.</div>
    </c:if>
    <c:if test="${param.msg == 'rejected'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-x-circle me-1"></i>Syllabus rejected.</div>
    </c:if>
    <c:if test="${param.msg == 'published'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-cloud-check me-1"></i>Syllabus published.</div>
    </c:if>

    <div class="card-dark mb-4">
        <div class="p-3 border-bottom">
            <h6 class="mb-0"><i class="bi bi-hourglass me-2" style="color:#fbbf24;"></i>Pending Syllabuses (${pendingSyllabuses != null ? pendingSyllabuses.size() : 0})</h6>
        </div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead><tr><th>Code</th><th>Name</th><th>Curriculum</th><th>Status</th><th>Actions</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pendingSyllabuses}">
                            <tr><td colspan="5" class="text-center py-4 text-muted">No pending Syllabuses.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cs" items="${pendingSyllabuses}">
                                <tr>
                                    <td><code class="text-primary">${cs.subject != null ? cs.subject.subjectCode : 'N/A'}</code></td>
                                    <td>${cs.subject != null ? cs.subject.subjectName : 'Unknown Subject'}</td>
                                    <td>${cs.curriculum != null ? cs.curriculum.curriculumCode : 'N/A'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cs.syllabusStatus eq 'PendingReview'}"><span class="badge bg-warning text-dark">Pending</span></c:when>
                                            <c:when test="${cs.syllabusStatus eq 'Draft'}"><span class="badge bg-secondary">Draft</span></c:when>
                                            <c:when test="${cs.syllabusStatus eq 'ApprovedForPublish'}"><span class="badge bg-success">Ready</span></c:when>
                                            <c:when test="${cs.syllabusStatus eq 'ChangesRequested'}"><span class="badge bg-danger">Revise</span></c:when>
                                            <c:when test="${cs.syllabusStatus eq 'Published'}"><span class="badge bg-primary">Published</span></c:when>
                                            <c:otherwise><span class="badge bg-secondary">${cs.syllabusStatus}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/review/form?syllabusId=${cs.syllabusId}" class="btn btn-sm btn-outline-light">
                                            <i class="bi bi-eye me-1"></i>Review
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

    <c:if test="${not empty readyToPublish}">
        <div class="card-dark mb-4">
            <div class="p-3 border-bottom">
                <h6 class="mb-0"><i class="bi bi-cloud-upload me-2" style="color:#4fc3f7;"></i>Ready For Admin Publish (${readyToPublish.size()})</h6>
            </div>
            <div class="table-responsive">
                <table class="table table-dark-custom mb-0">
                    <thead><tr><th>Code</th><th>Name</th><th>Version</th><th>Status</th><th>Action</th></tr></thead>
                    <tbody>
                        <c:forEach var="sy" items="${readyToPublish}">
                            <tr>
                                <td><code class="text-primary">${sy.subject != null ? sy.subject.subjectCode : 'N/A'}</code></td>
                                <td>${sy.syllabusName}</td>
                                <td>${sy.version}</td>
                                <td><span class="badge bg-success">${sy.status}</span></td>
                                <td>
                                    <div class="d-flex gap-1">
                                        <a href="${pageContext.request.contextPath}/syllabus/detail?id=${sy.syllabusId}" class="btn btn-sm btn-outline-light">
                                            <i class="bi bi-box-arrow-up-right me-1"></i>Open
                                        </a>
                                        <form method="post" action="${pageContext.request.contextPath}/review/publish" style="display:inline;">
                                            <input type="hidden" name="syllabusId" value="${sy.syllabusId}">
                                            <button type="submit" class="btn btn-sm btn-success" onclick="return confirm('Publish this syllabus?')">
                                                <i class="bi bi-cloud-check me-1"></i>Publish
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

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
                <thead><tr><th>Date</th><th>Syllabus</th><th>Reviewer</th><th>Total Score</th><th>Status</th><th>Comment</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty reviews}">
                            <tr><td colspan="6" class="text-center py-4 text-muted">No review records.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="rv" items="${reviews}">
                                <tr>
                                    <td><fmt:formatDate value="${rv.reviewDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td>${rv.syllabus.subject.subjectCode} - ${rv.syllabus.syllabusName}</td>
                                    <td>${rv.reviewer.fullName}</td>
                                    <td><fmt:formatNumber value="${rv.totalScore}" minFractionDigits="1" maxFractionDigits="1"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${rv.status == 'Approved'}"><span class="badge bg-success">Approved</span></c:when>
                                            <c:otherwise><span class="badge bg-danger">Rejected</span></c:otherwise>
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