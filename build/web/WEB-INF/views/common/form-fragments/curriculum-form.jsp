<form method="post" action="${pageContext.request.contextPath}/curriculum/list">
    <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
    <c:if test="${isEdit}"><input type="hidden" name="curriculumId" value="${curriculum.curriculumId}"/></c:if>

    <div class="mb-3">
        <label class="detail-label">Program *</label>
        <select name="programId" class="form-select form-select-dark w-100" required>
            <option value="">-- Select Program --</option>
            <c:forEach var="p" items="${programs}">
                <option value="${p.programId}" ${curriculum.programId == p.programId ? 'selected' : ''}>${p.programCode} — ${p.programName}</option>
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
            <input type="text" name="version" class="form-control form-control-dark w-100" value="${curriculum.version}">
        </div>
        <div class="col-md-4 mb-3">
            <label class="detail-label">Total Credits</label>
            <input type="number" name="totalCredits" class="form-control form-control-dark w-100" value="${curriculum.totalCredits}">
        </div>
    </div>
    <div class="mb-3">
        <label class="detail-label">Curriculum Name *</label>
        <input type="text" name="curriculumName" class="form-control form-control-dark w-100" value="${curriculum.curriculumName}" required>
    </div>
    <div class="mb-3">
        <label class="detail-label">English Name</label>
        <input type="text" name="englishName" class="form-control form-control-dark w-100" value="${curriculum.englishName}">
    </div>
    <div class="mb-3">
        <label class="detail-label">Description</label>
        <textarea name="description" class="form-control form-control-dark w-100">${curriculum.description}</textarea>
    </div>
    <div class="row">
        <div class="col-md-6 mb-3">
            <label class="detail-label">Decision No</label>
            <input type="text" name="decisionNo" class="form-control form-control-dark w-100" value="${curriculum.decisionNo}">
        </div>
    </div>
    <button type="submit" class="btn btn-primary-custom">
        <i class="bi bi-save me-1"></i>${isEdit ? 'Update' : 'Create'}
    </button>
</form>
