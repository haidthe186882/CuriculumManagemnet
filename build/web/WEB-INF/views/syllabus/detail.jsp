<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="syllabus" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Syllabus Detail — LTMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
              rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">
        <%@ include file="/WEB-INF/views/common/styles.jsp" %>
    </head>

    <body>
        <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
        <div class="main-content">
            <c:if test="${param.msg == 'submitted'}">
                <div class="alert alert-success-dark mb-3"><i class="bi bi-send-check me-1"></i>Syllabus has been submitted to reviewer.</div>
            </c:if>
            <c:if test="${param.msg == 'approvedForPublish'}">
                <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Reviewer accepted the syllabus and sent it to admin for publication.</div>
            </c:if>
            <c:if test="${param.msg == 'changesRequested'}">
                <div class="alert alert-danger mb-3"><i class="bi bi-arrow-counterclockwise me-1"></i>Reviewer requested changes. Designer can revise and resubmit.</div>
            </c:if>
            <c:if test="${param.msg == 'published'}">
                <div class="alert alert-success-dark mb-3"><i class="bi bi-cloud-check me-1"></i>Syllabus is now published.</div>
            </c:if>
            <div class="topbar">
                <div>
                    <div class="page-title">${syllabus.syllabusName}</div>
                    <div class="page-subtitle">${syllabus.subject.subjectCode} —
                        ${syllabus.subject.subjectName}</div>
                </div>
                <div class="d-flex gap-2">
                    <c:if test="${canEditSyllabus}">
                        <a href="${pageContext.request.contextPath}/syllabus/edit?id=${syllabus.syllabusId}"
                           class="btn btn-primary-custom">
                            <i class="bi bi-pencil-square me-1"></i>Edit
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/syllabus/list"
                       class="btn btn-secondary-custom">
                        <i class="bi bi-arrow-left me-1"></i>Back
                    </a>
                </div>
            </div>

            <div class="card-dark p-4">
                <div class="row">
                    <div class="col-md-4">
                        <div class="detail-label">Subject Code</div>
                        <div class="detail-value"><code style="color:var(--accent);">${syllabus.subject.subjectCode}</code></div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Credits</div>
                        <div class="detail-value">${syllabus.subject.credits}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Version</div>
                        <div class="detail-value">${syllabus.version}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Status</div>
                        <div class="detail-value">
                            <c:choose>
                                <c:when test="${syllabus.status == 'Published' or syllabus.status == 'ApprovedForPublish'}"><span class="badge-status badge-approved">${syllabus.status}</span></c:when>
                                <c:when test="${syllabus.status == 'ChangesRequested'}"><span class="badge-status badge-rejected">${syllabus.status}</span></c:when>
                                <c:otherwise><span class="badge-status badge-draft">${syllabus.status}</span></c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Time Allocation</div>
                        <div class="detail-value">${syllabus.timeAllocation}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Scoring Scale</div>
                        <div class="detail-value">${syllabus.scoringScale}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Min Avg to Pass</div>
                        <div class="detail-value">${syllabus.minAvgMarkToPass}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Decision No</div>
                        <div class="detail-value">${syllabus.decisionNo}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Approved Date</div>
                        <div class="detail-value"><fmt:formatDate value="${syllabus.approvedDate}" pattern="dd/MM/yyyy"/></div>
                    </div>
                    <c:if test="${not empty syllabus.englishName}">
                        <div class="col-12">
                            <div class="detail-label">English Name</div>
                            <div class="detail-value">${syllabus.englishName}</div>
                        </div>
                    </c:if>
                    <div class="col-12">
                        <div class="detail-label">Description</div>
                        <div class="detail-value" style="white-space:pre-wrap;">${syllabus.description}</div>
                    </div>
                    <div class="col-12">
                        <div class="detail-label">Student Tasks</div>
                        <div class="detail-value">${syllabus.studentTasks}</div>
                    </div>
                    <div class="col-12">
                        <div class="detail-label">Tools</div>
                        <div class="detail-value">${syllabus.tools}</div>
                    </div>
                    <c:if test="${not empty latestReview}">
                        <div class="col-md-4 mt-3">
                            <div class="detail-label">Latest Review</div>
                            <div class="detail-value"><fmt:formatDate value="${latestReview.reviewDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                        </div>
                        <div class="col-md-4 mt-3">
                            <div class="detail-label">Reviewer</div>
                            <div class="detail-value">${latestReview.reviewer.fullName}</div>
                        </div>
                        <div class="col-md-4 mt-3">
                            <div class="detail-label">Total Score</div>
                            <div class="detail-value"><fmt:formatNumber value="${latestReview.totalScore}" minFractionDigits="1" maxFractionDigits="1"/> / <fmt:formatNumber value="${rubricMaximumScore}" minFractionDigits="0" maxFractionDigits="0"/></div>
                        </div>
                    </c:if>
                    <c:if test="${sessionScope.loggedUser.role.roleName != 'Guest'}">
                        <div class="col-12 mt-2">
                            <c:choose>
                                <c:when test="${empty materials}">
                                    <button class="btn btn-primary-custom" id="downloadMaterialBtn" disabled title="No material link available">
                                        <i class="bi bi-download me-1"></i>Download Material (Unavailable)
                                    </button>
                                </c:when>
                                <c:when test="${materials.size() == 1}">
                                    <a href="${materials[0].link}"
                                       target="_blank" class="btn btn-primary-custom" id="downloadMaterialBtn">
                                        <i class="bi bi-download me-1"></i>Download Material
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <div class="dropdown d-inline-block">
                                        <button class="btn btn-primary-custom dropdown-toggle" type="button" id="downloadMaterialBtn" data-bs-toggle="dropdown" aria-expanded="false">
                                            <i class="bi bi-download me-1"></i>Download Material
                                        </button>
                                        <ul class="dropdown-menu shadow" aria-labelledby="downloadMaterialBtn" style="border-radius: 10px; border: 1px solid var(--border); padding: 6px 0; min-width: 280px;">
                                            <c:forEach var="mat" items="${materials}">
                                                <li>
                                                    <a class="dropdown-item py-2 px-3" href="${mat.link}" target="_ blank" style="font-size: 0.9rem; color: #1f2937; white-space: normal; font-weight: 500;">
                                                        ${mat.materialDescription}
                                                    </a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>

            <c:if test="${canPublishSyllabus}">
                <div class="card-dark mt-4 p-4">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div>
                            <h6 class="mb-1">Admin Publication</h6>
                            <div class="text-muted">Reviewer accepted this syllabus. Admin can publish it for learners.</div>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/review/publish">
                            <input type="hidden" name="syllabusId" value="${syllabus.syllabusId}">
                            <button type="submit" class="btn btn-primary-custom"><i class="bi bi-cloud-upload me-1"></i>Publish Syllabus</button>
                        </form>
                    </div>
                </div>
            </c:if>

            <c:if test="${canReviewSyllabus}">
                <div class="card-dark mt-4 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                        <div>
                            <h6 class="mb-1">Syllabus Review Rubric</h6>
                            <div class="text-muted">Reviewer scores each section using a common rubric. Every section includes a comment box for feedback to designer.</div>
                        </div>
                        <div class="badge bg-warning text-dark">Maximum: <fmt:formatNumber value="${rubricMaximumScore}" minFractionDigits="0" maxFractionDigits="0"/> points</div>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/review/submit">
                        <input type="hidden" name="syllabusId" value="${syllabus.syllabusId}">
                        <div class="table-responsive">
                            <table class="table table-dark-custom mb-3">
                                <thead>
                                    <tr>
                                        <th>Section</th>
                                        <th>Guidance</th>
                                        <th style="width: 120px;">Max</th>
                                        <th style="width: 160px;">Score</th>
                                        <th>Reviewer Comment</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="criterion" items="${reviewRubric}">
                                        <tr>
                                            <td>
                                                <strong>${criterion.name}</strong>
                                                <input type="hidden" name="criterionKey" value="${criterion.key}">
                                                <input type="hidden" name="criterionName" value="${criterion.name}">
                                                <input type="hidden" name="criterionMaxScore" value="${criterion.maxScore}">
                                            </td>
                                            <td style="white-space: normal;">${criterion.guidance}</td>
                                            <td><fmt:formatNumber value="${criterion.maxScore}" minFractionDigits="0" maxFractionDigits="0"/></td>
                                            <td><input type="number" step="0.5" min="0" max="${criterion.maxScore}" name="criterionScore" class="form-control form-control-dark" required></td>
                                            <td><textarea name="criterionComment" class="form-control form-control-dark" rows="2" placeholder="Comment for this section..."></textarea></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="mb-3">
                            <label class="detail-label">Overall Reviewer Comment</label>
                            <textarea name="overallComment" class="form-control form-control-dark" rows="3" placeholder="Summary feedback for designer and admin..."></textarea>
                        </div>
                        <div class="d-flex gap-2 flex-wrap">
                            <button type="submit" name="decision" value="approve" class="btn btn-success-custom"><i class="bi bi-check-lg me-1"></i>Accept And Send To Admin</button>
                            <button type="submit" name="decision" value="reject" class="btn btn-danger-custom"><i class="bi bi-arrow-counterclockwise me-1"></i>Reject And Return To Designer</button>
                        </div>
                    </form>
                </div>
            </c:if>

            <!-- CLO Table Card -->
            <div class="card-dark mt-4">
                <div class="p-3 border-bottom">
                    <h6 class="mb-0">Course Learning Outcomes (CLOs)</h6>
                </div>
                <div class="table-responsive">
                    <table class="table table-dark-custom mb-0">
                        <thead>
                            <tr>
                                <th
                                    style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                    #</th>
                                <th
                                    style="width: 200px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                    CLO Name</th>
                                <th
                                    style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                    CLO Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty clos}">
                                    <tr>
                                        <td colspan="3" class="text-center py-4 text-muted">No CLOs defined
                                            for this syllabus yet.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="clo" items="${clos}" varStatus="st">
                                        <tr>
                                            <td style="padding: 0.8rem 0.9rem;">${st.count}</td>
                                            <td style="padding: 0.8rem 0.9rem;"><strong
                                                    style="color: #111827;">${clo.cloCode}</strong></td>
                                            <td style="padding: 0.8rem 0.9rem;">${clo.description}</td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
                <c:if test="${not empty clos}">
                    <div class="p-3 border-top">
                        <a href="${pageContext.request.contextPath}/syllabus/clo-mapping?id=${syllabus.syllabusId}">
                            <i class="bi bi-diagram-3 me-1"></i>View mapping of CLOs to PLOs
                        </a>
                    </div>
                </c:if>
            </div>

            <div class="card-dark mt-4">
                <div class="p-3 border-bottom">
                    <h6 class="mb-0">Program Alignment</h6>
                </div>
                <div class="p-3">
                    <c:choose>
                        <c:when test="${empty curriculumAlignments}">
                            <div class="text-muted">
                                <i class="bi bi-info-circle me-1"></i>
                                This subject <strong>${syllabus.subject.subjectCode}</strong> is not linked to any curriculum yet, 
                                so PO/PLO alignment cannot be shown.
                                <c:if test="${sessionScope.loggedUser.role.roleName == 'Admin'}">
                                    <br><small>To enable alignment, add this subject to a curriculum via the Curriculum detail page.</small>
                                </c:if>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="align" items="${curriculumAlignments}" varStatus="alignStatus">
                                <div class="card-dark mb-4" style="box-shadow:none;">
                                    <div class="p-3 border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
                                        <div>
                                            <div style="font-weight: 700; color: #111827;">
                                                ${align.curriculum.curriculumCode} - ${align.curriculum.curriculumName}
                                            </div>
                                            <div class="text-muted">POs, PLOs and CLO-PLO mapping for this curriculum.</div>
                                        </div>
                                        <div class="d-flex align-items-center flex-wrap gap-2">
                                            <span class="badge-status badge-draft">${align.pos.size()} PO(s) · ${align.plos.size()} PLO(s)</span>
                                            <a href="${pageContext.request.contextPath}/curriculum/detail?id=${align.curriculum.curriculumId}"
                                               class="text-muted text-decoration-none" style="font-size:0.78rem;">
                                                Open curriculum <i class="bi bi-box-arrow-up-right ms-1"></i>
                                            </a>
                                        </div>
                                    </div>

                                    <div class="row g-3 p-3">
                                        <div class="col-lg-6">
                                            <div class="card-dark h-100" style="box-shadow:none; border:1px solid var(--border);">
                                                <div class="p-3 border-bottom"><h6 class="mb-0">Program Objectives (POs)</h6></div>
                                                <div class="table-responsive">
                                                    <table class="table table-dark-custom mb-0">
                                                        <thead>
                                                            <tr>
                                                                <th style="width: 70px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">#</th>
                                                                <th style="width: 140px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">PO Code</th>
                                                                <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">Description</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:choose>
                                                                <c:when test="${empty align.pos}">
                                                                    <tr><td colspan="2" class="text-center py-4 text-muted">No POs defined for this curriculum.</td></tr>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:forEach var="po" items="${align.pos}" varStatus="poStatus">
                                                                        <tr>
                                                                            <td>${poStatus.count}</td>
                                                                            <td><strong style="color: #111827;">${po.poCode}</strong></td>
                                                                            <td style="white-space: pre-line;">${po.description}</td>
                                                                        </tr>
                                                                    </c:forEach>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-lg-6">
                                            <div class="card-dark h-100" style="box-shadow:none; border:1px solid var(--border);">
                                                <div class="p-3 border-bottom"><h6 class="mb-0">Program Learning Outcomes (PLOs)</h6></div>
                                                <div class="table-responsive">
                                                    <table class="table table-dark-custom mb-0">
                                                        <thead>
                                                            <tr>
                                                                <th style="width: 70px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">#</th>
                                                                <th style="width: 140px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">PLO Code</th>
                                                                <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal;">Description</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:choose>
                                                                <c:when test="${empty align.plos}">
                                                                    <tr><td colspan="2" class="text-center py-4 text-muted">No PLOs defined for this curriculum.</td></tr>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:forEach var="plo" items="${align.plos}" varStatus="ploStatus">
                                                                        <tr>
                                                                            <td>${ploStatus.count}</td>
                                                                            <td><strong style="color: #111827;">${plo.ploCode}</strong></td>
                                                                            <td style="white-space: pre-line;">${plo.description}</td>
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

                                    <div class="card-dark mx-3 mb-3" style="box-shadow:none; border:1px solid var(--border);">
                                        <div class="p-3 border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
                                            <h6 class="mb-0">CLO to PLO Mapping</h6>
                                            <span class="text-muted" style="font-size: 0.9rem;">Rows: CLOs of this syllabus · Columns: PLOs of this curriculum</span>
                                        </div>
                                        <div class="table-responsive">
                                            <table class="table table-dark-custom mb-0 text-center">
                                                <thead>
                                                    <tr>
                                                        <th style="width: 140px; background-color: var(--accent) !important; color: #ffffff !important; text-align: left; padding: 0.9rem;">CLO</th>
                                                        <th style="min-width: 260px; background-color: var(--accent) !important; color: #ffffff !important; text-align: left; padding: 0.9rem;">Description</th>
                                                        <c:forEach var="plo" items="${align.plos}">
                                                            <th class="text-center" style="min-width: 90px; background-color: var(--accent) !important; color: #ffffff !important; padding: 0.9rem;">${plo.ploCode}</th>
                                                        </c:forEach>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:choose>
                                                        <c:when test="${empty clos}">
                                                            <tr>
                                                                <td colspan="${align.plos.size() + 2}" class="text-center py-4 text-muted">No CLOs defined for this syllabus yet.</td>
                                                            </tr>
                                                        </c:when>
                                                        <c:when test="${empty align.plos}">
                                                            <tr>
                                                                <td colspan="2" class="text-center py-4 text-muted">No PLOs available, so mapping cannot be shown.</td>
                                                            </tr>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:forEach var="clo" items="${clos}">
                                                                <tr>
                                                                    <td style="text-align: left; padding: 0.8rem 0.9rem;"><strong style="color: #111827;">${clo.cloCode}</strong></td>
                                                                    <td style="text-align: left; white-space: pre-line; padding: 0.8rem 0.9rem;">${clo.description}</td>
                                                                    <c:forEach var="plo" items="${align.plos}">
                                                                        <c:set var="mapKey" value="${plo.ploId}_${clo.cloId}" />
                                                                        <td class="text-center" style="padding: 0.8rem 0.9rem; font-size: 1.05rem; font-weight: 700; color: #111827;">
                                                                            <c:choose>
                                                                                <c:when test="${align.ploCloMappings[mapKey]}">✓</c:when>
                                                                                <c:otherwise><span class="text-muted">-</span></c:otherwise>
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
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Sessions Table Card -->
            <c:if test="${sessionScope.loggedUser.role.roleName != 'Guest'}">
                <div class="card-dark mt-4">
                    <div class="p-3 border-bottom">
                        <h6 class="mb-0">Sessions</h6>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-dark-custom mb-0">
                            <thead>
                                <tr>
                                    <th
                                        style="width: 70px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        Session</th>
                                    <th
                                        style="width: 220px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        Topic</th>
                                    <th
                                        style="width: 140px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        Learning-Teaching Type</th>
                                    <th
                                        style="width: 250px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        LO</th>
                                    <th
                                        style="width: 150px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        ITU</th>
                                    <th
                                        style="width: 180px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        Student Materials</th>
                                    <th
                                        style="width: 220px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        Student's Tasks</th>
                                    <th
                                        style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem; font-size: 0.95rem;">
                                        URLs</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty sessions}">
                                        <tr>
                                            <td colspan="8" class="text-center py-4 text-muted">No sessions
                                                defined for this syllabus yet.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="sItem" items="${sessions}">
                                            <tr>
                                                <td style="padding: 0.8rem 0.9rem;">${sItem.sessionNo}</td>
                                                <td style="padding: 0.8rem 0.9rem; white-space: pre-line;">${sItem.topic}</td>
                                                <td style="padding: 0.8rem 0.9rem; white-space: pre-line;">${sItem.learningTeachingType}</td>
                                                <td style="padding: 0.8rem 0.9rem; white-space: pre-line;">${sItem.lo}</td>
                                                <td style="padding: 0.8rem 0.9rem; white-space: pre-line;">${sItem.itu}</td>
                                                <td style="padding: 0.8rem 0.9rem; white-space: pre-line;">${sItem.studentMaterials}</td>
                                                <td style="padding: 0.8rem 0.9rem; white-space: pre-line;">${sItem.studentTasks}</td>
                                                <td style="padding: 0.8rem 0.9rem;">
                                                    <c:if test="${not empty sItem.urls}">
                                                        <a href="${sItem.urls}" target="_blank"
                                                           style="color: #0288d1; text-decoration: none; font-weight: 500;">
                                                            Link
                                                        </a>
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
            </c:if>

            <c:if test="${not empty reviews}">
                <div class="card-dark mt-4">
                    <div class="p-3 border-bottom">
                        <h6 class="mb-0">Review History</h6>
                    </div>
                    <div class="p-3">
                        <c:forEach var="review" items="${reviews}">
                            <div class="border rounded-3 p-3 mb-3" style="border-color: var(--border) !important; background: rgba(255,255,255,0.55);">
                                <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
                                    <div>
                                        <div style="font-weight: 700; color: #111827;">${review.reviewer.fullName}</div>
                                        <div class="text-muted"><fmt:formatDate value="${review.reviewDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                                    </div>
                                    <div class="text-end">
                                        <div>
                                            <c:choose>
                                                <c:when test="${review.status == 'Approved'}"><span class="badge-status badge-approved">${review.status}</span></c:when>
                                                <c:otherwise><span class="badge-status badge-rejected">${review.status}</span></c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="mt-1 text-muted">Score: <fmt:formatNumber value="${review.totalScore}" minFractionDigits="1" maxFractionDigits="1"/> / <fmt:formatNumber value="${rubricMaximumScore}" minFractionDigits="0" maxFractionDigits="0"/></div>
                                    </div>
                                </div>
                                <c:if test="${not empty review.comment}">
                                    <div class="mb-3">
                                        <div class="detail-label">Overall Comment</div>
                                        <div class="detail-value" style="white-space: pre-wrap;">${review.comment}</div>
                                    </div>
                                </c:if>
                                <div class="table-responsive">
                                    <table class="table table-dark-custom mb-0">
                                        <thead>
                                            <tr>
                                                <th>Section</th>
                                                <th>Score</th>
                                                <th>Comment</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="item" items="${review.items}">
                                                <tr>
                                                    <td>${item.criterionName}</td>
                                                    <td><fmt:formatNumber value="${item.score}" minFractionDigits="1" maxFractionDigits="1"/> / <fmt:formatNumber value="${item.maxScore}" minFractionDigits="0" maxFractionDigits="0"/></td>
                                                    <td style="white-space: pre-wrap;">${item.comment}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>

</html>
