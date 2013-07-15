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
    <script src="${pageContext.request.contextPath}/resources/javascript/app/utils.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/permissionService.js"
            type="text/javascript"></script>
</head>
<body>
<div class="container">
    <%-- List of plugins. --%>
    <table id="plugins-table" class="table table-row table-bordered">
        <c:choose>
            <c:when test="${!(empty pluginConfiguration.properties)}">
                <thead>
                <tr>
                    <th id="property-name">
                        <spring:message code="label.plugins.plugin.property.name"/>
                    </th>
                    <th id="property-type">
                        <spring:message code="label.plugins.plugin.property.type"/>
                    </th>
                    <th id="property-value">
                        <spring:message code="label.plugins.plugin.property.value"/>
                    </th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="property" items="${pluginConfiguration.properties}" varStatus="i">
                    <%-- Property --%>
                    <tr>
                        <td>
                            <c:out value="${property.name}"/>
                        </td>
                        <td>
                            <c:out value="${property.type}"/>
                        </td>
                        <td>
                            <c:out value="${property.value}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </c:when>
        </c:choose>
    </table>
</div>
</body>
