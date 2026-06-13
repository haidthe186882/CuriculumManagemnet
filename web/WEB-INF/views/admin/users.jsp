<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="admin"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management — LTMS</title>
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
            <div class="page-title">User Management</div>
            <div class="page-subtitle">Manage system users and roles</div>
        </div>
    </div>

    <c:if test="${not empty param.msg}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Operation successful.</div>
    </c:if>

    <div class="row g-3">
        <div class="col-lg-8">
            <div class="card-dark p-3 mb-3">
                <form method="get" action="${pageContext.request.contextPath}/admin/users">
                    <div class="row g-2">
                        <div class="col-md-7">
                            <input type="text" name="keyword" class="search-bar form-control w-100"
                                   placeholder="Search by name or email..." value="${keyword}">
                        </div>
                        <div class="col-md-3">
                            <select name="status" class="form-select form-select-dark w-100">
                                <option value="">All Status</option>
                                <option value="Active" ${selectedStatus=='Active' ? 'selected' : ''}>Active</option>
                                <option value="Inactive" ${selectedStatus=='Inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary-custom w-100">Search</button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="card-dark">
                <div class="table-responsive">
                    <table class="table table-dark-custom mb-0">
                        <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Department</th><th>Status</th><th>Actions</th></tr></thead>
                        <tbody>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td class="detail-value">${u.fullName}</td>
                                    <td class="text-muted">${u.email}</td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/users" class="d-inline">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="userId" value="${u.userId}">
                                            <select name="roleId" class="form-select form-select-dark form-select-sm" style="width:140px;display:inline-block;">
                                                <c:forEach var="r" items="${roles}">
                                                    <option value="${r.roleId}" ${r.roleId == u.role.roleId ? 'selected' : ''}>${r.roleName}</option>
                                                </c:forEach>
                                            </select>
                                            <label class="ms-2"><input type="checkbox" name="isReviewer" ${u.reviewer ? 'checked' : ''}/> Reviewer</label>
                                            <label class="ms-2"><input type="checkbox" name="isDesigner" ${u.designer ? 'checked' : ''}/> Designer</label>
                                            <button type="submit" class="btn btn-action btn-primary-custom btn-sm ms-2">Update</button>
                                        </form>
                                    </td>
                                    <td>${u.department}</td>
                                    <td>${u.status}</td>
                                    <td>
                                        <c:if test="${u.status == 'Active'}">
                                            <form method="post" action="${pageContext.request.contextPath}/admin/users" class="d-inline">
                                                <input type="hidden" name="action" value="updateStatus">
                                                <input type="hidden" name="userId" value="${u.userId}">
                                                <input type="hidden" name="status" value="Inactive">
                                                <button type="submit" class="btn btn-action btn-danger-custom btn-sm">Deactivate</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${u.status != 'Active'}">
                                            <form method="post" action="${pageContext.request.contextPath}/admin/users" class="d-inline">
                                                <input type="hidden" name="action" value="updateStatus">
                                                <input type="hidden" name="userId" value="${u.userId}">
                                                <input type="hidden" name="status" value="Active">
                                                <button type="submit" class="btn btn-action btn-success-custom btn-sm">Activate</button>
                                            </form>
                                        </c:if>
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
                <h6 class="mb-3">Add New User</h6>
                <form method="post" action="${pageContext.request.contextPath}/admin/users">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-2">
                        <input type="text" name="fullName" class="form-control form-control-dark w-100" placeholder="Full Name *" required>
                    </div>
                    <div class="mb-2">
                        <input type="email" name="email" class="form-control form-control-dark w-100" placeholder="Email *" required>
                    </div>
                    <div class="mb-2">
                        <input type="password" name="password" class="form-control form-control-dark w-100" placeholder="Password (default: 123456)">
                    </div>
                    <div class="mb-2">
                        <input type="text" name="department" class="form-control form-control-dark w-100" placeholder="Department">
                    </div>
                    <div class="mb-3">
                        <select name="roleId" class="form-select form-select-dark w-100" required>
                            <c:forEach var="r" items="${roles}">
                                <option value="${r.roleId}">${r.roleName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-2 form-check">
                        <input class="form-check-input" type="checkbox" id="isReviewer" name="isReviewer">
                        <label class="form-check-label" for="isReviewer">Grant Reviewer</label>
                    </div>
                    <div class="mb-3 form-check">
                        <input class="form-check-input" type="checkbox" id="isDesigner" name="isDesigner">
                        <label class="form-check-label" for="isDesigner">Grant Designer</label>
                    </div>
                    <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-person-plus me-1"></i>Add User</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
