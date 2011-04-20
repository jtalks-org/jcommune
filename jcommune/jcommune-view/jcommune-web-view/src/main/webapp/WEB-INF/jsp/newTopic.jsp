<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%--
  Created by IntelliJ IDEA.
  User: Christoph
  Date: 17.04.2011
  Time: 11:46:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title></head>
<body>
<form:form method="POST" modelAttribute="">
    <table border="2" width="100%">
        <tr>
            <td width="80%"><spring:message code="label.topic"/></td>
            <td width="10%"><spring:message code="label.author"/></td>
            <td width="10%"><spring:message code="label.date"/></td>
        </tr>
    </table>

     <input type="submit" value="<spring:message code="label.addtopic"/>"/>
</form:form>
</body>
</html>