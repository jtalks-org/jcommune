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
<%@ attribute name="uri" required="true" type="java.lang.String" %>
<%@ attribute name="page" required="true" type="org.springframework.data.domain.Page" %>
<%--An additional parameter is needed in case the request, except the "page", to send other parameters.
 They will add to "page" parameters. for example "&somaName=someValue"--%>
<%@ attribute name="additionalParams" required="false" type="java.util.HashMap" %>
<%@ attribute name="numberLink" required="false" type="java.lang.Integer" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>

<%--Set default value for numberLink attribute, if it it wasn't passed.--%>
<c:if test="${empty numberLink}">
  <c:set var="numberLink" value="3"/>
</c:if>

<c:if test="${page.number > 1}">
  <jtalks:pageUrl var="link" value="${uri}" page="1" params="${additionalParams}"/>
  <li>
    <a href="${link}" title="<spring:message code='pagination.first'/>">
      <span>&laquo;</span>
    </a>
  </li>
</c:if>

<c:forEach var="i" begin="1" step="1" end="${numberLink}">
  <%--JSTL doesn't have reverse for-each, therefore this trick used.--%>
  <c:set var="j" value="${numberLink - i + 1}"/>
  <c:if test="${page.number > j}">
    <jtalks:pageUrl var="link" value="${uri}" page="${page.number - j}" params="${additionalParams}"/>
    <li><a href="${link}">${page.number - j}</a></li>
  </c:if>
</c:forEach>

<%--Link to current page is disabled.--%>
<c:if test="${page.totalPages > 1}">
  <li class='active'><a href='#'><c:out value="${page.number}"/></a></li>
</c:if>

<c:forEach var="i" begin="0" step="1" end="${numberLink - 1}">
  <c:if test="${page.number + i < page.totalPages}">
    <jtalks:pageUrl var="link" value="${uri}" page="${page.number + i + 1}" params="${additionalParams}"/>
    <li><a href="${link}">${page.number + i + 1}</a></li>
  </c:if>
</c:forEach>

<c:if test="${page.number < page.totalPages}">
  <jtalks:pageUrl var="link" value="${uri}" page="${page.totalPages}" params="${additionalParams}"/>
  <li>
    <a href="${link}" title="<spring:message code='pagination.last'/>">
      <span>&raquo;</span>
    </a>
  </li>
</c:if>
