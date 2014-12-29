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
  <title>
    <c:out value="${cmpTitlePrefix}"/>
    <c:out value="${cmpDescription}"/>
  </title>
</head>
<body>
<div class="container">
  <div class="row forum-sections-header">

    <h1 class="pull-left logo-text">
      <c:choose>
        <c:when test="${sessionScope.adminMode == true}">
          <span class="cursor-pointer" id="cmpDescription"><c:out value="${cmpDescription}"/></span>
        </c:when>
        <c:otherwise>
          <a class="invisible-link" href="${pageContext.request.contextPath}/"><c:out value="${cmpDescription}"/></a>
        </c:otherwise>
      </c:choose>
    </h1>

    <div class="pull-right">
      <span class="forum-sections-header-actions">
        <a href="${pageContext.request.contextPath}/topics/recent" title="" class="forum-sections-recent-unanswered">
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
    <c:if test="${sessionScope.adminMode == true}">
      <c:set var="colspanOfSectionName" value="1"/>
    </c:if>

    <c:forEach var="section" items="${sectionList}">
      <jtalks:isSectionVisible section="${section}">
        <tr>
          <td colspan="${colspanOfSectionName}" class="table-title">
            <h2 class="h-nostyle">
              <a href="${pageContext.request.contextPath}/sections/${section.id}"><c:out value="${section.name}"/></a>
            </h2>
          </td>
        </tr>
        <c:forEach var="branch" items="${section.branches}" varStatus="i">
          <c:set var="isBranchVisible" value="false"/>
          <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH' permission='BranchPermission.VIEW_TOPICS'>
            <c:set var="isBranchVisible" value="true"/>
          </jtalks:hasPermission>
          <c:if test="${sessionScope.adminMode == true}">
            <c:set var="isBranchVisible" value="true"/>
          </c:if>
          <c:if test="${isBranchVisible}">
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
                <div class="pull-left">
                  <h3 class="h-nostyle">
                  <c:if test="${sessionScope.adminMode == true}">
                    <a class="branch-title" href="#" id='branchLabel${branch.id}'>
                  </c:if>
                  <c:if test="${sessionScope.adminMode != true}">
                    <a class="branch-title" href="${pageContext.request.contextPath}/branches/${branch.id}">
                  </c:if>
                    <c:out value="${branch.name}"/>
                    </a>
                  </h3>
                  <span class="forum-sections-branch-description-container" id='branchDescriptionLabel${branch.id}'>
                    <c:out value="${branch.description}"/>
                  </span>

                  <div class="forum-sections-moderators-container">
                    <jtalks:moderators moderators="${branch.moderatorsGroup.users}" visibleIfEmpty="false"/>
                  </div>
                </div>

                <c:if test="${sessionScope.adminMode == true}">
                  <div class="pull-right">
                    <a class="btn" href="${pageContext.request.contextPath}/branch/permissions/${branch.id}">
                      <spring:message code="permissions.edit"/>
                    </a>
                  </div>
                </c:if>
              </td>
              <c:if test="${sessionScope.adminMode != true}">
                <td class="topics-posts shrink-to-fit">
                  <spring:message code="label.section.header.topics"/>:
                  <span class='test-topics-count space-left-small'><c:out value="${branch.topicCount}"/></span><br/>
                  <spring:message code="label.section.header.messages"/>:
                  <span class='test-posts-count space-left-small'><c:out value="${branch.postCount}"/></span>
                </td>
                <td class="latest-by shrink-to-fit">
                  <c:if test="${branch.topicCount>0}">
                    <i class="icon-calendar"></i>
                    <a class="date" href="${pageContext.request.contextPath}/posts/${branch.lastPost.id}">
                      <jtalks:format value="${branch.lastPost.creationDate}"/>
                    </a>

                    <p>
                      <spring:message code="label.topic.last_post_by"/>
                      <a class="space-left-small"
                         href="${pageContext.request.contextPath}/users/${branch.lastPost.userCreated.id}"
                         title="<spring:message code='label.tips.view_profile'/>">
                        <c:out value="${branch.lastPost.userCreated.username}"/>
                      </a>
                    </p>
                  </c:if>
                </td>
              </c:if>
            </tr>
          </c:if>
        </c:forEach>
        <c:if test="${sessionScope.adminMode == true}">
          <tr>
            <td>
              <div id='newBranch${section.id}' class="add-branch-button"> +
                <spring:message code="label.branch.add"/>
              </div>
            </td>
          </tr>
        </c:if>
      </jtalks:isSectionVisible>
    </c:forEach>
    </tbody>
  </table>

  <div class="well forum-sections-stats-container">
    <strong>
      <spring:message code="label.onlineUsersInfo.messagesCount"/>
    </strong>
    <span class="test-messages"> <c:out value="${messagesCount}"/> </span>
    <br/>
    <strong>
      <spring:message code="label.onlineUsersInfo.registeredUsers.count"/>
    </strong>
    <span class="test-registered-users"> <c:out value="${registeredUsersCount}"/> </span>
  </div>

  <%-- Users --%>
  <div id="users-stats" class="well forum-sections-userstats-container">
    <strong><spring:message code="label.onlineUsersInfo.visitors"/></strong>
      <span class='test-visitors-total'>
        <c:out value="${visitors}"/>
      </span>,
    <span class="space-left-small"><spring:message code="label.onlineUsersInfo.visitors.registered"/></span>
      <span class='test-visitors-registered'>
        <c:out value="${visitorsRegistered}"/>
      </span>,
    <span class="space-left-small"><spring:message code="label.onlineUsersInfo.visitors.guests"/></span>
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