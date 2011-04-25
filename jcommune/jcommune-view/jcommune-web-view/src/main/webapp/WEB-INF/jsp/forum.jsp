<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%--
  Created by IntelliJ IDEA.
  User: Christoph
  Date: 17.04.2011
  Time: 11:41:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title></title></head>
<body>
<form:form method="POST">
<table border="2" width="100%">
    <tr>
        <td width="80%"><spring:message code="label.topic"/></td>
        <td width="10%"><spring:message code="label.author"/></td>
        <td width="10%"><spring:message code="label.date"/></td>
    </tr>
    
    <c:forEach var="topics" items="${topicsList}">
      <tr>
        <td><c:out value="${topics.topicName}"/></td>
        <td><c:out value="${topics.userCreated}"/> </td>  
      </tr>
    </c:forEach>

</table>

    <input type="submit" value="<spring:message code="label.addtopic"/>"/>
</form:form>
</body>
</html>