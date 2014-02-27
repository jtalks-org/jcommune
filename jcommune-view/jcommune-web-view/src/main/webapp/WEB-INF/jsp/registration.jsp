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
<jsp:useBean id="registrationPlugins" type="java.util.Map" scope="request"/>
<html>
<head>
  <title>
      <c:out value="${cmpTitlePrefix}"/>
      <spring:message code="label.signup"/>
  </title>
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
          <form:input path="userDto.username" class="reg_input" type="text"/>
          <br/>
          <form:errors path="userDto.username" cssClass="help-inline"/>
        </div>
      </div>
      <div class="control-group">
        <span class="reg_info"><spring:message code="label.tip.email"/></span>

        <div class="controls">
          <form:input path="userDto.email" class="reg_input" type="text"/>
          <br/>
          <form:errors path="userDto.email" cssClass="help-inline"/>
        </div>
      </div>
      <div class="control-group">
        <span class="reg_info"><spring:message code="label.tip.password"/></span>

        <div class="controls">
          <form:password path="userDto.password" class="reg_input" showPassword="true" />
          <br/>
          <form:errors path="userDto.password" cssClass="help-inline"/>
        </div>
      </div>
      <div class="control-group">
        <span class="reg_info"><spring:message code="label.tip.confirmation"/></span>

        <div class="controls">
          <form:password path="passwordConfirm" class="reg_input" showPassword="true"/>
          <br/>
          <form:errors path="passwordConfirm" cssClass="help-inline"/>
        </div>
      </div>
      <div class="hide-element">
        <span class="reg_info"><spring:message code="label.tip.honeypot.captcha"/></span>
        <div class="controls">
          <form:input path="honeypotCaptcha" class="reg_input"/>
        </div>
      </div>

      <c:forEach items="${registrationPlugins}" var="plugin">
        <div id="plugin-${plugin.key}-body" class="control-group">
          <c:out value="${plugin.value}" escapeXml="false"/>
          <form:errors path="userDto.captchas[plugin-${plugin.key}]" cssClass="help-inline"/>
        </div>
      </c:forEach>

      <div class="control-group">
        <div class="controls">
          <c:if test="${not empty param.reg_error}">
              <span class="help-inline">
                  <c:choose>
                    <c:when test="${param.reg_error == 1}">
                      <spring:message code="label.registration.connection.error"/>
                    </c:when>
                    <c:when test="${param.reg_error == 2}">
                      <spring:message code="label.registration.failture"/>
                    </c:when>
                    <c:when test="${param.reg_error == 3}">
                      <spring:message code="label.honeypot.not.null"/>
                    </c:when>
                  </c:choose>
              </span>
          </c:if>
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
