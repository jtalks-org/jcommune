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
<jsp:include page="../template/topLine.jsp"/>

<div class="container">
    <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span3">
            <h3>
                <spring:message code="label.messagesWithoutAnswers"/>
            </h3>
        </div>
        
        <div class="span9">
            <div class="pagination pull-right forum-pagination">
                <ul>
                    <jtalks:pagination uri="" page="${topicsPage}" pagingEnabled="${pagingEnabled}"/>
                </ul>
            </div>
        </div>
    </div>
        
    <!-- Topics table -->
    <table id="topics-table" cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered">
       <c:choose>
           <c:when test="${!(empty list)}">
		        <thead>
		            <tr>
		                <th class="status-col"></th>
		                <th><spring:message code="label.branch.header.topics"/></th>
		                <th class="author-col"><spring:message code="label.branch.header.author"/></th>
		                <th class="posted-in-col"><spring:message code="label.branch.header.branches"/></th>
		                <th class="posts-views forum-posts-view-header"><spring:message code="label.branch.header.posts_views"/></th>
		                <th class="latest-by forum-latest-by-header"><spring:message code="label.branch.header.lastMessage"/></th>
		            </tr>
		        </thead>
		        <tbody>
                    <c:forEach var="item" items="${list}">
                        <tr>
                            <td class="status-col">
                                <c:set var="hasNewPosts" value="false"/>
                                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                                    <c:if test="${topic.hasUpdates}">
                                        <c:set var="hasNewPosts" value="true"/>
                                    </c:if>
                                </sec:authorize>
                                
                                <c:choose>
                                    <c:when test="${hasNewPosts}">
                                        <a href="${pageContext.request.contextPath}/posts/${topic.firstUnreadPostId}">
                                            <img class="status-img" 
                                                src="${pageContext.request.contextPath}/resources/images/new_badge.png" 
                                                title="<spring:message code="label.topic.new_posts"/>" />
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <img class="status-img" 
                                            src="${pageContext.request.contextPath}/resources/images/old_badge.png" 
                                            title="<spring:message code="label.topic.no_new_posts"/>" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                            <c:choose>
                                <c:when test="${item.announcement=='true'}">
                                    <span class='sticky'>
                                        <spring:message code="label.marked_as_announcement"/>
                                    </span>
                                </c:when>
                                <c:when test="${item.sticked=='true'}">
                                    <span class='sticky'>
                                        <spring:message code="label.marked_as_sticked"/> 
                                    </span>
                                </c:when>
                            </c:choose>
                            <c:if test="${item.hasPoll}">
                                <a style="color: red;"
                                       href="${pageContext.request.contextPath}/topics/${item.id}">
                                        [POLL]</a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/topics/${item.id}">
                                <c:out value="${item.title}"/>
                            </a>
                            </td>
                            
                            <td class="author-col">
                                <a href='${pageContext.request.contextPath}/users/${item.topicStarter.id}'
                                    title="<spring:message code="label.topic.header.author"/>">
                                    <c:out value="${item.topicStarter.username}"/>
                                </a>
                            </td>
                            <td class="posted-in-col">
                                <a href="${pageContext.request.contextPath}/branches/${item.branch.id}">
                                    <c:out value="${item.branch.name}"/>
                                </a>
                            </td>
                            
                            <td class="posts-views">
                                <spring:message code="label.section.header.messages"/>: <span class='test-posts-count'>
                                <c:out value="${item.postCount}"/></span><br />
                                <spring:message code="label.branch.header.views"/>: <span class='test-views'>
                                <c:out value="${item.views}"/></span>
                            </td>
                            <td class="latest-by">
                                <i class="icon-calendar"></i>
                                <a class="date" href="${pageContext.request.contextPath}/posts/${item.lastPost.id}">
                                    <jtalks:format value="${item.lastPost.creationDate}"/>
                                </a>
                                <p><spring:message code="label.topic.last_post_by"/> 
                                    <a href="${pageContext.request.contextPath}/users/${item.lastPost.userCreated.id}">
                                        <c:out value="${item.lastPost.userCreated.username}"/>
                                    </a>
                                </p>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </c:when>
            <c:otherwise>
                <tbody>
                    <tr>
                        <td>
                            <spring:message code="label.messagesWithoutAnswers.empty"/>
                        </td>
                    </tr>
                </tbody>
            </c:otherwise>
        </c:choose>
    </table>
        
    <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span12">
            <div class="pagination pull-right forum-pagination-container">
                <ul>
                    <jtalks:pagination uri="" page="${topicsPage}" pagingEnabled="${pagingEnabled}"/>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>