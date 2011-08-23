<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.jtalks.jcommune.web.view.messages" />
<fmt:setLocale value="en"/>

<html>
<head>
</head>
<body>Welcome to JCommune project
<br>
<a href="${pageContext.request.contextPath}/main.html">&nbsp;&nbsp;
       <span class="nav"><fmt:message key="label.forum"/> </span> </a>


</body>
</html>
