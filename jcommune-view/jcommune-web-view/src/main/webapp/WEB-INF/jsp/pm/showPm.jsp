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
    <script language="javascript"
      src="${pageContext.request.contextPath}/resources/javascript/custom/privateMessages.js"></script>
    <title><spring:message code="label.pm_title"/></title>
</head>
<body>
<div class="wrap pm_page">
    <jsp:include page="../../template/topLine.jsp"/>
    <jsp:include page="../../template/logo.jsp"/>

    <div class="all_forums">
        <jsp:include page="../../template/pmNavigationMenu.jsp"/>
        <div class="pm_read">
            <div class="pm_header">
                <div class="pm_left">
                    <div>
                        <span><spring:message code="label.date"/>:</span>
                        <jtalks:format value="${pm.creationDate}"/>
                    </div>
                    <div>
                        <span><spring:message code="label.sender"/>:</span>
                        <a href="${pageContext.request.contextPath}/users/${pm.userFrom.encodedUsername}">
                         <c:out value="${pm.userFrom.username}"/>
                        </a>
                    </div>
                    <div>
                        <span><spring:message code="label.pm.recipient"/>:</span>
                        <a href="${pageContext.request.contextPath}/users/${pm.userTo.encodedUsername}">
                         <c:out value="${pm.userTo.username}"/>
                        </a>
                    </div>
                </div>
                <div class="pm_right">
                    <c:out value="${pm.title}"/>
                </div>
            </div>
            <div class="pm_body">
                <div class="pm_left">
                    <c:if test="${pm.replyAllowed && (pm.userTo eq user)}">
                        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <form:form action="${pageContext.request.contextPath}/reply/${pm.id}" method="GET">
                            <input class="button" type="submit" value="<spring:message code="label.reply"/>"/>
                        </form:form>
                        <form:form action="${pageContext.request.contextPath}/quote/${pm.id}" method="GET">
                            <input class="button" type="submit" value="<spring:message code="label.quote"/>"/>
                        </form:form>
                    </sec:authorize>
                    </c:if>
                    <div class="del">
                      <a id="deleteOnePM" class="button delete" href="${pageContext.request.contextPath}/pm"
                        rel="<spring:message code="label.deletePMConfirmation"/>">
                        <spring:message code="label.delete"/>
                      </a>
                      <input id="PMId" hidden="true" value="${pm.id}"/>
                      <form:form id="deleteForm" method="DELETE"/>
                    </div>
                </div>
                <div class="pm_right">
                    <jtalks:bb2html bbCode="${pm.body}"/>
                    <c:if test="${pm.userFrom.signature!=null}">
                    <div class="signature">
                        -------------------------
                        <br/>
                        <span><c:out value="${pm.userFrom.signature}"/></span>
                    </div>
                </c:if>
                </div>

            </div>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
