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

<jsp:include page="../template/topLine.jsp"/>

<div class="container">     
        
    <div class="row forum-sections-header">
        <a href="${pageContext.request.contextPath}/">
            <h1 class="pull-left logo-text">
                <spring:message code="label.section.jtalks_forum"/>
            </h1>
        </a>
        <div class="pull-right">
            <span class="forum-sections-header-actions"> 
                <a href="${pageContext.request.contextPath}/topics/recent" title="" class="forum-sections-recent-unanswered">
                    <spring:message code="label.recent"/>
                </a>
                <br />
                <a href="${pageContext.request.contextPath}/topics/unanswered" title="" class="forum-sections-recent-unanswered">
                    <spring:message code="label.messagesWithoutAnswers"/>
                </a>
            </span>
            <a href="${pageContext.request.contextPath}/topics/recent.rss" title="Feed subscription">
                <img src="${pageContext.request.contextPath}/resources/images/rss-icon.png" alt="" class="rss-icon">
            </a>
        </div>
    </div>
    <hr style="margin: 0px;"/>
      
    <!-- Topics table -->
    <c:forEach var="section" items="${sectionList}">
        <h3>
            <a href="${pageContext.request.contextPath}/sections/${section.id}">
                <c:out value="${section.name}"/>
            </a>
        </h3>

        <table id="topics-table" cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered">
            <tbody>
                <c:forEach var="branch" items="${section.branches}" varStatus="i">
                    <tr>
                        <td class="status-col">
                            <img class="status-img" src="${pageContext.request.contextPath}/resources/images/closed.png" 
                                alt=""
                                title='<spring:message code="label.section.close_forum"/>'/>
                        </td>
                        <td  class="title-col">
                            <a class="branch-title" href="${pageContext.request.contextPath}/branches/${branch.id}">
                                <c:out value="${branch.name}"/>
                            </a>           
                            <br />
                            <span class="forum-sections-branch-description-container"><c:out value="${branch.description}"/></span>
                            <br />
                            <div class="forum-sections-moderators-container">
                                <span><spring:message code="label.section.moderators"/></span> 
                                <a href="#">Vurn</a>
                            </div> 
                        </td>
                        <td class="posts-views">
                            <spring:message code="label.section.header.topics"/>: <c:out value="${branch.topicCount}"/><br />
                            <spring:message code="label.section.header.messages"/>: <c:out value="${branch.postCount}"/></td>
                            
                        <td class="latest-by">
                            <c:if test="${branch.topicCount>0}">
                                <i class="icon-calendar"></i>
                                <a class="date" href="${pageContext.request.contextPath}/posts/${branch.lastPostInLastUpdatedTopic.id}">
                                    <jtalks:format value="${branch.lastPostInLastUpdatedTopic.creationDate}"/>
                                </a>
                                <p>
                                    by 
                                    <a href="${pageContext.request.contextPath}/users/${branch.lastPostInLastUpdatedTopic.userCreated.encodedUsername}">
                                        <c:out value="${branch.lastPostInLastUpdatedTopic.userCreated.username}"/>
                                    </a>
                                </p>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>    
    </c:forEach>        
    <!-- END OF Topics table -->
    
    <div class="well forum-sections-stats-container">
        <strong><spring:message code="label.onlineUsersInfo.messagesCount"/> </strong><span class="test-messages"><c:out value="${messagesCount}"/></span>
        <br />
        <strong><spring:message code="label.onlineUsersInfo.registeredUsers.count"/> </strong><span class="test-registered-users"><c:out value="${registeredUsersCount}"/></span>
    </div>
    
    <!-- Users -->
    <div id="users-stats" class="well forum-sections-userstats-container">
        <strong><spring:message code="label.onlineUsersInfo.visitors"/> </strong><span class='test-visitors-total'><c:out value="${visitors}"/></span>, 
        <spring:message code="label.onlineUsersInfo.visitors.registered"/> <span class='test-visitors-registered'><c:out value="${visitorsRegistered}"/></span>, 
        <spring:message code="label.onlineUsersInfo.visitors.guests"/> <span class='test-visitors-guests'><c:out value="${visitorsGuests}"/></span> 
        <br />
        <c:if test="${!(empty usersRegistered)}">
            <strong><spring:message code="label.onlineUsersInfo.registeredUsers"/></strong>
            <c:forEach items="${usersRegistered}" var="user">
                <c:choose>
                    <c:when test="${user.role=='ROLE_ADMIN'}">
                        <a href="${pageContext.request.contextPath}/users/${user.encodedUsername}"
                                title="Click to view profile"
                                class="label label-important">${user.username}</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/users/${user.encodedUsername}"
                                title="Click to view profile">
                                <c:out value="${user.username}"/></a>
                    </c:otherwise>
                 </c:choose>
            </c:forEach>
        </c:if>
    </div>
    <!-- END OF Users -->
</div>
</body>