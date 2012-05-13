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
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-ui.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/datepicker.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/poll.js"
            type="text/javascript"></script>
    <%--todo need to set proper localization
        <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-ui-i18n.min.js"
                type="text/javascript"></script>
    --%>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/styles/jquery-ui.css"
          type="text/css" media="all"/>
</head>
<body>
<div class="wrap answer_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <form:form action="${pageContext.request.contextPath}/topics/new?branchId=${branchId}"
                   method="POST" modelAttribute="topicDto">
            <h2><a class="heading" href="#"><c:out value="${topic.title}"/></a></h2>

            <div class="forum_misc_info">
                <h2><spring:message code="h.new_topic"/></h2>
            </div>
            <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
            <div class="forum_header_table">
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="h.new_topic"/></span>
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
            </ul>

            <jtalks:bbeditor labelForAction="label.addtopic"
                             postText="${topicDto.bodyText}"
                             bodyParameterName="bodyText"
                             back="${pageContext.request.contextPath}/branches/${branchId}"/>

            <jtalks:newPoll titleNameValue="pollTitle"
                            pollOptionsNameValue="pollItems"
                            singleNameValue="single"
                            singleValue="true"
                            multipleValue="false"
                            endingDateNameValue="endingDate"/>
        </form:form>
    </div>

    <div class="forum_message_cell_text" id="previewPoll">

    </div>

    <div class="footer_buffer"></div>
</div>
</body>