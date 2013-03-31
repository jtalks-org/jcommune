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

<head>
    <meta name="description" content="<c:out value="${topic.title}"/>">
    <title><c:out value="${topic.title}"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/utils.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/subscription.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jqery.impromptu.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/moveTopic.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/poll.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/codeHighlighting.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/permissionService.js"
            type="text/javascript"></script>
</head>
<body>

<c:set var="authenticated" value="${false}"/>
<div class="container">
<%-- Topic header --%>
<div id="branch-header">
    <h3>
        <c:out value="${topic.title}"/>
    </h3>

    <div id="right-block">
        <sec:authorize access="isAuthenticated()">
           <span id="subscribe">
               <i class="icon-star"></i>
               <c:choose>
                   <c:when test="${subscribed}">
                       <a id="subscription" class="button top_button"
                          href="${pageContext.request.contextPath}/topics/${topic.id}/unsubscribe"
                          title="<spring:message code="label.unsubscribe.tooltip"/>">
                           <spring:message code="label.unsubscribe"/>
                       </a>
                   </c:when>
                   <c:otherwise>
                       <a id="subscription" class="button top_button"
                          href="${pageContext.request.contextPath}/topics/${topic.id}/subscribe"
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

<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

<%-- Upper pagination --%>
<div class="row-fluid upper-pagination forum-pagination-container">
    <div class="span3">
        <jtalks:topicControls topic="${topic}"/>
        &nbsp; <%-- For proper pagination layout without buttons--%>
    </div>

    <%-- Pagination --%>
    <div class="span9">
        <div class="pagination pull-right forum-pagination">
            <ul>
                <jtalks:pagination uri="${topicId}" page="${postsPage}" pagingEnabled="${pagingEnabled}"/>
            </ul>
        </div>
    </div>
    <%-- END OF Pagination --%>

</div>
<%-- END OF Upper pagination --%>

<%-- Include poll row. --%>
<c:if test="${topic.poll != null}">
    <div class="row well poll">
        <div class="span4  poll-row">
            <jtalks:poll poll="${topic.poll}"/>
        </div>
    </div>
</c:if>

<%-- Setup indicators --%>
<c:if test="${topic.codeReview != null}">
    <%-- Below  hidden properties are used in CR script --%>
    <input type="hidden" id="has-code-review" value="true"/>
    <input type="hidden" id="codeReviewId" value="${topic.codeReview.id}"/>
    <input type="hidden" id="branchId" value="${topic.branch.id}"/>
    <sec:authorize access="isAuthenticated()">
        <sec:authentication property="principal.id" var="userId"/>
        <input type="hidden" id="userId" value="${userId}"/>
    </sec:authorize>
</c:if>

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

<%--We need different logic for code review and other posts because CR uses differnet phpBB processing--%>
<c:remove var="isCodeReviewPost" scope="request"/>  
<c:if test="${isFirstPost && (topic.codeReview != null)}">
    <c:set var="isCodeReviewPost" value="true" scope="request"/>
</c:if>

