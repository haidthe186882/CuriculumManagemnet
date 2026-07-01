<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <c:set var="activeMenu" value="curriculum" />
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Curriculum List — LTMS</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
                    rel="stylesheet">
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
                    rel="stylesheet">
                <%@ include file="/WEB-INF/views/common/styles.jsp" %>
            </head>

            <body>

                <!-- Sidebar -->
                <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>

                    <!-- Main -->
                    <div class="main-content">
                        <div class="topbar">
                            <div>
                                <div class="page-title">Curriculum Management</div>
                                <div class="page-subtitle">Browse and manage training programs</div>
                            </div>

                            <c:if
                                test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.designer}">
                                <a href="${pageContext.request.contextPath}/curriculum/create"
                                    class="btn btn-primary-custom">
                                    <i class="bi bi-plus-lg me-1"></i> New Curriculum
                                </a>
                            </c:if>
                        </div>

                        <!-- Alert -->
                        <c:if test="${param.msg == 'created'}">
                            <div class="alert alert-success-dark d-flex align-items-center gap-2 mb-3">
                                <i class="bi bi-check-circle-fill"></i> Curriculum created successfully.
                            </div>
                        </c:if>
                        <c:if test="${param.msg == 'submitted'}">
                            <div class="alert alert-success-dark d-flex align-items-center gap-2 mb-3">
                                <i class="bi bi-send-check"></i> Curriculum submitted for review.
                            </div>
                        </c:if>

                        <!-- Stats - Hidden per request -->
                        <div class="row g-3 mb-4 d-none">
                            <div class="col-6 col-md-3">
                                <div class="stat-card">
                                    <div class="stat-number">${totalCount}</div>
                                    <div class="stat-label"><i class="bi bi-book me-1"></i>Total Curriculum</div>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="stat-card">
                                    <div class="stat-number" style="color:#34d399;">
                                        <c:set var="approvedCount" value="0" />
                                        <c:forEach var="c" items="${curriculums}">
                                            <c:if test="${c.isActive}">
                                                <c:set var="approvedCount" value="${approvedCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${approvedCount}
                                    </div>
                                    <div class="stat-label"><i class="bi bi-check-circle me-1"></i>Approved</div>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="stat-card">
                                    <div class="stat-number" style="color:#fbbf24;">
                                        <c:set var="pendingCount" value="0" />
                                        <c:forEach var="c" items="${curriculums}">
                                            <c:if test="${not c.isActive}">
                                                <c:set var="pendingCount" value="${pendingCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${pendingCount}
                                    </div>
                                    <div class="stat-label"><i class="bi bi-hourglass me-1"></i>Pending Review</div>
                                </div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="stat-card">
                                    <div class="stat-number" style="color:#94a3b8;">
                                        <c:set var="draftCount" value="0" />
                                        <c:forEach var="c" items="${curriculums}">
                                            <c:if test="${not c.isActive}">
                                                <c:set var="draftCount" value="${draftCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${draftCount}
                                    </div>
                                    <div class="stat-label"><i class="bi bi-pencil-square me-1"></i>Draft</div>
                                </div>
                            </div>
                        </div>

                        <!-- Search & Filter -->
                        <div class="card-dark p-3 mb-3">
                            <form method="get" action="${pageContext.request.contextPath}/curriculum/list">
                                <div class="row g-2">
                                    <c:set var="isStaff"
                                        value="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.role.roleName == 'Reviewer'}" />
                                    <div class="${isStaff ? 'col-md-4' : 'col-md-7'}">
                                        <div class="input-group">
                                            <input type="text" name="keyword"
                                                class="search-bar form-control border-end-0"
                                                placeholder="Search by name, code..." value="${keyword}">
                                            <span class="input-group-text bg-white border-start-0"
                                                style="border-color: var(--border);">
                                                <i class="bi bi-search" style="color: var(--muted);"></i>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <select name="majorId" class="form-select form-select-dark w-100"
                                            onchange="this.form.submit()">
                                            <option value="">All Programs</option>
                                            <c:forEach var="m" items="${majors}">
                                                <option value="${m.majorId}" ${selectedMajorId==m.majorId ? 'selected'
                                                    : '' }>${m.majorName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <c:if test="${isStaff}">
                                        <div class="col-md-3">
                                            <select name="status" class="form-select form-select-dark w-100"
                                                onchange="this.form.submit()">
                                                <option value="">All Status</option>
                                                <option value="Draft" ${selectedStatus=='Draft' ? 'selected' : '' }>
                                                    Draft</option>
                                                <option value="Pending" ${selectedStatus=='Pending' ? 'selected' : '' }>
                                                    Pending Review</option>
                                                <option value="Approved" ${selectedStatus=='Approved' ? 'selected' : ''
                                                    }>Approved</option>
                                                <option value="Archived" ${selectedStatus=='Archived' ? 'selected' : ''
                                                    }>Archived</option>
                                            </select>
                                        </div>
                                    </c:if>
                                    <div class="col-md-2">
                                        <button type="submit" class="btn btn-primary-custom w-100">
                                            <i class="bi bi-search me-1"></i>Search
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="card-dark">
                            <div class="table-responsive">
                                <table class="table table-dark-custom mb-0">
                                    <thead>
                                        <tr>
                                            <th style="cursor: default; user-select: none;"></th>
                                            <th style="cursor: default; user-select: none;">Code</th>
                                            <th style="cursor: default; user-select: none;">Curriculum Name</th>
                                            <th style="cursor: default; user-select: none;">Program</th>
                                            <th style="cursor: default; user-select: none;">Credits</th>
                                            <th style="cursor: default; user-select: none;">Is Active</th>
                                            <th style="cursor: default;">Decision Date</th>
                                            <th style="cursor: default;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty curriculums}">
                                                <tr>
                                                    <td colspan="8" class="text-center py-5 text-muted">
                                                        <i class="bi bi-inbox display-6 d-block mb-2"></i>
                                                        No curriculum found matching your search criteria.
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="c" items="${curriculums}" varStatus="st">
                                                    <tr class="curriculum-row">
                                                        <td class="text-muted">${st.count}</td>
                                                        <td><code
                                                                style="color:var(--accent);background:rgba(255,106,0,0.06);padding:2px 8px;border-radius:4px;">${c.curriculumCode}</code>
                                                        </td>
                                                        <td>
                                                            <div class="detail-value">${c.curriculumName}</div>
                                                            <div class="text-muted" style="font-size:0.78rem;">
                                                                ${c.englishName}</div>
                                                        </td>
                                                        <td class="text-muted">${c.majorName}</td>
                                                        <td><span class="detail-value">${c.totalCredits}</span></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${c.isActive}">
                                                                    <span class="badge-status badge-approved"><i
                                                                            class="bi bi-check-circle me-1"></i>Active</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge-status badge-draft"><i
                                                                            class="bi bi-pencil me-1"></i>Inactive</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="text-muted">
                                                            <c:if test="${not empty c.decisionDate}">
                                                                <fmt:formatDate value="${c.decisionDate}"
                                                                    pattern="dd/MM/yyyy" />
                                                            </c:if>
                                                        </td>
                                                        <td class="align-middle">
                                                            <div class="d-flex gap-2 justify-content-center">
                                                                <a href="${pageContext.request.contextPath}/curriculum/detail?id=${c.curriculumId}"
                                                                    class="btn btn-sm btn-outline-warning">
                                                                    <i class="bi bi-eye"></i> View
                                                                </a>

                                                                <c:if
                                                                    test="${not c.isActive and sessionScope.loggedUser.role.roleName == 'Admin'}">
                                                                    <button type="button"
                                                                        class="btn btn-sm btn-outline-primary"
                                                                        data-bs-toggle="modal"
                                                                        data-bs-target="#assignModal_${c.curriculumId}">
                                                                        <i class="bi bi-person-plus"></i> Assign
                                                                    </button>
                                                                </c:if>
                                                            </div>

                                                            <c:if
                                                                test="${not c.isActive and sessionScope.loggedUser.role.roleName == 'Admin'}">
                                                                <div class="modal fade text-start"
                                                                    id="assignModal_${c.curriculumId}" tabindex="-1"
                                                                    aria-hidden="true">
                                                                    <div class="modal-dialog modal-dialog-centered">
                                                                        <form
                                                                            action="${pageContext.request.contextPath}/curriculum"
                                                                            method="POST"
                                                                            class="modal-content bg-white border-0 shadow">
                                                                            <input type="hidden" name="action"
                                                                                value="assign">
                                                                            <input type="hidden" name="curriculumId"
                                                                                value="${c.curriculumId}">

                                                                            <div class="modal-header border-bottom">
                                                                                <h5 class="modal-title text-dark">Assign
                                                                                    Staff</h5>
                                                                                <button type="button" class="btn-close"
                                                                                    data-bs-dismiss="modal"></button>
                                                                            </div>

                                                                            <div class="modal-body text-dark">
                                                                                <p class="small text-muted mb-3">Phân
                                                                                    công Designer và Reviewer cho chương
                                                                                    trình
                                                                                    <strong>${c.curriculumCode}</strong>.
                                                                                </p>

                                                                                <div class="mb-3">
                                                                                    <label
                                                                                        class="form-label small fw-bold">Select
                                                                                        Designer</label>
                                                                                    <select name="designerId"
                                                                                        class="form-select border-secondary">
                                                                                        <option value="">-- Leave Blank
                                                                                            / None --</option>
                                                                                        <c:forEach var="d"
                                                                                            items="${designers}">
                                                                                            <option value="${d.userId}">
                                                                                                ${d.fullName}
                                                                                                (${d.email})</option>
                                                                                        </c:forEach>
                                                                                    </select>
                                                                                </div>

                                                                                <div class="mb-3">
                                                                                    <label
                                                                                        class="form-label small fw-bold">Select
                                                                                        Reviewer</label>
                                                                                    <select name="reviewerId"
                                                                                        class="form-select border-secondary">
                                                                                        <option value="">-- Leave Blank
                                                                                            / None --</option>
                                                                                        <c:forEach var="r"
                                                                                            items="${reviewers}">
                                                                                            <option value="${r.userId}">
                                                                                                ${r.fullName}
                                                                                                (${r.email})</option>
                                                                                        </c:forEach>
                                                                                    </select>
                                                                                </div>
                                                                            </div>

                                                                            <div
                                                                                class="modal-footer border-top bg-light">
                                                                                <button type="button"
                                                                                    class="btn btn-secondary"
                                                                                    data-bs-dismiss="modal">Cancel</button>
                                                                                <button type="submit"
                                                                                    class="btn btn-primary">Save
                                                                                    Assignments</button>
                                                                            </div>
                                                                            //
                                                                        </form>
                                                                    </div>
                                                                </div>
                                                            </c:if>
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


                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

            </body>

            </html>