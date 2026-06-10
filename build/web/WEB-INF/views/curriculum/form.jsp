<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="curriculum"/>
<c:set var="isEdit" value="${not empty curriculum.curriculumId}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Curriculum' : 'New Curriculum'}" />
<c:set var="pageSubtitle" value="Fill in curriculum information" />
<c:set var="backUrl" value="${pageContext.request.contextPath}/curriculum/list" />
<c:set var="bodyPage" value="/WEB-INF/views/common/form-fragments/curriculum-form.jsp" />
<jsp:include page="/WEB-INF/views/common/form-template.jsp" />
