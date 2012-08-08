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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
    <title><spring:message code="h.edit_topic"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/custom/leaveConfirm.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/bbeditorEffects.js"
            type="text/javascript"></script>
</head>
<body>

<div class="container">

    <h2><c:out value="${topicDto.topic.title}"/></h2>

    <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

    <form:form name="editForm"
               action="${pageContext.request.contextPath}/topics/${topicId}/edit?branchId=${branchId}"
               method="POST" modelAttribute="topicDto" class='well anti-multipost'>
        <form:hidden path="topic.id"/>

        <div class='control-group'>
            <label for='subject' class='control-label'><spring:message code="label.topic.title"/></label>
            <spring:message code='label.topic.topic_title' var='topicTitlePlaceholder'/>
            <form:input path="topic.title" id="subject" type="text" name="subject" size="45"
                        maxlength="255"
                        class="post script-confirm-unsaved" placeholder="${topicTitlePlaceholder}"/>
            <br/>

            <form:errors path="topic.title" id="subject" type="text" name="subject" size="45"
                         maxlength="255"
                         class="post" cssClass="error"/>
        </div>

        <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH' 
            permission='GeneralPermission.ADMIN'>
            <div class='control-group'>
                <form:checkbox path="topic.sticked" value="true" class="confirm-unsaved form-check-radio-box"/>
                <label for='sticked' class='string optional'>
                    <spring:message code="label.sticked"/>
                </label>

                <form:errors path="topic.sticked"/>
            </div>
            <div class='control-group'>
                <form:checkbox path="topic.announcement" value="true"
                               class="script-confirm-unsaved form-check-radio-box"/>
                <label for='announcement' class='string optional'>
                    <spring:message code="label.announcement"/>
                </label>

                <form:errors path="topic.announcement"/>
            </div>
        </jtalks:hasPermission>

        <jtalks:bbeditor labelForAction="label.save"
                         postText="${topic.bodyText}"
                         bodyParameterName="bodyText"
                         back="${pageContext.request.contextPath}/topics/${topicId}"/>
        <div class="control-group">
            <br/>
            <c:choose>
                <c:when test="${topicDto.notifyOnAnswers}">
                    <form:checkbox id="notify" path="notifyOnAnswers" name="notify" checked="checked"
                                   class="right-margin"/><spring:message
                        code="label.answer.notify_message"/>
                </c:when>
                <c:otherwise>
                    <form:checkbox id="notify" path="notifyOnAnswers" name="notify"
                                   class="right-margin"/><spring:message
                        code="label.answer.notify_message"/>
                </c:otherwise>
            </c:choose>
        </div>

    </form:form>

    <a href="${pageContext.request.contextPath}/topics/${topicId}" class='back-btn'>
        <i class="icon-arrow-left"></i>
        <spring:message code="label.back"/>
    </a>
</div>
</body>
</html>