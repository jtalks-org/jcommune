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
    <title><spring:message code="label.section.jtalks_forum"/></title>
</head>
<body>
    <jsp:include page="../template/topLine.jsp"/>

    <div class="container">
        <!-- Section header -->
        <div id="branch-header">
            <h2><c:out value="${section.name}"/></h2>
            <span class="inline-block"></span>
        </div>
        <!-- END OF Branch header -->
        
        <!-- Branches table -->
        <table id="topics-table" cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered">
        <c:choose>
            <c:when test="${!(empty section.branches)}">
                <thead>
                    <tr>
                        <th class="status-col"></th>
                        <th><spring:message code="label.section.header.branches"/></th>
                        <th class="topics-posts forum-posts-view-header"><spring:message code="label.branch.header.topics_posts"/></th>
                        <th class="latest-by forum-latest-by-header"><spring:message code="label.branch.header.lastMessage"/></th>
                    </tr>
                </thead>
                
                <tbody>
                    <c:forEach var="branch" items="${section.branches}" varStatus="i">
                        <sec:accesscontrollist hasPermission="6" domainObject="${branch}">
	                        <tr>
	                            <td class="status-col">
	                                <img class="status-img" 
	                                    src="${pageContext.request.contextPath}/resources/images/closed.png" 
	                                    title="<spring:message code="label.section.close_forum"/>" />
	                            </td>
	                            <td>
	                                <a href="${pageContext.request.contextPath}/branches/${branch.id}">
	                                    <c:out value="${branch.name}"/>
	                                </a>
	                                <br />
	                                
	                                <c:out value="${branch.description}"/>
	                                <a href="#"><spring:message code="label.section.faq"/></a>
	                                <br/>
	                                <strong><spring:message code="label.section.moderators"/></strong>
	                                <a class="label label-success" href="#">Vurn</a>                                    
	                            </td>
	                            <td class="topics-posts">
	                                <spring:message code="label.section.header.topics"/>: <span class='test-topics-count'><c:out value="${branch.topicCount}"/></span><br />
	                                <spring:message code="label.section.header.messages"/>: <span class='test-posts-count'><c:out value="${branch.postCount}"/></span>
	                            </td>
	                            <td class="latest-by">
	                                <c:if test="${branch.topicCount>0}">
		                                <i class="icon-calendar"></i>
		                                <a class="date" href="${pageContext.request.contextPath}/posts/${branch.lastPostInLastUpdatedTopic.id}">
	                                        <jtalks:format value="${branch.lastPostInLastUpdatedTopic.creationDate}"/>
		                                </a>
		                                <p><spring:message code="label.topic.last_post_by"/> 
		                                    <a href="${pageContext.request.contextPath}/users/${branch.lastPostInLastUpdatedTopic.userCreated.id}">
	                                            <c:out value="${branch.lastPostInLastUpdatedTopic.userCreated.username}"/>
		                                    </a>
		                                </p>
	                                </c:if>
	                            </td>
	                        </tr>
                        </sec:accesscontrollist>
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
        
        <!-- Users -->
        <div id="users-stats" class="well forum-user-stats-container">
            <c:if test="${!(empty viewList)}">
                <strong><spring:message code="label.section.now_browsing"/></strong> 
            </c:if>
            <c:forEach var="innerUser" items="${viewList}">
                <%--todo
                <c:choose>
                   <c:when test="${innerUser.role=='ROLE_ADMIN'}">
                        <c:set var='labelClass' value='label label-important'/>
                    </c:when>--%>
                   <%-- <c:otherwise>--%>
                        <c:set var='labelClass' value=''/>
                    <%--</c:otherwise>
                </c:choose>  --%>
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

