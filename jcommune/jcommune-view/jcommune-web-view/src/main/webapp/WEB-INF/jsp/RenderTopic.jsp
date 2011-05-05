<%@ page import="org.jtalks.jcommune.model.entity.Topic" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<form:form action="${pageContext.request.contextPath}/forum.html" method="GET">
    <table border="0" width="100%">
        <h2><spring:message code="label.topic"/>:<c:out value="${selectedTopic.title}"/></h2>  <br>
        <c:forEach var="posts" items="${selectedTopic.posts}">
            <tr>
                <td width="20%"><spring:message code="label.author"/>: <c:out value="${posts.userCreated.nickName}"/></td>
                <td width="80%"><spring:message code="label.text"/>: <c:out value="${posts.postContent}"/></td>
            </tr>
        </c:forEach>
    </table>  <br>
    <input type="submit" value="<spring:message code="label.back"/>"/>
</form:form>
</body>
</html>