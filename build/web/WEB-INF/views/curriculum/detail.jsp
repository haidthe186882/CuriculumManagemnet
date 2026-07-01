<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<c:set var="canDesign" value="${canEdit and curriculum.status == 0}"/>
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
            <%-- Chỉ hiện Edit/Submit khi user thực sự được assign Designer (hoặc là Admin) vào ĐÚNG curriculum này, và curriculum còn ở Draft --%>
            <c:if test="${canDesign}">
                <a href="${pageContext.request.contextPath}/curriculum/edit?id=${curriculum.curriculumId}" class="btn btn-secondary-custom">
                    <i class="bi bi-pencil me-1"></i>Edit
                </a>
                <form method="post" action="${pageContext.request.contextPath}/curriculum" class="d-inline">
                    <input type="hidden" name="action" value="submit">
                    <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                    <button type="submit" class="btn btn-primary-custom" onclick="return confirm('Submit for review?')">
                        <i class="bi bi-send me-1"></i>Submit for Review
                    </button>
                </form>
            </c:if>
            <!-- <a href="${pageContext.request.contextPath}/curriculum/po?id=${curriculum.curriculumId}" class="btn btn-secondary-custom">
                <i class="bi bi-eye me-1"></i>View PO
            </a>-->
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
    <c:if test="${param.msg == 'subjectAdded'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Subject added to curriculum.</div>
    </c:if>
    <c:if test="${param.msg == 'subjectRemoved'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Subject removed from curriculum.</div>
    </c:if>
    <c:if test="${param.msg == 'subjectAddFailed'}">
        <div class="mb-3" style="background: rgba(239,68,68,0.06); border:1px solid rgba(239,68,68,0.18); border-radius:10px; padding: 0.8rem 1rem; color:#b91c1c;">
            <i class="bi bi-exclamation-triangle me-1"></i>Could not add subject — it may already be in this curriculum.
        </div>
    </c:if>
    <c:if test="${param.msg == 'lockedForEdit'}">
        <div class="mb-3" style="background: rgba(255,206,102,0.12); border:1px solid rgba(255,206,102,0.3); border-radius:10px; padding: 0.8rem 1rem; color:#b45309;">
            <i class="bi bi-lock me-1"></i>This curriculum is no longer in Draft, so its subject list is locked.
        </div>
    </c:if>
    <c:if test="${param.msg == 'poAdded'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PO added.</div>
    </c:if>
    <c:if test="${param.msg == 'poDeleted'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PO deleted.</div>
    </c:if>
    <c:if test="${param.msg == 'ploAdded'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PLO added.</div>
    </c:if>
    <c:if test="${param.msg == 'ploDeleted'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PLO deleted.</div>
    </c:if>
    <c:if test="${param.msg == 'mappingSaved'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PO-PLO mapping saved.</div>
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
                    <div class="col-12"><div class="detail-label">Description</div><div class="detail-value" style="white-space:pre-wrap;">${curriculum.description}</div></div>
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
                    <form method="post" action="${pageContext.request.contextPath}/curriculum" class="mb-2">
                        <input type="hidden" name="action" value="approve">
                        <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                        <textarea name="comment" class="form-control form-control-dark mb-2" placeholder="Approval comment (optional)"></textarea>
                        <button type="submit" class="btn btn-success-custom w-100"><i class="bi bi-check-lg me-1"></i>Approve</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/curriculum">
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
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
        <h6 class="mb-0">Subjects in Curriculum</h6>
        <c:if test="${curriculum.status != 0}">
            <span class="text-muted" style="font-size:0.78rem;"><i class="bi bi-lock me-1"></i>Locked (not Draft)</span>
        </c:if>
    </div>

    <c:if test="${canDesign}">
        <div class="p-3 border-bottom">
            <form method="post" action="${pageContext.request.contextPath}/curriculum">
                <input type="hidden" name="action" value="addSubject">
                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                <div class="row g-2 align-items-end">
                    <div class="col-md-5">
                        <label class="detail-label">Subject</label>
                        <select name="subjectId" class="form-control form-control-dark w-100" required>
                            <option value="">-- Select subject to add --</option>
                            <c:forEach var="s" items="${availableSubjects}">
                                <option value="${s.subjectId}">${s.subjectCode} — ${s.subjectName} (${s.credits} cr)</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="detail-label">Semester</label>
                        <input type="number" name="semesterNo" class="form-control form-control-dark w-100" min="1" max="12" value="1" required>
                    </div>
                    <div class="col-md-3">
                        <div class="form-check mt-2">
                            <input type="checkbox" name="isMandatory" class="form-check-input" id="isMandatoryCheck" checked>
                            <label class="form-check-label" for="isMandatoryCheck">Mandatory</label>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-plus-lg me-1"></i>Add</button>
                    </div>
                </div>
            </form>
        </div>
    </c:if>

        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead><tr><th>#</th><th>Code</th><th>Subject</th><th>Semester</th><th>Credits</th><th>Mandatory</th><th>Syllabus</th>
                    <c:if test="${canDesign}"><th>Action</th></c:if>
                </tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty subjects}">
                            <tr><td colspan="${canDesign ? 8 : 7}" class="text-center py-4 text-muted">No subjects linked yet.</td></tr>
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
                                    <c:if test="${canDesign}">
                                        <td>
                                            <form method="post" action="${pageContext.request.contextPath}/curriculum"
                                                  onsubmit="return confirm('Remove this subject from the curriculum?');">
                                                <input type="hidden" name="action" value="removeSubject">
                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                <input type="hidden" name="curriculumSubjectId" value="${cs.curriculumSubjectId}">
                                                <button type="submit" class="btn btn-action btn-danger-custom"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <!-- ===== Program Objectives (POs) ===== -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
            <h6 class="mb-0">Program Objectives (POs)</h6>
            <a href="${pageContext.request.contextPath}/curriculum/po?id=${curriculum.curriculumId}" class="text-muted text-decoration-none" style="font-size:0.78rem;">
                Open full PO/PLO page <i class="bi bi-box-arrow-up-right ms-1"></i>
            </a>
        </div>

        <c:if test="${canDesign}">
            <div class="p-3 border-bottom">
                <form method="post" action="${pageContext.request.contextPath}/curriculum">
                    <input type="hidden" name="action" value="addPo">
                    <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                    <input type="hidden" name="returnTo" value="detail">
                    <div class="row g-2">
                        <div class="col-md-2">
                            <input type="text" name="poCode" class="form-control form-control-dark w-100" placeholder="PO Code (PO1)" required>
                        </div>
                        <div class="col-md-8">
                            <input type="text" name="description" class="form-control form-control-dark w-100" placeholder="Description" required>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-plus-lg me-1"></i>Add PO</button>
                        </div>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th style="width:60px;">#</th>
                        <th style="width:140px;">PO Code</th>
                        <th>Description</th>
                        <c:if test="${canDesign}"><th style="width:80px;">Action</th></c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pos}">
                            <tr><td colspan="${canDesign ? 4 : 3}" class="text-center py-4 text-muted">No POs defined yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="po" items="${pos}" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><strong style="color:#111827;">${po.poCode}</strong></td>
                                    <td>${po.description}</td>
                                    <c:if test="${canDesign}">
                                        <td>
                                            <form method="post" action="${pageContext.request.contextPath}/curriculum"
                                                  onsubmit="return confirm('Delete this PO? Its mappings will also be removed.');">
                                                <input type="hidden" name="action" value="deletePo">
                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                <input type="hidden" name="poId" value="${po.poId}">
                                                <input type="hidden" name="returnTo" value="detail">
                                                <button type="submit" class="btn btn-action btn-danger-custom"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <!-- ===== Program Learning Outcomes (PLOs) ===== -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Program Learning Outcomes (PLOs)</h6></div>

        <c:if test="${canDesign}">
            <div class="p-3 border-bottom">
                <form method="post" action="${pageContext.request.contextPath}/curriculum">
                    <input type="hidden" name="action" value="addPlo">
                    <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                    <input type="hidden" name="returnTo" value="detail">
                    <div class="row g-2">
                        <div class="col-md-2">
                            <input type="text" name="ploCode" class="form-control form-control-dark w-100" placeholder="PLO Code (PLO1)" required>
                        </div>
                        <div class="col-md-8">
                            <input type="text" name="description" class="form-control form-control-dark w-100" placeholder="Description" required>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-plus-lg me-1"></i>Add PLO</button>
                        </div>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="table-responsive">
            <table class="table table-dark-custom table-plo mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">#</th>
                        <th style="width: 200px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">PLO Code</th>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">Description</th>
                        <c:if test="${canDesign}"><th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">Action</th></c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty plos}">
                            <tr><td colspan="${canDesign ? 4 : 3}" class="text-center py-4 text-muted">No PLOs linked yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="plo" items="${plos}" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><strong style="color: #111827;">${plo.ploCode}</strong></td>
                                    <td>${plo.description}</td>
                                    <c:if test="${canDesign}">
                                        <td>
                                            <form method="post" action="${pageContext.request.contextPath}/curriculum"
                                                  onsubmit="return confirm('Delete this PLO? Its mappings will also be removed.');">
                                                <input type="hidden" name="action" value="deletePlo">
                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                <input type="hidden" name="ploId" value="${plo.ploId}">
                                                <input type="hidden" name="returnTo" value="detail">
                                                <button type="submit" class="btn btn-action btn-danger-custom"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <!-- ===== PO ↔ PLO Mapping ===== -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
            <h6 class="mb-0">Mapping POs to PLOs</h6>
            <c:if test="${canDesign and not empty pos and not empty plos}">
                <span class="text-muted" style="font-size:0.8rem;">Tick the boxes, then click Save Mapping</span>
            </c:if>
        </div>

        <c:if test="${canDesign}">
            <form id="mappingFormDetail" method="post" action="${pageContext.request.contextPath}/curriculum">
                <input type="hidden" name="action" value="saveMapping">
                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                <input type="hidden" name="returnTo" value="detail">
        </c:if>

        <div class="table-responsive">
            <table class="table table-dark-custom mb-0 text-center">
                <thead>
                    <tr>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-align: left; padding: 0.9rem; width: 120px;">PLO(s)</th>
                        <c:forEach var="po" items="${pos}">
                            <th style="background-color: var(--accent) !important; color: #ffffff !important; text-align: center; padding: 0.9rem;">${po.poCode}</th>
                        </c:forEach>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty plos or empty pos}">
                            <tr>
                                <td colspan="${pos.size() + 1}" class="text-center py-4 text-muted">Add at least 1 PO and 1 PLO to start mapping.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="plo" items="${plos}">
                                <tr>
                                    <td style="text-align: left; padding: 0.8rem 0.9rem; font-weight: 600; color: #111827;">${plo.ploCode}</td>
                                    <c:forEach var="po" items="${pos}">
                                        <c:set var="mapKey" value="${po.poId}_${plo.ploId}"/>
                                        <td style="padding: 0.8rem 0.9rem; font-size: 1.15rem; font-weight: 600; color: #111827;">
                                            <c:choose>
                                                <c:when test="${canDesign}">
                                                    <input type="checkbox" name="mapKey" value="${mapKey}"
                                                           ${mappings[mapKey] ? 'checked' : ''} style="width:18px;height:18px;cursor:pointer;">
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${mappings[mapKey]}">✓</c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <c:if test="${canDesign}">
            <c:if test="${not empty pos and not empty plos}">
                <div class="p-3 border-top text-end">
                    <button type="submit" form="mappingFormDetail" class="btn btn-primary-custom">
                        <i class="bi bi-save me-1"></i>Save Mapping
                    </button>
                </div>
            </c:if>
            </form>
        </c:if>
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
