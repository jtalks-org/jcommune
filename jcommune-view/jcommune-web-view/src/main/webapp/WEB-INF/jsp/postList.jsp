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
</head>
<body>
<jsp:include page="../template/topLine.jsp"/>

<c:set var="authenticated" value="${false}"/>
<div class="container">
<!-- Topic header -->
    <div id="branch-header">
        <h3>
          <c:out value="${topic.title}"/>
        </h3>
        <div id="right-block">
        <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
            <span id="subscribe">
                <i class="icon-star"></i>
                <c:choose>
                    <c:when test="${subscribed}">
                        <a id="subscription" class="button top_button"
                           href="${pageContext.request.contextPath}/topics/${topic.id}/unsubscribe"
                           title="<spring:message code="label.unsubscribe"/>">
                            <spring:message code="label.unsubscribe"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a id="subscription" class="button top_button"
                            href="${pageContext.request.contextPath}/topics/${topic.id}/subscribe"
                            title='<spring:message code="label.subscribe"/>'>
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
      <!-- END OF Topic header -->
      
      <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
      
      <!-- Upper pagination -->
      <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span3">
            <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
                <a id="new-topic-btn" class="btn btn-primary" 
                    href="${pageContext.request.contextPath}/posts/new?topicId=${topicId}" 
                    title="Create new post in this topic">
                    <spring:message code="label.answer"/>
                </a>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                <span class="topicId" id="${topic.id}">
                <a id="move_topic" href="#" class="btn" title="Move this topic">
                    <spring:message code="label.topic.move"/>
                </a>
                </span>
            </sec:authorize>
            &nbsp; <%-- For proper pagination layout without buttons--%>
        </div>

        <!-- Pagination -->
        <div class="span9">
            <div class="pagination pull-right forum-pagination" >
                <ul>
                    <jtalks:pagination uri="${topicId}" pagination="${pag}" list="${posts}"/>
                 
                    <c:if test="${pag.maxPages>1}">
                        <c:if test="${pag.pagingEnabled == true}">
                            <li>
                            <a href="?pagingEnabled=false">
                                <spring:message code="label.showAll"/>
                            </a>
                            </li>
                        </c:if>
                    </c:if>
                    <c:if test="${pag.pagingEnabled == false}">
                        <li>
                        <a href="?pagingEnabled=true">
                            <spring:message code="label.showPages"/>
                        </a>
                        </li>
                    </c:if>
                
                    <c:if test="${previousTopic != null}">
                        <li>
                            <a href="${pageContext.request.contextPath}/topics/${previousTopic.id}"
                                title="<spring:message code='label.topic.previous'/>">
                                <spring:message code='label.topic.previous'/>
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${nextTopic != null}">
                        <li>
                            <a href="${pageContext.request.contextPath}/topics/${nextTopic.id}"
                                title="<spring:message code='label.topic.next'/>">
                                <spring:message code='label.topic.next'/>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
        <!-- END OF Pagination -->
        
      </div>
      <!-- END OF Upper pagination -->

    <!-- Include poll row. -->
    <jsp:include page="../template/pollRow.jsp"/>
    
    <div>
    <!-- List of posts. -->
    <c:forEach var="post" items="${list}" varStatus="i">
        <!-- Post -->
        <div class="post">
            <table class="table table-striped table-bordered table-condensed">
            <tr>
                <td class="post-date">    
                    <i class="icon-calendar"></i>
                    <jtalks:format value="${post.creationDate}"/>
                </td>
                <td class="top-buttons">
                    &nbsp;
                    <div class="btn-toolbar post-btn-toolbar">
                     <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                         <c:choose>
                             <c:when test="${pag.page == 1 && i.index == 0}">
                                 <%-- first post - urls to delete & edit topic --%>
                                 <c:set var="delete_url"
                                        value="${pageContext.request.contextPath}/topics/${topic.id}"/>
                                 <c:set var="edit_url"
                                        value="${pageContext.request.contextPath}/topics/${topic.id}/edit?branchId=${branchId}"/>
                                 <c:set var="confirm_message" value="label.deleteTopicConfirmation"/>
                             </c:when>
                             <c:otherwise>
                                 <%-- url to delete & edit post --%>
                                 <c:set var="delete_url"
                                        value="${pageContext.request.contextPath}/posts/${post.id}"/>
                                 <c:set var="edit_url"
                                        value="${pageContext.request.contextPath}/posts/${post.id}/edit?topicId=${topic.id}"/>
                                 <c:set var="confirm_message" value="label.deletePostConfirmation"/>
                             </c:otherwise>
                         </c:choose>
                         
                         <div class="btn-group">
                             <a id="edit_button" href="${edit_url}" rel="${branchId}" 
                                 class="btn btn-mini" title="Edit this post">
                                 <i class="icon-edit"></i>
                                 <spring:message code="label.edit"/>
                             </a>
                             <a href="${delete_url}" class="btn btn-mini btn-danger delete" 
                                 title="Remove this post" 
                                 rel="<spring:message code="${confirm_message}"/>">
                                 <i class="icon-remove icon-white"></i>
                                 <spring:message code="label.delete"/>
                             </a>
                         </div>
                     </sec:accesscontrollist>
             
                     <div class="btn-group">
                         <a class="btn btn-mini postLink" title="Link to this post" rel="${post.id}"
                            href="${pageContext.request.contextPath}/posts/${post.id}">
                             <i class="icon-link"></i>
                         </a>
                         <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
                             <a class="btn btn-mini" href='javascript:quote(${post.id});' 
                                 title="Quote this post">
                                 <i class="icon-quote"></i><spring:message code="label.quotation"/>
                             </a>
                         </sec:authorize>
                         <a class="btn btn-mini" href="#" name="${post.id}">&#8657</a>
                     </div>
                    </div>
                </td>
            </tr>
            <tr class="post-content-tr">
                <td class="userinfo">
                    <div>
                        <c:set var="online" value='<i class="icon-online" title="User online"></i>'/>
                        <c:set var="offline" value='<i class="icon-offline" title="User offline"></i>'/>
                        <jtalks:ifContains collection="${usersOnline}" object="${post.userCreated}"
                                       successMessage="${online}" failMessage="${offline}"/>
                        <a class='post-userinfo-username' 
                                href="${pageContext.request.contextPath}/users/${post.userCreated.encodedUsername}">
                            <c:out value="${post.userCreated.username}"/>                   
                        </a>
                    </div>
              
                    <span class="thumbnail post-userinfo-avatal">
                        <div class="wraptocenter"><span></span>
                            <img src="${pageContext.request.contextPath}/${post.userCreated.encodedUsername}/avatar" alt="" />
                        </div>
                    </span>

                    <div>
                        &nbsp;<br />
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
                        <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
                            <c:if test='${encodedUsername != post.userCreated.encodedUsername}'>
                                <div>
                                    <a href="${pageContext.request.contextPath}/pm/new/${post.userCreated.encodedUsername}" 
                                        title='<spring:message code="label.pm.send"/>'>
                                        <img src="${pageContext.request.contextPath}/resources/images/message-icon.png"/>
                                    </a>
                                </div>
                            </c:if>
                        </sec:authorize>
                    </div>
                </td>
                <td class='post-content-td'>
                    <div>
                        <jtalks:bb2html bbCode="${post.postContent}"/>
                        <c:if test="${post.modificationDate!=null}">
                            <br/><br/><br/>
                            <spring:message code="label.modify"/>
                            <jtalks:format value="${post.modificationDate}"/>
                        </c:if>
                    </div>
                    <div align="left">
                        ${post.userCreated.renderedSignature}
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                </td>
                <td style="border-left: 0px;">
                </td>
            </tr>
            </table>
        </div>  
        <!-- END OF Post -->
    </c:forEach>
