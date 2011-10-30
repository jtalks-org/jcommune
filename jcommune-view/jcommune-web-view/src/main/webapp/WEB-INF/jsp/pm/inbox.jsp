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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.pm_title"/></title>
</head>
<body>
<jsp:include page="../../template/topLine.jsp"/>
<h1><spring:message code="label.inbox"/></h1>

<div class="all_forums">

    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

    <jsp:include page="pmNavigationMenu.jsp"/>
    <div>
        <table>
            <tr>
                <td><spring:message code="label.sender"/></td>
                <td><spring:message code="label.title"/></td>
                <td><spring:message code="label.sending_date"/></td>
            </tr>
            <c:forEach var="pm" items="${pmList}">
                <c:choose>
                    <c:when test="${pm.read}">
                        <tr>
                    </c:when>
                    <c:otherwise>
                        <tr bgcolor="#b0c4de">
                    </c:otherwise>
                </c:choose>
                <td><c:out value="${pm.userFrom.username}"/></td>
                <td><a href="${pageContext.request.contextPath}/pm/${pm.id}">
                    <c:out value="${pm.title}"/></a></td>
                <td><jtalks:format value="${pm.creationDate}"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
</html>
