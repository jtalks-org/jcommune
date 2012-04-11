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
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/json2.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/contacts.js"
            type="text/javascript"></script>
</head>
<body>
<div class="wrap userdetails_page">
    <sec:authentication property="principal.username" var="auth" scope="request"/>
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

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
                    <span class="break_word"><c:out value="${user.username}"/></span>
                </li>
                <li class="forum_row">
                    <label><spring:message code="label.firstname"/></label>
                    <span class="break_word"><c:out value="${user.firstName}"/></span>
                </li>
                <li class="forum_row">
                    <label><spring:message code="label.lastname"/></label>
                    <span class="break_word"><c:out value="${user.lastName}"/></span>
                </li>
                <c:if test="${user.signature != null}">
                    <li class="forum_row">
                        <label><spring:message code="label.signature"/></label>
                        <span class="signature"><c:out value="${user.signature}"/></span>
                    </li>
                </c:if>
                <c:choose>
                    <%--Do not show my email to other users--%>
                    <c:when test="${user.username == auth}">
                        <li class="forum_row">
                            <label><spring:message code="label.email"/></label>
                            <span class="break_word"><c:out value="${user.email}"/></span>
                        </li>
                        <li class="forum_row">
                            <label><spring:message code="label.language"/></label>
                            <span><spring:message code="${language.languageNameLabel}"/></span>
                        </li>
                        <li class="forum_row">
                            <label><spring:message code="label.pageSize"/></label>
                            <span><c:out value="${pageSize}"/></span>
                        </li>
                    </c:when>
                </c:choose>
                <li class="forum_row">
                    <label><spring:message code="label.location"/></label>
                    <span class="break_word"><c:out value="${user.location}"/></span>
                </li>
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
                    <span><c:out value="${user.postCount}"/></span>
                </li>
            </ul>

            <label><spring:message code="label.contacts.header"/></label>

            <div id="contacts">
                <c:forEach var="contact" items="${user.userContacts}">
                    <div class="contact">
                        <label><img src="${pageContext.request.contextPath}${contact.type.icon}" alt=""><c:out
                                value="${contact.type.typeName}"/></label>
                        <span>${contact.displayValue}</span>
                        <c:if test="${user.username == auth}">
                            <input type="hidden" value="${contact.id}"/>
                            <a class="button" id="${contact.id}" href="#">
                                <spring:message code="label.contacts.delete"/>
                            </a>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${user.username == auth}">
                <a class="button" id="add_contact" href="#">
                    <spring:message code="label.contacts.addMore"/>
                </a>
            </c:if>

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
                <c:if test="${user.username != auth}">
                    <a class="button"
                       href="${pageContext.request.contextPath}/pm/new/${user.encodedUsername}">
                        <spring:message code="label.pm.send"/>
                    </a>
                </c:if>
            </div>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
