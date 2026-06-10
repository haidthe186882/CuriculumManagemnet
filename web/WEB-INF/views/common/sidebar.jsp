<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="sidebar">
    <div class="brand">LT<span>MS</span></div>
    <div class="nav-section">Main Menu</div>
    <a class="nav-link ${activeMenu == 'curriculum' ? 'active' : ''}" href="${pageContext.request.contextPath}/curriculum/list">
        <i class="bi bi-book"></i> Curriculums
    </a>
    <a class="nav-link ${activeMenu == 'subject' ? 'active' : ''}" href="${pageContext.request.contextPath}/subject/list">
        <i class="bi bi-journal-text"></i> Subjects
    </a>
    <a class="nav-link ${activeMenu == 'syllabus' ? 'active' : ''}" href="${pageContext.request.contextPath}/syllabus/list">
        <i class="bi bi-file-earmark-text"></i> Syllabuses
    </a>

    <c:if test="${sessionScope.loggedUser.role.roleName == 'Reviewer' or sessionScope.loggedUser.role.roleName == 'Admin'}">
        <div class="nav-section">Review</div>
        <a class="nav-link ${activeMenu == 'review' ? 'active' : ''}" href="${pageContext.request.contextPath}/review/list">
            <i class="bi bi-clipboard-check"></i> Review List
        </a>
    </c:if>
    <c:if test="${sessionScope.loggedUser.role.roleName == 'Admin'}">
        <div class="nav-section">Administration</div>
        <a class="nav-link ${activeMenu == 'admin' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">
            <i class="bi bi-people"></i> User Management
        </a>
    </c:if>

    <div style="position:absolute; bottom:1.5rem; left:0; right:0; padding: 0 1.5rem;">
        <c:choose>
            <c:when test="${not empty sessionScope.loggedUser}">
                <div style="display:flex;align-items:center;gap:0.75rem;margin-bottom:0.75rem;">
                    <div style="width:36px;height:36px;background:linear-gradient(135deg,#4fc3f7,#0288d1);
                                border-radius:50%;display:flex;align-items:center;justify-content:center;
                                font-weight:700;color:#fff;font-size:0.9rem;">
                        ${sessionScope.loggedUser.fullName.charAt(0)}
                    </div>
                    <div>
                        <div style="color:#fff;font-size:0.85rem;font-weight:500;">${sessionScope.loggedUser.fullName}</div>
                        <div style="color:#64748b;font-size:0.75rem;">${sessionScope.loggedUser.role.roleName}</div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/logout"
                   class="nav-link" style="border-radius:8px;color:#ef4444;">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="nav-link">
                    <i class="bi bi-box-arrow-in-right"></i> Login
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
