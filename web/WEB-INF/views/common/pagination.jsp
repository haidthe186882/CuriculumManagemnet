<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Required: currentPage, totalPages, totalItems, pageFrom, pageTo, pageSize
  paginationPath  — e.g. /curriculum/list
  paginationQuery — optional &key=value filter string
--%>
<style>
    .ltms-pagination { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.75rem; padding:0.85rem 1.1rem; border-top:1px solid var(--border); }
    .ltms-pagination .info { color:var(--muted); font-size:0.85rem; }
    .ltms-pagination .page-link {
        color:#374151; border:1px solid var(--border); background:#fff; padding:0.35rem 0.7rem;
        font-size:0.85rem; font-weight:500; border-radius:8px; margin:0 2px;
    }
    .ltms-pagination .page-item.active .page-link {
        background: linear-gradient(135deg, var(--accent), var(--accent-dark));
        border-color: transparent; color:#fff;
    }
    .ltms-pagination .page-item.disabled .page-link { opacity:0.45; pointer-events:none; }
    .ltms-pagination .page-link:hover { background:#f9fafb; color:var(--accent-dark); }
    .ltms-pagination .page-item.active .page-link:hover { color:#fff; background: linear-gradient(135deg, var(--accent), var(--accent-dark)); }
</style>
<c:if test="${totalItems > 0}">
    <div class="ltms-pagination">
        <div class="info">
            Showing <strong>${pageFrom}</strong>-<strong>${pageTo}</strong> of <strong>${totalItems}</strong>
            <span class="ms-1">(page ${currentPage}/${totalPages})</span>
        </div>
        <c:if test="${totalPages > 1}">
            <nav aria-label="Table pagination">
                <ul class="pagination mb-0">
                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}${paginationPath}?page=${currentPage - 1}&amp;pageSize=${pageSize}${paginationQuery}">
                            <i class="bi bi-chevron-left"></i>
                        </a>
                    </li>
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:choose>
                            <c:when test="${totalPages <= 7 or p == 1 or p == totalPages or (p >= currentPage - 1 and p <= currentPage + 1)}">
                                <li class="page-item ${p == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}${paginationPath}?page=${p}&amp;pageSize=${pageSize}${paginationQuery}">${p}</a>
                                </li>
                            </c:when>
                            <c:when test="${p == 2 and currentPage > 3}">
                                <li class="page-item disabled"><span class="page-link">…</span></li>
                            </c:when>
                            <c:when test="${p == totalPages - 1 and currentPage < totalPages - 2}">
                                <li class="page-item disabled"><span class="page-link">…</span></li>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}${paginationPath}?page=${currentPage + 1}&amp;pageSize=${pageSize}${paginationQuery}">
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>
</c:if>
