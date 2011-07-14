<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<sec:authentication property="principal.username" var="auth" scope="request"/>
<head>
    <title><spring:message code="label.user"/> - "${auth}"</title>
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>
<div id="editUserdetails" >
    <form:form id = "editProfileForm" name="editProfileForm" 
        action="${pageContext.request.contextPath}/user/edit.html"
        modelAttribute="editedUser" method="POST">
    <ul>
        <li>
            <label><spring:message code="label.username"/></label>
            <span>"${auth}"</span>
        </li>
        <li>
            <label><spring:message code="label.email"/></label>
            <form:input path="email" size="25" value="${editedUser.email}"/>            
            <form:errors path="email" cssClass="error"/>
        </li>
        <li>
            <label><spring:message code="label.firstname"/></label>
            <form:input path="firstName" size="25" value="${editedUser.firstName}"/>
            <form:errors path="firstName" cssClass="error"/>                   
        </li>
        <li>
            <label><spring:message code="label.lastname"/></label>   
            <form:input path="lastName" size="25" value="${editedUser.lastName}"/>
            <form:errors path="lastName" cssClass="error"/>         
        </li>
        <li>
            <label><spring:message code="label.newPassword"/></label>   
            <form:input path="newUserPassword" size="25" type="password"/>
            <form:errors path="newUserPassword" cssClass="error"/>         
        </li>
        <li>
            <label><spring:message code="label.newPasswordConfiration"/></label>
            <form:input path="newUserPasswordConfirm" size="25" type="password"/>
            <form:errors path="newUserPasswordConfirm" cssClass="error"/>         
        </li>
        <li>        
            <label><spring:message code="label.currentPassword"/></label>   
            <form:input path="currentUserPassword" size="25" type="password"/>
            <form:errors path="currentUserPassword" cssClass="error"/>
        </li>
        <li>
            <input type="submit" value="<spring:message code="label.save_changes"/>"/>
        </li>
    </ul>
    </form:form>
</div>
</body>
</html>
