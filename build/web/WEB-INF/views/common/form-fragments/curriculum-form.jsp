<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form method="post" action="${pageContext.request.contextPath}/curriculum/list">
    <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
    <c:if test="${isEdit}"><input type="hidden" name="curriculumId" value="${curriculum.curriculumId}"/></c:if>

    <div class="mb-3">
    <label class="detail-label">Major / Program</label>
    <select name="majorId" class="form-select form-select-dark w-100" required>
        <option value="">-- Select Major --</option>
        <c:forEach var="m" items="${majors}">
            <option value="${m.majorId}" ${curriculum.majorId == m.majorId ? 'selected' : ''}>
                <c:out value="${m.majorCode} - ${m.majorName}" default="Lỗi phân tích cú pháp JavaBeans" />
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
        <div class="col-md-4 mb-3">
            <label class="detail-label">Total Credits</label>
            <input type="number" name="totalCredits" class="form-control form-control-dark w-100" value="${curriculum.totalCredits}">
        </div>
    <button type="submit" class="btn btn-primary-custom">
        <i class="bi bi-save me-1"></i>${isEdit ? 'Update' : 'Create'}
    </button>
</form>
    
    <c:if test="${!isEdit}">
    <div class="container-fluid px-4 mt-3">
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="card mb-4 border-secondary bg-dark text-white">
            <div class="card-header bg-secondary text-white d-flex align-items-center">
                <i class="bi bi-file-earmark-excel me-2 fs-5 text-warning"></i>
                <strong class="card-title mb-0">Quick Import Curriculum from Excel</strong>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/curriculum/importExcel" enctype="multipart/form-data">
                    <div class="row align-items-center g-3">
                        <div class="col-md-3">
                            <label class="form-label mb-0 text-light">Select Excel Template (.xlsx):</label>
                        </div>
                        <div class="col-md-6">
                            <input type="file" name="excelFile" class="form-control form-control-dark" accept=".xlsx, .xls" required />
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-success w-100">
                                <i class="bi bi-cloud-upload me-1"></i> Load Data From Excel
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>
