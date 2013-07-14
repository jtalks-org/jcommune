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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<head>
    <meta name="description" content="<c:out value="${topic.title}"/>">
    <%-- Add plugins --%>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/utils.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/permissionService.js"
            type="text/javascript"></script>
</head>
<body>
    <c:set var="authenticated" value="${false}"/>
    <div class="container">
        <%-- List of plugins. --%>
        <table id="plugins-table" class="table table-row table-bordered">
            <c:choose>
                <c:when test="${!(empty section.branches)}">
                    <thead>
                        <tr>
                            <th id="plugin-name">
                                <spring:message code="label.section.header.branches"/>
                            </th>
                            <th id="plugin-actions">
                                <spring:message code="label.branch.header.topics_posts"/>
                            </th>
                            <th id="plugin-is-enabled">
                                <spring:message code="label.branch.header.lastMessage"/>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="plugin" items="${plugins}" varStatus="i">
                            <%-- Plugin --%>
                            <tr>
                                <td>
                                    <c:out value="${plugin.name}"/>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/plugins/${plugin.name}"
                                       title="<spring:message code='label.tips.view_profile'/>">
                                    </a>
                                </td>
                                <td>
                                    <form:checkbox path="" value="${plugin.state == 'ENABLED'}"
                                                   class="form-check-radio-box script-has-tooltip" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </c:when>
            </c:choose>
        </table>
    </div>
</body>
