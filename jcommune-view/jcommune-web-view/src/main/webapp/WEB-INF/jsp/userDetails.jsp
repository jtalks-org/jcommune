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
<%@ page contentType="text/html;charset=UTF-8" language="java"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<head>
  <meta name="description" content="<c:out value="${user.username}"/>">
  <title><spring:message code="label.user"/> - <c:out value="${user.username}"/></title>
</head>
<body>
<sec:authentication property="principal.username" var="auth" scope="request"/>
<sec:authentication property="principal.id" var="userId"/>

<div class="container">
<div id="userdetails" class="userprofile user-profile-container">
<div class="user-profile-header">
                <span class="pull-left thumbnail">
                    <span class="wraptocenter">
                        <img src="${pageContext.request.contextPath}/users/${user.id}/avatar" alt=""/>
                    </span>
                </span>

  <h2 class="pull-right user-profile-username">
    <span class='test-username'><c:out value="${user.username}"/></span>
  </h2>
</div>

<div class="clearfix"></div>
<div class="user-profile-top-buttons">
  <c:if test="${user.username != auth}">
    <jtalks:hasPermission targetId='${userId}' targetType='USER' permission='ProfilePermission.SEND_PRIVATE_MESSAGES'>
      <div class="user-profile-buttons-send">
        <a class="btn btn-mini btn-info"
           href="${pageContext.request.contextPath}/pm/new/${user.id}">
          <spring:message code="label.pm.send"/>
        </a>
      </div>
    </jtalks:hasPermission>
  </c:if>

  <a class="btn btn-mini pull-right user-profile-buttons-posts"
     href="${pageContext.request.contextPath}/users/${user.id}/postList">
    <spring:message code="label.postList"/>
  </a>
</div>

<div class="clearfix"></div>
<hr class="user-profile-hr"/>

<div>
  <form class="form-horizontal">
    <fieldset>
      <div class="control-group">
        <label class="control-label"> <spring:message
                code="label.firstname"/>
        </label>

        <div class="controls">
          <label class="input-xlarge box-label test-firstname"> <c:out
                  value='${user.firstName}'/>
          </label>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label"> <spring:message
                code="label.lastname"/>
        </label>

        <div class="controls">
          <label class="input-xlarge box-label test-lastname"> <c:out
                  value='${user.lastName}'/>
          </label>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label"> <spring:message
                code="label.signature"/>
        </label>

        <div class="controls">
          <label class="input-xlarge box-label test-signature"> <jtalks:bb2html
                  bbCode='${user.signature}'/>
          </label>
        </div>
      </div>

      <%--Do not show my email to other users without permissions--%>
      <c:set var="isCanEditProfile" value="false"/>
      <c:set var="isShowAllFields" value="false"/>
      <c:if test="${user.username != auth}">
        <jtalks:hasPermission targetId='${userId}' targetType='USER'
                              permission='ProfilePermission.EDIT_OTHERS_PROFILE'>
          <c:set var="isCanEditProfile" value="true"/>
          <c:set var="isShowAllFields" value="true"/>
        </jtalks:hasPermission>
      </c:if>
      <c:if test="${user.username == auth}">
        <jtalks:hasPermission targetId='${userId}' targetType='USER'
                              permission='ProfilePermission.EDIT_OWN_PROFILE'>
          <c:set var="isCanEditProfile" value="true"/>
          <c:set var="isShowAllFields" value="true"/>
        </jtalks:hasPermission>
      </c:if>

      <c:if test="${isShowAllFields}">
        <div class="control-group">
          <label class="control-label"> <spring:message
                  code="label.email"/>
          </label>

          <div class="controls">
            <label class="input-xlarge box-label test-mail"> <c:out
                    value='${user.email}'/>
            </label>
          </div>
        </div>
        <div class="control-group">
          <label class="control-label"> <spring:message
                  code="label.language"/>
          </label>

          <div class="controls">
            <label class="input-xlarge box-label test-language"> <spring:message
                    code='${language.languageNameLabel}'/>
            </label>
          </div>
        </div>
        <div class="control-group">
          <label class="control-label"> <spring:message
                  code="label.pageSize"/>
          </label>

          <div class="controls">
            <label class="input-xlarge box-label test-pagesize"> <c:out
                    value='${user.pageSize}'/>
            </label>
          </div>
        </div>
        <div class="control-group">
          <label class="control-label"> <spring:message
                  code="label.autosubscribe"/>
          </label>

          <div class="controls">
            <label class="test-autosubscribe"></label>
            <form:checkbox path="user.autosubscribe" value="${user.autosubscribe}"
                           class="form-check-radio-box" disabled="true"/>
          </div>
        </div>
        <div class="control-group">
          <label class="control-label"> <spring:message
                  code="label.mentioning.notifications.enabled"/>
          </label>

          <div class="controls">
            <label class="test-mentioningNotificationsEnabled"></label>
            <form:checkbox path="user.mentioningNotificationsEnabled"
                           value="${user.mentioningNotificationsEnabled}"
                           class="form-check-radio-box" disabled="true"/>
          </div>
        </div>
        <div class="control-group">
            <label class="control-label"><spring:message code="label.send.pm.notification.enabled"/></label>
            <div class="controls">
                  <label class="test-sendPmNotification"></label>
                  <form:checkbox path="user.sendPmNotification"
                                 value="${user.sendPmNotification}"
                                 class="form-check-radio-box" disabled="true"/>
            </div>
        </div>
      </c:if>
      <div class="control-group">
        <label class="control-label"> <spring:message
                code="label.location"/>
        </label>

        <div class="controls">
          <label class="input-xlarge box-label test-location "> <c:out value='${user.location}'/>
          </label>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label"> <spring:message
                code="label.registrationDate"/>
        </label>

        <div class="controls">
          <label class="input-xlarge box-label test-registrationdate">
            <jtalks:format value='${user.registrationDate}'/>
          </label>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label"> <spring:message
                code="label.lastlogin"/>
        </label>

        <div class="controls">
          <label class="input-xlarge box-label test-lastlogin"> <jtalks:format
                  value='${user.lastLogin}'/>
          </label>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label user-profile-labels-postcount">
          <spring:message code="label.postcount"/>
        </label>

        <div class="controls">
                                <span class="label label-info test-posts-count"><c:out
                                        value="${user.postCount}"/></span>
        </div>
      </div>

      <c:if test="${!empty user.userContacts}">
        <hr class="user-profile-hr"/>

        <h4>
          <spring:message code="label.contacts.header"/>
        </h4>
        <ul id="contacts" class="contacts">
          <c:forEach var="contact" items="${user.userContacts}">
            <li><span class="contact"
                    title="<c:out value='${contact.type.typeName}'/>">
                  <img src="${pageContext.request.contextPath}${contact.type.icon}"
                       title="<c:out value="${contact.type.typeName}"/>">
                  <span class="space-left-small">
                    <jtalks:prepareLink incomingLink='${contact.displayValue}'/>
                  </span>
                </span>
            </li>
          </c:forEach>
        </ul>
      </c:if>

      <c:if test="${isCanEditProfile}">
        <div class="user-profile-buttons-form-actions">
          <a class="btn btn-primary"
             href="${pageContext.request.contextPath}/users/edit/${user.id}"> <spring:message
                  code="label.edit_profile"/>
          </a>
        </div>
      </c:if>
    </fieldset>
  </form>
</div>
<div class="clearfix"></div>
</div>
</div>
</body>
