<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Curriculum List — LTMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
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

                <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.designer}">
                    <a href="${pageContext.request.contextPath}/curriculum/create" class="btn btn-primary-custom">
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
                            <c:set var="approvedCount" value="0"/>
                            <c:forEach var="c" items="${curriculums}">
                                <c:if test="${c.isActive}"><c:set var="approvedCount" value="${approvedCount + 1}"/></c:if>
                            </c:forEach>
                            ${approvedCount}
                        </div>
                        <div class="stat-label"><i class="bi bi-check-circle me-1"></i>Approved</div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="stat-card">
                        <div class="stat-number" style="color:#fbbf24;">
                            <c:set var="pendingCount" value="0"/>
                            <c:forEach var="c" items="${curriculums}">
                                <c:if test="${not c.isActive}"><c:set var="pendingCount" value="${pendingCount + 1}"/></c:if>
                            </c:forEach>
                            ${pendingCount}
                        </div>
                        <div class="stat-label"><i class="bi bi-hourglass me-1"></i>Pending Review</div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="stat-card">
                        <div class="stat-number" style="color:#94a3b8;">
                            <c:set var="draftCount" value="0"/>
                            <c:forEach var="c" items="${curriculums}">
                                <c:if test="${not c.isActive}"><c:set var="draftCount" value="${draftCount + 1}"/></c:if>
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
                        <div class="${(sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.role.roleName == 'Reviewer') ? 'col-md-7' : 'col-md-10'}">
                            <div class="input-group">
                                <input type="text" name="keyword" class="search-bar form-control border-end-0"
                                       placeholder="Search by name, code..." value="${keyword}">
                                <span class="input-group-text bg-white border-start-0" style="border-color: var(--border);">
                                    <i class="bi bi-search" style="color: var(--muted);"></i>
                                </span>
                            </div>
                        </div>
                        <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin' or sessionScope.loggedUser.role.roleName == 'Reviewer'}">
                            <div class="col-md-3">
                                <select name="status" class="form-select form-select-dark w-100">
                                    <option value="">All Status</option>
                                    <option value="Draft"    ${selectedStatus=='Draft'    ? 'selected' : ''}>Draft</option>
                                    <option value="Pending"  ${selectedStatus=='Pending'  ? 'selected' : ''}>Pending Review</option>
                                    <option value="Approved" ${selectedStatus=='Approved' ? 'selected' : ''}>Approved</option>
                                    <option value="Archived" ${selectedStatus=='Archived' ? 'selected' : ''}>Archived</option>
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
                                <th style="cursor: pointer; user-select: none;">#</th>
                                <th class="sortable" data-sort="code" style="cursor: pointer; user-select: none;">Code <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                                <th class="sortable" data-sort="name" style="cursor: pointer; user-select: none;">Curriculum Name <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                                <th class="sortable" data-sort="major" style="cursor: pointer; user-select: none;">Program <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                                <th class="sortable" data-sort="credits" style="cursor: pointer; user-select: none;">Credits <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                                <th class="sortable" data-sort="version" style="cursor: pointer; user-select: none;">Version <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                                <th class="sortable" data-sort="status" style="cursor: pointer; user-select: none;">Is Active <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                                <th style="cursor: default;">Decision Date</th>
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
                                        <tr class="curriculum-row" data-detail-url="${pageContext.request.contextPath}/curriculum/detail?id=${c.curriculumId}" style="cursor: pointer;">
                                            <td class="text-muted">${st.count}</td>
                                            <td><code style="color:var(--accent);background:rgba(255,106,0,0.06);padding:2px 8px;border-radius:4px;">${c.curriculumCode}</code></td>
                                            <td>
                                                <div class="detail-value">${c.curriculumName}</div>
                                                <div class="text-muted" style="font-size:0.78rem;">${c.englishName}</div>
                                            </td>
                                            <td class="text-muted">${c.majorName}</td>
                                            <td><span class="detail-value">${c.totalCredits}</span> <span class="text-muted" style="font-size:0.8rem;"> cr</span></td>
                                            <td class="text-muted">${c.version}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${c.isActive}">
                                                        <span class="badge-status badge-approved"><i class="bi bi-check-circle me-1"></i>Active</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge-status badge-draft"><i class="bi bi-pencil me-1"></i>Inactive</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-muted">
                                                <c:if test="${not empty c.decisionDate}">
                                                    <fmt:formatDate value="${c.decisionDate}" pattern="dd/MM/yyyy"/>
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
        <script>
            // Make curriculum list rows clickable
            document.addEventListener('DOMContentLoaded', function() {
                const curriculumRows = document.querySelectorAll('.curriculum-row');
                curriculumRows.forEach(row => {
                    row.addEventListener('click', function(e) {
//                        if (e.target.closest('a, button')) return;
                        if (e.target.closest('a, button, input, select, form, .modal')) return;
                        const detailUrl = this.getAttribute('data-detail-url');
                        if (detailUrl) window.location.href = detailUrl;
                    });
                    
                    row.addEventListener('mouseenter', function() {
                        this.style.backgroundColor = 'rgba(255, 106, 0, 0.08)';
                    });
                    row.addEventListener('mouseleave', function() {
                        this.style.backgroundColor = '';
                    });
                });
                
                

                
                // Table sorting functionality
                const sortableHeaders = document.querySelectorAll('th.sortable');
                let currentSort = { column: null, direction: 'asc' };
                
                sortableHeaders.forEach(header => {
                    header.addEventListener('click', function() {
                        const sortColumn = this.getAttribute('data-sort');
                        const tbody = document.querySelector('tbody');
                        const rows = Array.from(tbody.querySelectorAll('tr.curriculum-row'));
                        
                        if (currentSort.column === sortColumn) {
                            currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
                        } else {
                            currentSort.column = sortColumn;
                            currentSort.direction = 'asc';
                        }
                        
                        sortableHeaders.forEach(h => {
                            const icon = h.querySelector('i');
                            if (icon) icon.className = 'bi bi-arrow-down-up';
                            h.style.opacity = '1';
                        });
                        const activeIcon = this.querySelector('i');
                        if (activeIcon) {
                            activeIcon.className = currentSort.direction === 'asc' ? 'bi bi-arrow-up' : 'bi bi-arrow-down';
                        }
                        
                        rows.sort((a, b) => {
                            let aVal, bVal;
                            
                            switch(sortColumn) {
                                case 'code':
                                    aVal = a.cells[1].textContent.trim().toLowerCase();
                                    bVal = b.cells[1].textContent.trim().toLowerCase();
                                    break;
                                case 'name':
                                    aVal = a.cells[2].textContent.trim().toLowerCase();
                                    bVal = b.cells[2].textContent.trim().toLowerCase();
                                    break;
                                case 'major':
                                    aVal = a.cells[3].textContent.trim().toLowerCase();
                                    bVal = b.cells[3].textContent.trim().toLowerCase();
                                    break;
                                case 'credits':
                                    aVal = parseFloat(a.cells[4].textContent.trim()) || 0;
                                    bVal = parseFloat(b.cells[4].textContent.trim()) || 0;
                                    break;
                                case 'version':
                                    aVal = a.cells[5].textContent.trim().toLowerCase();
                                    bVal = b.cells[5].textContent.trim().toLowerCase();
                                    break;
                                case 'status':
                                    aVal = a.cells[6].textContent.trim().toLowerCase();
                                    bVal = b.cells[6].textContent.trim().toLowerCase();
                                    break;
                            }
                            
                            if (currentSort.direction === 'asc') {
                                return aVal > bVal ? 1 : aVal < bVal ? -1 : 0;
                            } else {
                                return aVal < bVal ? 1 : aVal > bVal ? -1 : 0;
                            }
                        });
                        
                        rows.forEach(row => tbody.appendChild(row));
                    });
                });
            });
        </script>
    </body>
</html>l>
