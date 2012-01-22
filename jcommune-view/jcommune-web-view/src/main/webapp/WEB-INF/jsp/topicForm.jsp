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
<html>
<head>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
</head>
<body>
<div class="wrap answer_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <form:form name="editForm"
                   action="${pageContext.request.contextPath}/topics/${topicId}/edit?branchId=${branchId}"
                   method="POST" modelAttribute="topicDto" onsubmit="doCheck();return true;">
            <form:hidden path="id"/>

            <div class="forum_misc_info">
                <h2 class="heading"><spring:message code="h.edit_topic"/></h2>
            </div>

            <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

            <div class="forum_header_table"> <!-- Шапка топика -->
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="h.edit_topic"/></span>
                    <span class="empty_cell"></span>
                </div>
            </div>
            <ul class="forum_table">
                <li class="forum_row">
                    <div class="forum_answer_left">
                        <spring:message code="label.topic.title"/>
                    </div>
                    <div class="forum_answer_right">
                        <form:input path="topicName" id="subject" type="text" name="subject" size="45"
                                    maxlength="255" tabindex="1"
                                    class="post"/>
                        <br>
                        <form:errors path="topicName" id="subject" type="text" name="subject" size="45"
                                     maxlength="255" tabindex="1"
                                     class="post" cssClass="error"/>
                    </div>
                </li>
                <sec:authorize access="hasRole('ROLE_ADMIN')">

                    <li class="forum_row">
                        <div class="forum_answer_left">
                            <spring:message code="label.topic.options"/>
                        </div>
                        <div class="forum_answer_right" style="text-align: left;">
                                <form:checkbox path="sticked" value="true"/>
                                <spring:message code="label.sticked"/>
                                <form:errors path="sticked"/>
                            <br/>

                                <form:input path="topicWeight" size="1"/>
                            <form:label path="topicWeight">
                                <spring:message code="label.weight"/>
                            </form:label>
                                <form:errors path="topicWeight"/>
                            <br/>
                                <form:checkbox path="announcement" value="true"/>
                                <spring:message code="label.announcement"/>
                                <form:errors path="announcement"/>

                    </li>
                </sec:authorize>
            </ul>
            <jtalks:bbeditor labelForAction="label.save"
                             postText="${topicDto.bodyText}"
                             bodyParameterName="bodyText"
                             back="${pageContext.request.contextPath}/topics/${topicId}"/>

        </form:form>
    </div>
</div>
<div class="footer_buffer"></div>
</body>
</html>