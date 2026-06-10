<form method="post" action="${pageContext.request.contextPath}/subject/list">
    <input type="hidden" name="action" value="create">
    <div class="row">
        <div class="col-md-4 mb-3">
            <label class="detail-label">Subject Code *</label>
            <input type="text" name="subjectCode" class="form-control form-control-dark w-100" required>
        </div>
        <div class="col-md-4 mb-3">
            <label class="detail-label">Credits *</label>
            <input type="number" name="credits" class="form-control form-control-dark w-100" required min="1">
        </div>
        <div class="col-md-4 mb-3">
            <label class="detail-label">Department</label>
            <input type="text" name="department" class="form-control form-control-dark w-100" placeholder="e.g. Computing">
        </div>
    </div>
    <div class="mb-3">
        <label class="detail-label">Subject Name *</label>
        <input type="text" name="subjectName" class="form-control form-control-dark w-100" required>
    </div>
    <div class="mb-3">
        <label class="detail-label">English Name</label>
        <input type="text" name="englishName" class="form-control form-control-dark w-100">
    </div>
    <div class="mb-3">
        <label class="detail-label">Description</label>
        <textarea name="description" class="form-control form-control-dark w-100"></textarea>
    </div>
    <button type="submit" class="btn btn-primary-custom"><i class="bi bi-save me-1"></i>Create</button>
</form>
