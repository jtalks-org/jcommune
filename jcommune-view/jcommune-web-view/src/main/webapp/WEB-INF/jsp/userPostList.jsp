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


<html>
<head>
    <title><spring:message code="label.postListOfUser"/> <c:out value="${user.username}"/></title>
</head>
<body>
<c:set var="authenticated" value="${false}"/>
<h1><spring:message code="label.postListOfUser"/> ${user.username}</h1>

<div class="wrap topic_page">
<jsp:include page="../template/topLine.jsp"/>
<!-- Начало всех форумов -->
<div class="all_forums">
<h2><a class="heading" href="#"><c:out value="${topic.title}"/></a></h2>

    <div class="forum_top_right_link">

        <jtalks:display uri="" pagination="${pag}" numberLink="3" list="${posts}">
        <nobr>
            <span class="nav_bottom" >
                <c:if test="${pag.maxPages>1}">
                <spring:message code="label.onPage"/>
                </c:if>
            </jtalks:display>
            </span>
        </nobr>
        </div>
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <c:set var="authenticated" value="${true}"/>
        </sec:authorize>
        <c:if test="${authenticated==true}">
            <a class="button"
               href="${pageContext.request.contextPath}/users/${user.encodedUsername}">
                <spring:message code="label.backToProfile"/>
            </a>
        </c:if>
        <br>
        &nbsp; &nbsp; &nbsp;

        <!-- Начало группы форумов -->
        <div class="forum_header_table"> <!-- Шапка топика -->
            <div class="forum_header">
                <span class="forum_header_userinfo"><spring:message code="label.info"/></span>
                <span class="forum_header_topic"><spring:message code="label.topic.header.message"/></span>
            </div>
        </div>
        <ul class="forum_table"> <!-- Список сообщений -->
            <jtalks:display uri="${topicId}" pagination="${pag}" numberLink="3" list="${posts}">
            <c:forEach var="post" items="${list}" varStatus="i">
                <li class="forum_row"> <!-- Сообщение -->
                    <div class="forum_userinfo">
                        <div class="user_info">Branch</div>
                        <a class="username"
                           href="${pageContext.request.contextPath}/branches/${post.topic.branch.id}">
                            <c:out value="${post.topic.branch.name}"/></a>
                        <br>
                        <div class="user_info">Topic</div>
                        <a class="username"
                           href="${pageContext.request.contextPath}/topics/${post.topic.id}">
                            <c:out value="${post.topic.title}"/></a>
                    </div>
                    <div class="forum_message_cell">
                        <div class="post_details">
                            <a class="button" href="javascript:copyLink(${post.id})">
                                <spring:message code="label.link"/>
                            </a>
                            <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                                <c:choose>
                                    <c:when test="${page == 1 && i.index == 0}">

                                        <c:set var="delete_url"
                                               value="${pageContext.request.contextPath}/topics/${topicId}/delete?branchId=${branchId}"/>
                                    </c:when>
                                    <c:otherwise>

                                        <c:set var="delete_url"
                                               value="${pageContext.request.contextPath}/posts/${post.id}/delete?topicId=${topicId}"/>
                                    </c:otherwise>
                                </c:choose>
                                <a class="button" href="${delete_url}"><spring:message code="label.delete"/></a>
                            </sec:accesscontrollist>


                            <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                                <c:choose>
                                    <c:when test="${page == 1 && i.index == 0}">

                                        <c:set var="edit_url"
                                               value="${pageContext.request.contextPath}/topics/${topicId}/edit?branchId=${branchId}"/>
                                    </c:when>
                                    <c:otherwise>

                                        <c:set var="edit_url"
                                               value="${pageContext.request.contextPath}/posts/${post.id}/edit?topicId=${topicId}"/>
                                    </c:otherwise>
                                </c:choose>
                                <a class="button" href="${edit_url}"><spring:message code="label.edit"/></a>
                            </sec:accesscontrollist>
                            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                                <a class="button" href="#"><spring:message
                                        code="label.quotation"/></a>
                            </sec:authorize>
                            <a name="${post.id}" href="#${post.id}"><spring:message
                                    code="label.added"/>&nbsp;<jtalks:format
                                    value="${post.creationDate}"/></a>
                        </div>
                        <p class="forum_message_cell_text">
                            <c:out value="${post.postContent}"/>
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
                                <c:out value="${post.userCreated.signature}"/>
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

        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <a class="button"
               href="${pageContext.request.contextPath}/users/${user.encodedUsername}">
                <spring:message code="label.backToProfile"/>
            </a>
        </sec:authorize>
        <c:if test="${pag.maxPages>1}">
            <c:if test="${pag.pagingEnabled==true}">
                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                    <a class="button"
                       href="?pagingEnabled=false"><spring:message code="label.showAll"/></a>
                    &nbsp; &nbsp; &nbsp;
                </sec:authorize>
            </c:if>
        </c:if>
        <c:if test="${pag.pagingEnabled == false}">
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <a class="button"
                   href="?pagingEnabled=true"><spring:message code="label.showPages"/></a>
                &nbsp; &nbsp; &nbsp;
            </sec:authorize>
        </c:if>

        &nbsp; &nbsp; &nbsp;

    </div>
    <!-- Конец всех форумов -->
    <div class="footer_buffer"></div>
</div>
</body>
</html>