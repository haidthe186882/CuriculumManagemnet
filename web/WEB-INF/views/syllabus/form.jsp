<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="syllabus"/>
<c:set var="pageTitle" value="New Syllabus" />
<c:set var="pageSubtitle" value="Create syllabus for a subject" />
<c:set var="backUrl" value="${pageContext.request.contextPath}/syllabus/list" />
<c:set var="bodyPage" value="/WEB-INF/views/common/form-fragments/syllabus-form.jsp" />
<jsp:include page="/WEB-INF/views/common/form-template.jsp" />
