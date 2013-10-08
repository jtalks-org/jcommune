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
<%--Let's define possible beans that are used on the page, this will give us autocompletion and error highlighting--%>
<jsp:useBean id="branch" type="org.jtalks.jcommune.model.entity.Branch" scope="request"/>
<jsp:useBean id="cmpDescription" type="java.lang.String" scope="request"/>
<jsp:useBean id="subscribed" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="breadcrumbList" type="java.util.List" scope="request"/>
<jsp:useBean id="viewList" type="java.util.List" scope="request"/>
<jsp:useBean id="topicsPage" type="org.springframework.data.domain.Page" scope="request"/>
<head>
  <meta name="description" content="<c:out value="${branch.name}"/>">
  <title><c:out value="${branch.name}"/> - <c:out value="${cmpDescription}"/></title>
</head>
<body>
<div class="container">
<%-- Branch header --%>
<div id="branch-header">
  <h1>
    <a class="invisible-link" href="${pageContext.request.contextPath}/branches/${branch.id}">
      <c:out value="${branch.name}"/>
    </a>
  </h1>

  <div id="right-block">
    <sec:authorize access="isAuthenticated()">
      <span id="mark-all-viewed">
        <i class="icon-check"></i>
        <a href="${pageContext.request.contextPath}/branches/${branch.id}/markread">
          <spring:message code="label.mark_all_topics_read"/>
        </a>
      </span>

      <span id="subscribe">
        <i class="icon-star"></i>
        <c:choose>
          <c:when test="${subscribed}">
            <a id="subscription" class="button top_button"
               href="${pageContext.request.contextPath}/branches/${branch.id}/unsubscribe"
               title="<spring:message code="label.unsubscribe.tooltip"/>">
              <spring:message code="label.unsubscribe"/>
            </a>
          </c:when>
          <c:otherwise>
            <a id="subscription" class="button top_button"
               href="${pageContext.request.contextPath}/branches/${branch.id}/subscribe"
               title='<spring:message code="label.subscribe.tooltip"/>'>
              <spring:message code="label.subscribe"/>
            </a>
          </c:otherwise>
        </c:choose>
      </span>
    </sec:authorize>
  </div>
  <span class="inline-block"></span>
</div>
<%-- END OF Branch header --%>

<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

<%-- Upper pagination --%>
<div class="row-fluid upper-pagination forum-pagination-container">
  <div class="span4">
    <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH' permission='BranchPermission.CREATE_POSTS'>
    <a class="new-topic-btn btn btn-primary space-left-medium-nf"
         href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}"
         title="<spring:message code='label.addtopic.tip'/>" data-placement="right">
        <spring:message code="label.addtopic"/>
      </a>
    </jtalks:hasPermission>
    <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH' permission='BranchPermission.CREATE_CODE_REVIEW'>
    <a class="new-code-review-btn btn btn-primary space-left-medium-nf"
         href="${pageContext.request.contextPath}/reviews/new?branchId=${branch.id}"
         title="<spring:message code='label.addCodeReview.tip'/>" data-placement="right">
        <spring:message code="label.addCodeReview"/>
      </a>
    </jtalks:hasPermission>
    &nbsp; <%-- For proper pagination layout without buttons--%>
  </div>
  <div class="span8">
    <div class="pagination pull-right forum-pagination">
      <ul>
        <jtalks:pagination uri="${branch.id}" page="${topicsPage}"/>
      </ul>
    </div>
  </div>

</div>
<%-- END OF Upper pagination --%>

  <%--you cannot use <spring> tag inside of an attribute, thus defining it as a separate var--%>
  <spring:message code="label.branch.empty" var="messageToShowIfNoTopics"/>
  <jtalks:topicList topics="${topicsPage.content}" messageToShowIfNoTopics='${messageToShowIfNoTopics}'/>

<%-- Bottom pagination --%>
<div class="row-fluid upper-pagination forum-pagination-container">
  <div class="span4">
    <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH' permission='BranchPermission.CREATE_POSTS'>
      <a class="new-topic-btn btn btn-primary space-left-medium-nf"
         href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}"
         title="<spring:message code='label.addtopic.tip'/>" data-placement="right">
        <spring:message code="label.addtopic"/>
      </a>
    </jtalks:hasPermission>
    <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH' permission='BranchPermission.CREATE_CODE_REVIEW'>
      <a class="new-code-review-btn btn btn-primary space-left-medium-nf"
         href="${pageContext.request.contextPath}/reviews/new?branchId=${branch.id}"
         title="<spring:message code="label.addCodeReview.tip"/>" data-placement="right">
        <spring:message code="label.addCodeReview"/>
      </a>
    </jtalks:hasPermission>
    &nbsp; <%-- For proper pagination layout without buttons--%>
  </div>

  <div class="span8">
    <div class="pagination pull-right forum-pagination">
      <ul><jtalks:pagination uri="${branch.id}" page="${topicsPage}"/></ul>
    </div>
  </div>

</div>
<%-- END OF Bottom pagination --%>
<%-- Users --%>
<div id="users-stats" class="well forum-user-stats-container">
  <jtalks:moderators moderators="${branch.moderatorsGroup.users}"/>
  <br/>
  <strong><spring:message code="label.branch.now_browsing"/></strong>
  <jtalks:users users="${viewList}" branch="${branch}"/>
</div>
<%-- END OF Users --%>
</div>
</body>
