<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<html>
  <table>
    <tr><td>
        <a href="${pageContext.request.contextPath}/pm/inbox.html">
            <spring:message code="label.inbox"/></a>
      </td></tr>
    <tr><td>
        <a href="${pageContext.request.contextPath}/pm/outbox.html">
            <spring:message code="label.outbox"/></a>
      </td></tr>
    <tr><td>
        <a href="${pageContext.request.contextPath}/pm/new.html">
            <spring:message code="label.new_pm"/></a>
      </td></tr>
  </table>
</html>