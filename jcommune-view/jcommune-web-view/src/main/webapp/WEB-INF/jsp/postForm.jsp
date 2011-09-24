<%--

    Copyright (C) 2011  jtalks.org Team
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
    Also add information on how to contact you by electronic and paper mail.
    Creation date: Apr 12, 2011 / 8:05:19 PM
    The jtalks.org Project

--%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head></head>
<body>
<div id="answer">
    <jtalks:form name="editForm" modelAttribute="postDto" method="POST"
           action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/post/${postId}/save.html">
        <form:hidden path="id"/>
        <div>            
            <form:label path="bodyText"><spring:message code="label.text"/></form:label>
            <form:textarea path="bodyText"/>
            <form:errors path="bodyText" cols="30" rows="10"/>
            <br />
            <a href="${pageContext.request.contextPath}/topic/${topicId}.html" class="coolbutton" ><spring:message code='label.back'/></a>
            <button type="submit" class="coolbutton"><spring:message code='label.save'/></button>
        </div>
    </jtalks:form>
</div>
</body>
</html>