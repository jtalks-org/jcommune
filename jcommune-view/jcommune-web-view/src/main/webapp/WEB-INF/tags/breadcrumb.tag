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
<%@ tag body-content="empty" %>
<%@ attribute name="breadcrumbList" required="true" rtexprvalue="true" type="java.util.ArrayList" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<ul class="breadcrumb">
  <c:forEach var="breadcrumb" items="${breadcrumbList}" varStatus="loop">
    <li>
      <c:if test="${loop.index == 1}">
        <h3 class="h-nostyle">
      </c:if>
      <c:if test="${loop.index == 2}">
        <h2 class="h-nostyle">
      </c:if>
      <c:choose>
            <%--create root breadcrumb--%>
        <c:when test="${breadcrumb.value == 'Forum'}">
          <a href="${pageContext.request.contextPath}/">
            <fmt:message key="label.forum"/>
          </a>
        </c:when>
            <%--create section, topic, branch, post breadcrumb (items with id)--%>
        <c:otherwise>
          <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}/${breadcrumb.id}">
            <c:out value="${breadcrumb.value}"/>
          </a>
        </c:otherwise>
      </c:choose>
      <c:if test='${!loop.last}'>
        <span class="divider">/</span>
      </c:if>
      <c:if test="${loop.index == 1}">
        </h3>
      </c:if>
      <c:if test="${loop.index == 2}">
        </h2>
      </c:if>
    </li>
  </c:forEach>
</ul>