<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<form:form action="/jcommune/createNewTopic.html"  method="POST">
    <table border="2" width="100%">
        <tr>
            <td width="30%"><spring:message code="label.topic"/> <input type="text" name="topic" /></td>
        </tr>
        <tr>
            <td width="30%"><spring:message code="label.author"/> <input type="text" name="author"/></td>
        </tr>
        <tr>
            <td height="200"><spring:message code="label.text"/> <textarea name="bodytext" cols="40" rows="10"></textarea></td>
        </tr>

    </table>

    <input type="submit" value="<spring:message code="label.addtopic"/>"/>
</form:form>
</body>
</html>