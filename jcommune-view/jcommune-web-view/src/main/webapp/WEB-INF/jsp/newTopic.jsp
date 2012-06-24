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
    <title><spring:message code="h.new_topic"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/datepicker/js/jquery-1.7.2.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/datepicker/js/jquery-ui-1.8.21.custom.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/datepicker/development-bundle/ui/i18n/jquery.ui.datepicker-ru.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/datepicker.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/newPoll.js"
            type="text/javascript"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/custom/leaveConfirm.js"></script>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/datepicker/development-bundle/themes/smoothness/jquery.ui.datepicker.css"
          type="text/css" media="all"/>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/styles/jquery-ui.css"
          type="text/css" media="all"/>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/datepicker/css/smoothness/jquery-ui-1.8.21.custom.css"
          type="text/css" media="all"/>

    <script src="${pageContext.request.contextPath}/resources/javascript/custom/bbeditorEffects.js"
            type="text/javascript"></script>
</head>
<body>
<jsp:include page="../template/topLine.jsp"/>

<div class="container">
    <h2><a class="heading" href="#"><c:out value="${topic.title}"/></a></h2>

    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

    <form:form action="${pageContext.request.contextPath}/topics/new?branchId=${branchId}"
               method="POST" modelAttribute="topicDto" class="well anti-multipost">
        <div class='control-group'>
            <div class='controls'>
                <spring:message code='label.topic.topic_title' var='topicTitlePlaceholder'/>
                <form:input path="topicName" id="subject" type="text" name="subject" size="45"
                            maxlength="255" tabindex="1"
                            class="span11" placeholder="${topicTitlePlaceholder}"/>
                <form:errors path="topicName" id="subject" type="text" name="subject" size="45"
                             maxlength="255" tabindex="1"
                             class="post" cssClass="help-inline"/>
            </div>
        </div>

        <jtalks:bbeditor labelForAction="label.addtopic"
                         postText="${topicDto.bodyText}"
                         bodyParameterName="bodyText"
                         back="${pageContext.request.contextPath}/branches/${branchId}"/>
        <div class="control-group">
            <br/>
            <form:checkbox id="notify" path="notifyOnAnswers" name="notify" checked="checked" value="${notifyOnAnswers}"
                           style="margin-right: 10px;"/><spring:message
                code="label.answer.notify_message"/>
        </div>
        <br/>
        <br/>
        <jtalks:newPoll titleNameValue="pollTitle"
                        pollOptionsNameValue="pollItems"
                        multipleName="multiple"
                        multipleValue="${topicDto.multiple}"
                        endingDateNameValue="endingDate"/>
    </form:form>

    <a href="${pageContext.request.contextPath}/branches/${branchId}" class='back-btn'>
        <i class="icon-arrow-left"></i>
        <spring:message code="label.back"/>
    </a>
</div>
</body>