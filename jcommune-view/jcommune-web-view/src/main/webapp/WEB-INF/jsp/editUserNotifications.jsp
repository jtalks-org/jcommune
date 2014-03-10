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