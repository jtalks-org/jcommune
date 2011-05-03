<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head></head>
  <body>
  <form:form action="/jcommune/backToAllTopics.html" method="POST">
    <table border="2" width="100%">
        <tr>
            <td width="30%"><spring:message code="label.topic"/> </td>
        </tr>
        <tr>
            <td width="30%"><spring:message code="label.author"/> </td>
        </tr>
        <tr>
            <td height="200"><spring:message code="label.text"/> </textarea></td>
        </tr>

    </table>

    <input type="submit" value="<spring:message code="label.addtopic"/>"/>
</form:form>
  </body>
</html>