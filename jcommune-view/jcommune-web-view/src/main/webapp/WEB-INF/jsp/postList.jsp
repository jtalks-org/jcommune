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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <title><c:out value="${topic.title}"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/utils.js"
            type="text/javascript"></script>
</head>
<body>
<c:set var="authenticated" value="${false}"/>
<h1>JTalks</h1>

<div class="wrap topic_page">
<jsp:include page="../template/topLine.jsp"/>
<!-- Начало всех форумов -->
<div class="all_forums">
<h2><a class="heading" href="#"><c:out value="${topic.title}"/></a></h2>
<span class="nav_bottom">
<c:if test="${previousTopic != null}">
    <a href="${pageContext.request.contextPath}/topics/${previousTopic.id}">
        <spring:message code="label.topic.previous"/></a>
</c:if>
    &nbsp;
    <c:if test="${nextTopic != null}">
        <a href="${pageContext.request.contextPath}/topics/${nextTopic.id}">
            <spring:message code="label.topic.next"/></a>
    </c:if>
</span>
<br>

<div class="forum_top_right_link">

    <jtalks:display uri="${topicId}" pagination="${pag}" numberLink="3" list="${posts}">
    <nobr>
            <span class="nav_bottom">
                <c:if test="${pag.maxPages>1}">
                    <spring:message code="label.onPage"/>
                </c:if>
            </jtalks:display>
            </span>
    </nobr>
</div>
<a class="button top_button" href="${pageContext.request.contextPath}/branches/${branchId}">
    <spring:message code="label.back"/>
</a>
<sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
    <a class="button top_button" href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}">
        <spring:message code="label.topic.new_topic"/></a>
    <a class="button top_button" href="${pageContext.request.contextPath}/posts/new?topicId=${topicId}">
        <spring:message code="label.answer"/></a>
    <c:set var="authenticated" value="${true}"/>
</sec:authorize>
<c:if test="${authenticated==false}">
    <a class="button top_button disabled" href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}">
        <spring:message code="label.topic.new_topic"/></a>
    <a class="button top_button disabled"
       href="${pageContext.request.contextPath}/posts/new?topicId=${topicId}">
        <spring:message code="label.answer"/>
    </a>
</c:if>
&nbsp; &nbsp; &nbsp;

<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
<br>
<!-- Начало группы форумов -->
<div class="forum_header_table"> <!-- Шапка топика -->
    <div class="forum_header">
        <span class="forum_header_userinfo"><spring:message code="label.topic.header.author"/></span>
        <span class="forum_header_topic"><spring:message code="label.topic.header.message"/></span>
    </div>
