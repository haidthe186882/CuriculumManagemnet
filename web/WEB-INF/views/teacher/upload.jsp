<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Material — Teacher</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Upload Material</div>
            <div class="page-subtitle">Upload documents and resources for your courses</div>
        </div>
    </div>

    <div class="card-dark p-4">
        <c:if test="${not empty sessionScope.msg}">
            <div class="alert alert-success">${sessionScope.msg}</div>
            <c:remove var="msg" scope="session"/>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/teacher/upload" enctype="multipart/form-data">
            <div class="mb-3">
                <label class="form-label">Select file</label>
                <input type="file" name="file" class="form-control">
            </div>
            <button type="submit" class="btn btn-primary-custom">Upload</button>
        </form>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
