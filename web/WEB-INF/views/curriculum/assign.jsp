<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Assign Staff — LTMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <%@ include file="/WEB-INF/views/common/styles.jsp" %>
        <style>
            .assignee-email{
                display:inline-block;
                font-size:.82rem;
                color:#111827;
                font-weight:500;
            }
            .assignee-row{
                display:flex;
                align-items:center;
                justify-content:space-between;
                gap:6px;
                margin-bottom:4px;
            }
            .btn-remove-assign{
                background:none;
                border:none;
                color:#dc2626;
                font-size:1rem;
                line-height:1;
                padding:0 2px;
                cursor:pointer;
                opacity:.7;
                transition:opacity .12s;
            }
            .btn-remove-assign:hover{
                opacity:1;
            }
            .not-assigned{
                color:var(--muted);
                font-size:.82rem;
                font-style:italic;
            }
            tr.target-highlight:target{
                background:rgba(255,106,0,0.12);
                box-shadow: inset 3px 0 0 var(--accent);
            }
            .import-btn{
                background:#fff;
                border:1px solid var(--border);
                border-radius:10px;
                color:#374151;
                padding:0.68rem 1.1rem;
                font-weight:600;
                font-size:0.9rem;
                display:inline-flex;
                align-items:center;
                gap:6px;
                text-decoration:none;
                transition:all .12s;
                cursor:pointer;
            }
            .import-btn:hover{
                background:#f9fafb;
                border-color:#d1d5db;
                color:#111827;
            }
            .import-btn .bi-file-earmark-excel{
                color:#16a34a;
            }
        </style>
    </head>
    <body>

        <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>

        <div class="main-content">
            <div class="topbar">
                <div>
                    <div class="page-title">Assign Staff</div>
                    <div class="page-subtitle">
                        <code style="color:var(--accent);">${curriculum.curriculumCode}</code> · ${curriculum.curriculumName}
                    </div>
                </div>
                <div class="d-flex gap-2">
                    <button type="button" class="import-btn" data-bs-toggle="modal" data-bs-target="#importAssignModal">
                        <i class="bi bi-file-earmark-excel"></i> Import from Excel
                    </button>
                    <a href="${pageContext.request.contextPath}/curriculum/list" class="btn btn-secondary-custom">
                        <i class="bi bi-arrow-left me-1"></i> Back to List
                    </a>
                </div>
            </div>

            <%-- Alerts --%>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success-dark d-flex align-items-center gap-2 mb-3">
                    <i class="bi bi-check-circle-fill"></i> ${successMessage}
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="mb-3" style="background: rgba(239,68,68,0.06); border:1px solid rgba(239,68,68,0.18); border-radius:10px; padding: 0.8rem 1rem; color:#b91c1c;">
                    <i class="bi bi-exclamation-triangle me-1"></i>${errorMessage}
                </div>
            </c:if>
            <c:if test="${not empty importErrors}">
                <div class="mb-3" style="background: rgba(239,68,68,0.06); border:1px solid rgba(239,68,68,0.18); border-radius:10px; padding: 0.8rem 1rem; color:#b91c1c;">
                    <i class="bi bi-exclamation-triangle me-1"></i>Some rows were skipped: ${importErrors}
                </div>
            </c:if>

            <div class="row g-3">
                <%-- Bulk assign form: gan Designer/Reviewer cho toan bo Curriculum --%>
                <div class="col-lg-4">
                    <div class="card-dark p-3 h-100">
                        <div class="detail-value mb-2"><i class="bi bi-people-fill me-1"></i>Bulk Assign</div>
                        <p class="small text-muted mb-3">
                            Assigns the selected Designer and/or Reviewer to every subject in this
                            curriculum that is not completed yet.
                        </p>
                        <form action="${pageContext.request.contextPath}/curriculum" method="POST">
                            <input type="hidden" name="action" value="assign">
                            <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Designer</label>
                                <select name="designerId" class="form-select form-select-dark w-100">
                                    <option value="">-- Leave Blank / None --</option>
                                    <c:forEach var="d" items="${designers}">
                                        <option value="${d.userId}">${d.fullName} (${d.email})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Reviewer</label>
                                <select name="reviewerId" class="form-select form-select-dark w-100">
                                    <option value="">-- Leave Blank / None --</option>
                                    <c:forEach var="r" items="${reviewers}">
                                        <option value="${r.userId}">${r.fullName} (${r.email})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary-custom w-100">
                                <i class="bi bi-check2-circle me-1"></i>Save Assignments
                            </button>
                        </form>
                    </div>
                </div>

                <%-- Currently assigned staff, per subject --%>
                <div class="col-lg-8">
                    <div class="card-dark p-0 h-100">
                        <div class="p-3 pb-0 detail-value"><i class="bi bi-person-check me-1"></i>Current Assignments</div>
                        <div class="table-responsive">
                            <table class="table table-dark-custom mb-0">
                                <thead>
                                    <tr>
                                        <th>Subject</th>
                                        <th>Designer</th>
                                        <th>Reviewer</th>
                                        <th>Design Status</th>
                                        <c:if test="${not empty subjects}"><th style="min-width:230px;">Reassign</th></c:if>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty subjects}">
                                            <tr>
                                                <td colspan="5" class="text-center py-4 text-muted">
                                                    <i class="bi bi-inbox display-6 d-block mb-2"></i>
                                                    No subjects linked to this curriculum yet.
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="cs" items="${subjects}">
                                                <tr id="subj-${cs.subject.subjectId}" class="target-highlight">
                                                    <td>
                                                        <code style="color:var(--accent);">${cs.subject.subjectCode}</code>
                                                        <div class="text-muted" style="font-size:.8rem;">${cs.subject.subjectName}</div>
                                                    </td>
                                                    <td style="min-width:170px;">
                                                        <c:set var="hasDesigner" value="false"/>
                                                        <c:forEach var="a" items="${assignments}">
                                                            <c:if test="${a.subjectId == cs.subject.subjectId and a.assignmentType == 'Designer'}">
                                                                <c:set var="hasDesigner" value="true"/>
                                                                <div class="assignee-row">
                                                                    <span class="assignee-email">${a.user.email}</span>
                                                                    <form method="post" action="${pageContext.request.contextPath}/curriculum" class="d-inline">
                                                                        <input type="hidden" name="action" value="unassign">
                                                                        <input type="hidden" name="assignmentId" value="${a.assignmentId}">
                                                                        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                                        <button type="submit" class="btn-remove-assign" title="Remove designer" onclick="return confirm('Remove this assignment?');">
                                                                            <i class="bi bi-x-circle"></i>
                                                                        </button>
                                                                    </form>
                                                                </div>
                                                            </c:if>
                                                        </c:forEach>
                                                        <c:if test="${!hasDesigner}"><span class="not-assigned">Not assigned</span></c:if>
                                                    </td>
                                                    <td style="min-width:170px;">
                                                        <c:set var="hasReviewer" value="false"/>
                                                        <c:forEach var="a" items="${assignments}">
                                                            <c:if test="${a.subjectId == cs.subject.subjectId and a.assignmentType == 'Reviewer'}">
                                                                <c:set var="hasReviewer" value="true"/>
                                                                <div class="assignee-row">
                                                                    <span class="assignee-email">${a.user.email}</span>
                                                                    <form method="post" action="${pageContext.request.contextPath}/curriculum" class="d-inline">
                                                                        <input type="hidden" name="action" value="unassign">
                                                                        <input type="hidden" name="assignmentId" value="${a.assignmentId}">
                                                                        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                                        <button type="submit" class="btn-remove-assign" title="Remove reviewer" onclick="return confirm('Remove this assignment?');">
                                                                            <i class="bi bi-x-circle"></i>
                                                                        </button>
                                                                    </form>
                                                                </div>
                                                            </c:if>
                                                        </c:forEach>
                                                        <c:if test="${!hasReviewer}"><span class="not-assigned">Not assigned</span></c:if>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${cs.subject.syllabusStatusCode == 2}">
                                                                <span class="badge-status badge-approved"><i class="bi bi-check-circle me-1"></i>Completed</span>
                                                            </c:when>
                                                            <c:when test="${cs.subject.syllabusStatusCode == 1}">
                                                                <span class="badge-status badge-rejected"><i class="bi bi-hourglass me-1"></i>Pending Review</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge-status badge-draft"><i class="bi bi-pencil me-1"></i>Draft</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:if test="${cs.subject.syllabusStatusCode != 2}">
                                                            <form method="post" action="${pageContext.request.contextPath}/curriculum" class="d-flex gap-1">
                                                                <input type="hidden" name="action" value="assignSubject">
                                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                                <input type="hidden" name="subjectId" value="${cs.subject.subjectId}">
                                                                <input type="hidden" name="returnTo" value="assign">
                                                                <select name="userId" class="form-control form-control-dark form-control-sm" required>
                                                                    <option value="">-- Designer --</option>
                                                                    <c:forEach var="d" items="${designers}">
                                                                        <option value="${d.userId}">${d.fullName}</option>
                                                                    </c:forEach>
                                                                </select>
                                                                <input type="hidden" name="assignmentType" value="Designer">
                                                                <button type="submit" class="btn btn-action btn-view" title="Assign Designer"><i class="bi bi-person-plus"></i></button>
                                                            </form>
                                                            <form method="post" action="${pageContext.request.contextPath}/curriculum" class="d-flex gap-1 mt-1">
                                                                <input type="hidden" name="action" value="assignSubject">
                                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                                <input type="hidden" name="subjectId" value="${cs.subject.subjectId}">
                                                                <input type="hidden" name="returnTo" value="assign">
                                                                <select name="userId" class="form-control form-control-dark form-control-sm" required>
                                                                    <option value="">-- Reviewer --</option>
                                                                    <c:forEach var="r" items="${reviewers}">
                                                                        <option value="${r.userId}">${r.fullName}</option>
                                                                    </c:forEach>
                                                                </select>
                                                                <input type="hidden" name="assignmentType" value="Reviewer">
                                                                <button type="submit" class="btn btn-action btn-view" title="Assign Reviewer"><i class="bi bi-person-plus"></i></button>
                                                            </form>
                                                        </c:if>
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
            </div>
        </div>

        <%-- Import from Excel Modal --%>
        <div class="modal fade" id="importAssignModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <form action="${pageContext.request.contextPath}/curriculum/importAssignExcel" method="POST" enctype="multipart/form-data" class="modal-content bg-white border-0 shadow">
                    <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                    <div class="modal-header border-bottom">
                        <h5 class="modal-title text-dark"><i class="bi bi-file-earmark-excel text-success me-1"></i>Import Assignments from Excel</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body text-dark">
                        <p class="small text-muted mb-3">
                            Upload an Excel file (.xlsx) with columns:
                            <strong>Subject Code, Designer Name, Designer Email, Reviewer Name, Reviewer Email</strong>.
                            The Subject Code must belong to this curriculum, and each email must match a registered
                            Designer/Reviewer account. Leave Designer or Reviewer columns blank to skip that role for a row.
                        </p>
                        <div class="mb-2">
                            <input type="file" name="assignExcelFile" class="form-control" accept=".xlsx,.xls" required>
                        </div>
                    </div>
                    <div class="modal-footer border-top bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary"><i class="bi bi-upload me-1"></i>Import</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
