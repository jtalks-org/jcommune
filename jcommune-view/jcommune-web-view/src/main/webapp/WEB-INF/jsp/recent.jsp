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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Forum</title>
</head>
<body>
<h1>JTalks</h1>

<div class="wrap branch_page">
    <jsp:include page="../template/topLine.jsp"/>
    <!-- Начало всех форумов -->
    <div class="all_forums">
        <h2><a class="heading" href="#"><c:out value="${recent}"/></a></h2>

        <jtalks:display uri="" pagination="${pag}" list="${topics}">
        <nobr>
            <span class="nav_bottom">
                <c:if test="${pag.maxPages>1}">
                    <spring:message code="label.onPage"/>
                </c:if>
            </jtalks:display>
            </span>
        </nobr>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

        <!-- Начало группы форумов -->
        <div class="forum_header_table"> <!-- Шапка бранча -->
            <div class="forum_header">
                <span class="forum_header_icon"></span>
                <span class="forum_header_topics"><spring:message code="label.branch.header.topics"/></span>
                <span class="forum_header_answers"><spring:message code="label.branch.header.answers"/></span>
                <span class="forum_header_author"><spring:message code="label.branch.header.author"/></span>
                <span class="forum_header_clicks"><spring:message code="label.branch.header.views"/></span>
                <span class="forum_header_last_message"><spring:message code="label.branch.header.lastMessage"/></span>
            </div>
        </div>

        <ul class="forum_table"> <!-- Список топиков -->
            <jtalks:display uri="" pagination="${pag}" numberLink="3" list="${topics}">
            <c:forEach var="topic" items="${list}">
                <li class="forum_row"> <!-- Топик -->
                    <div class="forum_icon"> <!-- Иконка с кофе -->
                        <img class="icon" src="${pageContext.request.contextPath}/resources/images/closed_cup.png"
                             alt=""
                             title="Форум закрыт"/>
                    </div>
                    <c:choose>
                        <c:when test="${topic.announcement=='true'}">
                            <div class="forum_info"> <!-- Ссылка на тему -->
                                <h4><span class="sticky"><spring:message code="label.marked_as_announcement"/> </span><a
                                        class="forum_link"
                                        href="${pageContext.request.contextPath}/topics/${topic.id}">
                                    <c:out value="${topic.title}"/></a></h4>
                            </div>
                        </c:when>
                        <c:when test="${topic.sticked=='true'}">
                            <div class="forum_info"> <!-- Ссылка на тему -->
                                <h4><span class="sticky"><spring:message code="label.marked_as_sticked"/> </span><a
                                        class="forum_link"
                                        href="${pageContext.request.contextPath}/topics/${topic.id}">
                                    <c:out value="${topic.title}"/></a></h4>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="forum_info"> <!-- Ссылка на тему -->
                                <h4><a class="forum_link"
                                       href="${pageContext.request.contextPath}/topics/${topic.id}"><c:out
                                        value="${topic.title}"/></a></h4>
                                <h5><c:out value="${topic.lastPost.shortContent}"/></h5>

                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="forum_answers">
                        <c:out value="${topic.postCount}"/>
                    </div>
                    <div class="forum_author">
                        <a href="${pageContext.request.contextPath}/users/${topic.topicStarter.encodedUsername}"
                           title="<spring:message code="label.topic.header.author"/>"><c:out value="${topic.topicStarter.username}"/></a>
                    </div>
                    <div class="forum_clicks">
                        <c:out value="${topic.views}"/>
                    </div>
                    <div class="forum_last_message">
                        <a href="${pageContext.request.contextPath}/topics/${topic.id}">
                            <jtalks:format value="${topic.lastPost.creationDate}"/></a>
                        <br/>
                        <a class="last_message_user"
                           href="${pageContext.request.contextPath}/users/${topic.lastPost.userCreated.encodedUsername}">
                            <c:out value="${topic.lastPost.userCreated.username}"/></a>
                        <a href="#"><img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                         alt="<spring:message code="label.section.header.lastMessage"/>"/></a>
                    </div>
                </li>
            </c:forEach>
        </ul>

        <!-- Конец группы форумов -->
        <nobr>
            <span class="nav_bottom">
                <c:if test="${pag.maxPages>1}">
                    <spring:message code="label.onPage"/>
                </c:if>
            </jtalks:display>
            </span>
        </nobr>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>


        <div class="forum_misc_info">
            <spring:message code="label.page"/> <c:out value="${page}"/> <spring:message code="label.of"/> <c:out
                value="${maxPages}"/>

        </div>
    </div>
    <!-- Конец всех форумов -->
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>
</body>
</html>