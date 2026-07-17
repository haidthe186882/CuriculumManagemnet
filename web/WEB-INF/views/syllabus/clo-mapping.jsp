<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="syllabus" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>CLO — PLO Mapping — LTMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
              rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">
        <%@ include file="/WEB-INF/views/common/styles.jsp" %>
    </head>

    <body>
        <%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
        <div class="main-content">
            <div class="topbar">
                <div>
                    <div class="page-title">CLO — PLO Mapping</div>
                    <div class="page-subtitle">${syllabus.subject.subjectCode} — ${syllabus.syllabusName}</div>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/syllabus/detail?id=${syllabus.syllabusId}"
                       class="btn btn-secondary-custom">
                        <i class="bi bi-arrow-left me-1"></i>Back to Syllabus
                    </a>
                </div>
            </div>

            <!-- Syllabus info header -->
            <div class="card-dark p-4 mb-4">
                <div class="row">
                    <div class="col-md-4">
                        <div class="detail-label">Subject Code</div>
                        <div class="detail-value">${syllabus.subject.subjectCode}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Syllabus Name</div>
                        <div class="detail-value">${syllabus.syllabusName}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="detail-label">Syllabus English Name</div>
                        <div class="detail-value">${syllabus.englishName}</div>
                    </div>
                </div>
            </div>

            <c:if test="${empty mappingTables}">
                <div class="card-dark p-4 text-center text-muted">
                    <i class="bi bi-diagram-3 display-6 d-block mb-2"></i>
                    This subject is not linked to any curriculum yet, so there is no PLO set to map against.
                </div>
            </c:if>

            <c:forEach var="table" items="${mappingTables}">
                <div class="card-dark mb-4">
                    <div class="p-3 border-bottom" style="background: var(--accent); border-radius: 12px 12px 0 0;">
                        <h6 class="mb-0" style="color:#fff;">
                            Mapping of CLOs to PLOs of Curriculum ${table.curriculumCode}
                            <span style="font-weight:400; opacity:0.85;"> — ${table.curriculumName}</span>
                        </h6>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-dark-custom mb-0 text-center">
                            <thead>
                                <tr>
                                    <th style="text-align:left; background-color: var(--accent) !important; color:#fff !important; text-transform:none;">CLO</th>
                                    <c:choose>
                                        <c:when test="${empty table.plos}">
                                            <th style="background-color: var(--accent) !important; color:#fff !important; text-transform:none;">
                                                No PLO defined for this curriculum
                                            </th>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="plo" items="${table.plos}">
                                                <th style="background-color: var(--accent) !important; color:#fff !important; text-transform:none;" title="${plo.description}">
                                                    ${plo.ploCode}
                                                </th>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty clos}">
                                        <tr><td colspan="${table.plos.size() + 1}" class="text-center py-4 text-muted">No CLOs defined for this syllabus yet.</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="clo" items="${clos}">
                                            <tr>
                                                <td style="text-align:left;"><strong style="color:#111827;">${clo.cloCode}</strong></td>
                                                <c:forEach var="plo" items="${table.plos}">
                                                    <td>
                                                        <c:if test="${table.matrix[clo.cloId][plo.ploId]}">
                                                            <i class="bi bi-check-lg" style="color:#16a34a; font-weight:700;"></i>
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
            </c:forEach>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
