<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Combo Detail - ${combo.comboCode}</title>
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
                <div class="page-title">Combo Detail: <span class="text-warning">${combo.comboCode}</span></div>
                <div class="page-subtitle text-muted">${combo.comboName} | ${combo.englishName}</div>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/combo?action=list&curriculumId=${combo.curriculumId}" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Combo List
                </a>
            </div>
        </div>

        <div class="row">
<!--            <div class="col-lg-12">
                <div class="card-dark p-4 mb-4">
                    <h6 class="text-uppercase text-secondary mb-3"><i class="bi bi-info-circle me-1"></i> Combo Description</h6>
                    <p class="text-muted">${not empty combo.description ? combo.description : 'No description available.'}</p>
                </div>
            </div>-->

            <div class="col-lg-12">
                <div class="card-dark p-4">
                    <h6 class="mb-3"><i class="bi bi-book me-1"></i> Subjects in this Combo</h6>
                    <div class="table-responsive">
                        <table class="table table-dark-custom align-middle mb-0">
                            <thead>
                                <tr>
                                    <th>Subject Code</th>
                                    <th>Subject Name</th>
                                    <th>English Name</th>
                                    <th class="text-center">Credits</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${subjects}">
                                    <tr>
                                        <td><span class="badge bg-secondary">${s.subjectCode}</span></td>
                                        <td class="fw-bold">${s.subjectName}</td>
                                        <td class="text-muted">${s.englishName}</td>
                                        <td class="text-center fw-bold text-info">${s.credits}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>