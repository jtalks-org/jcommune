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
    <title><spring:message code="label.postListOfUser"/> <c:out value="${user.username}"/></title>
</head>
<body>
<div class="container">
    <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span3">
            <h3><spring:message code="label.postListOfUser"/><c:out value="${user.username}"/></h3>
        </div>
       
        <div class="span9">
            <div class="pagination pull-right forum-pagination">
                <ul>
                    <jtalks:pagination uri="${topicId}" page="${postsPage}" numberLink="3" pagingEnabled="${pagingEnabled}"/>
                 
                    <c:if test="${postsPage.totalPages>1}">
                        <c:if test="${pagingEnabled == true}">
                            <li>
                                <a href="?pagingEnabled=false">
                                    <spring:message code="label.showAll"/>
                                </a>
                            </li>
                        </c:if>
                    </c:if>
                    <c:if test="${pagingEnabled == false}">
                        <li>
                            <a href="?pagingEnabled=true">
                                <spring:message code="label.showPages"/>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
                 
    <%-- Topics table --%>
    <div class='post'>  
    <table class="table table-striped table-bordered table-condensed">     
        <c:choose>
            <c:when test="${!(empty postsPage.content)}">
                <thead>
                    <tr>
                        <th><spring:message code="label.info"/></th>
                        <th><spring:message code="label.topic.header.message"/></th>
                    </tr>
                </thead> 
                <tbody>
                    <c:forEach var="post" items="${postsPage.content}" varStatus="i">
                    <tr class='post-content-tr'>
                        <td class='userinfo'>
                            <spring:message code='label.branch.header.branches'/>
                            <a class="forum_message_cell_text"
                                   href="${pageContext.request.contextPath}/branches/${post.topic.branch.id}">
                                <c:out value="${post.topic.branch.name}"/></a>
                            <br>

                            <spring:message code='label.branch.header.topics'/>
                            <a class="forum_message_cell_text"
                                   href="${pageContext.request.contextPath}/topics/${post.topic.id}">
                                    <c:out value="${post.topic.title}"/></a>
                        </td>
                        <td class='post-content-td'>
                            <div>
                                <div class='user-posts-date-title'>
                                    <spring:message code="label.added"/>&nbsp;
                                    <jtalks:format value="${post.creationDate}"/>
                                </div>
                                <div class='user-posts-buttons'>
                                    
                                        <a class="btn btn-mini"
                                            href="${pageContext.request.contextPath}/posts/${post.id}">
                                                <spring:message code="label.goToPost"/>
                                        </a>
                                    
                                </div>
                            </div>
                            <hr/>
                            <div>                                    
                                <jtalks:bb2html bbCode="${post.postContent}"/>
                                <br/><br/><br/>
                            </div>
                            <c:if test="${post.modificationDate!=null}">
                                <hr/>
                                <div>
                                    <spring:message code="label.modify"/>
                                    <jtalks:format value="${post.modificationDate}"/>
                                </div>
                            </c:if>
                            
                        </td>
                    </tr>
                    </c:forEach>
                </tbody>
            </c:when>
            <c:otherwise>
                <tbody>
                <tr>
                    <td>
                        <spring:message code="label.postListOfUser.empty"/>
                    </td>
                </tr>
                </tbody>
            </c:otherwise>
        </c:choose>
    </table>
    </div>
    
    <%-- Pagination --%>
    <div class="row-fluid upper-pagination forum-pagination-container">
        <div class="span12">
            <div class="pagination pull-right forum-pagination">
                <ul>
                    <jtalks:pagination uri="${topicId}" page="${postsPage}" numberLink="3" pagingEnabled="${pagingEnabled}"/>
                 
                    <c:if test="${postsPage.totalPages>1}">
                        <c:if test="${pagingEnabled == true}">
                            <li>
                                <a href="?pagingEnabled=false">
                                    <spring:message code="label.showAll"/>
                                </a>
                            </li>
                        </c:if>
                    </c:if>
                    <c:if test="${pagingEnabled == false}">
                        <li>
                            <a href="?pagingEnabled=true">
                                <spring:message code="label.showPages"/>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
    <%-- END OF Pagination --%>
    <sec:authorize access="isAuthenticated()">
        <a href="${pageContext.request.contextPath}/users/${user.id}" class="back-btn">
            <i class="icon-arrow-left"></i>
            <spring:message code="label.back"/>
        </a>
    </sec:authorize>
        
</div>
</body>
