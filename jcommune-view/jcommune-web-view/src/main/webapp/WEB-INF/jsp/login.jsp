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
<%--@elvariable id="username" type="java.util.List"--%>
<%--@elvariable id="login_error" type="java.util.List"--%>
<html>
<head>
  <title>
      <c:out value="${cmpTitlePrefix}"/>
      <spring:message code="label.signin"/>
  </title>
</head>
<body>

<%-- Container --%>
<div class="container form-login-related">
  <form:form id="login-form" name="form" action='${pageContext.request.contextPath}/login' 
             modelAttribute="loginUserDto" method="POST" class='form-vertical'>
    <fieldset>
      <legend><spring:message code="label.signin"/></legend>

      <div class="control-group">
        <label for="userName" class="control-label"><spring:message code="label.username"/> </label>

        <div class="controls">
          <form:input class="reg_input" type="text" path="userName" id="userName"/>
        </div>
      </div>

      <div class="control-group">
        <label for="password" class="control-label"><spring:message code="label.password"/> </label>

        <div class="controls">
            <form:input type="password" path="password" id="password"/>
          <c:if test="${not empty param.login_error}">
            <span class="help-inline">
              <c:choose>
                <c:when test="${param.login_error == 1}">
                  <spring:message code="label.login_error"/>
                </c:when>
                <c:when test="${param.login_error == 2}">
                  <spring:message code="label.login_cookies_were_theft"/>
                </c:when>
                <c:when test="${param.login_error == 3}">
                  <spring:message code="label.authentication.connection.error"/>
                </c:when>
              </c:choose>
            </span>
          </c:if>
        </div>
      </div>

        <div class="control-group">
            <label class="rememberme-lbl"><input type="checkbox" name="_spring_security_remember_me" class="form-check-radio-box" checked="checked"><spring:message code="label.auto_logon"/></label>
        </div>

      <div class="form-actions">
        <input type="submit" class="btn btn-primary" value="<spring:message code="label.signin"/>"/>
        <a class="signup" href="${pageContext.request.contextPath}/user/new"><spring:message
                  code="label.signup.rightnow"/></a>

        <br/>
        <a href='<c:url value="/password/restore"/>'><spring:message
                code="label.restorePassword.prompt"/></a>
      </div>
    </fieldset>
    <input type="hidden" name="referer" id="referer" value="<c:url value='${referer}'/>" />
  </form:form>
</div>
</body>
</html>
