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
  <title>
      <c:out value="${cmpTitlePrefix}"/>
      <spring:message code="label.restorePassword.header"/>
  </title>
</head>
<div class="container form-login-related">
  <form:form id="form" name="form" modelAttribute="dto"
             action='${pageContext.request.contextPath}/password/restore' method="POST"
             class="form-vertical">

    <fieldset>
      <legend><spring:message code="label.restorePassword.header"/></legend>

      <p><spring:message code="label.restorePassword.text"/></p>

      <div class='control-group'>
        <label class="control-label">
          <spring:message code="label.email"/>
        </label>

        <div class='controls'>
          <form:input path="userEmail" type="text" size="20"/>
            <br>
          <form:errors path="userEmail" cssClass="help-inline"/>
          <c:if test="${not empty message}">
            <input id="restorePassSuccess" type="hidden" value="<spring:message code="${message}"/>"/>
          </c:if>
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary"><spring:message code="label.send"/></button>
      </div>
    </fieldset>
  </form:form>
</div>