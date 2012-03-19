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
</head>
<body>
<div class="wrap topic_page">
<jsp:include page="../template/topLine.jsp"/>
<jsp:include page="../template/logo.jsp"/>
<c:set var="authenticated" value="${false}"/>
<div class="all_forums">
<h2 class="heading break_word"><c:out value="${topic.title}"/></h2>

<div class="forum_info_top">
    <div>
        <div> <!-- top left -->

        </div>
        <div> <!-- top right -->

        </div>
    </div>
    <div class="info_top_lower">
        <div> <!-- bottom left -->
            <a class="button top_button" href="${pageContext.request.contextPath}/branches/${branchId}">
                <spring:message code="label.back"/>
            </a>
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <c:choose>
                    <c:when test="${subscribed}">
                        <a id="subscription" class="button top_button"
                           href="${pageContext.request.contextPath}/topics/${topic.id}/unsubscribe">
                            <spring:message code="label.unsubscribe"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a id="subscription" class="button top_button"
                           href="${pageContext.request.contextPath}/topics/${topic.id}/subscribe">
                            <spring:message code="label.subscribe"/>
                        </a>
                    </c:otherwise>
                </c:choose>
                <a class="button top_button" href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}">
                    <spring:message code="label.topic.new_topic"/></a>
                <a class="button top_button" href="${pageContext.request.contextPath}/posts/new?topicId=${topicId}">
                    <spring:message code="label.answer"/></a>
                <c:set var="authenticated" value="${true}"/>
            </sec:authorize>
            <c:if test="${pag.maxPages>1}">
                <c:if test="${pag.pagingEnabled==true}">
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="button" href="?pagingEnabled=false"><spring:message code="label.showAll"/></a>
                    </sec:authorize>
                </c:if>
            </c:if>
            <c:if test="${pag.pagingEnabled == false}">
                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                    <a class="button" href="?pagingEnabled=true"><spring:message code="label.showPages"/></a>
                </sec:authorize>
            </c:if>
            <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
        </div>
        <div class="info_top_lower_right"> <!-- bottom right -->
            <c:if test="${previousTopic != null}">
                <a class="button but_arrow arrow_left"
                   href="${pageContext.request.contextPath}/topics/${previousTopic.id}"
                   title="<spring:message code='label.topic.previous'/>">
                </a>
            </c:if>
            <c:if test="${nextTopic != null}">
                <a class="button but_arrow"
                   href="${pageContext.request.contextPath}/topics/${nextTopic.id}"
                   title="<spring:message code='label.topic.next'/>">
                </a>
            </c:if>
                <span class="nav_top">
                    <jtalks:pagination uri="${topicId}" pagination="${pag}" list="${posts}"/>
                </span>
        </div>
    </div>
</div>
<div class="forum_header_table">
    <div class="forum_header">
        <span class="forum_header_userinfo"><spring:message code="label.topic.header.author"/></span>
        <span class="forum_header_topic"><spring:message code="label.topic.header.message"/></span>
    </div>
