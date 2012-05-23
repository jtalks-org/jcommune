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
    <div class="container">     
        
        <div class="row" style="margin:0">
        <a href="${pageContext.request.contextPath}/"><h1 class="pull-left" style="font-family: 'Carter One', cursive;"><spring:message code="label.section.jtalks_forum"/></h1></a>
        <div class="pull-right">
          <span style="display:inline-block; vertical-align:middle; text-align: right;"> 
            <a href="${pageContext.request.contextPath}/topics/recent" title="" style="font-weight: bold;  padding-left: 2px; color: rgb(85, 85, 85);">
                <spring:message code="label.recent"/>
            </a>
            <br />
            <a href="${pageContext.request.contextPath}/topics/unanswered" title="" style="font-weight: bold;  padding-left: 2px; color: rgb(85, 85, 85);">
                <spring:message code="label.messagesWithoutAnswers"/>
            </a>
          </span>
          <a href="${pageContext.request.contextPath}/topics/recent.rss" title="Feed subscription">
            <img src="${pageContext.request.contextPath}/resources/images/rss-icon.png" alt="" style="vertical-align:middle">
          </a>
        </div>
      </div>
      <hr style="margin: 0px;"/>
      
      <!-- Topics table -->
        <c:forEach var="section" items="${sectionList}">
            <div class="forum_header_table">
                <h3>
                <a href="${pageContext.request.contextPath}/sections/${section.id}">
                    <c:out value="${section.name}"/>
                </a>
                </h3>
            </div>

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
                            <span style="font-style: italic; font-size: 12px; white-space:nowrap;"><c:out value="${branch.description}"/></span>
                            <br />
                            <div style="font-size: 11px">
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
                                <a class="date" href="${pageContext.request.contextPath}/posts/${branch.lastUpdatedTopic.lastPost.id}">
                                    <jtalks:format value="${branch.lastUpdatedTopic.lastPost.creationDate}"/>
                                </a>
                                <p>
                                    by 
                                    <a href="${pageContext.request.contextPath}/users/${branch.lastUpdatedTopic.lastPost.userCreated.encodedUsername}">
                                        <c:out value="${branch.lastUpdatedTopic.lastPost.userCreated.username}"/>
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
    
    <div class="well" style="min-height: 10px; padding: 5px; margin: 0px; margin-bottom: 5px;">
        <strong><spring:message code="label.onlineUsersInfo.messagesCount"/> </strong><c:out value="${messagesCount}"/>
        <br />
        <strong><spring:message code="label.onlineUsersInfo.registeredUsers.count"/> </strong><c:out value="${registeredUsersCount}"/>
    </div>
    
    <!-- Users -->
    <div id="users-stats" class="well" style="min-height: 10px; padding: 5px; margin: 0px;">
        <strong><spring:message code="label.onlineUsersInfo.visitors"/> </strong><c:out value="${visitors}"/>, 
        <spring:message code="label.onlineUsersInfo.visitors.registered"/> <c:out value="${visitorsRegistered}"/>, 
        <spring:message code="label.onlineUsersInfo.visitors.guests"/> <c:out value="${visitorsGuests}"/> 
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