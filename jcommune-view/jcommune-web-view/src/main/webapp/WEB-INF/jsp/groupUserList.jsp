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
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title>
        <c:out value="${cmpTitlePrefix}"/>
        <c:out value="${group.name}"/>
    </title>
</head>
<body>
<div class="container">
    <div class="inline-block">
        <h2>
            <spring:message code="label.administration.groupUserList"/>&nbsp;
            <c:out value="${group.name}"/>
        </h2>
    </div>
    <table class="table table-bordered grid-table display">
        <thead>
        <tr>
            <th><spring:message code="label.group.user.name"/></th>
            <th><spring:message code="label.group.user.email"/></th>
        </tr>
        </thead>
        <c:forEach var="user" items="${groupUsersPage.content}">
            <tr id='${user.id}' class="grid-row user-row-class">
                <td id="user-name">
                    <c:out value="${user.username}"/>
                </td>
                <td>
                    <c:out value="${user.email}"/>
                </td>
            </tr>
        </c:forEach>
    </table>
    <div>
        <div class="pagination pull-right forum-pagination">
            <ul>
                <jtalks:pagination uri="${group.id}" page="${groupUsersPage}"/>
            </ul>
        </div>
    </div>
</div>
</body>
