<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activeMenu" scope="request" value="curriculum"/>
<c:set var="isEdit" scope="request" value="${not empty curriculum.curriculumId}"/>
<c:set var="pageTitle" scope="request" value="${isEdit ? 'Edit Curriculum' : 'New Curriculum'}"/>
<c:set var="pageSubtitle" scope="request" value="Fill in curriculum information"/>
<c:set var="backUrl" scope="request" value="${pageContext.request.contextPath}/curriculum/list"/>
<c:set var="bodyPage"
       scope="request"
       value="/WEB-INF/views/common/form-fragments/curriculum-form.jsp"/>
<jsp:include page="/WEB-INF/views/common/form-template.jsp"/>

