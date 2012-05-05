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
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<head>
    <title><spring:message code="label.answer_to"/>: <c:out value="${topicTitle}"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
</head>
<body>
<div class="wrap answer_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <h2><a class="heading" href="${pageContext.request.contextPath}/topics/${topicId}">
            <c:out value="${topicTitle}"/>
        </a></h2>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
        <div id="answer">
            <form:form action="${pageContext.request.contextPath}/posts/${postId}/edit?topicId=${topicId}"
                       method="POST" modelAttribute="postDto">
                <form:hidden path="topicId"/>
                <form:hidden path="id"/>
                <div class="forum_header_table">
                    <div class="forum_header">
                        <span class="forum_header_answer"><spring:message code="label.post.edit"/></span>
                        <span class="empty_cell"></span>
                    </div>
                </div>
                <jtalks:bbeditor labelForAction="label.save"
                                 postText="${postDto.bodyText}"
                                 bodyParameterName="bodyText"
                                 back="${pageContext.request.contextPath}/topics/${topicId}"/>
            </form:form>
        </div>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>