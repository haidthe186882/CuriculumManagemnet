<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Curriculum POs — LTMS</title>
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
            <div class="page-title">Program Objectives (POs)</div>
            <div class="page-subtitle">${curriculum.curriculumName} (<code style="color:var(--accent);">${curriculum.curriculumCode}</code>)</div>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/curriculum/detail?id=${curriculum.curriculumId}" class="btn btn-secondary-custom">
                <i class="bi bi-arrow-left me-1"></i>Back to Detail
            </a>
        </div>
    </div>

    <!-- PO Table Card -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Program Objectives (POs)</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">#</th>
                        <th style="width: 200px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">PO Name</th>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">PO Description</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pos}">
                            <tr><td colspan="3" class="text-center py-4 text-muted">No POs defined for this curriculum yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="po" items="${pos}" varStatus="st">
                                <tr>
                                    <td style="padding: 0.8rem 0.9rem;">${st.count}</td>
                                    <td style="padding: 0.8rem 0.9rem;"><strong style="color: #111827;">${po.poCode}</strong></td>
                                    <td style="padding: 0.8rem 0.9rem;">${po.description}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Mapping POs to PLOs Card -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Mapping POs to PLOs</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0 text-center">
                <thead>
                    <tr>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-align: left; padding: 0.9rem; width: 120px;">PLO(s)</th>
                        <c:forEach var="po" items="${pos}">
                            <th style="background-color: var(--accent) !important; color: #ffffff !important; text-align: center; padding: 0.9rem;">${po.poCode}</th>
                        </c:forEach>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty plos}">
                            <tr>
                                <td colspan="${pos.size() + 1}" class="text-center py-4 text-muted">No PLOs defined for this curriculum yet.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="plo" items="${plos}">
                                <tr>
                                    <td style="text-align: left; padding: 0.8rem 0.9rem; font-weight: 600; color: #111827;">${plo.ploCode}</td>
                                    <c:forEach var="po" items="${pos}">
                                        <c:set var="mapKey" value="${po.poId}_${plo.ploId}"/>
                                        <td style="padding: 0.8rem 0.9rem; font-size: 1.15rem; font-weight: 600; color: #111827;">
                                            <c:if test="${mappings[mapKey]}">
                                                ✓
                                            </c:if>
                                        </td>
                                    </c:forEach>
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
