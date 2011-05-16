<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.register"/></title>
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet>
</head>

<body>
<div id="stylized" class="registration">
    <form:form id="form" name="form" action='${pageContext.request.contextPath}/user.html'
               modelAttribute="newUser" method="POST">
        <h1>Sign-up</h1>

        <p>Please fill this form for sign-up</p>

        <form:label path="username"><spring:message code="label.username"/>
            <span class="small">Must be 3-20 characters</span>
        </form:label>
        <form:input path="username" type="text" size="20"/>
        <form:errors path="username"/>

        <form:label path="email">Email
            <span class="small">Enter valid email</span>
        </form:label>
        <form:input path="email" type="text" size="25"/>
        <form:errors path="email"/>

        <form:label path="firstName">First Name
            <span class="small">Your first name</span>
        </form:label>
        <form:input path="firstName" type="text" size="25"/>
        <form:errors path="firstName"/>

        <form:label path="lastName">Last Name
            <span class="small">Your last name</span>
        </form:label>
        <form:input path="lastName" type="text" size="25"/>
        <form:errors path="lastName"/>

        <form:label path="password"><spring:message code="label.password"/>
            <span class="small">Must be more than 4 characters</span>
        </form:label>
        <form:input path="password" type="password" size="20"/>
        <form:errors path="password"/>

        <form:label path="passwordConfirm">Confirm password
            <span class="small">Repeat password</span>
        </form:label>
        <form:input path="passwordConfirm" type="password" size="20"/>
        <form:errors path="passwordConfirm"/>

        <button type="submit">Sign-up</button>
        <div class="spacer"></div>
    </form:form>
</div>

</body>
</html>