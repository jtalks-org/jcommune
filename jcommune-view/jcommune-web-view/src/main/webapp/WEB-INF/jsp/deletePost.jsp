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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Delete</title>
</head>
<body>
<div>

    <spring:message code="label.deletePostConfirmation"/>
    <form:form action='${pageContext.request.contextPath}/posts/${postId}?topicId=${topicId}' method="DELETE">
        <input type="submit" value="<spring:message code="label.yes"/>"/>
    </form:form>
    <form:form action='${pageContext.request.contextPath}/topics/${topicId}' method="GET">
        <input type="submit" value="<spring:message code="label.cancel"/>"/>
    </form:form>
</div>
</body>
</html>