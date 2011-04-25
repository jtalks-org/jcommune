<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<form action="/jcommune/createNewTopic.html" method="POST">
    <table border="2" width="100%">
        <tr>
            <td width="30%">Topic: <input type="text" name="topic" /></td>
        </tr>
        <tr>
            <td width="30%">Author: <input type="text" name="author"/></td>
        </tr>
        <tr>
            <td height="200">Text: <textarea name="bodytext"  cols="40" rows="10"></textarea></td>
        </tr>

    </table>

    <input type="submit" value="Submit new topic"/>
</form>
</body>
</html>