</div>
<ul class="forum_table">
    <c:forEach var="post" items="${list}" varStatus="i">
        <li class="forum_row">
            <div class="forum_userinfo">
                <a class="username"
                   href="${pageContext.request.contextPath}/users/${post.userCreated.encodedUsername}">
                    <c:out value="${post.userCreated.username}"/>
                </a>

                <div class="status">
                    <spring:message var="online" code="label.topic.online_users"/>
                    <spring:message var="offline" code="label.topic.offline_users"/>
                    <jtalks:ifContains collection="${usersOnline}" object="${post.userCreated}"
                                       successMessage="${online}" failMessage="${offline}"/>
                </div>
                <img src="${pageContext.request.contextPath}/${post.userCreated.encodedUsername}/avatar"
                     class="avatar"/>
                <br/>

                <div class="user_misc_info">
                    <span class="status"><spring:message code="label.topic.registered"/></span>
                    <jtalks:format pattern="dd.MM.yy" value="${post.userCreated.registrationDate}"/><br/>
                    <span class="status"><spring:message code="label.topic.message_count"/></span>
                    <c:out value="${post.userCreated.postCount}"/><br/>
                    <c:if test="${post.userCreated.location != null}">
                        <span class="status"><spring:message code="label.topic.from_whence"/></span>
                        <span class="break_word"><c:out value="${post.userCreated.location}"/></span>
                    </c:if>
                </div>
            </div>
            <div class="forum_message_cell">
                <div class="post_details">
                    <a class="button" name="${post.id}" href="#">&#8657;</a>
                    <a class="button postLink" rel="${post.id}">
                        <spring:message code="label.link"/>
                    </a>
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
                        <a class="button delete" href="${delete_url}"
                           rel="<spring:message code="${confirm_message}"/>">
                            <spring:message code="label.delete"/>
                        </a>
                        <a class="button" href="${edit_url}"><spring:message code="label.edit"/></a>
                    </sec:accesscontrollist>
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="button quote" href="javascript:quote(${post.id});">
                            <spring:message code="label.quotation"/>
                        </a>
                    </sec:authorize>
                    <c:if test="${i.index == 0}">
                        <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                            <div class="topicId" id="${topic.id}">
                                <a class="button" id="move_topic" href="#"><spring:message code="label.topic.move"/></a>
                            </div>
                        </sec:authorize>
                    </c:if>
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <c:if test="${lastReadPost == null || post.postIndexInTopic > lastReadPost}">
                            <span style="color: red;">[NEW]</span>
                        </c:if>
                    </sec:authorize>
                    <span name="${post.id}">
                        <spring:message code="label.added"/>&nbsp;
                        <jtalks:format value="${post.creationDate}"/>
                    </span>
                </div>
                <div class="forum_message_cell_text">
                    <jtalks:bb2html bbCode="${post.postContent}"/>
                    <br/><br/><br/>
                    <c:if test="${post.modificationDate!=null}">
                        <spring:message code="label.modify"/>
                        <jtalks:format value="${post.modificationDate}"/>
                    </c:if>
                </div>
                <c:if test="${post.userCreated.signature!=null}">
                    <div class="signature">
                        -------------------------
                        <br/>
                        <span><c:out value="${post.userCreated.signature}"/></span>
                    </div>
                </c:if>
            </div>
        </li>
    </c:forEach>
</ul>
<div class="forum_info_bottom">
    <div>
        <div>
            <a class="button" href="${pageContext.request.contextPath}/branches/${branchId}">
                <spring:message code="label.back"/>
            </a>
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <a class="button top_button" href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}">
                    <spring:message code="label.topic.new_topic"/></a>
                <a class="button top_button" href="${pageContext.request.contextPath}/posts/new?topicId=${topic.id}">
                    <spring:message code="label.answer"/></a>
                <c:set var="authenticated" value="${true}"/>
            </sec:authorize>
            <c:if test="${pag.maxPages>1}">
                <c:if test="${pag.pagingEnabled==true}">
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="button" href="?pagingEnabled=false"><spring:message code="label.showAll"/></a>
                    </sec:authorize>
                </c:if>
            </c:if>
            <c:if test="${pag.pagingEnabled == false}">
                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                    <a class="button" href="?pagingEnabled=true"><spring:message code="label.showPages"/></a>
                </sec:authorize>
            </c:if>
            <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
        </div>
        <div>
            <span class="nav_bottom">
                <jtalks:pagination uri="${topicId}" pagination="${pag}" list="${posts}"/>
            </span>
        </div>
    </div>
</div>
<br/>

<div class="forum_misc_info">
    <spring:message code="label.topic.moderators"/>
    <ul class="users_list">
        <li><a href="#">andreyko</a>,</li>
        <li><a href="#">Староверъ</a>,</li>
        <li><a href="#">Вася</a>.</li>
    </ul>
    <br/>
    <c:if test="${!(empty viewList)}">
        <spring:message code="label.topic.now_browsing"/>
    </c:if>
    <c:forEach var="innerUser" items="${viewList}">
        <a href="${pageContext.request.contextPath}/users/${innerUser.encodedUsername}">
            <c:out value="${innerUser.username}"/>
        </a>
        &nbsp;&nbsp;
    </c:forEach>
    <%--Fake form to delete posts and topics.
Without it we're likely to get lots of problems simulating HTTP DELETE via JS in a Spring fashion  --%>
    <form:form id="deleteForm" method="DELETE"/>
</div>
</div>
<div class="footer_buffer"></div>
</div>
</body>
