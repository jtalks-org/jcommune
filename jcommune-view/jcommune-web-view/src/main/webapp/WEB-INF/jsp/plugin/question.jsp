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
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:useBean id="question" type="org.jtalks.jcommune.model.entity.Topic" scope="request"/>
<head>
  <meta name="description" content="<c:out value="${question.title}"/>">
  <title>
    <c:out value="${cmpTitlePrefix}"/>
    <c:out value="${question.title}"/>
  </title>
</head>
<body>

<c:set var="authenticated" value="${false}"/>
<div class="container">
<%-- Topic header --%>
<div id="branch-header">
  <h1>
    <a class="invisible-link" href="${pageContext.request.contextPath}/topics/${question.id}">
      <c:out value="${question.title}"/>
    </a>
  </h1>

  <div id="right-block">
    <sec:authorize access="isAuthenticated()">
           <span id="subscribe">
               <i class="icon-star"></i>
               <c:choose>
                 <c:when test="${subscribed}">
                   <a id="subscription" class="button top_button"
                      href="${pageContext.request.contextPath}/topics/${question.id}/unsubscribe"
                      title="<spring:message code="label.unsubscribe.tooltip"/>">
                     <spring:message code="label.unsubscribe"/>
                   </a>
                 </c:when>
                 <c:otherwise>
                   <a id="subscription" class="button top_button"
                      href="${pageContext.request.contextPath}/topics/${question.id}/subscribe"
                      title='<spring:message code="label.subscribe.tooltip"/>'>
                     <spring:message code="label.subscribe"/>
                   </a>
                 </c:otherwise>
               </c:choose>
           </span>
      <c:set var="authenticated" value="${true}"/>
    </sec:authorize>
  </div>
  <span class='inline-block'></span>
</div>
<%-- END OF Topic header --%>

<%--<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>--%>

<%-- Upper pagination --%>
<div class="row-fluid upper-pagination forum-pagination-container">
  <div class="span3">
    <%--<jtalks:topicControls topic="${question}"/>--%>
    &nbsp; <%-- For proper pagination layout without buttons--%>
  </div>

  <%-- Pagination --%>
  <div class="span9">
    <div class="pagination pull-right forum-pagination">
      <ul>
        <jtalks:pagination uri="${topicId}" page="${postsPage}"/>
      </ul>
    </div>
  </div>
  <%-- END OF Pagination --%>

</div>
<%-- END OF Upper pagination --%>

<div>
<%-- List of posts. --%>
<c:forEach var="post" items="${postsPage.content}" varStatus="i">
<%-- Post --%>
<c:set var="isFirstPost" value="false"/>
<c:if test="${postsPage.number == 1 && i.index == 0}">
  <c:set var="isFirstPost" value="true"/>
</c:if>

<c:set var="postClass" value=""/>
<c:if test="${isFirstPost}">
  <c:set var="postClass" value="script-first-post"/>
</c:if>

<div class="post ${postClass}">
<div class="anchor">
  <a id="${post.id}">anchor</a>
</div>

<div class="question">
  <div class="question-left-panel pull-left">
    <div class="thumbnail wraptocenter">
      <img src="${pageContext.request.contextPath}/users/${post.userCreated.id}/avatar" alt=""/>
    </div>
    <div class="question-votes">
      <div class="vote-up"><i class="icon-arrow-up"></i></div>
      <span class="vote-result">13</span>
      <div class="vote-down"><i class="icon-arrow-down"></i></div>
    </div>
  </div>
  <div class="question-right-panel">
    <div class="question-header">
      <div class="question-date pull-right">
        <i class="icon-calendar"></i>
        <jtalks:format value="${post.creationDate}"/>
      </div>
      <div class="question-author">
        <a class='post-userinfo-username'
           href="${pageContext.request.contextPath}/users/${post.userCreated.id}"
           title="<spring:message code='label.tips.view_profile'/>">
          <c:out value="${post.userCreated.username}"/>
        </a>
      </div>
    </div>
    <div class="question-content">
      <jtalks:postContent text="${post.postContent}"
                          signature="${post.userCreated.signature}"/>
    </div>
    <div class="question-footer">
      <div class="btn-toolbar post-btn-toolbar">
        <div class="btn-group">
          <a href="${edit_url}" data-rel="${question.branch.id}"
           class="edit_button btn btn-mini" title="<spring:message code='label.tips.edit_post'/>">
            <i class="icon-edit"></i>
            <spring:message code="label.edit"/>
          </a>
          <a href="${delete_url}" class="btn btn-mini btn-danger delete"
             title="<spring:message code='label.tips.remove_post'/>"
             data-confirmationMessage="">
            <i class="icon-remove icon-white"></i>
            <spring:message code="label.delete"/>
          </a>
        </div>
      </div>
    </div>
  </div>
</div>
</div>
<%-- END OF Post --%>
</c:forEach>
</div>

<div class="row-fluid forum-pagination-container">
  <div class="span3">
    <%--<jtalks:topicControls topic="${question}"/>--%>
    &nbsp; <%-- For proper pagination layout without buttons--%>
  </div>

  <%-- Pagination --%>
  <div class="span9">
    <div class="pagination pull-right forum-pagination">
      <ul>
        <jtalks:pagination uri="${topicId}" page="${postsPage}"/>
      </ul>
    </div>
  </div>
  <%-- END OF Pagination --%>
</div>

<%-- Users --%>
<div id="users-stats" class="well forum-user-stats-container">
  <%--<jtalks:moderators moderators="${question.branch.moderatorsGroup.users}"/>--%>
  <br/>
  <strong><spring:message code="label.topic.now_browsing"/></strong>
  <%--<jtalks:users users="${viewList}" branch="${question.branch}"/>--%>
</div>
<%-- END OF Users --%>

<%--Fake form to delete posts and topics.
Without it we're likely to get lots of problems simulating HTTP DELETE via JS in a Spring fashion  --%>
<form:form id="deleteForm" method="DELETE"/>
</div>

<script>
  if ($('#bodyText\\.errors:visible').length > 0) {
    Utils.focusFirstEl('#postBody');
  }
</script>
</body>
