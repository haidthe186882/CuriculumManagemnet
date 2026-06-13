<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="admin"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Program Form — LTMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Program</div>
            <div class="page-subtitle">Create or edit a program</div>
        </div>
    </div>

    <div class="card-dark p-4" style="max-width:720px">
        <form method="post" action="${pageContext.request.contextPath}/program/list">
            <c:if test="${not empty program}">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="programId" value="${program.programId}">
            </c:if>
            <c:if test="${empty program}">
                <input type="hidden" name="action" value="create">
            </c:if>
            <div class="mb-2">
                <label class="detail-label">Program Code</label>
                <input type="text" name="programCode" class="form-control form-control-dark" value="${program.programCode}">
            </div>
            <div class="mb-2">
                <label class="detail-label">Program Name</label>
                <input type="text" name="programName" class="form-control form-control-dark" value="${program.programName}">
            </div>
            <div class="mb-3">
                <label class="detail-label">Description</label>
                <textarea name="description" class="form-control form-control-dark" rows="4">${program.description}</textarea>
            </div>
            <button class="btn btn-primary-custom">Save</button>
            <a href="${pageContext.request.contextPath}/program/list" class="btn btn-secondary-custom ms-2">Cancel</a>
        </form>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
