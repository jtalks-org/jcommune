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
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
    <title><spring:message code="label.section.jtalks_forum"/></title>
</head>
<body>
<div class="wrap section_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <div class="forum_header_table">
            <div class="forum_header">
                <h3 class="forum_header_link"><c:out value="${section.name}"/></h3>
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
                        <h4>
                            <a class="forum_link" href="${pageContext.request.contextPath}/branches/${branch.id}">
                                <c:out value="${branch.name}"/>
                            </a>
                        </h4>

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
                        <c:out value="${branch.postCount}"/>
                    </div>
                    <div class="forum_last_message">
                        <c:if test="${branch.topicCount>0}">
                            <span><jtalks:format value="${branch.lastUpdatedTopic.lastPost.creationDate}"/></span>
                            <br/>
                            <a href="${pageContext.request.contextPath}/users/${branch.lastUpdatedTopic.lastPost.userCreated.encodedUsername}">
                                <c:out value="${branch.lastUpdatedTopic.lastPost.userCreated.username}"/>
                            </a>
                            <a href="${pageContext.request.contextPath}/posts/${branch.lastUpdatedTopic.lastPost.id}">
                                <img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"/>
                            </a>
                        </c:if>
                    </div>
                </li>
            </c:forEach>
        </ul>
        <br />
        <c:if test="${!(empty viewList)}">
            <spring:message code="label.section.now_browsing"/>
        </c:if>
        <c:forEach var="innerUser" items="${viewList}">
            <a href="${pageContext.request.contextPath}/users/${innerUser.encodedUsername}">
                <c:out value="${innerUser.username}"/>
            </a>
        </c:forEach>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>

