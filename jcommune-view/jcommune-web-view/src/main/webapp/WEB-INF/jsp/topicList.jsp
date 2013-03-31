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
    <meta name="description" content="<c:out value="${branch.name}"/>">
    <title><c:out value="${branch.name}"/> - <c:out value="${cmpDescription}"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/subscription.js"
            type="text/javascript"></script>
</head>
<body>

<div class="container">
<%-- Branch header --%>
<div id="branch-header">
    <h2><c:out value="${branch.name}"/></h2>

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
        <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                              permission='BranchPermission.CREATE_POSTS'>
            <a id='new-topic-btn' class="btn btn-primary"
               href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}"
               title="<spring:message code='label.addtopic.tip'/>">
                <spring:message code="label.addtopic"/>
            </a>
        </jtalks:hasPermission>
        <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                              permission='BranchPermission.CREATE_CODE_REVIEW'>
            <a id='new-topic-btn' class="btn btn-primary"
               href="${pageContext.request.contextPath}/reviews/new?branchId=${branch.id}"
               title="<spring:message code='label.addCodeReview.tip'/>">
                <spring:message code="label.addCodeReview"/>
            </a>
        </jtalks:hasPermission>
        &nbsp; <%-- For proper pagination layout without buttons--%>
    </div>


    <div class="span8">
        <div class="pagination pull-right forum-pagination">
            <ul>
                <jtalks:pagination uri="${branch.id}" page="${topicsPage}" pagingEnabled="${pagingEnabled}"/>
            </ul>
        </div>
    </div>

</div>
<%-- END OF Upper pagination --%>

<%-- Topics table --%>
<table id="topics-table" class="table table-row table-bordered">
    <c:choose>
        <c:when test="${!(empty topicsPage.content)}">
            <thead>
            <tr>
                <sec:authorize access="isAuthenticated()">
                    <th class="status-col-small"></th>
                </sec:authorize>
                <th><spring:message code="label.branch.header.topics"/></th>
                <th class="author-col shrink-to-fit"><spring:message code="label.branch.header.author"/></th>
                <th class="posts-views-small forum-posts-view-header shrink-to-fit"><spring:message
                        code="label.branch.header.posts"/></th>
                <th class="posts-views-small forum-posts-view-header shrink-to-fit"><spring:message
                        code="label.branch.header.views"/></th>
                <th class="latest-by forum-latest-by-header shrink-to-fit"><spring:message
                        code="label.branch.header.lastMessage"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="topic" items="${topicsPage.content}">
                <%-- Topic row --%>
                <tr>
                    <sec:authorize access="isAuthenticated()">
                        <td class="status-col-small">
                            <jtalks:topicIconSmall topic="${topic}"/>
                        </td>
                    </sec:authorize>
                    <td class="posts-td-small">
                            <%--Some topic types should have a special prefix when displayed--%>
                        <c:if test="${topic.announcement=='true'}">
                            <span class="sticky"><spring:message code="label.marked_as_announcement"/> </span>
                        </c:if>
                        <c:if test="${topic.sticked=='true'}">
                            <span class="sticky"><spring:message code="label.marked_as_sticked"/></span>
                        </c:if>
                        <c:if test="${topic.hasPoll}">
                            <span class="sticky"><spring:message code="label.marked_as_poll"/></span>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/topics/${topic.id}">
                            <c:out value="${topic.title}"/>
                        </a>
                        <br/>
                        <sub class="created-by">by
                            <a href='${pageContext.request.contextPath}/users/${topic.topicStarter.id}"'>
                                <c:out value="${topic.topicStarter.username}"/>
                            </a>
                        </sub>
                    </td>
                    <td class="author-col shrink-to-fit">
                        <a href='${pageContext.request.contextPath}/users/${topic.topicStarter.id}'>
                            <c:out value="${topic.topicStarter.username}"/>
                        </a>
                    </td>
                    <td class="posts-views-small shrink-to-fit">
                        <span class='test-posts-count'><c:out value="${topic.postCount}"/></span>
                    </td>
                    <td class="posts-views-small shrink-to-fit">
                        <span class='test-views'><c:out value="${topic.views}"/></span>
                    </td>
                    <td class="latest-by shrink-to-fit">
                        <i class="icon-calendar"></i>
                        <a class="date" href="${pageContext.request.contextPath}/posts/${topic.lastPost.id}">
                            <jtalks:format value="${topic.lastPost.creationDate}"/>
                        </a>
                        &gt;&gt;
                        <a href="${pageContext.request.contextPath}/users/${topic.lastPost.userCreated.id}">
                            <c:out value="${topic.lastPost.userCreated.username}"/>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </c:when>
        <c:otherwise>
            <tbody>
            <tr>
                <td>
                    <spring:message code="label.branch.empty"/>
                </td>
            </tr>
            </tbody>
        </c:otherwise>
    </c:choose>
</table>
<%-- END OF Topics table --%>

<%-- Bottom pagination --%>
<div class="row-fluid upper-pagination forum-pagination-container">

    <div class="span4">
        <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                              permission='BranchPermission.CREATE_POSTS'>
            <a id='new-topic-btn' class="btn btn-primary"
               href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}"
               title="<spring:message code='label.addtopic.tip'/>">
                <spring:message code="label.addtopic"/>
            </a>
        </jtalks:hasPermission>
        <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                              permission='BranchPermission.CREATE_CODE_REVIEW'>
            <a id='new-topic-btn' class="btn btn-primary"
               href="${pageContext.request.contextPath}/reviews/new?branchId=${branch.id}"
               title="<spring:message code="label.addCodeReview.tip"/>">
                <spring:message code="label.addCodeReview"/>
            </a>
        </jtalks:hasPermission>
        &nbsp; <%-- For proper pagination layout without buttons--%>
    </div>

    <div class="span8">
        <div class="pagination pull-right forum-pagination">
            <ul>
                <jtalks:pagination uri="${branch.id}" page="${topicsPage}" pagingEnabled="${pagingEnabled}"/>
            </ul>
        </div>
    </div>

</div>
<%-- END OF Bottom pagination --%>


<%-- Users --%>
<div id="users-stats" class="well forum-user-stats-container">
    <strong><spring:message code="label.topic.moderators"/></strong>
    <jtalks:moderators moderators="${branch.moderatorsGroup.users}"/>
    <br/>
    <strong><spring:message code="label.branch.now_browsing"/></strong>
    <jtalks:users users="${viewList}" branch="${branch}"/>
</div>
<%-- END OF Users --%>

</div>
</body>
