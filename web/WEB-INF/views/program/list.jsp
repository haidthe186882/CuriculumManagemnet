<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="admin"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Program Management — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Program Management</div>
            <div class="page-subtitle">Create and maintain academic programs</div>
        </div>
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left"></i> Back To Admin Dashboard
            </a>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-lg-8">
            <div class="card-dark">
                <div class="table-responsive">
                    <table class="table table-dark-custom mb-0">
                        <thead><tr><th>Code</th><th>Name</th><th>Description</th><th>Actions</th></tr></thead>
                        <tbody>
                            <c:forEach var="p" items="${programs}">
                                <tr>
                                    <td>${p.programCode}</td>
                                    <td class="detail-value">${p.programName}</td>
                                    <td class="text-muted">${p.description}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/program/edit?id=${p.programId}" class="btn btn-action btn-sm">Edit</a>
                                        <form method="post" action="${pageContext.request.contextPath}/program/list" class="d-inline">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="programId" value="${p.programId}">
                                            <button type="submit" class="btn btn-danger-custom btn-sm">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card-dark p-4">
                <h6 class="mb-3">Add Program</h6>
                <form method="post" action="${pageContext.request.contextPath}/program/list">
                    <input type="hidden" name="action" value="create">
                    <div class="mb-2">
                        <input type="text" name="programCode" class="form-control form-control-dark" placeholder="Program Code" required>
                    </div>
                    <div class="mb-2">
                        <input type="text" name="programName" class="form-control form-control-dark" placeholder="Program Name" required>
                    </div>
                    <div class="mb-3">
                        <textarea name="description" class="form-control form-control-dark" rows="3" placeholder="Description"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary-custom w-100">Add Program</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
