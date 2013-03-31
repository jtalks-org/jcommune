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
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<head>
    <meta name="description" content="<c:out value="${section.name}"/>">
    <title><c:out value="${section.name}"/> - <c:out value="${cmpDescription}"/></title>
</head>
<body>

<div class="container">
    <%-- Section header --%>
    <div id="branch-header">
        <h2><c:out value="${section.name}"/></h2>
        <span class="inline-block"></span>
    </div>
    <%-- END OF Branch header --%>

    <%-- Branches table --%>
    <table id="topics-table" class="table table-row table-bordered">
        <c:choose>
            <c:when test="${!(empty section.branches)}">
                <thead>
                <tr>
                        <%--<sec:authorize access="isAuthenticated()">--%>
                        <%--<th class="status-col"></th>--%>
                        <%--</sec:authorize>--%>
                    <th><spring:message code="label.section.header.branches"/></th>
                    <th class="topics-posts forum-posts-view-header shrink-to-fit">
                        <spring:message code="label.branch.header.topics_posts"/>
                    </th>
                    <th class="latest-by forum-latest-by-header shrink-to-fit">
                        <spring:message code="label.branch.header.lastMessage"/>
                    </th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="branch" items="${section.branches}" varStatus="i">
                    <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                                          permission='BranchPermission.VIEW_TOPICS'>
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
                            <td>
                                <a href="${pageContext.request.contextPath}/branches/${branch.id}">
                                    <c:out value="${branch.name}"/>
                                </a>
                                <br/>

                                <c:out value="${branch.description}"/>
                                <br/>
                                <strong><spring:message code="label.section.moderators"/></strong>
                                <jtalks:moderators moderators="${branch.moderatorsGroup.users}"/>
                            </td>
                            <td class="topics-posts">
                                <spring:message code="label.section.header.topics"/>:
	                            <span class='test-topics-count'>
	                                <c:out value="${branch.topicCount}"/>
	                            </span>
                                <br/>
                                <spring:message code="label.section.header.messages"/>:
	                            <span class='test-posts-count'>
	                                <c:out value="${branch.postCount}"/>
	                            </span>
                            </td>
                            <td class="latest-by">
                                <c:if test="${branch.topicCount>0}">
                                    <i class="icon-calendar"></i>
                                    <a class="date"
                                       href="${pageContext.request.contextPath}/posts/${branch.lastPost.id}">
                                        <jtalks:format value="${branch.lastPost.creationDate}"/>
                                    </a>

                                    <p><spring:message code="label.topic.last_post_by"/>
                                        <a href="${pageContext.request.contextPath}/users/${branch.lastPost.userCreated.id}">
                                            <c:out value="${branch.lastPost.userCreated.username}"/>
                                        </a>
                                    </p>
                                </c:if>
                            </td>
                        </tr>
                    </jtalks:hasPermission>
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

    <%-- Users --%>
    <div id="users-stats" class="well forum-user-stats-container">
        <strong><spring:message code="label.section.now_browsing"/></strong>
        <jtalks:users users="${viewList}"/>
    </div>
    <%-- END OF Users --%>
</div>
</body>

