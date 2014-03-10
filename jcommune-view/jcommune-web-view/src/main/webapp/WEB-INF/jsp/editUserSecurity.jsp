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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<form:hidden path="userSecurityDto.userId" value="${editedUser.userSecurityDto.userId}"/>

<div class="clearfix"></div>
<hr class='user-profile-hr'/>

<div>
  <fieldset>
    <c:if test="${param.userId == editedUser.userSecurityDto.userId}">
      <div class="control-group">
        <label class="control-label"><spring:message code="label.currentPassword"/></label>
        <div class="controls">
          <form:input class="input-xlarge" type="password" path="userSecurityDto.currentUserPassword" tabindex="45"/>
          <br/>
          <form:errors path="userSecurityDto.currentUserPassword" cssClass="help-inline"/>
        </div>
      </div>
    </c:if>

    <div class="control-group">
      <label class="control-label"><spring:message code="label.newPassword"/></label>
      <div class="controls">
        <form:input class="input-xlarge" type="password" path="userSecurityDto.newUserPassword" tabindex="50"/>
        <br/>
        <form:errors path="userSecurityDto.newUserPassword" cssClass="help-inline"/>
      </div>
    </div>

    <div class="control-group">
      <label class="control-label"><spring:message code="label.newPasswordConfirmation"/></label>
      <div class="controls">
        <form:input class="input-xlarge" type="password" path="userSecurityDto.newUserPasswordConfirm" tabindex="55"/>
        <br/>
        <form:errors path="userSecurityDto.newUserPasswordConfirm" cssClass="help-inline"/>
      </div>
    </div>
  </fieldset>
</div>