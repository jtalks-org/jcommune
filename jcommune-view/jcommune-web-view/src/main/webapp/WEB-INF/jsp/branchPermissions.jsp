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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
  <meta name="description" content="<c:out value="${cmpDescription}"/>">
  <title>
    <c:out value="${cmpTitlePrefix}"/>
    <c:out value="${cmpDescription}"/>
  </title>
</head>
<body>
<div class="container">
  <div class="permissions-branch-header">
    <h1>${branch.name}</h1>
  </div>

  <div class="permissions-branch-header">
    <h3>
      <spring:message code="permissions.moderators"/>:
      <c:if test="${not empty branch.moderatorsGroup.name}">
        <span id="moderators-group-name">["${branch.moderatorsGroup.name}"]</span><spring:message code="permissions.group"/>
      </c:if>
    </h3>
  </div>

  <div class="permissions">
    <c:forEach items="${permissions.permissions}" var="entry">
      <div class="panel panel-primary">
        <div class="panel-heading">${entry.name}</div>
        <div class="panel-body">
          <div class="pull-left permission-type permission-allowed">
            <spring:message code="permissions.allowed"/>
          </div>
          <div class="pull-right edit-permission">
            <a class="btn editAllowedPermission"
               data-permission="${entry.mask}"
               data-branch="${branch.id}"
               data-permission-name="${entry.name}"
               href="#">
              </span><spring:message code="label.edit"/>
            </a>
          </div>
          <div class="permissions-container">
            <ul class="permissions-list">
              <c:forEach items="${permissions.accessListMap[entry].allowed}" var="group">
                <li> ${group.name}</li>
              </c:forEach>
            </ul>
          </div>

          <div class="cleared"></div>

          <div class="pull-left permission-type permission-restricted">
            <spring:message code="permissions.restricted"/>
          </div>
          <div class="pull-right edit-permission">
            <a class="btn editRestrictedPermission"
               data-permission="${entry.mask}"
               data-branch="${branch.id}"
               data-permission-name="${entry.name}"
               href="#">
              </span><spring:message code="label.edit"/>
            </a>
          </div>
          <div class="permissions-container">
            <ul class="permissions-list">
              <c:forEach items="${permissions.accessListMap[entry].restricted}" var="group">
                <li> ${group.name}</li>
              </c:forEach>
            </ul>
          </div>
          <div class="cleared"></div>
        </div>
      </div>
    </c:forEach>
  </div>
</div>
</body>