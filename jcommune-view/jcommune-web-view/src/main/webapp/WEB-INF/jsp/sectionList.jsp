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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<head>
    <title><spring:message code="label.section.jtalks_forum"/></title>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/screen.css"/>
</head>
<body>
<div class="wrap main_page">
    <jsp:include page="../template/topLine.jsp"/>
    <h1>JTalks</h1>
    <!-- Начало всех форумов -->
    <div class="all_forums">
        <a class="forum_top_right_link" href="${pageContext.request.contextPath}/topics/recent"><spring:message
                code="label.recent"/></a> <br/>
        <a class="forum_top_right_link" href="#"><spring:message code="label.messagesWithoutAnswers"/></a>

        <h2><a class="heading" href="#"><spring:message code="label.section.jtalks_forum"/></a></h2>
        <br/>

        <div class="forum_misc_info">
            <spring:message code="label.section.prog_forum"/>
        </div>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

        <c:forEach var="section" items="${sectionList}">
            <!-- Начало группы форумов -->
            <div class="forum_header_table"> <!-- Шапка группы форумов -->
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

            <ul class="forum_table"> <!-- Группа форумов -->
                <c:forEach var="branch" items="${section.branches}" varStatus="i">
                    <li class="forum_row"> <!-- Отдельный форум -->
                        <div class="forum_icon"> <!-- Иконка с кофе -->
                            <img class="icon" src="${pageContext.request.contextPath}/resources/images/closed_cup.png"
                                 alt=""
                                 title="<spring:message code="label.section.close_forum"/>"/>
                        </div>
                        <div class="forum_info"> <!-- Информация о форуме -->
                            <h4><a class="forum_link"
                                   href="${pageContext.request.contextPath}/branches/${branch.id}">
                                <c:out value="${branch.name}"/></a></h4> <!-- Ссылка на форум -->
                            <p>
                                <c:out value="${branch.description}"/>
                                <a href="#"><spring:message code="label.section.faq"/></a>
                                <br/>
                                <spring:message code="label.section.moderators"/> <a class="moderator" href="#">Vurn</a>
                            </p>
                        </div>
                        <div class="forum_themes">
                            <c:out value="${branch.topicCount}"/>
                        </div>
                        <div class="forum_messages">
                               FIX_ME!
                        </div>
                        <div class="forum_last_message">
                            <c:if test="${branch.topicCount>0}">
                                <span><jtalks:format value="${branch.lastUpdatedTopic.lastPost.creationDate}"/></span>
                                <br/>
                                <a href="${pageContext.request.contextPath}/users/${branch.lastUpdatedTopic.lastPost.userCreated.encodedUsername}">${branch.lastUpdatedTopic.lastPost.userCreated.username}</a>
                                <c:choose>
                                    <c:when test="${pageSize >= branch.lastUpdatedTopic.postCount}">
                                        <a href="${pageContext.request.contextPath}/topics/${branch.lastUpdatedTopic.id}#${branch.lastUpdatedTopic.lastPost.id}"><img
                                                src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                                alt="<spring:message code="label.section.header.lastMessage"/>"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${branch.lastUpdatedTopic.postCount % pageSize > 0}">
                                            <c:set var="additionalPage" value="${1}"/>
                                        </c:if>
                                        <c:if test="${branch.lastUpdatedTopic.postCount % pageSize == 0}">
                                            <c:set var="additionalPage" value="${0}"/>
                                        </c:if>
                                        <a href="${pageContext.request.contextPath}/topics/${branch.lastUpdatedTopic.id}?page=<fmt:formatNumber value="${(branch.lastUpdatedTopic.postCount - (branch.lastUpdatedTopic.postCount mod pageSize)) div pageSize + additionalPage}"/>#${branch.lastUpdatedTopic.lastPost.id}">
                                            <img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                                 alt="<spring:message code="label.section.header.lastMessage"/>"/>
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:forEach>
        <!-- Конец группы форумов -->
    </div>
    <!-- Конец всех форумов -->
    <div class="users_information">    <!-- Информация о посетителях -->
        <div class="forum_header_table"> <!-- Шапка группы -->
            <div class="forum_header">
                <h3><a class="users_information_link" href="#"><spring:message code="label.onlineUsersInfo"/> </a></h3>
                <span class="empty_cell"></span> <!-- Необходим для правильного отображения псевдотаблицы -->
            </div>
        </div>
        <div class="forum_table"> <!-- Таблица -->
            <div class="forum_row"> <!-- Отдельный ряд -->
                <div class="forum_info"> <!-- Содержимое ряда -->
                    <spring:message code="label.onlineUsersInfo.messagesCount"/> <c:out value="${messagesCount}"/>
                    <br/>
                    <spring:message code="label.onlineUsersInfo.registeredUsers.count"/> <c:out
                            value="${registeredUsersCount}"/>
                </div>
                <div class="empty_cell"></div>
                <!-- Необходим для правильного отображения псевдотаблицы -->
            </div>
            <div class="forum_row"> <!-- Отдельный ряд -->
                <div class="forum_info"> <!-- Содержимое ряда -->
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
                                        <li><a href="${pageContext.request.contextPath}/users/${user.username}"
                                               class="admin">
                                            <c:out value="${user.username}"/></a>&nbsp;&nbsp;</li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="${pageContext.request.contextPath}/users/${user.username}"
                                               class="user">
                                            <c:out value="${user.username}"/></a>&nbsp;&nbsp;</li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                                <%--<li><a href="#" class="moderator">andreyko</a>,</li>
                              <li><a href="#" class="admin">Староверъ</a>,</li>
                              <li><a href="#" class="user">Вася</a>.</li>--%>
                        </ul>
                    </c:if>
                </div>
                <div class="empty_cell"></div>
                <!-- Необходим для правильного отображения псевдотаблицы -->
            </div>
        </div>
    </div>
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>
</body>
</html>