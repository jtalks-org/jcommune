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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <meta name="description" content="<c:out value="${topicDto.topic.branch.name}"/>">
    <title><c:out value="${topicDto.topic.branch.name}"/> - <spring:message code="label.addCodeReview"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-ui.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/leaveConfirm.js"
            type="text/javascript"></script>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/styles/jquery-ui.css"
          type="text/css" media="all"/>
</head>
<body>
<div class="container">
    <div id="branch-header">
        <h3>
            <span id="topicTitle">
                <c:out value="${topicDto.topic.title}"/>
            </span>
        </h3>
    </div>
    <br>
    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

    <form:form action="${pageContext.request.contextPath}${submitUrl}"
               method="POST" modelAttribute="topicDto" class="well anti-multipost" enctype="multipart/form-data">
        <div class='control-group hide-on-preview'>
            <div class='controls'>
                <spring:message code='label.topic.topic_title' var='topicTitlePlaceholder'/>
                <form:input path="topic.title" id="subject" type="text" name="subject" size="45"
                            maxlength="255" tabindex="100"
                            class="span11 script-confirm-unsaved" placeholder="${topicTitlePlaceholder}"/>
                <form:errors path="topic.title" id="subject" type="text" name="subject" size="45"
                             maxlength="255"
                             class="post" cssClass="help-inline"/>
            </div>
        </div>
        
        <div class='control-group'>
            <spring:message code="placeholder.codereview.editor.content" var="placeholderEditorContent"/>
            <form:textarea path="bodyText" id="body" name="body" tabindex="200" style="width:100%;height: 350px"
                          placeholder="${placeholderEditorContent}" class="script-confirm-unsaved"/>
            <br>
            <form:errors path="bodyText" cssClass="help-inline"/>
        </div>
        
         <input id="post" type="submit" class="btn btn-primary" accesskey="s" name="post" tabindex="300"
                value="<spring:message code="label.save"/>"/>
    </form:form>
    
    <a href="${pageContext.request.contextPath}/branches/${branchId}" tabindex="1000" class='back-btn'>
        <i class="icon-arrow-left"></i>
        <spring:message code="label.back"/>
    </a>
</div>
<script>
    Utils.focusFirstEl('#subject');
</script>
</body>