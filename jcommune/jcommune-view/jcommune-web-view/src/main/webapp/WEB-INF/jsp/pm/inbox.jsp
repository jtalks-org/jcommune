<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.pm_title"/></title>
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>
<table>
    <tr>
        <td>
            <jsp:include page="pmNavigationMenu.jsp"/>
        </td>
        <td>
            <h1><spring:message code="label.inbox"/></h1>
            <table>
                <tr>
                    <td><spring:message code="label.sender"/></td>
                    <td><spring:message code="label.title"/></td>
                    <td><spring:message code="label.sending_date"/></td>
                </tr>
                <c:forEach var="pm" items="${pmList}">
                    <c:choose>
                        <c:when test="${pm.readed}">
                            <tr>
                        </c:when>
                        <c:otherwise>
                            <tr bgcolor="#b0c4de">
                        </c:otherwise>
                    </c:choose>
                    <td>
                        <c:out value="${pm.userFrom.username}"/>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/pm/inbox/${pm.id}.html"><c:out value="${pm.title}"/></a>
                    </td>
                    <td>
                        <joda:format value="${pm.creationDate}"
                                     locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                                     pattern="dd MMM yyyy HH:mm"/>
                    </td>
                    </tr>
                </c:forEach>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
