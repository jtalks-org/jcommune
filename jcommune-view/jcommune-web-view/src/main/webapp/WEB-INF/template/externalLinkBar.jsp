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

<div class="navbar">
    <div class="container-fluid">
        <c:if test="${not empty externalLinks}">
            <span id="externalLinks">
                <c:forEach var="link" items="${externalLinks}">
                    <a id="${link.id}" title="${link.hint}" href="${link.url}" name="${link.title}">
                        <c:out value="${link.title}"/>
                    </a>
                </c:forEach>
            </span>
        </c:if>
        <%--<c:if test="${not empty forumComponent}">--%>
        <jtalks:hasPermission targetId="${forumComponent.id}" targetType="COMPONENT"
                              permission="GeneralPermission.ADMIN">
            <span id="links_editor" title="$labelLinksEditor" class="icon-cog cursor-hand"></span>
        </jtalks:hasPermission>
        <%--
                </c:if>
        --%>
    </div>
</div>