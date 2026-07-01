<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Success / error from Excel import --%>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success-dark d-flex align-items-center gap-2 mb-3" role="alert">
        <i class="bi bi-check-circle-fill"></i> ${successMessage}
    </div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="mb-3" style="background:rgba(239,68,68,0.06);border:1px solid rgba(239,68,68,0.18);border-radius:10px;padding:.8rem 1rem;color:#b91c1c;">
        <i class="bi bi-exclamation-triangle me-1"></i>${errorMessage}
    </div>
</c:if>

<%-- ── Import preview (PLOs / POs / Subjects detected from Excel, pending Create) ── --%>
<c:if test="${not empty sessionScope.pendingImportPlos or not empty sessionScope.pendingImportPos or not empty sessionScope.pendingImportSubjects}">
<div class="card-dark mb-4 p-3" style="border-left:3px solid #16a34a;">
    <div style="font-size:.85rem;font-weight:600;color:#111827;margin-bottom:.75rem;">
        <i class="bi bi-eye me-1" style="color:#16a34a;"></i>Preview — data from Excel (will be saved on Create)
        <c:if test="${not empty sessionScope.pendingImportMappings}">
            &nbsp;·&nbsp;<span style="font-weight:400;font-size:.78rem;color:#6b7280;">${sessionScope.pendingImportMappings.size()} PO-PLO mapping pair(s) detected</span>
        </c:if>
    </div>

    <%-- PLOs preview --%>
    <c:if test="${not empty sessionScope.pendingImportPlos}">
    <div style="font-size:.78rem;font-weight:600;color:#374151;margin-bottom:.35rem;">
        PLOs (${sessionScope.pendingImportPlos.size()})
    </div>
    <div class="table-responsive mb-3">
        <table class="table table-dark-custom mb-0" style="font-size:.8rem;">
            <thead><tr><th style="width:120px;">Code</th><th>Description</th></tr></thead>
            <tbody>
                <c:forEach var="pr" items="${sessionScope.pendingImportPlos}">
                    <tr><td><strong>${pr.ploCode}</strong></td><td>${pr.description}</td></tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    </c:if>

    <%-- POs preview --%>
    <c:if test="${not empty sessionScope.pendingImportPos}">
    <div style="font-size:.78rem;font-weight:600;color:#374151;margin-bottom:.35rem;">
        POs (${sessionScope.pendingImportPos.size()})
    </div>
    <div class="table-responsive mb-3">
        <table class="table table-dark-custom mb-0" style="font-size:.8rem;">
            <thead><tr><th style="width:120px;">Code</th><th>Description</th></tr></thead>
            <tbody>
                <c:forEach var="pr" items="${sessionScope.pendingImportPos}">
                    <tr><td><strong>${pr.poCode}</strong></td><td>${pr.description}</td></tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    </c:if>

    <%-- Subjects preview --%>
    <c:if test="${not empty sessionScope.pendingImportSubjects}">
    <div style="font-size:.78rem;font-weight:600;color:#374151;margin-bottom:.35rem;">
        Subjects (${sessionScope.pendingImportSubjects.size()})
    </div>
    <div class="table-responsive">
        <table class="table table-dark-custom mb-0" style="font-size:.8rem;">
            <thead><tr><th>Code</th><th>Name</th><th style="width:80px;">Semester</th><th style="width:80px;">Credits</th><th>Pre-requisite</th></tr></thead>
            <tbody>
                <c:forEach var="sr" items="${sessionScope.pendingImportSubjects}">
                    <tr>
                        <td><code style="color:var(--accent);">${sr.subjectCode}</code></td>
                        <td>${sr.subjectName}</td>
                        <td>${sr.semesterNo}</td>
                        <td>${sr.credits}</td>
                        <td>${sr.preRequisite}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    </c:if>
</div>
</c:if>

