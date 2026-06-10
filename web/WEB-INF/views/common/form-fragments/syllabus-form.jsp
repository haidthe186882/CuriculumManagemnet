<form method="post" action="${pageContext.request.contextPath}/syllabus/list">
    <input type="hidden" name="action" value="create">
    <div class="mb-3">
        <label class="detail-label">Subject *</label>
        <select name="subjectId" class="form-select form-select-dark w-100" required>
            <option value="">-- Select Subject --</option>
            <c:forEach var="s" items="${subjects}">
                <option value="${s.subjectId}">${s.subjectCode} — ${s.subjectName}</option>
            </c:forEach>
        </select>
    </div>
    <div class="mb-3">
        <label class="detail-label">Syllabus Name *</label>
        <input type="text" name="syllabusName" class="form-control form-control-dark w-100" required>
    </div>
    <div class="row">
        <div class="col-md-6 mb-3">
            <label class="detail-label">English Name</label>
            <input type="text" name="englishName" class="form-control form-control-dark w-100">
        </div>
        <div class="col-md-3 mb-3">
            <label class="detail-label">Version</label>
            <input type="text" name="version" class="form-control form-control-dark w-100" value="1.0">
        </div>
        <div class="col-md-3 mb-3">
            <label class="detail-label">Min Avg to Pass</label>
            <input type="number" step="0.1" name="minAvgMarkToPass" class="form-control form-control-dark w-100" value="5.0">
        </div>
    </div>
    <div class="row">
        <div class="col-md-6 mb-3">
            <label class="detail-label">Time Allocation</label>
            <input type="text" name="timeAllocation" class="form-control form-control-dark w-100" placeholder="e.g. 45h LT + 45h TH">
        </div>
        <div class="col-md-6 mb-3">
            <label class="detail-label">Scoring Scale</label>
            <input type="text" name="scoringScale" class="form-control form-control-dark w-100" value="10">
        </div>
    </div>
    <div class="mb-3">
        <label class="detail-label">Description</label>
        <textarea name="description" class="form-control form-control-dark w-100"></textarea>
    </div>
    <button type="submit" class="btn btn-primary-custom"><i class="bi bi-save me-1"></i>Create</button>
</form>