<div class="post ${postClass}">
    <div class="anchor">
        <a id="${post.id}">anchor</a>
    </div>
    <table class="table table-row table-bordered table-condensed">
        <tr class="post-header">
            <td class="post-date">
                <i class="icon-calendar"></i>
                <jtalks:format value="${post.creationDate}"/>
            </td>
            <td class="top-buttons">
                &nbsp;
                <div class="btn-toolbar post-btn-toolbar">
                    <div class="btn-group">

                        <c:choose>
                            <c:when test="${isFirstPost}">
                                <%-- first post - urls to delete & edit topic --%>
                                <c:set var="delete_url"
                                       value="${pageContext.request.contextPath}/topics/${topic.id}"/>
                                <c:set var="edit_url"
                                       value="${pageContext.request.contextPath}/topics/${topic.id}/edit"/>
                                <c:set var="confirm_message" value="label.deleteTopicConfirmation"/>
                            </c:when>
                            <c:otherwise>
                                <%-- url to delete & edit post --%>
                                <c:set var="delete_url"
                                       value="${pageContext.request.contextPath}/posts/${post.id}"/>
                                <c:set var="edit_url"
                                       value="${pageContext.request.contextPath}/posts/${post.id}/edit"/>
                                <c:set var="confirm_message" value="label.deletePostConfirmation"/>
                            </c:otherwise>
                        </c:choose>
                        <sec:authorize access="isAuthenticated()">
                            <sec:authentication property="principal.id" var="userId"/>
                            <%--Edit post button start --%>
                            <c:set var="isEditButtonAvailable" value="false"/>
                            <%--  An ability to edit posts for author of this posts --%>
                            <c:if test='${userId == post.userCreated.id && topic.codeReview == null}'>
                                <jtalks:hasPermission targetId="${topic.branch.id}" targetType="BRANCH"
                                                      permission="BranchPermission.EDIT_OWN_POSTS">
                                    <c:set var="isEditButtonAvailable" value="true"/>
                                </jtalks:hasPermission>
                            </c:if>
                            <%--  An ability to edit posts for administrators and branch moderators --%>
                            <c:if test='${userId != post.userCreated.id && topic.codeReview == null}'>
                                <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                                                      permission='BranchPermission.EDIT_OTHERS_POSTS'>

                                    <c:set var="isEditButtonAvailable" value="true"/>
                                </jtalks:hasPermission>
                            </c:if>
                            <c:if test='${topic.codeReview == null}'>
                                <jtalks:hasPermission targetId="${topic.branch.id}" targetType="BRANCH"
                                                      permission="BranchPermission.CLOSE_TOPICS">
                                    <c:set var="hasCloseTopicPermission" value="true"/>
                                </jtalks:hasPermission>
                            </c:if>
                            <c:if test="${isEditButtonAvailable}">
                                <a id="edit_button" href="${edit_url}" rel="${topic.branch.id}"
                                   class="btn btn-mini" title="<spring:message code='label.tips.edit_post'/>">
                                    <i class="icon-edit"></i>
                                    <spring:message code="label.edit"/>
                                </a>
                            </c:if>
                        </sec:authorize>
                            <%--Edit post button end --%>
                            <%--Delete post button start --%>
                        <sec:authorize access="isAuthenticated()">
                            <sec:authentication property="principal.id" var="userId"/>
                            <c:set var="isDeleteButtonAvailable" value="false"/>
                            <c:choose>
                                <%--Controls for the first post, they affect topic--%>
                                <c:when test="${isFirstPost}">
                                    <jtalks:hasPermission targetId="${topic.branch.id}" targetType="BRANCH"
                                                          permission="BranchPermission.DELETE_OWN_POSTS">
                                        <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                                                              permission='BranchPermission.DELETE_OTHERS_POSTS'>
                                            <c:set var="isDeleteButtonAvailable" value="true"/>
                                        </jtalks:hasPermission>
                                    </jtalks:hasPermission>
                                </c:when>
                                <%--Controls for the any other ordinaru post--%>
                                <c:otherwise>
                                    <%--The ability of users to remove their own posts--%>
                                    <c:if test='${userId == post.userCreated.id}'>
                                        <jtalks:hasPermission targetId="${topic.branch.id}" targetType="BRANCH"
                                                              permission="BranchPermission.DELETE_OWN_POSTS">
                                            <c:set var="isDeleteButtonAvailable" value="true"/>
                                        </jtalks:hasPermission>
                                    </c:if>
                                    <%--The ability of users to remove posts of the other users(for moderators and admins)--%>
                                    <c:if test='${userId != post.userCreated.id}'>
                                        <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                                                              permission='BranchPermission.DELETE_OTHERS_POSTS'>
                                            <c:set var="isDeleteButtonAvailable" value="true"/>
                                        </jtalks:hasPermission>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${isDeleteButtonAvailable}">
                                <a href="${delete_url}" class="btn btn-mini btn-danger delete"
                                   title="<spring:message code='label.tips.remove_post'/>"
                                   rel="<spring:message code='${confirm_message}'/>">
                                    <i class="icon-remove icon-white"></i>
                                    <spring:message code="label.delete"/>
                                </a>
                            </c:if>
                        </sec:authorize>
                            <%--Delete post button end --%>
                    </div>


                    <div class="btn-group">
                        <a class="btn btn-mini postLink"
                           title="<spring:message code='label.tips.link_to_post'/>"
                           href="${pageContext.request.contextPath}/posts/${post.id}">
                            <i class="icon-link"></i>
                        </a>
                        <c:if test='${(!topic.closed || hasCloseTopicPermission) && topic.codeReview == null}'>
                            <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                                                  permission='BranchPermission.CREATE_POSTS'>
                                <a class="btn btn-mini" href='javascript:quote(${post.id},${topic.branch.id});'
                                   title="<spring:message code='label.tips.quote_post'/>">
                                    <i class="icon-quote"></i><spring:message code="label.quotation"/>
                                </a>
                            </jtalks:hasPermission>
                        </c:if>
                        <a class="btn btn-mini" href="#"
                           title="<spring:message code='label.tips.up'/>">&#8657;</a>
                    </div>
                </div>
            </td>
        </tr>
        <tr class="post-content-tr">
            <td class="userinfo">
                <div>
                    <spring:message var="onlineTip" code="label.tips.user_online"/>
                    <spring:message var="offlineTip" code="label.tips.user_offline"/>
                    <c:set var="online" value='<i class="icon-online" title="${onlineTip}"></i>'/>
                    <c:set var="offline" value='<i class="icon-offline" title="${offlineTip}"></i>'/>
                    <jtalks:ifContains collection="${usersOnline}" object="${post.userCreated}"
                                       successMessage="${online}" failMessage="${offline}"/>
                    <a class='post-userinfo-username'
                       href="${pageContext.request.contextPath}/users/${post.userCreated.id}"
                       title="<spring:message code='label.tips.view_profile'/>">
                        <c:out value="${post.userCreated.username}"/>
                    </a>
                </div>
             
                   <span class="thumbnail post-userinfo-avatal wraptocenter">
                        <img src="${pageContext.request.contextPath}/users/${post.userCreated.id}/avatar" alt=""/>
                   </span>

                <div>
                    &nbsp;<br/>

                    <div>
                        <spring:message code="label.topic.registered"/>
                        <jtalks:format pattern="dd.MM.yy" value="${post.userCreated.registrationDate}"/>
                    </div>
                    <c:if test="${post.userCreated.location != null}">
                        <div>
                            <spring:message code="label.topic.from_whence"/>
                            <c:out value="${post.userCreated.location}"/>
                        </div>
                    </c:if>
                    <div>
                        <spring:message code="label.topic.message_count"/>
                        <c:out value="${post.userCreated.postCount}"/>
                    </div>
                    <sec:authorize access="isAuthenticated()">
                        <sec:authentication property="principal.id" var="userId"/>
                        <jtalks:hasPermission targetId='${userId}' targetType='USER'
                                              permission='ProfilePermission.SEND_PRIVATE_MESSAGES'>
                            <c:if test='${userId != post.userCreated.id}'>
                                <div>
                                    <a href="${pageContext.request.contextPath}/pm/new/${post.userCreated.id}"
                                       title='<spring:message code="label.pm.send"/>'>
                                        <img src="${pageContext.request.contextPath}/resources/images/message-icon.png"/>
                                    </a>
                                </div>
                            </c:if>
                        </jtalks:hasPermission>
                    </sec:authorize>
                </div>
            </td>
            <td class='post-content-td'>
                <jtalks:postContent text="${post.postContent}"
                                    signature="${post.userCreated.signature}"
                                    modificationDate="${post.modificationDate}"/>
            </td>
        </tr>
        <tr class="post-header">
            <td>
            </td>
            <td class="left-border">
            </td>
        </tr>
    </table>
</div>
<%-- END OF Post --%>
</c:forEach>
</div>

<div class="row-fluid forum-pagination-container">
    <div class="span3">
        <jtalks:topicControls topic="${topic}"/>
        &nbsp; <%-- For proper pagination layout without buttons--%>
    </div>

    <%-- Pagination --%>
    <div class="span9">
        <div class="pagination pull-right forum-pagination">
            <ul>
                <jtalks:pagination uri="${topicId}" page="${postsPage}" pagingEnabled="${pagingEnabled}"/>
            </ul>
        </div>
    </div>
    <%-- END OF Pagination --%>
</div>

<%-- Users --%>
<div id="users-stats" class="well forum-user-stats-container">
    <strong><spring:message code="label.topic.moderators"/></strong>
    <jtalks:moderators moderators="${topic.branch.moderatorsGroup.users}"/>
    <br/>
    <strong><spring:message code="label.branch.now_browsing"/></strong>
    <jtalks:users users="${viewList}" branch="${topic.branch}"/>
</div>
<%-- END OF Users --%>

<%--Fake form to delete posts and topics.
Without it we're likely to get lots of problems simulating HTTP DELETE via JS in a Spring fashion  --%>
<form:form id="deleteForm" method="DELETE"/>
</div>

</body>
