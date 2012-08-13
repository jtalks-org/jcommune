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
<%@ attribute name="pagingEnabled" required="true" type="java.lang.Boolean" %>
<%@ attribute name="numberLink" required="false" type="java.lang.Integer" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${pagingEnabled}">
	<%--Set default value for numberLink attribute, if it it wasn't passed.--%>
	<c:if test="${empty numberLink}" >
		<c:set var="numberLink" value="6" />
	</c:if>
	
	<%--First page and previous page --%>
	<c:if test="${page.number > 1}">
	 	<li><a href='${uri}?page=1' 
	 		   title="<spring:message code="label.first"/>"><c:out value="<<"/></a></li>
		<li><a href='${uri}?page=${page.number - 1}' 
			   title="<spring:message code="label.previous"/>"><c:out value="<"/></a></li>
	</c:if>
	
	<%----%>
	<c:forEach var="i" begin="1" step="1" end="${numberLink}">
		<%--JSTL doesn't have reverse for-each, therefore this trick used.--%>
	 	<c:set var="j" value="${numberLink - i + 1}" />
	 	<c:if test="${page.number > j}">
	 		<li><a href='${uri}?page=${page.number - j}'>${page.number - j}</a></li>
	 	</c:if>
	</c:forEach>
	
	<%--Link to current page is disabled.--%>
	<c:if test="${page.totalPages > 1}">
		<li class='active'><a href='#'><c:out value="${page.number}"/></a></li>
	</c:if>
	
	<c:forEach var="i" begin="0" step="1" end="${numberLink - 1}">
		<c:if test="${page.number + i < page.totalPages}">
			<li><a href='${uri}?page=${page.number + i + 1}'>${page.number + i + 1}</a></li>
		</c:if>
	</c:forEach>
	
	<%--Last page and next page --%>
	<c:if test="${page.number < page.totalPages}">
		<li><a href='${uri}?page=${page.number + 1}'
			   title="<spring:message code="label.next"/>"><c:out value=">"/></a></li>
	 	<li><a href='${uri}?page=${page.totalPages}'
	 		   title="<spring:message code="label.last"/>"><c:out value=">>"/></a></li>
	</c:if>
</c:if>
