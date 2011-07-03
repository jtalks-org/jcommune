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
  <div align="left">
    <jsp:include page="pmNavigationMenu.jsp"/>
    <div>
      <h1><spring:message code="label.new_pm"/></h1>
      <form:form action="${pageContext.request.contextPath}/pm/new.html" modelAttribute="privateMessageDto" method="POST"
                 onsubmit="this.getAttribute('submitted')" name="editForm"> <!--Block multiple form submissions-->
      <form:hidden path="id"/>
        <table cellspacing=0 cellpadding=5 border="1">
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
            <td height="200" valign="top">
              <form:label path="body"><spring:message code="label.body"/></form:label>
              <form:textarea path="body"/>
              <form:errors path="body"/>
            </td>
          </tr>
        </table>
        <div style="float: left; width: 150px;">&nbsp;</div>
        <input type="submit" value="<spring:message code="label.send"/>"
               onclick="document.editForm.action='${pageContext.request.contextPath}/pm/new.html'"/>
        <input type="submit" value="<spring:message code="label.save"/>"
               onclick="document.editForm.action='${pageContext.request.contextPath}/pm/save.html'"/>
      </form:form>
    </div>
  </div>
</body>
</html>

