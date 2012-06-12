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
    <jsp:include page="../template/topLine.jsp"/>
    
    <div class="container">
        
        <h2><c:out value="${topicDto.topicName}"/></h2>
        
        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
        
        <form:form name="editForm"
                   action="${pageContext.request.contextPath}/topics/${topicId}/edit?branchId=${branchId}"
                   method="POST" modelAttribute="topicDto" class='well'>
            <form:hidden path="id"/>
            
            <div class='control-group'>
                <label for='subject' class='control-label'><spring:message code="label.topic.title"/></label>
                <form:input path="topicName" id="subject" type="text" name="subject" size="45"
                            maxlength="255" tabindex="1"
                            class="post confirm-unsaved" placeholder='<spring:message code="label.topic.topic_title"/>'/>
                <br/>
                
                <form:errors path="topicName" id="subject" type="text" name="subject" size="45"
                            maxlength="255" tabindex="1"
                            class="post" cssClass="error"/>
            </div>
                    
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <div class='control-group'>
                        <form:checkbox path="sticked" value="true" class="confirm-unsaved form-check-radio-box"/>
                        <label for='sticked' class='string optional'>
                            <spring:message code="label.sticked"/>
                        </label>
                        
                        <form:errors path="sticked"/>
                    </div>
                    <div class='control-group'>
                        <form:label path="topicWeight" class='control-label'>
                            <spring:message code="label.weight"/>
                        </form:label>
                        <form:input path="topicWeight" size="1" class="confirm-unsaved"/>
                    
                        <form:errors path="topicWeight"/>
                    </div>
                    <div class='control-group'>
                        <form:checkbox path="announcement" value="true" class="confirm-unsaved form-check-radio-box" />
                        <label for='announcement' class='string optional'><spring:message code="label.announcement"/></label>
                        
                        <form:errors path="announcement"/>
                    </div>
                </sec:authorize>
           
            <jtalks:bbeditor labelForAction="label.save"
                             postText="${topicDto.bodyText}"
                             bodyParameterName="bodyText"
                             back="${pageContext.request.contextPath}/topics/${topicId}"/>

        </form:form>
        
        <a href="${pageContext.request.contextPath}/branches/${branchId}" class='back-btn'>
            <i class="icon-arrow-left"></i>
            <spring:message code="label.back"/>
        </a>
    </div>
</body>
</html>