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
<%@ attribute name="section" required="true" type="org.jtalks.common.model.entity.Section" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%--variables --%>
<c:set var="visibleBranchesCount" value="0"/>
<%--calculate the count of visible branches --%>
<c:forEach var="branch" items="${section.branches}" varStatus="i">
  <jtalks:hasPermission targetId='${branch.id}' targetType='BRANCH'
                        permission='BranchPermission.VIEW_TOPICS'>
    <c:set var="visibleBranchesCount" value="${visibleBranchesCount + 1}"/>
  </jtalks:hasPermission>
</c:forEach>
<%--We must show the section that contains at least one visible branch or in admin mode--%>
<c:if test="${visibleBranchesCount > 0 || sessionScope.adminMode == true}">
  <jsp:doBody/>
</c:if>
