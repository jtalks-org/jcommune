<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.user"/> - ${user.username}</title>
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>
<sec:authentication property="principal.username" var="auth" scope="request"/>
<div id="userdetails">
    <ul>
        <li>
            <label><spring:message code="label.username"/></label>
            <span>${user.username}</span>
        </li>
        <li>
            <label>Email</label>
            <c:choose>
                <c:when test="${user.username == auth}">
                    <span>${user.email}</span>
                </c:when>
                <c:otherwise>
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <span>${user.email}</span>
                    </sec:authorize>
                </c:otherwise>
            </c:choose>
        </li>
        <li>
            <label><spring:message code="label.firstname"/></label>
            <span>${user.firstName}</span>
        </li>
        <li>
            <label><spring:message code="label.lastname"/></label>
            <span>${user.lastName}</span>
        </li>
        <li>
            <label><spring:message code="label.lastlogin"/></label>
            <span>
                <joda:format value="${user.lastLogin}"
                             locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                             pattern="dd MMM yyyy HH:mm"/>
            </span>
        </li>               
        <li> 
            <c:if test="${user.username == auth}">
	            <a href="${pageContext.request.contextPath}/user/edit.html">
	                <label>Edit</label>                
	            </a>
            </c:if>
        </li>
    </ul>
</div>
</body>
</html>
