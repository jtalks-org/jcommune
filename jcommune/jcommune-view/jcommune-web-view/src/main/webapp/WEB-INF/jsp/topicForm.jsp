<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<div id="content">
<form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/save.html" 
           modelAttribute="topicDto" method="POST" onsubmit="this.getAttribute('submitted')" 
           name="editForm"> <!--Block multiple form submissions-->
    <form:hidden path="id"/>
    <table border="2" width="100%">
        <tr>
            <td width="30%">
                <form:label path="topicName"><spring:message code="label.topic"/></form:label>
                <form:input path="topicName"/>
                <form:errors path="topicName"/>
            </td>
        </tr>
        <tr>
            <td height="200">
                <form:label path="bodyText"><spring:message code="label.text"/></form:label>
                <form:textarea path="bodyText"/>
                <form:errors path="bodyText"/>
            </td>
        </tr>
    </table>
    <table>
        <tr>
            <td><form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}.html" 
                           method="GET">
                    <input type="submit" value="<spring:message code="label.back"/>"/>
                </form:form></td>
            <td><input type="submit" value="<spring:message code="label.save"/>"
                       onclick="document.editForm.action=
                           '${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/save.html'"/>
            </td>
        </tr>
    </table>
</form:form>
</div> <!-- content -->
</body>
</html>