﻿<%--

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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://www.springframework.org/tags" %>
<sec:authentication property="principal.username" var="auth" scope="request"/>
<sec:authentication property="principal.id" var="userId" scope="request"/>
<head>
  <meta name="description" content="<c:out value="${label.user}"/>">
  <title>
    <c:out value="${cmpTitlePrefix}"/>
    <spring:message code="label.user"/> - "${editedUser.username}"
  </title>
</head>
<body>

<div class="container">
<div id="editUserDetails" class="userprofile user-profile-container">
  <form:form id="editProfileForm" name="editProfileForm"
             action="${pageContext.request.contextPath}/users/edit/${editedUser.userId}"
             modelAttribute="editedUser" method="POST"
             class="form-horizontal">

    <form:hidden id="avatar" path="avatar"/>
    <form:hidden id="editedUserId" path="userId" value="${editedUser.userId}"/>
    <form:hidden id="editedUsername" path="username" value="${editedUser.username}"/>

    <div class='user-profile-header'>
                    <span class="pull-left thumbnail">
                        <span id="avatarPreviewContainer" class="wraptocenter">
                            <%--String prefix "data:image/jpeg;base64," needed for correct image rendering--%>
                            <img id="avatarPreview" src="data:image/jpeg;base64,${editedUser.avatar}" alt=""/>
                        </span>
                    </span>

      <h2 class="pull-right user-profile-username"><c:out value="${editedUser.username}"/></h2>
    </div>
    <div class="clearfix"></div>
    <div class="user-profile-top-buttons">
      <div class="user-profile-buttons-avatar">
        <a id="upload" data-original-title="<spring:message code="label.uploadTitle"/>" href="#" class="btn btn-mini">
          <i class="icon-picture"></i>
          <spring:message code="label.avatar.load"/>
        </a>
        <a id="removeAvatar" href="#" class="btn btn-mini btn-danger space-left-big-nf"
           title="<spring:message code="label.avatar.remove" />">
          <i class="icon-remove icon-white"></i>
        </a>
      </div>
    </div>

    <div class="clearfix"></div>
    <hr class='user-profile-hr'/>

    <div>
      <fieldset>
        <div class="control-group">
          <label class="control-label"><spring:message code="label.firstname"/></label>

          <div class="controls">
            <form:input class="input-xlarge" path="firstName" value="${editedUser.firstName}" tabindex="1"/>
            <br/>
            <form:errors path="firstName" cssClass="help-inline"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.lastname"/></label>

          <div class="controls">
            <form:input class="input-xlarge" path="lastName" value="${editedUser.lastName}" tabindex="5"/>
            <br/>
            <form:errors path="lastName" cssClass="help-inline"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.signature"/></label>

          <div class="controls">
            <form:textarea class="input-xlarge" path="signature"
                           value="${editedUser.signature}" tabindex="10"/>
            <br/>
            <form:errors path="signature" cssClass="help-inline"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.email"/></label>

          <div class="controls">
            <form:input class="input-xlarge" path="email" tabindex="15"/>
            <br/>
            <form:errors path="email" cssClass="help-inline"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.pageSize"/></label>

          <div class="controls">
            <form:select path="pageSize"
                         items="${editedUser.pageSizesAvailable}"
                         class="input-mini" tabindex="25"/>
            <br/>
            <form:errors path="pageSize" cssClass="help-inline"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.autosubscribe"/></label>

          <div class="controls padding-top-profile">
            <spring:message var="autosubscribeTooltip"
                            code="label.tips.autoSubscribe"/>
            <form:checkbox path="autosubscribe" value="${editedUser.autosubscribe}"
                           class="form-check-radio-box script-has-tooltip"
                           data-original-title='${autosubscribeTooltip}' tabindex="30"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.mentioning.notifications.enabled"/></label>

          <div class="controls padding-top-profile">
            <spring:message var="mentioningNotificationsTooltip"
                            code="label.tips.userMentioningNotification"/>
            <form:checkbox path="mentioningNotificationsEnabled"
                           value="${editedUser.mentioningNotificationsEnabled}"
                           class="form-check-radio-box script-has-tooltip"
                           data-original-title='${mentioningNotificationsTooltip}' tabindex="35"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.send.pm.notification.enabled"/></label>

          <div class="controls padding-top-profile">
            <spring:message var="sendPmNotificationTooltip" code="label.tips.sendPmNotification"/>
            <form:checkbox path="sendPmNotification"
                           value="${editedUser.sendPmNotification}"
                           class="form-check-radio-box script-has-tooltip"
                           data-original-title='${sendPmNotificationTooltip}' tabindex="36"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.location"/></label>

          <div class="controls">
            <form:input path="location" class="input-xlarge" value="${editedUser.location}" tabindex="40"/>
            <br/>
            <form:errors path="location" cssClass="help-inline"/>
          </div>
        </div>

        <c:if test="${userId == editedUser.userId}">
          <div class="control-group">
            <label class="control-label"><spring:message code="label.currentPassword"/></label>

            <div class="controls">
              <form:input class="input-xlarge" type="password" path="currentUserPassword" tabindex="45"/>
              <br/>
              <form:errors path="currentUserPassword" cssClass="help-inline"/>
            </div>
          </div>
        </c:if>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.newPassword"/></label>

          <div class="controls">
            <form:input class="input-xlarge" type="password" path="newUserPassword" tabindex="50"/>
            <br/>
            <form:errors path="newUserPassword" cssClass="help-inline"/>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><spring:message code="label.newPasswordConfirmation"/></label>

          <div class="controls">
            <form:input class="input-xlarge" type="password" path="newUserPasswordConfirm" tabindex="55"/>
            <br/>
            <form:errors path="newUserPasswordConfirm" cssClass="help-inline"/>
          </div>
        </div>

        <hr class='user-profile-hr'/>
        <div class='user-profile-buttons-form-actions'>
          <button id="saveChanges" class="btn btn-primary" type="submit" tabindex="60">
            <spring:message code="label.save_changes"/>
          </button>
          <a href="${pageContext.request.contextPath}/users/${editedUser.userId}" class="btn space-left-medium"
             tabindex="60"><spring:message code="label.cancel"/>
          </a>
        </div>
      </fieldset>
    </div>
  </form:form>
  <div class="clearfix"></div>
</div>

<div class="userprofile user-profile-contacts-container">
  <h4><spring:message code="label.contacts"/></h4>
  <ul id='contacts' class="contacts">
    <c:forEach var="contact" items="${contacts}">
      <%-- Class 'contact' used in js for binding --%>
      <li class="contact">
        <input id="contactId" type="hidden" value="${contact.id}"/>
        <input id="contactOwnerId" type="hidden" value="${editedUser.userId}"/>
          <%-- Class 'button' used in js for binding --%>
        <a href="#" id="${contact.id}" class="btn btn-mini btn-danger button"
           title="<spring:message code='label.contacts.tips.delete'/>">
          <i class="icon-remove icon-white"></i>
        </a>

        <span class="contact" title="<c:out value='${contact.type.typeName}'/>">
            <img src="${pageContext.request.contextPath}${contact.type.icon}"
                 alt="<spring:message code='alt.contacts.contactType'/>">
            <span class="space-left-small">
                <jtalks:prepareLink incomingLink='${contact.displayValue}'/>
            </span>
        </span>
      </li>
    </c:forEach>
  </ul>

  <a id="add_contact" href="#" class="btn btn-mini btn-primary user-profile-buttons-addcontact">
    <spring:message code="label.contacts.addMore"/>
  </a>

  <div class="clearfix"></div>
</div>
</div>
</body>
