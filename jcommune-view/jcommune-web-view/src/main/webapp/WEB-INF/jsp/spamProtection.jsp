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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>
        <c:out value="${cmpTitlePrefix}"/>
        <spring:message code="label.spamProtection.title"/>
    </title>
</head>
<body>
<div class="container">
    <div id="status-message" class="alert plugin-popup hide alert-success"></div>
    <div class="inline-block">
        <h2><spring:message code="label.spamProtection.settings"/></h2>
    </div>
    <div class="inline-block pull-right">
        <input id="addSpamRuleBtn" type="submit" class="btn btn-primary"
               value="<spring:message code="label.spamRule.add"/>"/>
    </div>
    <table id="spam-rules-table" class="table table-bordered grid-table display">
        <thead>
        <tr>
            <th><spring:message code="label.spamProtection.column.rules"/></th>
            <th><spring:message code="label.spamProtection.column.description"/></th>
            <th><spring:message code="label.spamProtection.column.enabled"/></th>
        </tr>
        </thead>
        <c:forEach var="rule" items="${rules}">
            <tr id="spam-rule-${rule.id}" data-rule-id="${rule.id}" class="grid-row highlighted-row">
                <td id="regex-${rule.id}"><c:out value="${rule.regex}"/></td>
                <td id="description-${rule.id}"><c:out value="${rule.description}"/></td>
                <td>
                    <input type="checkbox" id="status-${rule.id}" <c:if test="${rule.enabled}">checked</c:if>/>
                    <div class="inline-block pull-right management-block">
                        <span id='editSpamRuleBtn' class="icon-pencil management-element"></span><span id='deleteSpamRuleBtn' class="icon-trash management-element"></span>
                    </div>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