</div>

<div class="row-fluid forum-pagination-container">
    <div class="span3">
        <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
            <a id="new-topic-btn" class="btn btn-primary" 
                    href="${pageContext.request.contextPath}/posts/new?topicId=${topicId}" 
                    title="Create new post in this topic">
                    <spring:message code="label.answer"/>
            </a>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                <span class="topicId" id="${topic.id}">
                    <a id="move_topic" href="#" class="btn" title="Move this topic">
                        <spring:message code="label.topic.move"/>
                    </a>
                </span>
            </sec:authorize>
            &nbsp; <%-- For proper pagination layout without buttons--%>
        </div>
        
        <!-- Pagination -->
        <div class="span9">
            <div class="pagination pull-right forum-pagination">
                <ul>
                    <jtalks:pagination uri="${topicId}" pagination="${pag}" list="${posts}"/>
                 
                    <c:if test="${pag.maxPages>1}">
                        <c:if test="${pag.pagingEnabled == true}">
                            <li>
                                <a href="?pagingEnabled=false">
                                    <spring:message code="label.showAll"/>
                                </a>
                            </li>
                        </c:if>
                    </c:if>
                    <c:if test="${pag.pagingEnabled == false}">
                        <li>
                            <a href="?pagingEnabled=true">
                                <spring:message code="label.showPages"/>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
        <!-- END OF Pagination -->
    </div>

    <!-- Users -->
    <div id="users-stats" class="well forum-user-stats-container">
        <strong><spring:message code="label.topic.moderators"/></strong> 
        <a href="#" title="Click to view profile">andreyko</a> 
        <a href="#" class="label label-success" title="Click to view profile">Староверъ</a>
        <a href="#" class="label label-important" title="Click to view profile">admin</a>
        <br />
        <c:if test="${!(empty viewList)}">
            <strong><spring:message code="label.branch.now_browsing"/></strong> 
        </c:if>
        <c:forEach var="innerUser" items="${viewList}">
            <c:choose>
                <c:when test="${innerUser.role=='ROLE_ADMIN'}">
                        <c:set var='labelClass' value='label label-important'/>
                    </c:when>
                    <c:otherwise>
                        <c:set var='labelClass' value=''/>
                </c:otherwise>
            </c:choose>
            <a href="${pageContext.request.contextPath}/users/${innerUser.encodedUsername}" 
                title="Click to view profile"
                class='${labelClass}'>
                <c:out value="${innerUser.username}"/>
            </a>
        </c:forEach>
     </div>
     <!-- END OF Users -->

<%--Fake form to delete posts and topics.
Without it we're likely to get lots of problems simulating HTTP DELETE via JS in a Spring fashion  --%>
    <form:form id="deleteForm" method="DELETE"/>
</div>
    
</body>
