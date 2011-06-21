<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
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
          <h1><spring:message code="label.new_pm"/></h1>
          <form:form action="new.html" modelAttribute="privateMessageDto" method="POST" 
                     onsubmit="this.getAttribute('submitted')"> <!--Block multiple form submissions-->
            <table border="1" width="100%">
              <tr>
                <td width="30%">
                  <form:label path="recipient"><spring:message code="label.recipient"/></form:label>
                  <form:input path="recipient"/>
                  <form:errors path="recipient"/>          
                </td>
              </tr>
              <tr>
                <td width="30%">
                  <form:label path="title"><spring:message code="label.title"/></form:label>
                  <form:input path="title"/>
                  <form:errors path="title"/>
                </td>
              </tr>
              <tr>
                <td height="200">
                  <form:label path="body"><spring:message code="label.body"/></form:label>
                  <form:textarea path="body"/>
                  <form:errors path="body"/>
                </td>
              </tr>
            </table>
            <input type="submit" value="<spring:message code="label.send"/>"/>
          </form:form>
        </td>
      </tr>
    </table>

  </body>
</html>

