<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
  <head>
    <title><spring:message code="label.new_pm"/></title>
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
          <h1><spring:message code="label.new_pm"/></h1>
          
        </td>
      </tr>
    </table>
    
  </body>
</html>

