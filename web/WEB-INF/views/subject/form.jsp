<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeMenu" value="subject"/>
<c:set var="pageTitle" value="New Subject" />
<c:set var="pageSubtitle" value="Create a new subject" />
<c:set var="backUrl" value="${pageContext.request.contextPath}/subject/list" />
<c:set var="bodyPage" value="/WEB-INF/views/common/form-fragments/subject-form.jsp" />
<jsp:include page="/WEB-INF/views/common/form-template.jsp" />
