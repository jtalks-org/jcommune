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
<html>
<head>
    <title><spring:message code="label.signup"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/captcha.js"
            type="text/javascript"></script>
</head>

<body>
<div class="wrap registration_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>
    <div class="all_forums">
        <form:form id="form" name="form" action='${pageContext.request.contextPath}/user/new'
                     modelAttribute="newUser" method="POST">
            <div class="forum_header_table">
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="label.fillmessage"/></span>
                    <span class="empty_cell"></span>
                </div>
            </div>
            <div class="forum_table" id="stylized">

                <div class="forum_row">
                    <form:label path="username"> <spring:message code="label.username"/></form:label>
                    <div>
                        <form:input path="username" class="reg_input" type="text"/></br>
                        <form:errors path="username" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.username"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="email"><spring:message code="label.email"/></form:label>
                    <div>
                        <form:input path="email" class="reg_input" type="text"/></br>
                        <form:errors path="email" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.email"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="password"><spring:message code="label.password"/></form:label>

                    <div>
                        <form:input path="password" class="reg_input" type="password"/></br>
                        <form:errors path="password" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.password"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="passwordConfirm"><spring:message code="label.confirmation"/></form:label>

                    <div>
                        <form:input path="passwordConfirm" class="reg_input" type="password"/></br>
                        <form:errors path="passwordConfirm" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.confirmation"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="captcha"><spring:message code="label.captcha"/></form:label>

                    <div>
                        <img id="captcha_img"  src='${pageContext.request.contextPath}/captcha-image' class="captcha"/>
                        <img id="captcha_refresh" src='${pageContext.request.contextPath}/resources/images/captcha-refresh.gif'/>
                        <form:input path="captcha" class="captcha_input" type="text"/></br>
                        <form:errors path="captcha" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.captcha"/></span>
                </div>
            </div>
            <div class="form_controls">
                <button type="submit" class="button">
                    <spring:message code="label.signup"/>
                </button>
            </div>
        </form:form>
    </div>
    <div class="footer_buffer"></div>
</div>
</body>
</html>
