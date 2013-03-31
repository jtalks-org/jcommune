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
    <meta name="description" content="<c:out value="${cmpDescription}"/>">
    <title><c:out value="${cmpDescription}"/></title>
</head>
<body>
<div class="container">
    <div class="row forum-sections-header">
        <h1 class="pull-left logo-text">
            <c:out value="${cmpDescription}"/>
        </h1>

        <div class="pull-right">
           <span class="forum-sections-header-actions">
               <a href="${pageContext.request.contextPath}/topics/recent" title=""
                  class="forum-sections-recent-unanswered">
                   <spring:message code="label.recent"/>
               </a>
               <br/>
               <a href="${pageContext.request.contextPath}/topics/unanswered" title=""
                  class="forum-sections-recent-unanswered">
                   <spring:message code="label.messagesWithoutAnswers"/>
               </a>
           </span>
            <a href="${pageContext.request.contextPath}/topics/recent.rss"
               title="<spring:message code='label.tips.feed_subsription'/>">
                <img src="${pageContext.request.contextPath}/resources/images/rss-icon.png" alt="" class="rss-icon">
            </a>
        </div>
    </div>
    <hr class="forum-pagination"/>

    <%-- Sections and branches --%>
    <table id="topics-table" class="table table-row table-with-titles">
        <tbody>
        <c:set var="colspanOfSectionName" value="3"/>
        <sec:authorize access="isAuthenticated()">
            <%-- TODO: change to 4 during below fix about unread posts --%>
            <c:set var="colspanOfSectionName" value="3"/>
        </sec:authorize>

        <c:forEach var="section" items="${sectionList}">
            <jtalks:isSectionVisible section="${section}">
                <tr>
                    <th colspan="${colspanOfSectionName}">
                        <a href="${pageContext.request.contextPath}/sections/${section.id}">
                            <c:out value="${section.name}"/>
                        </a>
                    </th>
                </tr>
                <c:forEach var="branch" items="${section.branches}" varStatus="i">
                    <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                                          permission='BranchPermission.VIEW_TOPICS'>
                        <tr>
                                <%--TODO: fix in milstone 2--%>
                                <%--<sec:authorize access="isAuthenticated()">--%>
                                <%--<td class="status-col">--%>
                                <%--<c:choose>--%>
                                <%--<c:when test="${branch.unreadPosts}">--%>
                                <%--<img class="status-img"--%>
                                <%--src="${pageContext.request.contextPath}/resources/images/new-posts.png"--%>
                                <%--title="<spring:message code="label.topic.new_posts"/>"/>--%>
                                <%--</c:when>--%>
                                <%--<c:otherwise>--%>
                                <%--<img class="status-img"--%>
                                <%--src="${pageContext.request.contextPath}/resources/images/no-new-posts.png"--%>
                                <%--title="<spring:message code="label.topic.no_new_posts"/>"/>--%>
                                <%--</c:otherwise>--%>
                                <%--</c:choose>--%>
                                <%--</td>--%>
                                <%--</sec:authorize>--%>
                            <td class="title-col">
                                <a class="branch-title" href="${pageContext.request.contextPath}/branches/${branch.id}">
                                    <c:out value="${branch.name}"/>
                                </a>
                                <br/>
                                    <span class="forum-sections-branch-description-container"><c:out
                                            value="${branch.description}"/></span>
                                <br/>

                                <div class="forum-sections-moderators-container">
                                    <strong><spring:message code="label.section.moderators"/></strong>
                                    <jtalks:moderators moderators="${branch.moderatorsGroup.users}"/>
                                </div>
                            </td>
                            <td class="topics-posts shrink-to-fit">
                                <spring:message code="label.section.header.topics"/>: <span
                                    class='test-topics-count'><c:out
                                    value="${branch.topicCount}"/></span><br/>
                                <spring:message code="label.section.header.messages"/>: <span
                                    class='test-posts-count'><c:out
                                    value="${branch.postCount}"/></span></td>

                            <td class="latest-by shrink-to-fit">
                                <c:if test="${branch.topicCount>0}">
                                    <i class="icon-calendar"></i>
                                    <a class="date"
                                       href="${pageContext.request.contextPath}/posts/${branch.lastPost.id}">
                                        <jtalks:format value="${branch.lastPost.creationDate}"/>
                                    </a>

                                    <p><spring:message code="label.topic.last_post_by"/>
                                        <a href="${pageContext.request.contextPath}/users/${branch.lastPost.userCreated.id}">
                                            <c:out value="${branch.lastPost.userCreated.username}"/>
                                        </a>
                                    </p>
                                </c:if>
                            </td>
                        </tr>
                    </jtalks:hasPermission>
                </c:forEach>
            </jtalks:isSectionVisible>
        </c:forEach>
        </tbody>
    </table>
    <%-- END OF Topics table --%>

    <div class="well forum-sections-stats-container">
        <strong>
            <spring:message code="label.onlineUsersInfo.messagesCount"/>
        </strong>
        <span class="test-messages">
            <c:out value="${messagesCount}"/>
        </span>
        <br/>
        <strong>
            <spring:message code="label.onlineUsersInfo.registeredUsers.count"/>
        </strong>
        <span class="test-registered-users">
            <c:out value="${registeredUsersCount}"/>
        </span>
    </div>

    <%-- Users --%>
    <div id="users-stats" class="well forum-sections-userstats-container">
        <strong><spring:message code="label.onlineUsersInfo.visitors"/> </strong>
        <span class='test-visitors-total'>
            <c:out value="${visitors}"/>
        </span>,
        <spring:message code="label.onlineUsersInfo.visitors.registered"/>
        <span class='test-visitors-registered'>
            <c:out value="${visitorsRegistered}"/>
        </span>,
        <spring:message code="label.onlineUsersInfo.visitors.guests"/>
        <span class='test-visitors-guests'>
            <c:out value="${visitorsGuests}"/>
        </span>
        <br/>

        <strong>
            <spring:message code="label.onlineUsersInfo.registeredUsers"/>
        </strong>
        <jtalks:users users="${usersRegistered}"/>
    </div>
    <%-- END OF Users --%>
</div>
</body>