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
    <style>
        /* Tinh chỉnh cho Modal hiển thị đẹp mắt hơn */
        .modal-content.dark-theme {
            background-color: #1e1e2d;
            color: #fff;
            border: 1px solid #2b2b40;
        }
        .modal-header { border-bottom: 1px solid #2b2b40; }
        .modal-footer { border-top: 1px solid #2b2b40; }
        .table-detail th { width: 35%; color: #a1a5b7; font-weight: 500;}
        .table-detail td { color: #fff; }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">User Management</div>
            <div class="page-subtitle">Manage system users and roles</div>
        </div>
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back To Admin Dashboard
            </a>
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
                    <table class="table table-dark-custom mb-0 align-middle">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td class="detail-value">${u.fullName}</td>
                                    <td class="text-muted">${u.email}</td>
                                    <td>
                                        <span class="badge bg-secondary mb-1">${u.role.roleName}</span><br>
                                        <c:if test="${u.reviewer}"><span class="badge bg-info text-dark">Reviewer</span></c:if>
                                        <c:if test="${u.designer}"><span class="badge bg-warning text-dark">Designer</span></c:if>
                                    </td>
                                    <td>
                                        <span class="badge ${u.status == 'Active' ? 'bg-success' : 'bg-danger'}">${u.status}</span>
                                    </td>
                                    <td class="text-center">
                                        <button type="button" class="btn btn-action btn-info btn-sm text-white" title="View Detail"
                                                data-bs-toggle="modal" data-bs-target="#viewUserModal_${u.userId}">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                        
                                        <button type="button" class="btn btn-action btn-primary-custom btn-sm" title="Edit"
                                                data-bs-toggle="modal" data-bs-target="#editUserModal_${u.userId}">
                                            <i class="bi bi-pencil-square"></i>
                                        </button>

                                        <form method="post" action="${pageContext.request.contextPath}/admin/users" class="d-inline" onsubmit="return confirm('Confirm status change for this user?');">
                                            <input type="hidden" name="action" value="updateStatus">
                                            <input type="hidden" name="userId" value="${u.userId}">
                                            <c:choose>
                                                <c:when test="${u.status == 'Active'}">
                                                    <input type="hidden" name="status" value="Inactive">
                                                    <button type="submit" class="btn btn-action btn-danger-custom btn-sm" title="Deactivate"><i class="bi bi-lock"></i></button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="status" value="Active">
                                                    <button type="submit" class="btn btn-action btn-success-custom btn-sm" title="Activate"><i class="bi bi-unlock"></i></button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </td>
                                </tr>

                                <div class="modal fade" id="viewUserModal_${u.userId}" tabindex="-1" aria-hidden="true">
                                    <div class="modal-dialog modal-dialog-centered">
                                        <div class="modal-content dark-theme">
                                            <div class="modal-header">
                                                <h5 class="modal-title"><i class="bi bi-person-lines-fill me-2"></i>User Details</h5>
                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body p-0">
                                                <table class="table table-borderless table-detail mb-0 m-3 w-auto">
                                                    <tr><th>User ID:</th><td>${u.userId}</td></tr>
                                                    <tr><th>Full Name:</th><td>${u.fullName}</td></tr>
                                                    <tr><th>Email:</th><td>${u.email}</td></tr>
                                                    <tr><th>Phone:</th><td>${u.phoneNumber != null && !u.phoneNumber.isEmpty() ? u.phoneNumber : 'N/A'}</td></tr>
                                                    <tr><th>Primary Role:</th><td><span class="badge bg-primary">${u.role.roleName}</span></td></tr>
                                                    <tr><th>Additional Roles:</th>
                                                        <td>
                                                            <c:if test="${u.reviewer}"><span class="badge bg-info text-dark me-1">Reviewer</span></c:if>
                                                            <c:if test="${u.designer}"><span class="badge bg-warning text-dark">Designer</span></c:if>
                                                            <c:if test="${!u.reviewer && !u.designer}">None</c:if>
                                                        </td>
                                                    </tr>
                                                    <tr><th>Status:</th>
                                                        <td><span class="badge ${u.status == 'Active' ? 'bg-success' : 'bg-danger'}">${u.status}</span></td>
                                                    </tr>
                                                </table>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="modal fade" id="editUserModal_${u.userId}" tabindex="-1" aria-hidden="true">
                                    <div class="modal-dialog modal-dialog-centered">
                                        <div class="modal-content dark-theme">
                                            <div class="modal-header">
                                                <h5 class="modal-title"><i class="bi bi-pencil-square me-2"></i>Edit User Information</h5>
                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <form method="post" action="${pageContext.request.contextPath}/admin/users">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="userId" value="${u.userId}">
                                                
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label class="form-label text-muted small">Full Name *</label>
                                                        <input type="text" name="fullName" class="form-control form-control-dark w-100" value="${u.fullName}" required>
                                                    </div>
                                                    
                                                    <div class="mb-3">
                                                        <label class="form-label text-muted small">Phone Number</label>
                                                        <input type="text" name="phoneNumber" class="form-control form-control-dark w-100" value="${u.phoneNumber}">
                                                    </div>
                                                    
                                                    <div class="mb-3">
                                                        <label class="form-label text-muted small">Status</label>
                                                        <select name="status" class="form-select form-select-dark w-100">
                                                            <option value="Active" ${u.status == 'Active' ? 'selected' : ''}>Active</option>
                                                            <option value="Inactive" ${u.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                                                        </select>
                                                    </div>

                                                    <div class="mb-3">
                                                        <label class="form-label text-muted small">Current Primary Role</label>
                                                        <input type="text" class="form-control form-control-dark bg-secondary w-100" value="${u.role.roleName}" readonly>
                                                        <input type="hidden" name="roleId" value="${u.roleId}">
                                                    </div>
                                                    
                                                    <div class="d-flex gap-4">
                                                        <div class="form-check">
                                                            <input class="form-check-input" type="checkbox" id="editReviewer_${u.userId}" name="isReviewer" ${u.reviewer ? 'checked' : ''}>
                                                            <label class="form-check-label" for="editReviewer_${u.userId}">Reviewer</label>
                                                        </div>
                                                        <div class="form-check">
                                                            <input class="form-check-input" type="checkbox" id="editDesigner_${u.userId}" name="isDesigner" ${u.designer ? 'checked' : ''}>
                                                            <label class="form-check-label" for="editDesigner_${u.userId}">Designer</label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-primary-custom">Save Changes</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>

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