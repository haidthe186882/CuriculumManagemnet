<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="teacher"/>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Document Management — Teacher</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <%@ include file="/WEB-INF/views/common/styles.jsp" %>
        <style>
            /* Upload page specific styles */
            .modal-content {
                background: var(--card-bg);
                border: 1px solid var(--border);
                border-radius: 16px;
                box-shadow: 0 24px 48px rgba(0,0,0,0.08);
            }
            .modal-header {
                border-bottom: 1px solid var(--border);
                padding: 1.25rem 1.5rem;
            }
            .modal-title {
                font-weight: 700;
                color: #111827;
            }
            .modal-body {
                padding: 1.5rem;
            }
            .form-label {
                font-weight: 600;
                font-size: 0.82rem;
                color: #374151;
                text-transform: uppercase;
                letter-spacing: 0.3px;
                margin-bottom: 0.4rem;
            }
            .form-select {
                background: #ffffff;
                border: 1px solid var(--border);
                border-radius: 10px;
                color: #111827;
                padding: 0.7rem 0.9rem;
            }
            .form-select:focus {
                border-color: var(--accent);
                box-shadow: 0 0 0 3px rgba(255,106,0,0.08);
            }
            .btn-close {
                filter: none;
            }
            .material-card {
                background: var(--card-bg);
                border-radius: 12px;
                border: 1px solid var(--border);
                padding: 1rem 1.25rem;
                transition: all 0.15s ease;
                margin-bottom: 0.75rem;
            }
            .material-card:hover {
                border-color: rgba(255,106,0,0.2);
                box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            }
            .material-name {
                font-weight: 600;
                color: #111827;
                font-size: 0.95rem;
            }
            .material-meta {
                font-size: 0.8rem;
                color: var(--muted);
            }
            .material-link {
                color: var(--accent);
                text-decoration: none;
                font-size: 0.85rem;
                font-weight: 500;
                word-break: break-all;
            }
            .material-link:hover {
                text-decoration: underline;
            }
            .empty-state {
                text-align: center;
                padding: 3rem 1rem;
                color: var(--muted);
            }
            .empty-state i {
                font-size: 3rem;
                margin-bottom: 1rem;
                opacity: 0.4;
            }
            .stat-card {
                background: var(--card-bg);
                border-radius: 12px;
                border: 1px solid var(--border);
                padding: 1.25rem;
                text-align: center;
            }
            .stat-number {
                font-size: 1.75rem;
                font-weight: 700;
                color: #111827;
            }
            .stat-label {
                font-size: 0.78rem;
                color: var(--muted);
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
        </style>
    </head>
    <body>
        <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
        <div class="main-content">
            <!-- Top bar -->
            <div class="topbar">
                <div>
                    <div class="page-title">Document Management</div>
                    <div class="page-subtitle">Upload and manage your teaching materials</div>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <button class="btn btn-primary-custom" data-bs-toggle="modal" data-bs-target="#uploadModal">
                        <i class="bi bi-plus-lg me-1"></i>Upload New
                    </button>
                </div>
            </div>

            <!-- Success/Error messages -->
            <c:if test="${not empty sessionScope.msg}">
                <div class="alert alert-success-dark mb-3">
                    <i class="bi bi-check-circle me-1"></i>${sessionScope.msg}
                </div>
                <c:remove var="msg" scope="session"/>
            </c:if>

            <!-- Materials Table -->
            <div class="card-dark">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="fw-bold mb-0" style="color:#111827;">Your Materials</h6>
                </div>
                <div class="table-responsive">
                    <table class="table table-dark-custom mb-0 align-middle">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>File Name</th>
                                <th>Course</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Upload Link</th>
                                <th>Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty materials}">
                                    <tr>
                                        <td colspan="8">
                                            <div class="empty-state">
                                                <i class="bi bi-cloud-upload d-block"></i>
                                                <div class="fw-semibold mb-1">No materials uploaded yet</div>
                                                <div class="small">Click "Upload New" to add your first teaching material.</div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="m" items="${materials}" varStatus="loop">
                                        <tr>
                                            <td class="fw-semibold">${loop.index + 1}</td>
                                            <td>
                                                <div class="material-name">${m.materialName}</div>
                                            </td>
                                            <td>
                                                <span class="badge badge-draft">${m.subjectCode}</span>
                                                <div class="material-meta mt-1">${m.syllabusName}</div>
                                            </td>
                                            <td><span class="badge-status badge-pending">${m.materialType}</span></td>
                                            <td class="text-truncate" style="max-width:220px;">${m.description}</td>
                                            <td style="max-width:200px;">
                                                <c:if test="${not empty m.materialUrl}">
                                                    <a href="${m.materialUrl}" target="_blank" class="material-link">
                                                        <i class="bi bi-box-arrow-up-right me-1"></i>${m.materialUrl}
                                                    </a>
                                                </c:if>
                                                <c:if test="${empty m.materialUrl}">
                                                    <span class="text-muted">—</span>
                                                </c:if>
                                            </td>
                                            <td class="material-meta">${m.createdDateString}</td>
                                            <td>
                                                <div class="d-flex gap-1">
                                                    <a href="${pageContext.request.contextPath}/teacher/edit?id=${m.materialId}"
                                                       class="btn btn-action btn-view" title="Edit">
                                                        <i class="bi bi-pencil"></i>
                                                    </a>
                                                    <form method="post" action="${pageContext.request.contextPath}/teacher/upload"
                                                          style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this material?');">
                                                        <input type="hidden" name="action" value="delete"/>
                                                        <input type="hidden" name="materialId" value="${m.materialId}"/>
                                                        <button type="submit" class="btn btn-action btn-danger-custom" title="Delete">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </form>
                                                </div>
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

        <!-- ========== UPLOAD MODAL ========== -->
        <div class="modal fade" id="uploadModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="bi bi-cloud-upload me-2" style="color:var(--accent)"></i>Upload New Material</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form method="post" action="${pageContext.request.contextPath}/teacher/upload" id="uploadForm">
                            <input type="hidden" name="action" value="create"/>

                            <!-- File Name -->
                            <div class="mb-3">
                                <label class="form-label" for="materialName">
                                    <i class="bi bi-file-earmark me-1"></i>File Name <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="materialName" name="materialName"
                                       placeholder="e.g. Slide bài giảng tuần 1-3" required />
                            </div>

                            <div class="row g-3">
                                <!-- Course (Syllabus) -->
                                <div class="col-md-6">
                                    <label class="form-label" for="syllabusId">
                                        <i class="bi bi-journal-text me-1"></i>Course <span class="text-danger">*</span>
                                    </label>
                                    <select name="syllabusId" id="syllabusId" class="form-select" required>
                                        <option value="">— Select Course —</option>
                                        <c:forEach var="sy" items="${syllabuses}">
                                            <option value="${sy.syllabusId}">
                                                ${sy.subject.subjectCode} — ${sy.syllabusName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <!-- Material Type -->
                                <div class="col-md-6">
                                    <label class="form-label" for="materialType">
                                        <i class="bi bi-tag me-1"></i>Material Type
                                    </label>
                                    <select name="materialType" id="materialType" class="form-select">
                                        <option value="Slide">Slide</option>
                                        <option value="Video">Video</option>
                                        <option value="Document">Document</option>
                                        <option value="Exercise">Exercise</option>
                                        <option value="Other">Other</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Description -->
                            <div class="mb-3 mt-3">
                                <label class="form-label" for="description">
                                    <i class="bi bi-text-paragraph me-1"></i>Description
                                </label>
                                <textarea name="description" id="description" class="form-control form-control-dark" rows="3"
                                          placeholder="Brief description of this material..."></textarea>
                            </div>

                            <!-- Upload Link -->
                            <div class="mb-3">
                                <label class="form-label" for="materialUrl">
                                    <i class="bi bi-link-45deg me-1"></i>Upload Link <span class="text-danger">*</span>
                                </label>
                                <input type="url" class="form-control" id="materialUrl" name="materialUrl"
                                       placeholder="https://drive.google.com/..." required />
                                <div class="form-text text-muted" style="font-size:0.78rem;">
                                    Paste the URL to your material (Google Drive, OneDrive, YouTube, etc.)
                                </div>
                            </div>

                            <!-- Submit / Cancel -->
                            <div class="d-flex justify-content-end gap-2 mt-4 pt-3" style="border-top:1px solid var(--border);">
                                <button type="button" class="btn btn-secondary-custom" data-bs-dismiss="modal">
                                    <i class="bi bi-x-lg me-1"></i>Cancel
                                </button>
                                <button type="submit" class="btn btn-primary-custom">
                                    <i class="bi bi-upload me-1"></i>Submit
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- ========== EDIT MODAL (auto-opens if editMaterial is set) ========== -->
        <c:if test="${not empty editMaterial}">
        <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="bi bi-pencil-square me-2" style="color:var(--accent)"></i>Edit Material</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form method="post" action="${pageContext.request.contextPath}/teacher/upload" id="editForm">
                            <input type="hidden" name="action" value="update"/>
                            <input type="hidden" name="materialId" value="${editMaterial.materialId}"/>

                            <!-- File Name -->
                            <div class="mb-3">
                                <label class="form-label" for="editMaterialName">
                                    <i class="bi bi-file-earmark me-1"></i>File Name <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="editMaterialName" name="materialName"
                                       value="${editMaterial.materialName}" required />
                            </div>

                            <div class="row g-3">
                                <!-- Course (Syllabus) -->
                                <div class="col-md-6">
                                    <label class="form-label" for="editSyllabusId">
                                        <i class="bi bi-journal-text me-1"></i>Course <span class="text-danger">*</span>
                                    </label>
                                    <select name="syllabusId" id="editSyllabusId" class="form-select" required>
                                        <option value="">— Select Course —</option>
                                        <c:forEach var="sy" items="${syllabuses}">
                                            <option value="${sy.syllabusId}"
                                                ${sy.syllabusId == editMaterial.syllabusId ? 'selected' : ''}>
                                                ${sy.subject.subjectCode} — ${sy.syllabusName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <!-- Material Type -->
                                <div class="col-md-6">
                                    <label class="form-label" for="editMaterialType">
                                        <i class="bi bi-tag me-1"></i>Material Type
                                    </label>
                                    <select name="materialType" id="editMaterialType" class="form-select">
                                        <option value="Slide" ${editMaterial.materialType == 'Slide' ? 'selected' : ''}>Slide</option>
                                        <option value="Video" ${editMaterial.materialType == 'Video' ? 'selected' : ''}>Video</option>
                                        <option value="Document" ${editMaterial.materialType == 'Document' ? 'selected' : ''}>Document</option>
                                        <option value="Exercise" ${editMaterial.materialType == 'Exercise' ? 'selected' : ''}>Exercise</option>
                                        <option value="Other" ${editMaterial.materialType == 'Other' ? 'selected' : ''}>Other</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Description -->
                            <div class="mb-3 mt-3">
                                <label class="form-label" for="editDescription">
                                    <i class="bi bi-text-paragraph me-1"></i>Description
                                </label>
                                <textarea name="description" id="editDescription" class="form-control form-control-dark" rows="3">${editMaterial.description}</textarea>
                            </div>

                            <!-- Upload Link -->
                            <div class="mb-3">
                                <label class="form-label" for="editMaterialUrl">
                                    <i class="bi bi-link-45deg me-1"></i>Upload Link <span class="text-danger">*</span>
                                </label>
                                <input type="url" class="form-control" id="editMaterialUrl" name="materialUrl"
                                       value="${editMaterial.materialUrl}" required />
                                <div class="form-text text-muted" style="font-size:0.78rem;">
                                    Paste the URL to your material (Google Drive, OneDrive, YouTube, etc.)
                                </div>
                            </div>

                            <!-- Submit / Cancel -->
                            <div class="d-flex justify-content-end gap-2 mt-4 pt-3" style="border-top:1px solid var(--border);">
                                <a href="${pageContext.request.contextPath}/teacher/upload" class="btn btn-secondary-custom">
                                    <i class="bi bi-x-lg me-1"></i>Cancel
                                </a>
                                <button type="submit" class="btn btn-primary-custom">
                                    <i class="bi bi-check-lg me-1"></i>Submit
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        </c:if>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <c:if test="${not empty editMaterial}">
        <script>
            // Auto-open edit modal when editMaterial is present
            document.addEventListener('DOMContentLoaded', function() {
                var editModal = new bootstrap.Modal(document.getElementById('editModal'));
                editModal.show();
                // When closed, redirect back to list
                document.getElementById('editModal').addEventListener('hidden.bs.modal', function () {
                    window.location.href = '${pageContext.request.contextPath}/teacher/upload';
                });
            });
        </script>
        </c:if>
    </body>
</html>
