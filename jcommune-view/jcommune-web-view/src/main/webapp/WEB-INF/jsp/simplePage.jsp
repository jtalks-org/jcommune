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
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<head>
    <meta name="description" content="<c:out value="${simplePageDto.nameText}"/>">
    <title>
        <c:out value="${simplePageDto.nameText}"/>
    </title>

</head>
<body>
<div class="container">

    <div id="branch-header">
        <h3>
            <c:out value="${simplePageDto.nameText}"/>
        </h3>
    </div>

    <div>
        <div class="post">
            <table class="table table-row table-bordered table-condensed">
                <tr class="post-content-tr">
                    <td class='post-content-td'>
                        <div>
                            <jtalks:bb2html bbCode="${simplePageDto.contentText}"/>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <sec:authorize access="isAuthenticated()">
        <sec:authentication property="principal.id" var="userId"/>
        <jtalks:hasPermission targetId='${userId}' targetType='USER'
                              permission='ProfilePermission.CREATE_FORUM_FAQ'>
            <div>
                <a class="button" href="${pageContext.request.contextPath}/pages/${simplePageDto.pathName}/edit">
                    <spring:message code="label.edit"/>
                </a>
            </div>
        </jtalks:hasPermission>
    </sec:authorize>
</div>

<div class="footer_buffer">

</div>
</div>
</body>