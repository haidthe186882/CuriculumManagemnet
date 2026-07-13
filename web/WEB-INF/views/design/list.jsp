<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="design"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Design Assignments — LTMS</title>
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
            <div class="page-title">My Design Assignments</div>
            <div class="page-subtitle">Syllabuses assigned to you for design</div>
        </div>
    </div>

    <div class="card-dark p-3 mb-3">
        <form method="get" action="${pageContext.request.contextPath}/design/list">
            <div class="row g-2">
                <div class="col-md-10">
                    <input type="text" name="keyword" class="search-bar form-control w-100"
                           placeholder="Search by name or subject code..." value="${keyword}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary-custom w-100">Search</button>
                </div>
            </div>
        </form>
    </div>

    <div class="card-dark">
        <div class="p-3 border-bottom">
            <h6 class="mb-0"><i class="bi bi-pencil-square me-2" style="color:#4fc3f7;"></i>Assigned Syllabus (${assignedSubjects != null ? assignedSubjects.size() : 0})</h6>
        </div>
        <div class="table-responsive">
            <table class="table table-dark-custom mb-0">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Subject Code</th>
                        <th>Syllabus Name</th>
                        <th>BELONGS TO CURRICULUM</th>
                        <th>SEMESTER</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty assignedSubjects}">
                            <tr><td colspan="6" class="text-center">No syllabus assignments yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cs" items="${assignedSubjects}" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><code class="text-primary">${cs.subject.subjectCode}</code></td>
                                    <td>${cs.subject.subjectName}</td>
                                    <td><span class="badge bg-secondary">${cs.curriculum.curriculumCode}</span></td>
                                    <td>${cs.semesterNo}</td>
                                    <td>
                                        <%-- Sửa link nút Open trỏ về trang soạn thảo Syllabus của môn học đó --%>
                                        <a href="${pageContext.request.contextPath}/syllabus/edit?subjectId=${cs.subject.subjectId}&curriculumId=${cs.curriculumId}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-eye"></i> Open
                                        </a>
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
</body>
</html>
