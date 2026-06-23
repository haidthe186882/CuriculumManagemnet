<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<c:set var="canEdit" value="${sessionScope.loggedUser.role.roleName == 'Designer' or sessionScope.loggedUser.role.roleName == 'Admin'}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Curriculum POs &amp; PLOs — LTMS</title>
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
            <div class="page-title">Program Objectives (POs) &amp; PLOs</div>
            <div class="page-subtitle">${curriculum.curriculumName} (<code style="color:var(--accent);">${curriculum.curriculumCode}</code>)</div>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/curriculum/detail?id=${curriculum.curriculumId}" class="btn btn-secondary-custom">
                <i class="bi bi-arrow-left me-1"></i>Back to Detail
            </a>
        </div>
    </div>

    <c:if test="${param.msg == 'poAdded'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PO added.</div>
    </c:if>
    <c:if test="${param.msg == 'poDeleted'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PO deleted.</div>
    </c:if>
    <c:if test="${param.msg == 'ploAdded'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PLO added.</div>
    </c:if>
    <c:if test="${param.msg == 'ploDeleted'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>PLO deleted.</div>
    </c:if>
    <c:if test="${param.msg == 'mappingSaved'}">
        <div class="alert alert-success-dark mb-3"><i class="bi bi-check-circle me-1"></i>Mapping saved.</div>
    </c:if>

    <!-- Add PO form (Designer/Admin only) -->
    <c:if test="${canEdit}">
        <div class="card-dark p-3 mb-3">
            <h6 class="mb-3"><i class="bi bi-plus-circle me-2" style="color:var(--accent);"></i>Add Program Objective (PO)</h6>
            <form method="post" action="${pageContext.request.contextPath}/curriculum/po">
                <input type="hidden" name="action" value="addPo">
                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                <div class="row g-2">
                    <div class="col-md-2">
                        <input type="text" name="poCode" class="search-bar form-control w-100" placeholder="PO Code (e.g. PO1)" required>
                    </div>
                    <div class="col-md-8">
                        <input type="text" name="description" class="search-bar form-control w-100" placeholder="Description" required>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary-custom w-100">Add PO</button>
                    </div>
                </div>
            </form>
        </div>
    </c:if>

    <!-- PO Table Card -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Program Objectives (POs)</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">#</th>
                        <th style="width: 160px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">PO Code</th>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">Description</th>
                        <c:if test="${canEdit}">
                            <th style="width: 90px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">Action</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pos}">
                            <tr><td colspan="${canEdit ? 4 : 3}" class="text-center py-4 text-muted">No POs defined for this curriculum yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="po" items="${pos}" varStatus="st">
                                <tr>
                                    <td style="padding: 0.8rem 0.9rem;">${st.count}</td>
                                    <td style="padding: 0.8rem 0.9rem;"><strong style="color: #111827;">${po.poCode}</strong></td>
                                    <td style="padding: 0.8rem 0.9rem;">${po.description}</td>
                                    <c:if test="${canEdit}">
                                        <td style="padding: 0.8rem 0.9rem;">
                                            <form method="post" action="${pageContext.request.contextPath}/curriculum/po"
                                                  onsubmit="return confirm('Delete this PO? Its mappings will also be removed.');">
                                                <input type="hidden" name="action" value="deletePo">
                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                <input type="hidden" name="poId" value="${po.poId}">
                                                <button type="submit" class="btn btn-action btn-danger-custom"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Add PLO form (Designer/Admin only) -->
    <c:if test="${canEdit}">
        <div class="card-dark p-3 mb-3">
            <h6 class="mb-3"><i class="bi bi-plus-circle me-2" style="color:var(--accent);"></i>Add Program Learning Outcome (PLO)</h6>
            <form method="post" action="${pageContext.request.contextPath}/curriculum/po">
                <input type="hidden" name="action" value="addPlo">
                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                <div class="row g-2">
                    <div class="col-md-2">
                        <input type="text" name="ploCode" class="search-bar form-control w-100" placeholder="PLO Code (e.g. PLO1)" required>
                    </div>
                    <div class="col-md-8">
                        <input type="text" name="description" class="search-bar form-control w-100" placeholder="Description" required>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary-custom w-100">Add PLO</button>
                    </div>
                </div>
            </form>
        </div>
    </c:if>

    <!-- PLO Table Card -->
    <div class="card-dark mb-4">
        <div class="p-3 border-bottom"><h6 class="mb-0">Program Learning Outcomes (PLOs)</h6></div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th style="width: 80px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">#</th>
                        <th style="width: 160px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">PLO Code</th>
                        <th style="background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">Description</th>
                        <c:if test="${canEdit}">
                            <th style="width: 90px; background-color: var(--accent) !important; color: #ffffff !important; text-transform: none; letter-spacing: normal; padding: 0.9rem;">Action</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty plos}">
                            <tr><td colspan="${canEdit ? 4 : 3}" class="text-center py-4 text-muted">No PLOs defined for this curriculum yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="plo" items="${plos}" varStatus="st">
                                <tr>
                                    <td style="padding: 0.8rem 0.9rem;">${st.count}</td>
                                    <td style="padding: 0.8rem 0.9rem;"><strong style="color: #111827;">${plo.ploCode}</strong></td>
                                    <td style="padding: 0.8rem 0.9rem;">${plo.description}</td>
                                    <c:if test="${canEdit}">
                                        <td style="padding: 0.8rem 0.9rem;">
                                            <form method="post" action="${pageContext.request.contextPath}/curriculum/po"
                                                  onsubmit="return confirm('Delete this PLO? Its mappings will also be removed.');">
                                                <input type="hidden" name="action" value="deletePlo">
                                                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
                                                <input type="hidden" name="ploId" value="${plo.ploId}">
                                                <button type="submit" class="btn btn-action btn-danger-custom"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </c:if>
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
        <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
            <h6 class="mb-0">Mapping POs to PLOs</h6>
            <c:if test="${canEdit and not empty pos and not empty plos}">
                <span class="text-muted" style="font-size:0.8rem;">Tick the boxes, then click Save Mapping</span>
            </c:if>
        </div>

        <c:if test="${canEdit}">
            <form id="mappingForm" method="post" action="${pageContext.request.contextPath}/curriculum/po">
                <input type="hidden" name="action" value="saveMapping">
                <input type="hidden" name="curriculumId" value="${curriculum.curriculumId}">
        </c:if>

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
                        <c:when test="${empty plos or empty pos}">
                            <tr>
                                <td colspan="${pos.size() + 1}" class="text-center py-4 text-muted">Add at least 1 PO and 1 PLO to start mapping.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="plo" items="${plos}">
                                <tr>
                                    <td style="text-align: left; padding: 0.8rem 0.9rem; font-weight: 600; color: #111827;">${plo.ploCode}</td>
                                    <c:forEach var="po" items="${pos}">
                                        <c:set var="mapKey" value="${po.poId}_${plo.ploId}"/>
                                        <td style="padding: 0.8rem 0.9rem; font-size: 1.15rem; font-weight: 600; color: #111827;">
                                            <c:choose>
                                                <c:when test="${canEdit}">
                                                    <input type="checkbox" name="mapKey" value="${mapKey}"
                                                           ${mappings[mapKey] ? 'checked' : ''} style="width:18px;height:18px;cursor:pointer;">
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${mappings[mapKey]}">✓</c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <c:if test="${canEdit}">
            <c:if test="${not empty pos and not empty plos}">
                <div class="p-3 border-top text-end">
                    <button type="submit" form="mappingForm" class="btn btn-primary-custom">
                        <i class="bi bi-save me-1"></i>Save Mapping
                    </button>
                </div>
            </c:if>
            </form>
        </c:if>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
