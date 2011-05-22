<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Forum</title>
</head>
<body>
<form:form action="${pageContext.request.contextPath}/newTopic.html" method="GET">
    <table border="1" width="100%">
        <tr>
            <td width="80%"><spring:message code="label.topic"/></td>
            <td width="10%"><spring:message code="label.author"/></td>
            <td width="10%"><spring:message code="label.date"/></td>
        </tr>

        <c:forEach var="topics" items="${topicsList}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/branches/${branchId}/topics/${topics.id}.html"> <c:out
                        value="${topics.title}"/></a></td>
                <td><c:out value="${topics.topicStarter.username}"/></td>
                <td><joda:format value="${topics.creationDate}"
                                 locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                                 pattern="dd MMM yyyy HH:mm"/></td>
            </tr>
        </c:forEach>
    </table>
    <br>
    <input type="submit" value="<spring:message code="label.addtopic"/>"/>
    <input name="branchId" type="hidden" value="${branchId}">
</form:form>
</body>
</html>