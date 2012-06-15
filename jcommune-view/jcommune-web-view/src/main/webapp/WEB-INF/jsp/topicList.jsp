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
    <title><spring:message code="label.section.jtalks_forum"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/subscription.js"
            type="text/javascript"></script>
</head>
<body>
    <jsp:include page="../template/topLine.jsp"/>
    
<div class="container">
    <!-- Branch header -->
    <div id="branch-header">
        <h2><c:out value="${branch.name}"/></h2>
        <div id="right-block">
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
             <span id="mark-all-viewed">
                 <i class="icon-check"></i>
                 <a href="${pageContext.request.contextPath}/branches/${branch.id}/markread">
                     <spring:message code="label.mark_all_topics"/>
                 </a>
             </span>
             
             <span id="subscribe">
                 <i class="icon-star"></i>
                 <c:choose>
                     <c:when test="${subscribed}">
                         <a id="subscription" class="button top_button"
                             href="${pageContext.request.contextPath}/branches/${branch.id}/unsubscribe"
                             title="<spring:message code="label.unsubscribe"/>">
                             <spring:message code="label.unsubscribe"/>
                         </a>
                     </c:when>
                     <c:otherwise>
                         <a id="subscription" class="button top_button"
                             href="${pageContext.request.contextPath}/branches/${branch.id}/subscribe"
                             title='<spring:message code="label.subscribe"/>'>
                             <spring:message code="label.subscribe"/>
                         </a>
                     </c:otherwise>
                 </c:choose>
             </span>
            </sec:authorize>
        </div>
        <span style="display: inline-block; "></span>
    </div>
    <!-- END OF Branch header -->
        
    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
        
    <!-- Upper pagination -->
    <div class="row-fluid upper-pagination forum-pagination-container">

        <div class="span2">
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <a id='new-topic-btn' class="btn btn-primary" 
                    href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}" 
                    title="<spring:message code="label.addtopic"/>">
                    <spring:message code="label.addtopic"/>
                </a>
            </sec:authorize>
            &nbsp; <%-- For proper pagination layout without buttons--%>
       </div>
        
        

        <div class="span10">
          <div class="pagination pull-right forum-pagination">
            <ul>
                 <jtalks:pagination uri="${branch.id}" pagination="${pagination}" list="${topics}"/>
                 
                 <li>
                    <c:if test="${pagination.maxPages>1}">
                        <c:if test="${pagination.pagingEnabled == true}">
                            <a class="button"
                               href="?pagingEnabled=false"><spring:message code="label.showAll"/></a>
                        </c:if>
                    </c:if>
                    <c:if test="${pagination.pagingEnabled == false}">
                        <a class="button"
                           href="?pagingEnabled=true"><spring:message code="label.showPages"/>
                        </a>
                    </c:if>
                </li>
            </ul>
          </div>
        </div>

      </div>
      <!-- END OF Upper pagination -->
     
    <!-- Topics table -->
    <table id="topics-table" cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered">
        <c:choose>
            <c:when test="${!(empty topics)}">
                <thead>
                    <tr>
                        <th class="status-col"></th>
                        <th><spring:message code="label.branch.header.topics"/></th>
                        <th class="author-col"><spring:message code="label.branch.header.author"/></th>
                        <th class="posts-views forum-posts-view-header"><spring:message code="label.branch.header.posts_views"/></th>
                        <th class="latest-by forum-latest-by-header"><spring:message code="label.branch.header.lastMessage"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="topic" items="${list}">
                        <!-- Topic row -->
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
                                    <%--Some topic types should have a special prefix when displayed--%>
                                    <c:when test="${topic.announcement=='true'}">
                                        <span class="sticky"><spring:message code="label.marked_as_announcement"/> </span>
                                    </c:when>
                                    <c:when test="${topic.sticked=='true'}">
                                        <span class="sticky"><spring:message code="label.marked_as_sticked"/></span>
                                    </c:when>
                                </c:choose>
                                <c:if test="${topic.hasPoll}">
                                    <a style="color: red;"
                                       href="${pageContext.request.contextPath}/topics/${topic.id}">
                                        [POLL]</a>
                                </c:if>
                                <a href="${pageContext.request.contextPath}/topics/${topic.id}">
                                    <c:out value="${topic.title}"/>
                                </a>
                                <br />
                                <sub class="created-by">by 
                                    <a href='${pageContext.request.contextPath}/users/${topic.topicStarter.id}"'>
                                        <c:out value="${topic.topicStarter.username}"/>
                                    </a>
                                </sub>            
                            </td>
                            <td class="author-col">
                                <a href='${pageContext.request.contextPath}/users/${topic.topicStarter.id}'>
                                    <c:out value="${topic.topicStarter.username}"/>
                                </a>
                            </td>
                            <td class="posts-views">
                                <spring:message code="label.section.header.messages"/>:
                                <span class='test-posts-count'><c:out value="${topic.postCount}"/></span><br />
                                <spring:message code="label.branch.header.views"/>:
                                <span class='test-views'><c:out value="${topic.views}"/></span>
                            </td>
                            <td class="latest-by">
                                <i class="icon-calendar"></i>
                                <a class="date" href="${pageContext.request.contextPath}/posts/${topic.lastPost.id}">
                                    <jtalks:format value="${topic.lastPost.creationDate}"/>
                                </a>
                                <p>by 
                                    <a href="${pageContext.request.contextPath}/users/${topic.lastPost.userCreated.id}">
                                        <c:out value="${topic.lastPost.userCreated.username}"/>
                                    </a>
                                </p>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </c:when>
            <c:otherwise>
                <tbody><tr><td>
                    <spring:message code="label.branch.empty"/>
                </td></tr></tbody>
            </c:otherwise>
        </c:choose>
    </table>
    <!-- END OF Topics table -->
    
    <!-- Bottom pagination -->
    <div class="row-fluid upper-pagination forum-pagination-container">

        <div class="span2">
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <a id='new-topic-btn' class="btn btn-primary"
                    href="${pageContext.request.contextPath}/topics/new?branchId=${branch.id}" 
                    title="<spring:message code="label.addtopic"/>">
                    <spring:message code="label.addtopic"/>
                </a>
            </sec:authorize>
            &nbsp; <%-- For proper pagination layout without buttons--%>
        </div>
       

        <div class="span10">
            <div class="pagination pull-right forum-pagination">
                <ul>
                    <jtalks:pagination uri="${branch.id}" pagination="${pagination}" list="${topics}"/>
                 
                    <li>
                        <c:if test="${pagination.maxPages>1}">
                            <c:if test="${pagination.pagingEnabled == true}">
                                <a class="button" href="?pagingEnabled=false">
                                    <spring:message code="label.showAll"/>
                                </a>
                            </c:if>
                        </c:if>
                        <c:if test="${pagination.pagingEnabled == false}">
                            <a class="button" href="?pagingEnabled=true">
                                <spring:message code="label.showPages"/>
                            </a>
                        </c:if>
                    </li>
                </ul>
            </div>
        </div>

    </div>
    <!-- END OF Bottom pagination -->
    
        
    <!-- Users -->
    <div id="users-stats" class="well forum-user-stats-container">
        <strong><spring:message code="label.topic.moderators"/></strong> 
        <a href="#" title="<spring:message code='label.tips.view_profile'/>">andreyko</a> 
        <a href="#" class="label label-success" title="<spring:message code='label.tips.view_profile'/>">Староверъ</a>
        <a href="#" class="label label-important" title="<spring:message code='label.tips.view_profile'/>">admin</a>
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
            <a href="${pageContext.request.contextPath}/users/${innerUser.id}"
                title="<spring:message code='label.tips.view_profile'/>"
                class='${labelClass}'>
                <c:out value="${innerUser.username}"/>
            </a>
        </c:forEach>
    </div>
    <!-- END OF Users -->
      
</div>
</body>