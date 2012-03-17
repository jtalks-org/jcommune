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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
    <title><spring:message code="label.section.jtalks_forum"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/subscription.js"
            type="text/javascript"></script>
</head>
<body>
<div class="wrap branch_page">
    <jsp:include page="../template/logo.jsp"/>
    <jsp:include page="../template/topLine.jsp"/>
    <div class="all_forums">
        <div class="forum_info_top">
            <div>
                <div> <!-- top left -->
                    <h2 class="heading"><c:out value="${branch.name}"/></h2><br/>
                    <span class="forum_misc_info"><c:out value="${branch.description}"/></span>
                </div>
                <div> <!-- top right -->
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="forum_top_right_link"
                           href="${pageContext.request.contextPath}/branches/${branch.id}/markread">
                            <spring:message code="label.mark_all_topics"/>
                        </a>
                    </sec:authorize>
                </div>
            </div>
            <div class="info_top_lower"> <!-- bottom left -->
                <div>
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="button top_button"
                           href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}">
                            <spring:message code="label.addtopic"/>
                        </a>
                        <c:choose>
                            <c:when test="${subscribed}">
                                <a id="subscription" class="button top_button"
                                   href="${pageContext.request.contextPath}/branches/${branch.id}/unsubscribe">
                                    <spring:message code="label.unsubscribe"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a id="subscription" class="button top_button"
                                   href="${pageContext.request.contextPath}/branches/${branch.id}/subscribe">
                                    <spring:message code="label.subscribe"/>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </sec:authorize>
                    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
                </div>
                <div> <!-- bottom right -->
                    <span class="nav_top">
                        <jtalks:pagination uri="${branch.id}" pagination="${pagination}" list="${topics}"/>
                    </span>
                </div>
            </div>
        </div>
        <div class="forum_header_table">
            <div class="forum_header">
                <span class="forum_header_topics"><spring:message code="label.branch.header.topics"/></span>
                <span class="forum_header_answers"><spring:message code="label.section.header.messages"/></span>
                <span class="forum_header_author"><spring:message code="label.branch.header.author"/></span>
                <span class="forum_header_clicks"><spring:message code="label.branch.header.views"/></span>
                <span class="forum_header_last_message"><spring:message code="label.branch.header.lastMessage"/></span>
            </div>
        </div>
        <c:choose>
            <c:when test="${!(empty topics)}">
                <ul class="forum_table">
                    <c:forEach var="topic" items="${list}">
                        <li class="forum_row">
                            <div class="forum_icon">
                                <img class="icon"
                                     src="${pageContext.request.contextPath}/resources/images/closed_cup.png"
                                     alt=""
                                     title="<spring:message code="label.section.close_forum"/>"/>
                            </div>
                            <div class="forum_info">
                                <h4>
                                    <c:choose>  <%--Some topic types should have a special prefix when displayed--%>
                                        <c:when test="${topic.announcement=='true'}">
                                            <span class="sticky"><spring:message
                                                    code="label.marked_as_announcement"/> </span>
                                        </c:when>
                                        <c:when test="${topic.sticked=='true'}">
                                            <span class="sticky"><spring:message code="label.marked_as_sticked"/></span>
                                        </c:when>
                                    </c:choose>
                                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                                        <c:if test="${topic.hasUpdates}">
                                            <span style="color: red;">[NEW]</span>
                                        </c:if>
                                    </sec:authorize>
                                    <a class="forum_link" href="${pageContext.request.contextPath}/topics/${topic.id}">
                                        <span class="forum_message_cell_text"><c:out value="${topic.title}"/></span>
                                    </a>
                                </h4>
                            </div>
                            <div class="forum_answers">
                                <c:out value="${topic.postCount}"/>
                            </div>
                            <div class="forum_author">
                                <a href="${pageContext.request.contextPath}/users/${topic.topicStarter.encodedUsername}"
                                   title="<spring:message code="label.topic.header.author"/>"><c:out
                                        value="${topic.topicStarter.username}"/></a>
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
                                        ${topic.lastPost.userCreated.username}
                                </a>
                                <a href="${pageContext.request.contextPath}/posts/${topic.lastPost.id}">
                                    <img src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                         alt="<spring:message code="label.section.header.lastMessage"/>"/>
                                </a>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <ul class="forum_table">
                    <li class="forum_row empty_container">
                        <div>
                            <span class="empty">
                                <spring:message code="label.branch.empty"/>
                            </span>
                        </div>
                    </li>
                </ul>
            </c:otherwise>
        </c:choose>
        <div class="forum_info_bottom">
            <div>
                <div>
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="button"
                           href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}"><spring:message
                                code="label.addtopic"/>
                        </a>
                    </sec:authorize>
                    <c:if test="${pagination.maxPages>1}">
                        <c:if test="${pagination.pagingEnabled == true}">
                            <a class="button"
                               href="?pagingEnabled=false"><spring:message code="label.showAll"/></a>
                        </c:if>
                    </c:if>
                    <c:if test="${pagination.pagingEnabled == false}">
                        <a class="button"
                           href="?pagingEnabled=true"><spring:message code="label.showPages"/>
                        </a>
                    </c:if>
                    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
                </div>
                <div>
                    <span class="nav_bottom">
                        <jtalks:pagination uri="${branch.id}" pagination="${pagination}" list="${topics}"/>
                    </span>
                </div>
            </div>
        </div>
        <div class="forum_misc_info">
            <br/>
            <spring:message code="label.topic.moderators"/>
            <ul class="users_list">
                <li><a href="#">andreyko</a>,</li>
                <li><a href="#">Староверъ</a>,</li>
                <li><a href="#">Вася</a>.</li>
            </ul>
            <br/>
            <c:if test="${!(empty viewList)}">
                <spring:message code="label.branch.now_browsing"/>
            </c:if>
            <c:forEach var="innerUser" items="${viewList}">
                <a href="${pageContext.request.contextPath}/users/${innerUser.encodedUsername}">
                    <c:out value="${innerUser.username}"/>
                </a>
                &nbsp;&nbsp;
            </c:forEach>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>