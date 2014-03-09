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
<c:set var="isCanEditProfile" value="false"/>
<c:set var="isCanEditNotificationsAndSecurity" value="false"/>
<c:if test="${editedUser.username != auth}">
  <jtalks:hasPermission targetId='${userId}' targetType='USER' permission='ProfilePermission.EDIT_OTHERS_PROFILE'>
    <c:set var="isCanEditProfile" value="true"/>
    <c:set var="isCanEditNotificationsAndSecurity" value="true"/>
  </jtalks:hasPermission>
</c:if>
<c:if test="${editedUser.username == auth}">
  <c:set var="isCanEditNotificationsAndSecurity" value="true"/>
  <jtalks:hasPermission targetId='${userId}' targetType='USER' permission='ProfilePermission.EDIT_OWN_PROFILE'>
    <c:set var="isCanEditProfile" value="true"/>
  </jtalks:hasPermission>
</c:if>

<div class="container">

<div class="user-profile-container">
<div id="profileMenu" class="user-profile-menu">

  <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/profile"
    <c:choose>
     <c:when test="${editedUser.userProfileDto != null}">
       class="btn space-left-medium profile-menu-btn selected-tab"
     </c:when>
     <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
    </c:choose>
    tabindex="60"><spring:message code="label.profile"/>
  </a>
  <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/contacts"
    <c:choose>
      <c:when test="${editedUser.userContactsDto.contacts != null}">
        class="btn space-left-medium profile-menu-btn selected-tab"
      </c:when>
      <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
    </c:choose>
    tabindex="60"><spring:message code="label.contacts"/>
  </a>

  <c:if test="${isCanEditProfile || isCanEditNotificationsAndSecurity}">
    <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/notifications"
      <c:choose>
        <c:when test="${editedUser.userNotificationsDto != null}">
          class="btn space-left-medium profile-menu-btn selected-tab"
        </c:when>
        <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
      </c:choose>
     tabindex="60"><spring:message code="label.notifications"/>
    </a>
    <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/security"
      <c:choose>
        <c:when test="${editedUser.userSecurityDto != null}">
          class="btn space-left-medium profile-menu-btn selected-tab"
        </c:when>
        <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
      </c:choose>
      tabindex="60"><spring:message code="label.security"/></a>
  </c:if>
</div>
<div id="editUserDetails" class="userprofile">

<c:set var="formAction" value="${pageContext.request.contextPath}"/>
<c:choose>
  <c:when test="${editedUser.userProfileDto != null}">
    <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/profile"/>
  </c:when>
  <c:when test="${editedUser.userSecurityDto != null}">
    <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/security"/>
  </c:when>
  <c:when test="${editedUser.userNotificationsDto != null}">
    <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/notifications"/>
  </c:when>
  <c:when test="${editedUser.userContactsDto.contacts != null}">
    <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/contacts"/>
  </c:when>
</c:choose>

