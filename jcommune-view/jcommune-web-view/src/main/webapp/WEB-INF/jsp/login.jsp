<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.signin"/></title>
</head>
<body>
<c:if test="${not empty param.login_error}">
    <span style="color: red; ">
        <spring:message code="label.login_error"/>
    </span>
</c:if>
<form action='<c:url value="/j_spring_security_check"/>' method="POST">
    <p>
        <label for="j_username"><spring:message code="label.username"/></label>
        <input class="textbox" id="j_username" type='text' name='j_username'/>
        <br/>
        <label for="j_password"><spring:message code="label.password"/></label>
        <input class="textbox" id="j_password" type='password' name='j_password'/>
        <br/>
        <a href='<c:url value="/registration.html" />'><spring:message code="label.register"/></a><br/>
        <br/>
        <input type="submit" value="<spring:message code="label.signin"/>"/>
    </p>
</form>
</body>
</html>