<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="curriculum"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Semester Roadmap — ${curriculum.curriculumCode}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/views/common/styles.jsp" %>
    <style>
        .roadmap-summary {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 0.75rem;
        }
        @media (max-width: 768px) {
            .roadmap-summary { grid-template-columns: 1fr; }
        }
        .summary-tile {
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 0.9rem 1rem;
            background: rgba(248, 250, 252, 0.6);
        }
        .summary-tile .label {
            font-size: 0.75rem;
            color: var(--muted);
            margin-bottom: 0.25rem;
        }
        .summary-tile .value {
            font-size: 1.25rem;
            font-weight: 700;
            color: #111827;
        }
        .semester-card {
            border: 1px solid var(--border);
            border-radius: 12px;
            overflow: hidden;
            margin-bottom: 1.25rem;
            background: var(--card-bg);
        }
        .semester-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 1rem;
            padding: 0.85rem 1.15rem;
            border-bottom: 1px solid var(--border);
            background: linear-gradient(135deg, rgba(59,130,246,0.06), rgba(16,185,129,0.04));
        }
        .semester-title {
            display: flex;
            align-items: center;
            gap: 0.6rem;
            font-weight: 650;
            color: #111827;
        }
        .semester-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 2rem;
            height: 2rem;
            border-radius: 8px;
            background: rgba(59,130,246,0.12);
            color: #2563eb;
            font-weight: 700;
            font-size: 0.9rem;
        }
        .semester-meta {
            font-size: 0.82rem;
            color: var(--muted);
            white-space: nowrap;
        }
        .subject-row-link {
            cursor: pointer;
        }
        .subject-row-link:hover td {
            background: rgba(59,130,246,0.03);
        }
        .empty-roadmap {
            text-align: center;
            padding: 3rem 1rem;
            color: #94a3b8;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jsp" %>
<div class="main-content">
    <div class="topbar">
        <div>
            <div class="page-title">Semester Roadmap</div>
            <div class="page-subtitle">
                <code style="color:var(--accent);">${curriculum.curriculumCode}</code>
                — ${curriculum.curriculumName}
            </div>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/curriculum/detail?id=${curriculum.curriculumId}"
               class="btn btn-secondary-custom">
                <i class="bi bi-arrow-left me-1"></i>Back
            </a>
        </div>
    </div>

    <%-- Summary --%>
    <div class="card-dark p-4 mb-4">
        <div class="mb-3">
            <h6 class="mb-1">Learning path by semester</h6>
            <div class="text-muted" style="font-size:0.85rem;">
                Subjects grouped by semester
            </div>
        </div>
        <div class="roadmap-summary">
            <div class="summary-tile">
                <div class="label">Subjects</div>
                <div class="value">${totalSubjects}</div>
            </div>
            <div class="summary-tile">
                <div class="label">Total credits</div>
                <div class="value">${totalCredits}</div>
            </div>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty subjectsBySemester}">
            <div class="card-dark">
                <div class="empty-roadmap">
                    <i class="bi bi-inbox" style="font-size:2rem;display:block;margin-bottom:0.5rem;"></i>
                    No subjects in this curriculum yet.
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach var="entry" items="${subjectsBySemester}">
                <c:set var="sem" value="${entry.key}"/>
                <c:set var="semSubjects" value="${entry.value}"/>
                <c:set var="semCredits" value="${creditsBySemester[sem]}"/>

                <div class="semester-card">
                    <div class="semester-header">
                        <div class="semester-title">
                            <span class="semester-badge">
                                <c:choose>
                                    <c:when test="${sem == 0}">?</c:when>
                                    <c:otherwise>${sem}</c:otherwise>
                                </c:choose>
                            </span>
                            <span>
                                <c:choose>
                                    <c:when test="${sem == 0}">Unassigned semester</c:when>
                                    <c:otherwise>Semester ${sem}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="semester-meta">
                            ${semSubjects.size()} subject(s) · ${semCredits} credits
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-dark-custom mb-0">
                            <thead>
                                <tr>
                                    <th style="width:4%;">#</th>
                                    <th style="width:12%;">Code</th>
                                    <th>Subject</th>
                                    <th style="width:8%;">Credits</th>
                                    <th style="width:18%;">Prerequisite</th>
                                    <th style="width:14%;">Syllabus</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="cs" items="${semSubjects}" varStatus="st">
                                    <tr class="subject-row-link"
                                        onclick="window.location='${pageContext.request.contextPath}/subject/detail?id=${cs.subject.subjectId}&curriculumId=${curriculum.curriculumId}'">
                                        <td>${st.count}</td>
                                        <td><code style="color:var(--accent);">${cs.subject.subjectCode}</code></td>
                                        <td>${cs.subject.subjectName}</td>
                                        <td>${cs.subject.credits}</td>
                                        <td class="text-muted" style="font-size:0.82rem;">
                                            <c:choose>
                                                <c:when test="${not empty cs.subject.prerequisiteCodes}">
                                                    <code style="color:#b45309;">${cs.subject.prerequisiteCodes}</code>
                                                </c:when>
                                                <c:otherwise>None</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td onclick="event.stopPropagation();">
                                            <c:choose>
                                                <c:when test="${not empty cs.subject.syllabusId}">
                                                    <a href="${pageContext.request.contextPath}/syllabus/detail?id=${cs.subject.syllabusId}"
                                                       class="btn btn-sm btn-outline-info py-1 px-2" style="font-size:0.78rem;">
                                                        <i class="bi bi-file-earmark-text me-1"></i>View
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted" style="font-size:0.8rem;">No Syllabus</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
