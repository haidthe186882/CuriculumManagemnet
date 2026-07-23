<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Combo Management - ${curriculum.curriculumCode}</title>
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
                <div class="page-title">Combo Management</div>
                <div class="page-subtitle">Curriculum: ${curriculum.curriculumCode} - ${curriculum.curriculumName}</div>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/curriculum/detail?id=${curriculum.curriculumId}" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
            </div>
        </div>

        <div class="card-dark p-4">
            <div class="table-responsive">
                <table class="table table-dark-custom align-middle mb-0">
                    <thead>
                        <tr>
                            <th>Combo Code</th>
                            <th>Combo Name</th>
                            <th>English Name</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${comboList}">
                            <tr>
                                <td><span class="badge bg-primary">${c.comboCode}</span></td>
                                <td class="fw-bold">
                                    <a href="${pageContext.request.contextPath}/combo?action=detail&comboId=${c.comboId}&curriculumCode=${curriculum.curriculumCode}&comboName=${c.comboName}" 
                                       class="text-decoration-none">
                                        ${c.comboName}
                                    </a>
                                </td>
                                <td class="text-muted">${c.englishName}</td>
                                <td>
                                    <span class="badge ${c.active ? 'bg-success' : 'bg-danger'}">
                                        ${c.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty comboList}">
                            <tr>
                                <td colspan="5" class="text-center text-muted py-4">No combos found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>