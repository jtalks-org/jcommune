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
    <title><spring:message code="label.recent"/></title>
</head>
<body>

<div class="container">
    <%-- In cases when we have more than one pages we should show "mark all read pages" above pagination--%>
    <c:if test="${topicsPage.totalPages > 1}">
        <div id="recent-topics-header">
	        <div id="right-block">
	            <sec:authorize access="isAuthenticated()">
	                <span id="mark-all-viewed">
	                    <i class="icon-check"></i>
	                    <a href="${pageContext.request.contextPath}/recent/forum/markread">
	                        <spring:message code="label.mark_all_topics_read"/>
	                    </a>
	                </span>
	            </sec:authorize>
	        </div>
	    </div>
    </c:if>
    
    <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span3">
            <h3><spring:message code="label.recent"/></h3>
        </div>
        <div class="span9">
            <%-- 
                In cases when we have more than one pages we should show "mark all read pages" above pagination,
                otherwise "mark all read pages" should be in a place of pagination.
            --%>
	        <c:choose>
	            <c:when test="${topicsPage.totalPages > 1}">
	                <div class="pagination pull-right forum-pagination">
	                    <ul>
	                        <jtalks:pagination uri="${branch.id}" page="${topicsPage}" pagingEnabled="${pagingEnabled}"/>
	                    </ul>
	                </div>
	            </c:when>
	            <c:otherwise>
	                <sec:authorize access="isAuthenticated()">
	                    <div id="right-block">
		                    <span id="mark-all-viewed">
		                        <i class="icon-check"></i>
		                        <a href="${pageContext.request.contextPath}/recent/forum/markread">
		                            <spring:message code="label.mark_all_topics_read"/>
		                        </a>
		                    </span>
	                    </div>
	                </sec:authorize>
	            </c:otherwise>
	        </c:choose>
        </div>
    </div>

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
                    <th class="posted-in-col shrink-to-fit"><spring:message code="label.branch.header.branches"/></th>
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
                               <span class="sticky"><spring:message
                                       code="label.marked_as_poll"/> </span>
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
                            <a href='${pageContext.request.contextPath}/users/${topic.topicStarter.id}'
                               title="<spring:message code="label.topic.header.author"/>">
                                <c:out value="${topic.topicStarter.username}"/>
                            </a>
                        </td>
                        <td class="posted-in-col shrink-to-fit">
                            <a href="${pageContext.request.contextPath}/branches/${topic.branch.id}">
                                <c:out value="${topic.branch.name}"/>
                            </a>
                        </td>

                        <td class="posts-views-small shrink-to-fit">
                            <span class='test-posts-count'><c:out value="${topic.postCount}"/></span><br/>
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
                        <spring:message code="label.recent.empty"/>
                    </td>
                </tr>
                </tbody>
            </c:otherwise>
        </c:choose>
    </table>

    <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span12">
            <div class="pagination pull-right forum-pagination">
                <ul>
                    <jtalks:pagination uri="" page="${topicsPage}" pagingEnabled="${pagingEnabled}"/>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
