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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
    <title><spring:message code="label.section.jtalks_forum"/></title>
</head>
<body>
<div class="wrap main_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        &nbsp;
        <a class="forum_top_right_link" href="${pageContext.request.contextPath}/topics/recent.rss">
            <img src="${pageContext.request.contextPath}/resources/images/RSS.png">
        </a>
        <a class="forum_top_right_link" href="${pageContext.request.contextPath}/topics/recent">
            <spring:message code="label.recent"/>
        </a><br/>
        <a class="forum_top_right_link" href="${pageContext.request.contextPath}/topics/unanswered">
            <spring:message code="label.messagesWithoutAnswers"/>
        </a>

        <h2><a class="heading" href="#"><spring:message code="label.section.jtalks_forum"/></a></h2>

        <div class="forum_misc_info">
            <spring:message code="label.section.prog_forum"/>
        </div>
        <br/>
        <c:forEach var="section" items="${sectionList}">
            <div class="forum_header_table">
                <div class="forum_header">
                    <h3><a class="forum_header_link"
                           href="${pageContext.request.contextPath}/sections/${section.id}">
                        <c:out value="${section.name}"/></a></h3>
                    <span class="forum_header_themes"><spring:message code="label.section.header.topics"/></span>
                    <span class="forum_header_messages"><spring:message code="label.section.header.messages"/></span>
                    <span class="forum_header_last_message"><spring:message
                            code="label.section.header.lastMessage"/></span>
                </div>
            </div>

            <ul class="forum_table">
                <c:forEach var="branch" items="${section.branches}" varStatus="i">
                    <li class="forum_row">
                        <div class="forum_icon">
                            <img class="icon" src="${pageContext.request.contextPath}/resources/images/closed_cup.png"
                                 alt=""
                                 title="<spring:message code="label.section.close_forum"/>"/>
                        </div>
                        <div class="forum_info">
                            <h4><a class="forum_link"
                                   href="${pageContext.request.contextPath}/branches/${branch.id}">
                                <c:out value="${branch.name}"/></a></h4>

                            <p>
                                <c:out value="${branch.description}"/>
                                <br/>
                                <spring:message code="label.section.moderators"/> <a class="moderator" href="#">Vurn</a>
                            </p>
                        </div>
                        <div class="forum_themes">
                            <c:out value="${branch.topicCount}"/>
                        </div>
                        <div class="forum_messages">
                            <c:out value="${branch.postCount}"/>
                        </div>
                        <div class="forum_last_message">
                            <c:if test="${branch.topicCount>0}">
                                <span><jtalks:format value="${branch.lastUpdatedTopic.lastPost.creationDate}"/></span>
                                <br/>
                                <a href="${pageContext.request.contextPath}/users/${branch.lastUpdatedTopic.lastPost.userCreated.encodedUsername}">
                                        ${branch.lastUpdatedTopic.lastPost.userCreated.username}</a>
                                <a href="${pageContext.request.contextPath}/posts/${branch.lastUpdatedTopic.lastPost.id}">
                                    <img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                         alt="<spring:message code="label.section.header.lastMessage"/>"/>
                                </a>
                            </c:if>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:forEach>
    </div>
    <div class="users_information">
        <div class="forum_header_table">
            <div class="forum_header">
                <h3><span class="users_information_link"><spring:message code="label.onlineUsersInfo"/> </span></h3>
                <span class="empty_cell"></span>
            </div>
        </div>
        <div class="forum_table">
            <div class="forum_row">
                <div class="forum_info">
                    <spring:message code="label.onlineUsersInfo.messagesCount"/> <c:out value="${messagesCount}"/>
                    <br/>
                    <spring:message code="label.onlineUsersInfo.registeredUsers.count"/> <c:out
                        value="${registeredUsersCount}"/>
                </div>
                <div class="empty_cell"></div>
            </div>
            <div class="forum_row">
                <div class="forum_info">
                    <spring:message code="label.onlineUsersInfo.visitors"/> <c:out value="${visitors}"/>,
                    <spring:message code="label.onlineUsersInfo.visitors.registered"/> <c:out
                        value="${visitorsRegistered}"/>,
                    <spring:message code="label.onlineUsersInfo.visitors.guests"/> <c:out value="${visitorsGuests}"/>
                    <br/>
                    <c:if test="${!(empty usersRegistered)}">
                        <spring:message code="label.onlineUsersInfo.registeredUsers"/>
                        <ul class="users_list">
                            <c:forEach items="${usersRegistered}" var="user">
                                <c:choose>
                                    <c:when test="${user.role=='ROLE_ADMIN'}">
                                        <li><a href="${pageContext.request.contextPath}/users/${user.encodedUsername}"
                                               class="admin">${user.username}></a>&nbsp;&nbsp;
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="${pageContext.request.contextPath}/users/${user.encodedUsername}"
                                               class="user">
                                            <c:out value="${user.username}"/></a>&nbsp;&nbsp;</li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
                <div class="empty_cell"></div>
            </div>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>