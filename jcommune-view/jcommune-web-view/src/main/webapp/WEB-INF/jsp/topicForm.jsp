<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<div id="stylized">
<form:form name="editForm" modelAttribute="topicDto" method="POST"
           action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/save.html" 
           onsubmit="if (this.getAttribute('submitted')) return false; this.setAttribute('submitted','true');">
           <!--Block multiple form submissions-->
    <form:hidden path="id"/>
    <table>
        <tr>
            <td>
                <form:label path="topicName"><spring:message code="label.topic"/></form:label>
                <form:input path="topicName"/>
                <form:errors path="topicName"/>
            </td>
        </tr>
        <tr>
            <td>
                <form:label path="bodyText"><spring:message code="label.text"/></form:label>
                <form:textarea path="bodyText"/>
                <form:errors path="bodyText"/>
            </td>
        </tr>
    </table>
    <table>
        <tr>
            <td><input type="submit" value="<spring:message code='label.back'/>"
                            onclick="document.editForm.action=
                            '${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}.html'"/>
            </td>
            <td><input type="submit" value="<spring:message code='label.save'/>"
                            onclick="document.editForm.action=
                            '${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/save.html'"/>
            </td>
        </tr>
    </table>
</form:form>
</div> <!-- stylized -->
</body>
</html>