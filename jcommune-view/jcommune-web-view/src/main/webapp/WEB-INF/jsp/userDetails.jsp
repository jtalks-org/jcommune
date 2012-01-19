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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<head>
    <title><spring:message code="label.user"/> - ${user.username}</title>
</head>
<body>
<div class="wrap userdetails_page">
    <sec:authentication property="principal.username" var="auth" scope="request"/>
    <jsp:include page="../template/topLine.jsp"/>
    <h1><a href="${pageContext.request.contextPath}">
        <img src="${pageContext.request.contextPath}/resources/images/jtalks.png"/>
    </a></h1>

    <div class="all_forums">
        <div id="userdetails">
            <div class="forum_header_table">
                <div class="forum_header">
                    <span class="forum_header_generic"><spring:message code="label.profile"/></span>
                    <span class="empty_cell"></span>
                </div>
            </div>
            <ul class="forum_table" id="stylized">
                <li class="forum_row">
                    <label><spring:message code="label.username"/></label>
                    <span><c:out value="${user.username}"/></span>
                </li>
                <li class="forum_row">
                    <label><spring:message code="label.firstname"/></label>
                    <span><c:out value="${user.firstName}"/></span>
                </li>
                <li class="forum_row">
                    <label><spring:message code="label.lastname"/></label>
                    <span><c:out value="${user.lastName}"/></span>
                </li>
                <c:if test="${user.signature != null}">
                    <li class="forum_row">
                        <label><spring:message code="label.signature"/></label>
                        <span class="signature"><c:out value="${user.signature}"/></span>
                    </li>
                </c:if>
                <c:choose>
                    <%--Do not show mu email to other users--%>
                    <c:when test="${user.username == auth}">
                        <li class="forum_row">
                            <label><spring:message code="label.email"/></label>
                            <span><c:out value="${user.email}"/></span>
                        </li>
                        <li class="forum_row">
                            <label><spring:message code="label.language"/></label>
                            <span><spring:message code="${language.languageNameLabel}"/></span>
                        </li>
                        <li class="forum_row">
                            <label><spring:message code="label.location"/></label>
                            <span><c:out value="${user.location}"/></span>
                        </li>
                        <li class="forum_row">
                            <label><spring:message code="label.numberOfTopicsOnPage"/></label>
                            <span><c:out value="${pageSize}"/></span>
                        </li>
                    </c:when>
                </c:choose>
                <li class="forum_row">
                    <label><spring:message code="label.lastlogin"/></label>
                    <span><jtalks:format value="${user.lastLogin}"/></span>
                </li>
                <li class="forum_row">
                    <label><spring:message code="label.registrationDate"/></label>
                    <span><jtalks:format value="${user.registrationDate}"/></span>
                </li>
                <c:if test="${user.avatar != null}">
                    <li class="forum_row">
                        <label><spring:message code="label.avatar"/></label>
                            <span class="avatar">
                                <img src="${pageContext.request.contextPath}/${user.encodedUsername}/avatar" alt=""/>
                            </span>
                    </li>
                </c:if>
                <li class="forum_row">
                    <label><spring:message code="label.postcount"/></label>
                    <span><c:out value="${user.userPostCount}"/></span>
                </li>
                <c:forEach var="contact" items="${user.contacts}">
                    <li class="forum_row">
                        <label><img src="${contact.icon}" alt=""><c:out value="${contact.type}"/></label>
                        <span><c:out value="${contact.value}"/></span>
                    </li>
                </c:forEach>
            </ul>
            <div class="form_controls">
                <c:if test="${user.username == auth}">
                    <a class="button" href="${pageContext.request.contextPath}/users/edit">
                        <spring:message code="label.edit_profile"/>
                    </a>
                </c:if>
                <a class="button"
                   href="${pageContext.request.contextPath}/users/${user.encodedUsername}/postList">
                    <spring:message code="label.postList"/>
                </a>
            </div>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
