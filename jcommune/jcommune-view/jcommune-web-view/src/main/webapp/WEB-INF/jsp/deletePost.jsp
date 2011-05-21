<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
<title>Delete</title>
</head>
<body>
	<div>
        <c:out value="Are you sure you want to delete Post ?"/>
		<form:form action='${pageContext.request.contextPath}/deletePost.html' method="DELETE">
		    <input name="topicId" type="hidden" value="${topicId}"/>
            <input name="postId" type="hidden" value="${postId}"/>
		    <input type="submit" value="<spring:message code="label.yes"/>"/>
		</form:form>
		<form:form action='${pageContext.request.contextPath}/topics/${topicId}.html' method="GET">
            <input type="submit" value="<spring:message code="label.cancel"/>"/>
        </form:form>
	</div>
</body>
</html>