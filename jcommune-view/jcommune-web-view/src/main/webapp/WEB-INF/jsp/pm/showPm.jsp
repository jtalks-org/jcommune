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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<head>
    <title><spring:message code="label.pm_title"/></title>
</head>
<body>
<div class="wrap pm_page">
    <jsp:include page="../../template/topLine.jsp"/>
    <jsp:include page="../../template/logo.jsp"/>

    <div class="all_forums">
        <jsp:include page="../../template/pmNavigationMenu.jsp"/>
        <div class="forum_misc_info">
        </div>
        <div>
            <div style="float: left">
                <h3><c:out value="${pm.title}"/></h3>
            </div>
            <div style="float: right">
                <h3><jtalks:format value="${pm.creationDate}"/></h3>
            </div>
            <div style="clear:right;"></div>
            <table cellspacing=0 cellpadding=5 border="1">
                <tr>
                    <td><spring:message code="label.sender"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/users/${pm.userFrom.encodedUsername}">
                            <c:out value="${pm.userFrom.username}"/>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td><spring:message code="label.recipient"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/users/${pm.userTo.encodedUsername}">
                            <c:out value="${pm.userTo.username}"/>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td valign="top"><spring:message code="label.body"/></td>
                    <td><c:out value="${pm.body}"/></td>
                </tr>
            </table>

            <table>
                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                    <tr>
                        <td>
                            <form:form action="${pageContext.request.contextPath}/reply/${pm.id}" method="GET">
                                <input type="submit" value="<spring:message code="label.reply"/>"/>
                            </form:form>
                        </td>
                        <td>
                            <form:form action="${pageContext.request.contextPath}/quote/${pm.id}" method="GET">
                                <input type="submit" value="<spring:message code="label.quote"/>"/>
                            </form:form>
                        </td>
                    </tr>
                </sec:authorize>
            </table>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
