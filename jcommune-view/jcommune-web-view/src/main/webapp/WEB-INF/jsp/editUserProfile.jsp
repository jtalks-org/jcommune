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
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>

<form:hidden path="userProfileDto.userId" value="${editedUser.userProfileDto.userId}"/>

<div class="user-profile-top-buttons">
  <c:if test="${param.isCanEditProfile}">
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

  <c:if test="${editedUser.username != param.auth}">
    <jtalks:hasPermission targetId='${param.userId}' targetType='USER' permission='ProfilePermission.SEND_PRIVATE_MESSAGES'>
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
      <c:when test="${param.isCanEditProfile}">
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

    <c:if test="${param.isCanEditProfile}">
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
          <c:when test="${param.isCanEditProfile}">
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

    <c:if test="${param.isCanEditProfile}">
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