<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Material — Teacher</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<c:set var="activeMenu" value="teacher"/>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Upload Material</div>
            <div class="page-subtitle">5 materials · 4 published</div>
        </div>
        <div class="d-flex align-items-center gap-2">
            <div class="me-2">
                <select class="form-select form-select-dark">
                    <option>All Courses</option>
                    <option>swp1</option>
                    <option>m1201</option>
                </select>
            </div>
            <div class="me-2">
                <select class="form-select form-select-dark">
                    <option>All Status</option>
                    <option>Published</option>
                    <option>Draft</option>
                </select>
            </div>
            <div class="me-3">
                <input class="search-bar" placeholder="Search materials..." />
            </div>
            <button class="btn btn-primary-custom" data-bs-toggle="modal" data-bs-target="#uploadModal"><i class="bi bi-upload me-1"></i>Upload New</button>
        </div>
    </div>

    <c:if test="${not empty sessionScope.msg}">
        <div class="alert alert-success-dark mb-3">${sessionScope.msg}</div>
        <c:remove var="msg" scope="session"/>
    </c:if>

    <div class="card-dark mt-3">
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0 align-middle">
                <thead><tr><th>File</th><th>Course</th><th>Description</th><th>Size</th><th>Uploaded</th><th>Status</th><th>Actions</th></tr></thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty materials}">
                            <tr><td colspan="7" class="text-center py-5 text-muted">No materials found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="m" items="${materials}">
                                <tr>
                                    <td>
                                        <div class="fw-semibold">${m.fileName}</div>
                                        <div class="text-muted small">${m.fileName}</div>
                                    </td>
                                    <td><span class="badge badge-draft">${m.courseCode}</span></td>
                                    <td class="text-truncate" style="max-width:320px">${m.description}</td>
                                    <td>${m.sizeReadable}</td>
                                    <td>${m.uploadedDateString}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${m.status == 'Published'}"><span class="badge-status badge-approved">Published</span></c:when>
                                            <c:otherwise><span class="badge-status badge-draft">${m.status}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="#" class="btn btn-action btn-view"><i class="bi bi-eye"></i></a>
                                        <a href="#" class="btn btn-action"><i class="bi bi-download"></i></a>
                                        <a href="#" class="btn btn-action btn-danger-custom"><i class="bi bi-trash"></i></a>
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

<!-- Upload Modal -->
<div class="modal fade" id="uploadModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Upload Material</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form method="post" action="${pageContext.request.contextPath}/teacher/upload" enctype="multipart/form-data">
          <div class="mb-3">
            <div class="card-dark p-4 text-center" style="border:dashed 2px var(--border);">
                <i class="bi bi-cloud-upload" style="font-size:28px;color:var(--muted)"></i>
                <div class="mt-2 text-muted">Drag & drop files here, or click to browse</div>
                <div class="text-muted small">PDF, PPTX, DOCX, MP4, MOV — max 500 MB each</div>
            </div>
            <input type="file" name="file" class="form-control mt-2" />
          </div>
          <div class="row g-2">
            <div class="col-6">
              <label class="form-label">Course</label>
              <select name="course" class="form-select form-select-dark">
                <option value="swp1">swp1 — Software Development</option>
              </select>
            </div>
            <div class="col-6">
              <label class="form-label">Visibility</label>
              <select name="visibility" class="form-select form-select-dark">
                <option>Published</option>
                <option>Draft</option>
              </select>
            </div>
          </div>
          <div class="mb-3 mt-2">
            <label class="form-label">Description</label>
            <textarea name="description" class="form-control form-control-dark" placeholder="Brief description of this material..."></textarea>
          </div>
          <div class="d-flex justify-content-end gap-2">
            <button type="button" class="btn btn-secondary-custom" data-bs-dismiss="modal">Cancel</button>
            <button type="submit" class="btn btn-primary-custom">Upload</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
