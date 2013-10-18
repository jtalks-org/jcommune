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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication property="principal.id" var="senderId"/>
<head>
  <title><spring:message code="label.new_pm"/></title>
</head>
<body>


<div class="container">
  <div class="row">
    <div class="span2">
      <jsp:include page="../../template/newPrivateMessage.jsp"/>
      <jsp:include page="../../template/pmFolders.jsp"/>
    </div>

    <div class="span9">
      <form:form action="${pageContext.request.contextPath}/pm"
                 method="POST" modelAttribute="privateMessageDto" name="editForm"
                 class="well anti-multipost">
        <form:hidden path="id"/>

        <div class='control-group'>
          <div class='controls'>
            <spring:message code="label.pm.recipient" var="placeholderRecipient"/>
            <form:input class="span3 script-confirm-unsaved" path="recipient" tabindex="100"
                        placeholder="${placeholderRecipient}"/>
            <br/>
            <form:errors path="recipient" cssClass="help-inline"/>
          </div>
        </div>

        <div class='control-group'>
          <div class='controls'>
            <spring:message code="label.pm.title" var="placeholderTitle"/>
            <form:input class="span8 script-confirm-unsaved" path="title" tabindex="101"
                        placeholder="${placeholderTitle}"/>
            <br/>
            <form:errors path="title" cssClass="help-inline"/>
          </div>
        </div>

        <c:set var="hasPermissionToSend" value="false"/>
        <jtalks:hasPermission targetId='${senderId}' targetType='USER'
                              permission='ProfilePermission.SEND_PRIVATE_MESSAGES'>
          <c:set var="hasPermissionToSend" value="true"/>
        </jtalks:hasPermission>


        <jtalks:bbeditor labelForAction="label.send"
                         postText="${privateMessageDto.body}"
                         bodyParameterName="body"
                         showSubmitButton="${hasPermissionToSend}"
                         back="${pageContext.request.contextPath}/inbox"/>

        <input id="savePM" type="submit" class="btn margin-left-big" tabindex="500" name="save_pm"
               value="<spring:message code="label.save"/>"
               onclick="document.editForm.action='${pageContext.request.contextPath}/pm/save';return true;"/>

      </form:form>

    </div>
  </div>
</div>
<div class="footer_buffer"></div>
</body>
