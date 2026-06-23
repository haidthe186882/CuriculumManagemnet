<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Curriculum Detail — LTMS</title>
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
            <div class="page-title">${curriculum.curriculumName}</div>
            <div class="page-subtitle"><code style="color:var(--accent);">${curriculum.curriculumCode}</code> · ${curriculum.majorName}</div>
        </div>
        <div class="d-flex gap-2">
            <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.designer}">
                <c:if test="${not curriculum.isActive}">
                    <a href="${pageContext.request.contextPath}/curriculum/edit?id=${curriculum.curriculumId}" class="btn btn-secondary-custom">
                        <i class="bi bi-pencil me-1"></i>Edit
                    </a>
                    <form method="post" action="${pageContext.request.contextPath}/curriculum/list" class="d-inline">
                        <input type="hidden" name="action" value="submit">
                        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                        <button type="submit" class="btn btn-primary-custom" onclick="return confirm('Submit for review?')">
                            <i class="bi bi-send me-1"></i>Submit for Review
                        </button>
                    </form>
                </c:if>
            </c:if>
            <a href="${pageContext.request.contextPath}/curriculum/po?id=${curriculum.curriculumId}" class="btn btn-secondary-custom">
                <i class="bi bi-eye me-1"></i>View PO
            </a>
            <a href="${pageContext.request.contextPath}/curriculum/list" class="btn btn-secondary-custom">
                <i class="bi bi-arrow-left me-1"></i>Back
            </a>
        </div>
    </div>

    <c:if test="${param.msg == 'updated'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Updated successfully.</div>
    </c:if>
    <c:if test="${param.msg == 'submitted'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-send-check me-1"></i>Submitted for review.</div>
    </c:if>

    <div class="row g-3 mb-4">
        <div class="col-md-8">
            <div class="card-dark p-4">
                <h6 class="mb-3">General Information</h6>
                <div class="row">
                    <div class="col-md-6"><div class="detail-label">English Name</div><div class="detail-value">${curriculum.englishName}</div></div>
                    <div class="col-md-3"><div class="detail-label">Version</div><div class="detail-value">${curriculum.version}</div></div>
                    <div class="col-md-3"><div class="detail-label">Total Credits</div><div class="detail-value">${curriculum.totalCredits}</div></div>
                    <div class="col-md-6"><div class="detail-label">Decision No</div><div class="detail-value">${curriculum.decisionNo}</div></div>
                    <div class="col-md-6"><div class="detail-label">Decision Date</div>
                        <div class="detail-value"><c:if test="${not empty curriculum.decisionDate}"><fmt:formatDate value="${curriculum.decisionDate}" pattern="dd/MM/yyyy"/></c:if></div>
                    </div>
                    <div class="col-12"><div class="detail-label">Description</div><div class="detail-value">${curriculum.description}</div></div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card-dark p-4">
                <div class="detail-label">isActive</div>
                <div class="mb-3">
                    <c:choose>
                        <c:when test="${curriculum.isActive}">
                            <span class="badge-status badge-approved"><i class="bi bi-check-circle me-1"></i>Active</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge-status badge-draft"><i class="bi bi-pencil me-1"></i>Inactive</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${sessionScope.loggedUser.role.roleName == 'Reviewer' and not curriculum.isActive}">
                    <form method="post" action="${pageContext.request.contextPath}/curriculum/list" class="mb-2">
                        <input type="hidden" name="action" value="approve">
                        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                        <textarea name="comment" class="form-control form-control-dark mb-2" placeholder="Approval comment (optional)"></textarea>
                        <button type="submit" class="btn btn-success-custom w-100"><i class="bi bi-check-lg me-1"></i>Approve</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/curriculum/list">
                        <input type="hidden" name="action" value="reject">
                        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                        <textarea name="comment" class="form-control form-control-dark mb-2" placeholder="Rejection reason" required></textarea>
                        <button type="submit" class="btn btn-danger-custom w-100"><i class="bi bi-x-lg me-1"></i>Reject</button>
                    </form>
                </c:if>
            </div>
        </div>
    </div>

    <div class="card-dark mb-4">
    <div class="p-3 border-bottom"><h6 class="mb-0">Subjects in Curriculum</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead><tr><th>#</th><th>Code</th><th>Subject</th><th>Semester</th><th>Credits</th><th>Mandatory</th><th>Syllabus</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty subjects}">
                            <tr><td colspan="7" class="text-center py-4 text-muted">No subjects linked yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cs" items="${subjects}" varStatus="st">
                                <tr class="subject-row" data-detail-url="${pageContext.request.contextPath}/subject/detail?id=${cs.subject.subjectId}" style="cursor: pointer;">
                                    <td>${st.count}</td>
                                    <td><code style="color:var(--accent);">${cs.subject.subjectCode}</code></td>
                                    <td>${cs.subject.subjectName}</td>
                                    <td>${cs.semesterNo}</td>
                                    <td>${cs.subject.credits}</td>
                                    <td><c:if test="${cs.mandatory}"><i class="bi bi-check-circle text-success"></i></c:if></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty cs.subject.syllabusId}">
                                                <a href="${pageContext.request.contextPath}/syllabus/detail?id=${cs.subject.syllabusId}" class="btn btn-sm btn-outline-info text-decoration-none py-1 px-2" style="font-size: 0.78rem;">
                                                    <i class="bi bi-file-earmark-text me-1"></i>View Syllabus
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted" style="font-size:0.8rem;">No Syllabus</span>
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

    <!-- PLO Table Section -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Program Learning Outcomes (PLOs)</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom table-plo mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">#</th>
                        <th style="width: 200px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">PLO Name</th>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">PLO Description</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty plos}">
                            <tr><td colspan="3" class="text-center py-4 text-muted">No PLOs linked yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="plo" items="${plos}" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><strong style="color: #111827;">${plo.ploCode}</strong></td>
                                    <td>${plo.description}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Reviewer' or sessionScope.loggedUser.role.roleName == 'Admin'}">
        <div class="card-dark">
        <div class="p-3 border-bottom"><h6 class="mb-0">Review History</h6></div>
            <div class="table-responsive">
                <table class="table table-dark-custom mb-0">
                    <thead><tr><th>Date</th><th>Reviewer</th><th>Status</th><th>Comment</th></tr></thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty reviews}">
                                <tr><td colspan="4" class="text-center py-4 text-muted">No reviews yet.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="rv" items="${reviews}">
                                    <tr>
                                        <td><fmt:formatDate value="${rv.reviewDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>${rv.reviewer.fullName}</td>
                                        <td>${rv.status}</td>
                                        <td>${rv.comment}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const subjectRows = document.querySelectorAll('.subject-row');
        subjectRows.forEach(row => {
            row.addEventListener('click', function(e) {
                if (e.target.closest('a, button')) return;
                const detailUrl = this.getAttribute('data-detail-url');
                if (detailUrl) window.location.href = detailUrl;
            });
            row.addEventListener('mouseenter', function() {
                this.style.backgroundColor = 'rgba(79, 195, 247, 0.08)';
            });
            row.addEventListener('mouseleave', function() {
                this.style.backgroundColor = '';
            });
        });
    });
</script>
</body>
</html>
