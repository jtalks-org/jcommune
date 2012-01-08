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
    <title><spring:message code="label.inbox"/></title>
</head>
<body>
<div class="wrap pm_page">
    <jsp:include page="../../template/topLine.jsp"/>
    <h1><a href="${pageContext.request.contextPath}">
        <img src="${pageContext.request.contextPath}/resources/images/jtalks.png"/>
    </a></h1>

    <div class="all_forums">

        <h2><a class="heading" href="#"><spring:message code="label.inbox"/></a></h2>
        <jsp:include page="../../template/pmNavigationMenu.jsp"/>

        <div class="forum_header_table" style="width: 100%">
            <div class="forum_header">
                <div class="forum_header_answer" style="width: 33%">
                    <spring:message code="label.sender"/>
                </div>
                <div class="forum_header_answer" style="width: 33%">
                    <spring:message code="label.title"/>
                </div>
                <div class="forum_header_answer" style="width: 33%">
                    <spring:message code="label.sending_date"/>
                </div>
            </div>
        </div>
        <ul class="forum_table">
            <c:forEach var="pm" items="${pmList}">
                <c:choose>
                    <c:when test="${pm.read}">
                        <li class="forum_row">
                    </c:when>
                    <c:otherwise>
                        <li class="forum_row" style="background: #b0c4de">
                    </c:otherwise>
                </c:choose>
                <div class="forum_answer_left">
                    <a href="${pageContext.request.contextPath}/users/${pm.userFrom.username}">
                        <c:out value="${pm.userFrom.username}"/>
                    </a>
                </div>
                <div class="forum_answer_left">
                    <a href="${pageContext.request.contextPath}/inbox/${pm.id}">
                        <c:out value="${pm.title}"/></a>
                </div>
                <div class="forum_answer_left">
                    <jtalks:format value="${pm.creationDate}"/>
                </div>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
