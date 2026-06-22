<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="subject"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subject List — LTMS</title>
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
            <div class="page-title">Subject Management</div>
            <div class="page-subtitle">Browse and manage subjects</div>
        </div>
        <c:if test="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin'}">
            <a href="${pageContext.request.contextPath}/subject/create" class="btn btn-primary-custom">
                <i class="bi bi-plus-lg me-1"></i>New Subject
            </a>
        </c:if>
    </div>

    <c:if test="${param.msg == 'created' or param.msg == 'updated'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Operation successful.</div>
    </c:if>

    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/subject/list">
            <div class="row g-2">
                <div class="col-md-10">
                    <div class="input-group">
                        <input type="text" name="keyword" class="search-bar form-control border-end-0"
                               placeholder="Search by code or name..." value="${keyword}">
                        <span class="input-group-text bg-white border-start-0" style="border-color: var(--border);">
                            <i class="bi bi-search" style="color: var(--muted);"></i>
                        </span>
                    </div>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100"><i class="bi bi-search"></i> Search</button>
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
                        <th class="sortable" data-sort="name" style="cursor: pointer; user-select: none;">Subject Name <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                        <th class="sortable" data-sort="department" style="cursor: pointer; user-select: none;">Department <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                        <th class="sortable" data-sort="credits" style="cursor: pointer; user-select: none;">Credits <i class="bi bi-arrow-down-up" style="font-size: 0.75rem; opacity: 0.5;"></i></th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty subjects}">
                            <tr><td colspan="5" class="text-center py-5 text-muted">No subjects found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="s" items="${subjects}" varStatus="st">
                                <tr class="subject-row" data-detail-url="${pageContext.request.contextPath}/subject/detail?id=${s.subjectId}" style="cursor: pointer;">
                                    <td>${st.count}</td>
                                    <td><code style="color:#4fc3f7;">${s.subjectCode}</code></td>
                                    <td>
                                        <div class="detail-value">${s.subjectName}</div>
                                        <div class="text-muted" style="font-size:0.78rem;">${s.englishName}</div>
                                    </td>
                                    <td class="text-muted">${s.department}</td>
                                    <td>${s.credits}</td>
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
    // Make subject list rows clickable
    document.addEventListener('DOMContentLoaded', function() {
        const subjectRows = document.querySelectorAll('.subject-row');
        subjectRows.forEach(row => {
            row.addEventListener('click', function(e) {
                if (e.target.closest('a, button')) return;
                const detailUrl = this.getAttribute('data-detail-url');
                if (detailUrl) window.location.href = detailUrl;
            });
            
            row.addEventListener('mouseenter', function() {
                this.style.backgroundColor = 'rgba(79, 195, 247, 0.08)';
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
                const rows = Array.from(tbody.querySelectorAll('tr.subject-row'));
                
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
                        case 'department':
                            aVal = a.cells[3].textContent.trim().toLowerCase();
                            bVal = b.cells[3].textContent.trim().toLowerCase();
                            break;
                        case 'credits':
                            aVal = parseFloat(a.cells[4].textContent.trim()) || 0;
                            bVal = parseFloat(b.cells[4].textContent.trim()) || 0;
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
</html>
