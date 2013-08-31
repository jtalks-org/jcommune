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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>

<div class="external-links-bar">
  <div class="external-links-container">
    <c:if test="${not empty forumComponent and sessionScope.adminMode == true}">
      <jtalks:hasPermission targetId="${forumComponent.id}" targetType="COMPONENT" permission="GeneralPermission.ADMIN">
        <span id="links_editor" data-placement="right" title='<fmt:message key="label.linksEditor"/>'
              class="icon-cog cursor-hand links_editor"></span>
      </jtalks:hasPermission>
    </c:if>
    <c:choose>
      <c:when test="${not empty externalLinks}">
        <span id="externalLinks">
          <c:forEach var="link" items="${externalLinks}">
            <span>
              <a title="<c:out value='${link.hint}'/>" href="<c:out value='${link.url}'/>"
                 id="big-screen-external-link-${link.id}">
                <c:out value="${link.title}"/>
              </a>
            </span>
          </c:forEach>
        </span>

      </c:when>
      <c:otherwise>
        <span id="externalLinks"></span>
      </c:otherwise>
    </c:choose>
  </div>
</div>