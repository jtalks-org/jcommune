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
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/registration.js"
            type="text/javascript"></script>
</head>

<body>

<div class="container form-login-related registration-page">
    <form:form id="form" name="form" action='${pageContext.request.contextPath}/user/new'
               modelAttribute="newUser" method="POST" class='form-vertical'>
        <fieldset>
            <legend><spring:message code="label.fillmessage"/></legend>
            <div class="control-group">
                <span class="reg_info"><spring:message code="label.tip.username"/></span>

                <div class="controls">
                    <form:input path="username" class="reg_input" type="text"/>
                    <br/>
                    <form:errors path="username" cssClass="help-inline"/>
                </div>
            </div>
            <div class="control-group">
                <span class="reg_info"><spring:message code="label.tip.email"/></span>

                <div class="controls">
                    <form:input path="email" class="reg_input" type="text"/>
                    <br/>
                    <form:errors path="email" cssClass="help-inline"/>
                </div>
            </div>
            <div class="control-group">
                <span class="reg_info"><spring:message code="label.tip.password"/></span>

                <div class="controls">
                    <form:input path="password" class="reg_input" type="password"/>
                    <br/>
                    <form:errors path="password" cssClass="help-inline"/>
                </div>
            </div>
            <div class="control-group">
                <span class="reg_info"><spring:message code="label.tip.confirmation"/></span>

                <div class="controls">
                    <form:input path="passwordConfirm" class="reg_input" type="password"/>
                    <br/>
                    <form:errors path="passwordConfirm" cssClass="help-inline"/>
                </div>
            </div>
            <div class="control-group">
                <span class="reg_info"><spring:message code="label.tip.captcha"/></span>

                <div class="controls img-container">
                    <img id="captcha-img" class="capcha-img" src='${pageContext.request.contextPath}/captcha/image'/>
                    <img id="captcha-refresh" class="capcha-img capcha-reload"
                         src='${pageContext.request.contextPath}/resources/images/captcha-refresh.png'/>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <form:input path="captcha" class="capcha-field" type="text" id="captcha"/>
                    <br>
                    <form:errors path="captcha" cssClass="help-inline"/>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <spring:message code="label.signup"/>
                </button>
            </div>
        </fieldset>
    </form:form>
</div>
</body>
</html>
