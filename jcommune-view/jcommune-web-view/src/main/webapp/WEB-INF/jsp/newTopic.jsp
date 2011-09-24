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
                <a  href="${pageContext.request.contextPath}/branch/${branchId}.html" class="coolbutton">
                    <input type="button" value="<spring:message code='label.back' />" ></a>
            </div>
        </jtalks:form>
    </div>
</body>
</html>