<%-- ── Quick Import bar (shown only for new curriculum or edit) ── --%>
<div class="card-dark mb-4 p-3 d-flex align-items-center justify-content-between flex-wrap gap-3"
     style="border-left:3px solid #16a34a;">
    <div>
        <div style="font-size:.9rem;font-weight:600;color:#111827;">
            <i class="bi bi-file-earmark-excel me-2" style="color:#16a34a;"></i>
            ${isEdit ? 'Re-import from Excel' : 'Import from Excel'}
        </div>
        <div style="font-size:.78rem;color:#6b7280;margin-top:2px;">
            <c:choose>
                <c:when test="${isEdit}">Refreshes Name, English Name, Description, Credits, Decision No/Date, Version, PLOs, POs and PO-PLO mapping.</c:when>
                <c:otherwise>Load all curriculum info, PLOs, POs, mapping, and subjects from a single <code>.xlsx</code> file in one step.</c:otherwise>
            </c:choose>
        </div>
    </div>
    <button type="button" class="btn btn-primary-custom" data-bs-toggle="modal" data-bs-target="#importExcelModal"
            style="white-space:nowrap;">
        <i class="bi bi-upload me-1"></i> Import Excel
    </button>
</div>

<%-- ── Manual form ── --%>
<form method="post" action="${pageContext.request.contextPath}/curriculum/list">
    <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
    <c:if test="${isEdit}"><input type="hidden" name="curriculumId" value="${curriculum.curriculumId}"/></c:if>

    <div class="mb-3">
        <label class="detail-label">Major / Program *</label>
        <select name="majorId" class="form-select form-select-dark w-100" required>
            <option value="">-- Select Major --</option>
            <c:forEach var="m" items="${majors}">
                <option value="${m.majorId}" ${curriculum.majorId == m.majorId ? 'selected' : ''}>
                    <c:out value="${m.majorCode} - ${m.majorName}" default="—"/>
                </option>
            </c:forEach>
        </select>
    </div>

    <div class="row">
        <div class="col-md-4 mb-3">
            <label class="detail-label">Curriculum Code *</label>
            <input type="text" name="curriculumCode" class="form-control form-control-dark w-100"
                   value="${curriculum.curriculumCode}" ${isEdit ? 'readonly' : ''} required>
        </div>
        <div class="col-md-4 mb-3">
            <label class="detail-label">Version</label>
            <input type="text" name="version" class="form-control form-control-dark w-100"
                   value="${curriculum.version}" placeholder="e.g. K20">
        </div>
        <div class="col-md-4 mb-3">
            <label class="detail-label">Total Credits</label>
            <input type="number" name="totalCredits" class="form-control form-control-dark w-100"
                   value="${curriculum.totalCredits}">
        </div>
    </div>

    <div class="mb-3">
        <label class="detail-label">Curriculum Name *</label>
        <input type="text" name="curriculumName" class="form-control form-control-dark w-100"
               value="${curriculum.curriculumName}" required>
    </div>

    <div class="mb-3">
        <label class="detail-label">English Name</label>
        <input type="text" name="englishName" class="form-control form-control-dark w-100"
               value="${curriculum.englishName}">
    </div>

    <div class="mb-3">
        <label class="detail-label">Description</label>
        <textarea name="description" class="form-control form-control-dark w-100"
                  rows="5">${curriculum.description}</textarea>
    </div>

    <div class="row">
        <div class="col-md-6 mb-3">
            <label class="detail-label">Decision No</label>
            <input type="text" name="decisionNo" class="form-control form-control-dark w-100"
                   value="${curriculum.decisionNo}" placeholder="e.g. 577/QĐ-ĐHFPT">
        </div>
        <div class="col-md-6 mb-3">
            <label class="detail-label">Decision Date</label>
            <input type="date" name="decisionDate" class="form-control form-control-dark w-100"
                   value="<c:if test="${not empty curriculum.decisionDate}"><fmt:formatDate value="${curriculum.decisionDate}" pattern="yyyy-MM-dd"/></c:if>">
        </div>
    </div>

    <button type="submit" class="btn btn-primary-custom">
        <i class="bi bi-save me-1"></i>${isEdit ? 'Update' : 'Create'}
    </button>
    <a href="${pageContext.request.contextPath}/curriculum/list" class="btn btn-secondary-custom ms-2">
        <i class="bi bi-arrow-left me-1"></i>Cancel
    </a>
</form>

<%-- Import Modal --%>
<%@ include file="/WEB-INF/views/common/import-modal.jsp" %>
