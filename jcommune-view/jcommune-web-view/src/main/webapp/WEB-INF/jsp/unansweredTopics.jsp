<<%--

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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<head>
    <title><spring:message code="label.messagesWithoutAnswers"/></title>
</head>
<body>
<div class="wrap branch_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <h2><a class="heading" href="#"><spring:message code="label.messagesWithoutAnswers"/></a></h2>
        <br/>
        <jtalks:pagination uri="" pagination="${pagination}" list="${topics}">
        <nobr>
            <span class="nav_top">
                </jtalks:pagination>
            </span>
        </nobr>
        <div class="forum_header_table">
            <div class="forum_header">
                <span class="forum_header_icon"></span>
                <span class="forum_header_topics"><spring:message code="label.branch.header.topics"/></span>
                <span class="forum_header_topics"><spring:message code="label.branch.header.branches"/></span>
                <span class="forum_header_author"><spring:message code="label.branch.header.author"/></span>
                <span class="forum_header_clicks"><spring:message code="label.branch.header.views"/></span>
                <span class="forum_header_last_message"><spring:message code="label.branch.header.lastMessage"/></span>
            </div>
        </div>

        <c:choose>
            <c:when test="${!(empty topics)}">
                <ul class="forum_table">
                    <c:forEach var="map" items="${list}">
                        <li class="forum_row">
                            <div class="forum_icon">
                                <img class="icon" src="${pageContext.request.contextPath}/resources/images/closed_cup.png"/>
                            </div>
                            <c:choose>
                                <c:when test="${map.announcement=='true'}">
                                    <div class="forum_info">
                                        <h4>
                                            <span class="sticky">
                                                <spring:message code="label.marked_as_announcement"/>
                                            </span>
                                            <a class="forum_link break_word"
                                               href="${pageContext.request.contextPath}/topics/${map.id}">
                                                <c:out value="${map.title}"/>
                                            </a>
                                        </h4>
                                    </div>
                                </c:when>
                                <c:when test="${map.sticked=='true'}">
                                    <div class="forum_info">
                                        <h4><span class="sticky">
                                            <spring:message code="label.marked_as_sticked"/> </span><a
                                                class="forum_link break_word"
                                                href="${pageContext.request.contextPath}/topics/${map.id}">
                                            <c:out value="${map.title}"/></a></h4>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="forum_info">
                                        <h4><a class="forum_link break_word"
                                               href="${pageContext.request.contextPath}/topics/${map.id}"><c:out
                                                value="${map.title}"/></a></h4>
                                        <br>
                                        <span class="truncated"><jtalks:bb2html bbCode="${map.lastPost.postContent}"/></span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                             <div class="forum_branches">
                                <h4>
                                    <a class="forum_link break_word"
                                       href="${pageContext.request.contextPath}/branches/${topic.branch.id}">
                                        <c:out value="${topic.branch.name}"/>
                                    </a>
                                </h4>
                            </div>
                            <div class="forum_author">
                                <a href="${pageContext.request.contextPath}/users/${map.topicStarter.encodedUsername}"
                                   title="<spring:message code="label.topic.header.author"/>"><c:out
                                        value="${map.topicStarter.username}"/></a>
                            </div>
                            <div class="forum_clicks">
                                <c:out value="${map.views}"/>
                            </div>
                            <div class="forum_last_message">
                                <a href="${pageContext.request.contextPath}/topics/${map.id}">
                                    <jtalks:format value="${map.lastPost.creationDate}"/></a>
                                <br/>
                                <a class="last_message_user"
                                   href="${pageContext.request.contextPath}/users/${map.lastPost.userCreated.encodedUsername}">
                                    <c:out value="${map.lastPost.userCreated.username}"/></a>
                                <a href="${pageContext.request.contextPath}/posts/${map.lastPost.id}">
                                    <img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                         alt="<spring:message code="label.section.header.lastMessage"/>"/>
                                </a>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
                <div class="forum_info_bottom">
                    <div>
                        <div>

                        </div>
                        <div>
                            <span class="nav_bottom">
                                <jtalks:pagination uri="" pagination="${pagination}" list="${topics}"/>
                            </span>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <ul class="forum_table">
                    <li class="forum_row empty_container">
                        <div>
                            <span class="empty">
                                <spring:message code="label.messagesWithoutAnswers.empty"/>
                            </span>
                        </div>
                    </li>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>