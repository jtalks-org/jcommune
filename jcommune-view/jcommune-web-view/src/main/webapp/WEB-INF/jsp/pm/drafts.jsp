<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.pm_title"/></title>
    <link href="${pageContext.request.contextPath}/resources/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>
<div align="left">
    <jsp:include page="pmNavigationMenu.jsp"/>
    <div>
        <h1><spring:message code="label.drafts"/></h1>
         <table>
             <tr>
                 <td><spring:message code="label.recipient"/></td>
                 <td><spring:message code="label.title"/></td>
                 <td><spring:message code="label.sending_date"/></td>
                 <td><spring:message code="label.edit"/></td>
             </tr>
             <c:forEach var="pm" items="${pmList}">
             <tr>
                 <td><c:out value="${pm.userTo.username}"/></td>
                 <td><a href="${pageContext.request.contextPath}/pm/drafts/${pm.id}.html">
                         <c:out value="${pm.title}"/></a>
                 </td>
                 <td><joda:format value="${pm.creationDate}"
                                  locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                                  pattern="dd MMM yyyy HH:mm"/></td>
                 <td><a href="${pageContext.request.contextPath}/pm/${pm.id}/edit.html">
                     <spring:message code="label.edit"/></a></td>
             </tr>
             </c:forEach>
             </table>
    </div>
</div>
</body>
</html>
