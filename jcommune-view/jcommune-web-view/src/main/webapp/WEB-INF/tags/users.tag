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
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag body-content="empty" %>
<%@ attribute name="users" required="true" type="java.util.List" %>
<%@ attribute name="branch" required="false" type="org.jtalks.common.model.entity.Branch" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:choose>
  <c:when test="${!(empty users)}">
    <c:forEach var="user" items="${users}" varStatus="i">
      <c:set var='labelClass' value=''/>
      <a href="${pageContext.request.contextPath}/users/${user.id}"
         class="${labelClass} space-left-small"
         title="<spring:message code='label.tips.view_profile'/>"><c:out value="${user.username}"/></a>&thinsp;
    </c:forEach>
  </c:when>
  <c:otherwise>
    <spring:message code='label.branch.users.empty'/>
  </c:otherwise>
</c:choose>