<form:form id="editProfileForm" name="editProfileForm" action="${formAction}"
           modelAttribute="editedUser" method="POST" class="form-horizontal">

  <div class='user-profile-header'>
    <form:hidden id="avatar" path="avatar" value="${editedUser.avatar}"/>
    <form:hidden id="editedUserId" path="userId" value="${editedUser.userId}"/>
    <form:hidden id="editedUsername" path="username" value="${editedUser.username}"/>
    <span class="pull-left thumbnail">
      <span id="avatarPreviewContainer" class="wraptocenter">
        <%--String prefix "data:image/jpeg;base64," needed for correct image rendering--%>
        <img id="avatarPreview" src="data:image/jpeg;base64,${editedUser.avatar}" alt=""/>
      </span>
    </span>

    <h2 class="pull-right user-profile-username"><c:out value="${editedUser.username}"/></h2>
  </div>
  <div class="clearfix"></div>

  <div class="clearfix"></div>

  <c:choose>
    <%--Profile--%>
    <c:when test="${editedUser.userProfileDto != null}">
      <form:hidden path="userProfileDto.userId" value="${editedUser.userProfileDto.userId}"/>

      <div class="user-profile-top-buttons">
        <c:if test="${isCanEditProfile}">
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
        </c:if>

        <c:if test="${editedUser.username != auth}">
          <jtalks:hasPermission targetId='${userId}' targetType='USER' permission='ProfilePermission.SEND_PRIVATE_MESSAGES'>
            <div class="user-profile-buttons-send">
              <a class="btn btn-mini btn-info"
                 href="${pageContext.request.contextPath}/pm/new?recipientId=${editedUser.userId}">
                <spring:message code="label.pm.send"/>
              </a>
            </div>
          </jtalks:hasPermission>
        </c:if>
        <a class="btn btn-mini pull-right user-profile-buttons-posts"
           href="${pageContext.request.contextPath}/users/${editedUser.userId}/postList">
          <spring:message code="label.postList"/>
        </a>
      </div>

      <div class="clearfix"></div>
      <hr class='user-profile-hr'/>

      <div>
        <fieldset>
          <c:choose>
            <c:when test="${isCanEditProfile}">
              <div class="control-group">
                <label class="control-label"><spring:message code="label.firstname"/></label>
                <div class="controls">
                  <form:input class="input-xlarge" path="userProfileDto.firstName"
                              value="${editedUser.userProfileDto.firstName}" tabindex="1"/>
                  <br/>
                  <form:errors path="userProfileDto.firstName" cssClass="help-inline"/>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label"><spring:message code="label.lastname"/></label>
                <div class="controls">
                  <form:input class="input-xlarge" path="userProfileDto.lastName"
                              value="${editedUser.userProfileDto.lastName}" tabindex="5"/>
                  <br/>
                  <form:errors path="userProfileDto.lastName" cssClass="help-inline"/>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label"><spring:message code="label.signature"/></label>
                <div class="controls">
                  <form:textarea class="input-xlarge" path="userProfileDto.signature"
                                 value="${editedUser.userProfileDto.signature}" tabindex="10"/>
                  <br/>
                  <form:errors path="userProfileDto.signature" cssClass="help-inline"/>
                </div>
              </div>
            </c:when>
            <c:otherwise>
              <div class="control-group">
                <label class="control-label"><spring:message code="label.firstname"/></label>
                <div class="controls">
                  <label class="input-xlarge box-label test-firstname">
                    <c:out value='${editedUser.userProfileDto.firstName}'/>
                  </label>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label"><spring:message code="label.lastname"/></label>
                <div class="controls">
                  <label class="input-xlarge box-label test-lastname">
                    <c:out value='${editedUser.userProfileDto.lastName}'/>
                  </label>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label"><spring:message code="label.signature"/></label>
                <div class="controls">
                  <label class="input-xlarge box-label test-signature">
                    <jtalks:bb2html bbCode='${editedUser.userProfileDto.signature}'/>
                  </label>
                </div>
              </div>
            </c:otherwise>
          </c:choose>

          <c:if test="${isCanEditProfile}">
            <div class="control-group">
              <label class="control-label"><spring:message code="label.email"/></label>
              <div class="controls">
                  <form:input class="input-xlarge" path="userProfileDto.email" tabindex="15"/><br/>
                  <form:errors path="userProfileDto.email" cssClass="help-inline"/>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label"><spring:message code="label.pageSize"/></label>
              <div class="controls">
                <form:select path="userProfileDto.pageSize" items="${editedUser.pageSizesAvailable}"
                             class="input-mini" tabindex="25"/><br/>
                <form:errors path="userProfileDto.pageSize" cssClass="help-inline"/>
              </div>
            </div>
          </c:if>

          <div class="control-group">
            <label class="control-label"><spring:message code="label.location"/></label>
            <div class="controls">
              <c:choose>
                <c:when test="${isCanEditProfile}">
                  <form:input path="userProfileDto.location" class="input-xlarge"
                              value="${editedUser.userProfileDto.location}" tabindex="40"/><br/>
                  <form:errors path="userProfileDto.location" cssClass="help-inline"/>
                </c:when>
                <c:otherwise>
                  <label class="input-xlarge box-label test-location ">
                    <c:out value='${editedUser.userProfileDto.location}'/>
                  </label>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

          <c:if test="${isCanEditProfile}">
            <div class="control-group">
              <label class="control-label"> <spring:message code="label.registrationDate"/>
              </label>

              <div class="controls">
                <label class="input-xlarge box-label test-registrationdate">
                  <jtalks:format value='${editedUser.userProfileDto.registrationDate}'/>
                </label>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label"> <spring:message code="label.lastlogin"/>
              </label>

              <div class="controls">
                <label class="input-xlarge box-label test-lastlogin">
                  <jtalks:format value='${editedUser.userProfileDto.lastLogin}'/>
                </label>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label user-profile-labels-postcount">
                <spring:message code="label.postcount"/>
              </label>
              <div class="controls">
                <span class="label label-info test-posts-count">
                  <c:out value="${editedUser.userProfileDto.postCount}"/>
                </span>
              </div>
            </div>
          </c:if>
        </fieldset>
      </div>
    </c:when>

     <%--Notifications--%>
    <c:when test="${editedUser.userNotificationsDto != null && isCanEditNotificationsAndSecurity}">
        <form:hidden path="userNotificationsDto.userId" value="${editedUser.userNotificationsDto.userId}"/>

        <div class="clearfix"></div>
        <hr class='user-profile-hr'/>

        <div>
          <fieldset>
            <div class="control-group notification-control">
              <label class="control-label"><spring:message code="label.autosubscribe"/></label>
              <div class="controls padding-top-profile">
                <spring:message var="autosubscribeTooltip" code="label.tips.autoSubscribe"/>
                <form:checkbox path="userNotificationsDto.autosubscribe" class="form-check-radio-box script-has-tooltip"
                               value="${editedUser.userNotificationsDto.autosubscribe}"
                               data-original-title='${autosubscribeTooltip}' tabindex="30"/>
              </div>
            </div>

            <div class="control-group notification-control">
              <label class="control-label"><spring:message code="label.mentioning.notifications.enabled"/></label>
              <div class="controls padding-top-profile">
                <spring:message var="mentioningNotificationsTooltip" code="label.tips.userMentioningNotification"/>
                <form:checkbox path="userNotificationsDto.mentioningNotificationsEnabled"
                               value="${editedUser.userNotificationsDto.mentioningNotificationsEnabled}"
                               class="form-check-radio-box script-has-tooltip"
                               data-original-title='${mentioningNotificationsTooltip}' tabindex="35"/>
              </div>
            </div>

            <div class="control-group notification-control">
              <label class="control-label"><spring:message code="label.send.pm.notification.enabled"/></label>
              <div class="controls padding-top-profile">
                <spring:message var="sendPmNotificationTooltip" code="label.tips.sendPmNotification"/>
                <form:checkbox path="userNotificationsDto.sendPmNotification"
                               value="${editedUser.userNotificationsDto.sendPmNotification}"
                               class="form-check-radio-box script-has-tooltip"
                               data-original-title='${sendPmNotificationTooltip}' tabindex="36"/>
              </div>
            </div>

          </fieldset>
        </div>
    </c:when>

    <%--Contacts--%>
    <c:when test="${editedUser.userContactsDto.contacts != null}">

      <div class="clearfix"></div>
      <hr class='user-profile-hr'/>

        <c:choose>
          <c:when test="${isCanEditProfile}">
            <h4><spring:message code="label.contacts"/></h4>
            <ul id='contacts' class="contacts">
              <c:forEach var="contact" items="${editedUser.userContactsDto.contacts}" varStatus="loop">
                <%-- Class 'contact' used in js for binding --%>
                <li class="contact">
                  <div class="control-group">
                    <input id="contactId" type="hidden" value="${contact.id}"/>
                      <%-- Class 'button' used in js for binding --%>
                    <a href="#" id="${contact.id}" class="btn btn-mini btn-danger button"
                       title="<spring:message code='label.contacts.tips.delete'/>">
                      <i class="icon-remove icon-white"></i>
                    </a>

                    <span class="contact" title="<c:out value='${contact.type.typeName}'/>">
                        <form:hidden path="userContactsDto.contacts[${loop.index}].id" value="${contact.id}"/>
                    </span>
                    <div class="controls">
                      <form:select class="input-medium" path="userContactsDto.contacts[${loop.index}].type.id"
                                   items="${editedUser.userContactsDto.contactTypes}" />
                      <form:input class="input-large" type="text" path="userContactsDto.contacts[${loop.index}].value"
                                  tabindex="45" value="${contact.value}"/>
                      <br/>
                      <form:errors path="userContactsDto.contacts[${loop.index}]" cssClass="help-inline contact-error"/>
                    </div>
                  </div>
                </li>
              </c:forEach>
            </ul>

            <a id="add_contact" href="#" class="btn btn-mini btn-primary user-profile-buttons-addcontact">
              <spring:message code="label.contacts.addMore"/>
            </a>
          </c:when>

          <c:otherwise>
            <h4>
              <spring:message code="label.contacts.header"/>
            </h4>
            <c:if test="${!empty editedUser.userContactsDto.contacts}">
              <ul id="contacts" class="contacts">
                <c:forEach var="contact" items="${editedUser.userContactsDto.contacts}">
                  <li><span class="contact">
                  <img src="${pageContext.request.contextPath}${contact.type.icon}"
                       alt="<c:out value="${contact.type.typeName}"/>" title="<c:out value="${contact.type.typeName}"/>">
                  <span class="space-left-small">
                    <jtalks:prepareLink incomingLink='${contact.displayValue}'/>
                  </span>
                </span>
                  </li>
                </c:forEach>
              </ul>
            </c:if>
          </c:otherwise>
        </c:choose>
    </c:when>

    <c:when test="${editedUser.userSecurityDto != null && isCanEditNotificationsAndSecurity}">
      <form:hidden path="userSecurityDto.userId" value="${editedUser.userSecurityDto.userId}"/>

      <div class="clearfix"></div>
      <hr class='user-profile-hr'/>

      <div>
        <fieldset>
          <c:if test="${userId == editedUser.userSecurityDto.userId}">
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
    </c:when>
  </c:choose>

  <c:if test="${isCanEditProfile || (isCanEditNotificationsAndSecurity &&
                                    (editedUser.userSecurityDto != null || editedUser.userNotificationsDto != null))}">
    <hr class='user-profile-hr'/>
    <div class='user-profile-buttons-form-actions'>
      <button id="saveChanges" class="btn btn-primary" type="submit" tabindex="60">
        <spring:message code="label.save_changes"/>
      </button>
      <a href="${pageContext.request.contextPath}/users/${editedUser.userId}" class="btn space-left-medium"
         tabindex="60"><spring:message code="label.cancel"/>
      </a>
    </div>
  </c:if>
</form:form>
</div>
</div>
</div>
</body>
