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
<head>
    <title><spring:message code="label.new_pm"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
</head>
<body>
<div class="wrap pm_page">
    <jsp:include page="../../template/topLine.jsp"/>
    <h1><a href="${pageContext.request.contextPath}">
        <img src="${pageContext.request.contextPath}/resources/images/jtalks.png"/>
    </a></h1>

    <div class="all_forums">
        <h2><a class="heading" href="#"><spring:message code="label.new_pm"/></a></h2>
        <div class="forum_misc_info">
        </div>
        <jsp:include page="../../template/pmNavigationMenu.jsp"/>
        <form:form action="${pageContext.request.contextPath}/pm"
                   method="POST" modelAttribute="privateMessageDto" name="editForm"
                   onsubmit="doCheck();">
            <ul class="forum_table">
                <li class="forum_row">
                    <div class="forum_answer_left">
                        <spring:message code="label.recipient"/>
                    </div>
                    <div class="forum_answer_right">
                        <form:input path="recipient" size="45" maxlength="60" tabindex="1" class="post"/>
                        <form:errors path="recipient"/>
                    </div>
                </li>
                <li class="forum_row">
                    <div class="forum_answer_left">
                        <spring:message code="label.title"/>
                    </div>
                    <div class="forum_answer_right">
                        <form:input path="title" size="45" maxlength="60" tabindex="1" class="post"/>
                        <form:errors path="title"/>
                    </div>
                </li>
            </ul>
            <jtalks:bbeditor labelForAction="label.send"
                             postText="${privateMessageDto.body}"
                             bodyParameterName="body"
                             back="${pageContext.request.contextPath}/inbox"/>
            <input id="save_pm" type="submit" class="button" name="save_pm" value="<spring:message code="label.save"/>"
                   onclick="doCheck();document.editForm.action='${pageContext.request.contextPath}/pm/save';return true;"/>
        </form:form>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
