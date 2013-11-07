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
<html>
<head>
  <title>
      <c:out value="${cmpTitlePrefix}"/>
      <spring:message code="label.sapeConfiguration"/>
  </title>
  <script type="text/javascript">
    $(document).ready(function () {
      $('#enableSape1').change();
    })
  </script>
</head>
<body>

<%-- Container --%>
<div class="container form-login-related">
  <form:form action='${pageContext.request.contextPath}/configuration/sape' method="POST"
             name="sape-configuration-form" id="sape-configuration-form"
             modelAttribute="sapeConfiguration" class="form-vertical">
    <fieldset>
      <legend><spring:message code="label.sapeConfiguration"/></legend>

      <div class="control-group">
        <form:checkbox path="enableSape" class="form-check-radio-box"/>
        <label class="string optional"><spring:message code="label.enableSape"/></label>
      </div>

      <div class="control-group">
        <label for="accountId" class="control-label"><spring:message code="label.accountId"/> </label>

        <div class="controls">
          <form:input type="text" path="accountId"/>
          <br/>
          <form:errors path="accountId" cssClass="help-inline"/>
        </div>
      </div>

      <div class="control-group">
        <label for="timeout" class="control-label"><spring:message code="label.timeout"/> </label>

        <div class="controls">
          <form:input type="text" path="timeout"/>
          <br/>
          <form:errors path="timeout" cssClass="help-inline"/>
        </div>
      </div>

      <div class="control-group">
        <label for="hostUrl" class="control-label"><spring:message code="label.hostUrl"/> </label>

        <div class="controls">
          <form:input type="text" path="hostUrl"/>
          <br/>
          <form:errors path="hostUrl" cssClass="help-inline"/>
        </div>
      </div>

      <div class="control-group">
        <label for="numberOfLinks" class="control-label"><spring:message code="label.numberOfLinks"/> </label>

        <div class="controls">
          <form:input type="text" path="numberOfLinks"/>
          <br/>
          <form:errors path="numberOfLinks" cssClass="help-inline"/>
        </div>
      </div>

      <div class="control-group">
        <form:checkbox path="showOnMainPage" class="form-check-radio-box"/>
        <label class="string optional"><spring:message code="label.showOnMainPage"/></label>
      </div>

      <div class="control-group">
        <form:checkbox path="showDummyLinks" class="form-check-radio-box"/>
        <label class="string optional"><spring:message code="label.showDummyLinks"/></label>
      </div>

      <div class="form-actions">
        <input type="submit" class="btn btn-primary" value="<spring:message code="label.save"/>"/>
        <a class='btn' href='<c:url value="/"/>'><spring:message code="label.cancel"/></a>
      </div>
    </fieldset>
  </form:form>
</div>
</body>
</html>