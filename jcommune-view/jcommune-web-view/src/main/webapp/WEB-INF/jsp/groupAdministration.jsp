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

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title>
        <c:out value="${cmpTitlePrefix}"/>
        <spring:message code="label.administration.userGroups"/>
    </title>
</head>
<body>

<div class="container">
    <h2><spring:message code="label.administration.userGroups"/> </h2>
    <table class="table table-bordered grid-table display" id="userGroups">
        <thead>
        <tr>
            <th><spring:message code="label.group.name"/></th>
            <th><spring:message code="label.group.numberOfMembers"/></th>
        </tr>
        </thead>
        <c:forEach var="group" items="${groups}">
            <tr class="grid-row">
                <td><c:out value="${group.name}"/></td>
                <td><c:out value="${group.numberOfUsers}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
