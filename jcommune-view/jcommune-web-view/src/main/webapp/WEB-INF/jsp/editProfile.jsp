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
<c:set var="isEditProfile" value="false"/>
<c:set var="isEditContacts" value="false"/>
<c:set var="isEditNotifications" value="false"/>
<c:set var="isEditSecurity" value="false"/>

<c:set var="formAction" value="${pageContext.request.contextPath}"/>
<c:if test="${editedUser.userProfileDto != null}">
  <c:set var="isEditProfile" value="true"/>
  <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/profile"/>
</c:if>
<c:if test="${editedUser.userContactsDto.contacts != null}">
  <c:set var="isEditContacts" value="true"/>
  <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/contacts"/>
</c:if>
<c:if test="${editedUser.userNotificationsDto != null}">
  <c:set var="isEditNotifications" value="true"/>
  <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/notifications"/>
</c:if>
<c:if test="${editedUser.userSecurityDto != null}">
  <c:set var="isEditSecurity" value="true"/>
  <c:set var="formAction" value="${pageContext.request.contextPath}/users/${editedUser.userId}/security"/>
</c:if>

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
     <c:when test="${isEditProfile}">
       class="btn space-left-medium profile-menu-btn selected-tab"
     </c:when>
     <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
    </c:choose>
    tabindex="60"><spring:message code="label.profile"/>
  </a>
  <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/contacts"
    <c:choose>
      <c:when test="${isEditContacts}">
        class="btn space-left-medium profile-menu-btn selected-tab"
      </c:when>
      <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
    </c:choose>
    tabindex="60"><spring:message code="label.contacts"/>
  </a>

  <c:if test="${isCanEditProfile || isCanEditNotificationsAndSecurity}">
    <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/notifications"
      <c:choose>
        <c:when test="${isEditNotifications}">
          class="btn space-left-medium profile-menu-btn selected-tab"
        </c:when>
        <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
      </c:choose>
     tabindex="60"><spring:message code="label.notifications"/>
    </a>
    <a href="${pageContext.request.contextPath}/users/${editedUser.userId}/security"
      <c:choose>
        <c:when test="${isEditSecurity}">
          class="btn space-left-medium profile-menu-btn selected-tab"
        </c:when>
        <c:otherwise>class="btn space-left-medium profile-menu-btn active"</c:otherwise>
      </c:choose>
      tabindex="60"><spring:message code="label.security"/></a>
  </c:if>
</div>
<div id="editUserDetails" class="userprofile">

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
    <c:when test="${isEditProfile}">
      <jsp:include page="editUserProfile.jsp">
        <jsp:param name="isCanEditProfile" value="${isCanEditProfile}"/>
        <jsp:param name="auth" value="${auth}"/>
        <jsp:param name="userId" value="${userId}"/>
      </jsp:include>
    </c:when>

     <%--Notifications--%>
    <c:when test="${isEditNotifications && isCanEditNotificationsAndSecurity}">
      <jsp:include page="editUserNotifications.jsp"/>
    </c:when>

    <%--Contacts--%>
    <c:when test="${isEditContacts}">
      <jsp:include page="editUserContacts.jsp">
        <jsp:param name="isCanEditProfile" value="${isCanEditProfile}"/>
      </jsp:include>
    </c:when>

    <c:when test="${isEditSecurity && isCanEditNotificationsAndSecurity}">
      <jsp:include page="editUserSecurity.jsp">
        <jsp:param name="userId" value="${userId}"/>
      </jsp:include>
    </c:when>
  </c:choose>

  <c:if test="${isCanEditProfile || (isCanEditNotificationsAndSecurity && (isEditSecurity || isEditNotifications))}">
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
