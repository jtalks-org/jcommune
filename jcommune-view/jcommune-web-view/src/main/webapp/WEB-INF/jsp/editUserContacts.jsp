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

<div class="clearfix"></div>
<hr class='user-profile-hr'/>

<c:choose>
  <c:when test="${param.isCanEditProfile}">
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
                           tabindex="${loop.index + 1}" items="${editedUser.userContactsDto.contactTypes}" />
              <form:input class="input-large" type="text" path="userContactsDto.contacts[${loop.index}].value"
                          tabindex="${loop.index + 1}" value="${contact.value}"/>
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