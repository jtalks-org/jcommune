<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
    <div id="answer">
        <jtalks:form action="${pageContext.request.contextPath}/branch/${branchId}/topic.html" modelAttribute="topicDto" method="POST">
             <div>
                <h2><spring:message code="h.new_topic" /></h2>

                <form:label path="topicName"><spring:message code="label.topic"/></form:label>
                <form:input path="topicName" size="50"/>
                <form:errors path="topicName"/>
                <br />
                <form:label path="bodyText"><spring:message code="label.text"/></form:label>
                <form:textarea path="bodyText"/>
                <form:errors path="bodyText" cols="30" rows="10"/>

                <div class="clear"></div>
                <button type="submit" class="coolbutton"><spring:message code="label.addtopic"/></button>
            </div>
        </jtalks:form>
    </div>
</body>
</html>