</div>
<ul class="forum_table"> <!-- Список сообщений -->
    <jtalks:display uri="${topicId}" pagination="${pag}" numberLink="3" list="${posts}">
    <c:forEach var="post" items="${list}" varStatus="i">
        <li class="forum_row"> <!-- Сообщение -->
            <div class="forum_userinfo">
                <a class="username"
                   href="${pageContext.request.contextPath}/users/${post.userCreated.encodedUsername}">
                    <c:out value="${post.userCreated.username}"/></a>

                <div class="status"><spring:message code="label.topic.online_users"/></div>

                <c:if test="${post.userCreated.avatar != null}">
                    <img src="${pageContext.request.contextPath}/${post.userCreated.encodedUsername}/avatar"
                         alt="Аватар" class="avatar"/>
                </c:if>

                <br/>

                <div class="user_misc_info">
                    <spring:message code="label.topic.registered"/> 13.04.09 <br/>
                    <spring:message code="label.topic.message_count"/> 661 <br/>
                    <spring:message code="label.topic.from_whence"/> good ol' 60s
                </div>
            </div>
            <div class="forum_message_cell">
                <div class="post_details">
                    <a class="button" href="javascript:createAndPromptPostLink(${post.id})">
                        <spring:message code="label.link"/>
                    </a>
                    <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                        <c:choose>
                            <c:when test="${pag.page == 1 && i.index == 0}">
                                <%-- first post - url to delete topic --%>
                                <c:set var="delete_url"
                                       value="${pageContext.request.contextPath}/topics/${topic.id}/delete?branchId=${branchId}"/>
                            </c:when>
                            <c:otherwise>
                                <%-- url to delete post --%>
                                <c:set var="delete_url"
                                       value="${pageContext.request.contextPath}/posts/${post.id}/delete?topicId=${topic.id}"/>
                            </c:otherwise>
                        </c:choose>
                        <a class="button" href="${delete_url}"><spring:message code="label.delete"/></a>
                    </sec:accesscontrollist>


                    <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                        <c:choose>
                            <c:when test="${pag.page == 1 && i.index == 0}">
                                <%-- first post - url to edit topic --%>
                                <c:set var="edit_url"
                                       value="${pageContext.request.contextPath}/topics/${topic.id}/edit?branchId=${branchId}"/>
                            </c:when>
                            <c:otherwise>
                                <%-- url to edit post --%>
                                <c:set var="edit_url"
                                       value="${pageContext.request.contextPath}/posts/${post.id}/edit?topicId=${topic.id}"/>
                            </c:otherwise>
                        </c:choose>
                        <a class="button" href="${edit_url}"><spring:message code="label.edit"/></a>
                    </sec:accesscontrollist>
                    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                        <a class="button" href="javascript:
                                document.getElementById('selection${post.id}').value = getSelectedText(${post.id});
                                document.forms['quoteForm${post.id}'].submit();">
                            <spring:message code="label.quotation"/>
                        </a>

                        <form action="${pageContext.request.contextPath}/posts/${post.id}/quote"
                              method="post" id='quoteForm${post.id}'>
                            <input name='selection' id='selection${post.id}' type='hidden'/>
                        </form>
                    </sec:authorize>
                    <a name="${post.id}" href="#${post.id}">
                        <spring:message code="label.added"/>&nbsp;
                        <jtalks:format value="${post.creationDate}"/>
                    </a>
                </div>
                <p class="forum_message_cell_text">
                    <span id='${post.id}'><jtalks:bb2html bbCode="${post.postContent}"/></span>
                    <br/><br/><br/>
                    <c:if test="${post.modificationDate!=null}">
                        <spring:message code="label.modify"/>
                        <jtalks:format value="${post.modificationDate}"/>
                    </c:if>
                </p>
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
<nobr>
    <c:if test="${pag.maxPages>1}">
        <span class="nav_bottom"><spring:message code="label.onPage"/>
        </c:if>
    </jtalks:display>
        </span>
</nobr>

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
<c:if test="${authenticated==false}">
    <a class="button top_button disabled" href="${pageContext.request.contextPath}/topics/new?branchId=${branchId}">
        <spring:message code="label.topic.new_topic"/></a>
    <a class="button top_button disabled"
       href="${pageContext.request.contextPath}/posts/new?topicId=${topic.id}">
        <spring:message code="label.answer"/>
    </a>
</c:if>
<c:if test="${pag.maxPages>1}">
    <c:if test="${pag.pagingEnabled==true}">
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <a class="button" href="?pagingEnabled=false"><spring:message code="label.showAll"/></a>
            &nbsp; &nbsp; &nbsp;
        </sec:authorize>
    </c:if>
</c:if>
<c:if test="${pag.pagingEnabled == false}">
    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
        <a class="button" href="?pagingEnabled=true"><spring:message code="label.showPages"/></a>
        &nbsp; &nbsp; &nbsp;
    </sec:authorize>
</c:if>

&nbsp; &nbsp; &nbsp;

<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

<div class="forum_misc_info">
    <spring:message code="label.page"/> <c:out value="${pag.page}"/> <spring:message code="label.of"/> <c:out
        value="${pag.maxPages}"/>
    <br/>
    <spring:message code="label.topic.moderators"/>
    <ul class="users_list">
        <li><a href="#">andreyko</a>,</li>
        <li><a href="#">Староверъ</a>,</li>
        <li><a href="#">Вася</a>.</li>
    </ul>
    <br/>
    <spring:message code="label.topic.now_browsing"/>
    <c:forEach var="innerUser" items="${viewList}">
          <a href="${pageContext.request.contextPath}/users/${innerUser}">
               <c:out value="${innerUser}"/>
          </a>
    </c:forEach>
</div>
</div>
<!-- Конец всех форумов -->
<div class="footer_buffer"></div>
<!-- Несемантичный буфер для прибития подвала -->
</div>
</body>