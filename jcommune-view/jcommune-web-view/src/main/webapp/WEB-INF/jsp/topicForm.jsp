<%--

    Copyright (C) 2011  JTalks.org Team
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<div id="answer">
    <jtalks:form name="editForm" modelAttribute="topicDto" method="POST"
           action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/save.html">
        <form:hidden path="id"/>
        <div>
            <h2><spring:message code="h.edit_topic" /></h2>

            <form:label path="topicName"><spring:message code="label.topic"/></form:label>
            <form:input path="topicName" size="50"/>
            <form:errors path="topicName"/>
            <br />
            <form:label path="bodyText"><spring:message code="label.text"/></form:label>
            <form:textarea path="bodyText"/>
            <form:errors path="bodyText" cols="30" rows="10"/>

	    <sec:authorize access="hasRole('ROLE_ADMIN')">
	      <br />
	      <spring:message code="label.sticked"/>
	      <form:checkbox path="sticked" value="true"></form:checkbox>
	      <form:errors path="sticked"/>
	      <br />
	      <form:label path="topicWeight"><spring:message code="label.weight"/></form:label>
	      <form:input path="topicWeight" size="20"/>
	      <form:errors path="topicWeight"/>
	      <br />
	      <spring:message code="label.announcement"/>
	      <form:checkbox path="announcement" value="true"></form:checkbox>
	      <form:errors path="announcement"/>
	    </sec:authorize>

            <div class="clear"></div>

            <a href="${pageContext.request.contextPath}/topic/${topicId}.html" class="coolbutton" ><spring:message code='label.back'/></a>
            <button type="submit" class="coolbutton"><spring:message code='label.save'/></button>
        </div>
    </jtalks:form>
</div>
</body>
</html>