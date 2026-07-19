<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="review"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Syllabus — LTMS</title>
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
            <div class="page-title">Review Syllabus</div>
            <div class="page-subtitle">${syllabus.subject.subjectCode} — ${syllabus.syllabusName} (v${syllabus.version})</div>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/review/list" class="btn btn-secondary-custom">
                <i class="bi bi-arrow-left me-1"></i>Back to Review List
            </a>
        </div>
    </div>

    <!-- Syllabus Summary -->
    <div class="card-dark p-3 mb-4">
        <div class="row g-3">
            <div class="col-md-3">
                <span class="text-muted" style="font-size:0.8rem;">Subject Code</span>
                <div><strong>${syllabus.subject.subjectCode}</strong></div>
            </div>
            <div class="col-md-3">
                <span class="text-muted" style="font-size:0.8rem;">Subject Name</span>
                <div><strong>${syllabus.subject.subjectName}</strong></div>
            </div>
            <div class="col-md-3">
                <span class="text-muted" style="font-size:0.8rem;">Version</span>
                <div><strong>${syllabus.version}</strong></div>
            </div>
            <div class="col-md-3">
                <span class="text-muted" style="font-size:0.8rem;">Status</span>
                <div>
                    <c:choose>
                        <c:when test="${syllabus.status == 'PendingReview'}"><span class="badge bg-warning text-dark">Pending Review</span></c:when>
                        <c:when test="${syllabus.status == 'ChangesRequested'}"><span class="badge bg-danger">Changes Requested</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">${syllabus.status}</span></c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-12">
                <a href="${pageContext.request.contextPath}/syllabus/detail?id=${syllabus.syllabusId}" target="_blank" class="btn btn-sm btn-action btn-view">
                    <i class="bi bi-box-arrow-up-right me-1"></i>View Full Syllabus Details
                </a>
            </div>
        </div>
    </div>

    <!-- Review Rubric Form -->
    <div class="card-dark">
        <div class="p-3 border-bottom">
            <h6 class="mb-0"><i class="bi bi-clipboard-check me-2" style="color:#fbbf24;"></i>Review Rubric (Total: 100 points)</h6>
        </div>
        <c:set var="latestReview" value="${(not empty previousReviews) ? previousReviews[0] : null}"/>
        <form method="post" action="${pageContext.request.contextPath}/review/submit" id="reviewForm">
            <input type="hidden" name="syllabusId" value="${syllabus.syllabusId}">

            <div class="table-responsive">
                <table class="table table-dark-custom mb-0" id="rubricTable">
                    <thead>
                        <tr>
                            <th style="width:5%;">#</th>
                            <th style="width:20%;">Criterion</th>
                            <th style="width:40%;">Guidance</th>
                            <th style="width:10%;">Max Score</th>
                            <th style="width:10%;">Score</th>
                            <th style="width:15%;">Comment</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="crit" items="${criteria}" varStatus="st">
                            <c:set var="prevItem" value="${null}"/>
                            <c:if test="${not empty latestReview and not empty latestReview.items}">
                                <c:forEach var="item" items="${latestReview.items}">
                                    <c:if test="${item.criterionKey eq crit.key}">
                                        <c:set var="prevItem" value="${item}"/>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                            <tr>
                                <td style="vertical-align: middle;">${st.count}</td>
                                <td style="vertical-align: middle;">
                                    <strong>${crit.name}</strong>
                                    <input type="hidden" name="criterionKey" value="${crit.key}">
                                    <input type="hidden" name="criterionName" value="${crit.name}">
                                    <input type="hidden" name="criterionMaxScore" value="${crit.maxScore}">
                                </td>
                                <td style="vertical-align: middle; font-size:0.85rem; color:#9ca3af;">${crit.guidance}</td>
                                <td style="vertical-align: middle; text-align:center;">
                                    <span class="badge bg-info">${crit.maxScore}</span>
                                </td>
                                <td style="vertical-align: middle;">
                                    <input type="number" name="criterionScore" class="form-control rubric-score"
                                           min="0" max="${crit.maxScore}" step="0.5"
                                           value="${not empty prevItem ? prevItem.score : '0'}"
                                           style="width:80px; text-align:center;" required ${readOnly ? 'readonly' : ''}>
                                </td>
                                <td style="vertical-align: middle;">
                                    <input type="text" name="criterionComment" class="form-control"
                                           placeholder="Optional comment"
                                           value="${not empty prevItem ? prevItem.comment : ''}"
                                           style="min-width:150px;" ${readOnly ? 'readonly' : ''}>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr style="background-color: rgba(255,255,255,0.05);">
                            <td colspan="4" class="text-end pe-3"><strong>Total Score</strong></td>
                            <td style="text-align:center;">
                                <strong id="totalScoreDisplay" style="font-size:1.2rem; color:#4fc3f7;">0</strong>
                                <span style="color:#9ca3af;"> / 100</span>
                            </td>
                            <td></td>
                        </tr>
                    </tfoot>
                </table>
            </div>

            <!-- Overall Comment -->
            <div class="p-3 border-top">
                <label class="form-label fw-semibold" for="overallComment">
                    <i class="bi bi-chat-left-text me-1"></i>Overall Comment
                </label>
                <textarea name="overallComment" id="overallComment" class="form-control" rows="3"
                          placeholder="Provide your overall assessment of this syllabus..." ${readOnly ? 'readonly' : ''}>${not empty latestReview ? latestReview.comment : ''}</textarea>
            </div>

            <!-- Action Buttons -->
            <c:if test="${!readOnly}">
            <div class="p-3 border-top d-flex justify-content-between align-items-center">
                <div id="formError" class="text-danger" style="display:none;">
                    <i class="bi bi-exclamation-triangle me-1"></i>
                    <span id="formErrorMessage"></span>
                </div>
                <div id="scoreHint" class="text-warning" style="font-size:0.85rem;">
                    <i class="bi bi-info-circle me-1"></i>
                    Score ≥ 90 → auto-approve for publishing. Score < 90 → send back to Designer for revision.
                </div>
                <div class="d-flex gap-2 ms-auto">
                    <button type="button" class="btn btn-primary btn-lg px-4" onclick="submitReview()">
                        <i class="bi bi-send-check me-1"></i>Submit Review
                    </button>
                </div>
            </div>
            </c:if>
            <c:if test="${readOnly}">
            <div class="p-3 border-top">
                <div class="alert alert-info mb-0">
                    <i class="bi bi-info-circle me-1"></i>You are viewing feedback in read-only mode. Scores and comments below are from the reviewer.
                </div>
            </div>
            </c:if>
        </form>
    </div>

    <!-- Previous Reviews (if any) -->
    <c:if test="${not empty previousReviews}">
        <div class="card-dark mt-4">
            <div class="p-3 border-bottom">
                <h6 class="mb-0"><i class="bi bi-clock-history me-2" style="color:#4fc3f7;"></i>Previous Reviews</h6>
            </div>
            <div class="table-responsive">
                <table class="table table-dark-custom mb-0">
                    <thead><tr><th>Date</th><th>Reviewer</th><th>Score</th><th>Decision</th><th>Comment</th></tr></thead>
                    <tbody>
                        <c:forEach var="rv" items="${previousReviews}">
                            <tr>
                                <td><fmt:formatDate value="${rv.reviewDate}" pattern="dd/MM/yyyy HH:mm"/></td>
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
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Tính tổng score real-time
    document.querySelectorAll('.rubric-score').forEach(function(input) {
        input.addEventListener('input', updateTotalScore);
    });

    function updateTotalScore() {
        var total = 0;
        document.querySelectorAll('.rubric-score').forEach(function(input) {
            var val = parseFloat(input.value);
            if (!isNaN(val)) {
                var max = parseFloat(input.max);
                if (val < 0) val = 0;
                if (val > max) val = max;
                input.value = val;
                total += val;
            }
        });
        document.getElementById('totalScoreDisplay').textContent = total.toFixed(1);
    }

    function submitReview() {
        var scores = document.querySelectorAll('.rubric-score');
        var allFilled = true;
        scores.forEach(function(input) {
            if (input.value === '' || input.value === null) {
                allFilled = false;
            }
        });

        if (!allFilled) {
            document.getElementById('formError').style.display = 'block';
            document.getElementById('formErrorMessage').textContent =
                'Please fill in scores for all criteria before submitting.';
            return;
        }

        document.getElementById('formError').style.display = 'none';
        document.getElementById('reviewForm').submit();
    }

    // Initialize
    updateTotalScore();
</script>
</body>
</html>