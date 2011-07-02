<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
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
          <table>
            <tr>
              <td><h3><c:out value="${pm.title}"/></h3></td>
              <td><h3><joda:format value="${pm.creationDate}"
                               locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                               pattern="dd MMM yyyy HH:mm"/>
                 </h3></td>
            </tr>
          </table>
          &nbsp;
          <table>
            <tr>
              <td><spring:message code="label.sender"/></td>
              <td><c:out value="${pm.userFrom.username}"/></td>
            </tr><tr>
            <tr>
              <td><spring:message code="label.recipient"/></td>
              <td><c:out value="${pm.userTo.username}"/></td>
            </tr>
          </table>
          &nbsp;
          <table border="1" width="100%">
            <tr>
              <td><c:out value="${pm.body}"/></td>
            </tr>
          </table>
    </td>
  </tr>
</table>
</body>
</html>
