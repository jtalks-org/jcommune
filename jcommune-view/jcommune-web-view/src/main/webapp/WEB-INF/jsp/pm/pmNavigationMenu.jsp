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
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="forum_header_table">
    <div class="forum_header">
        <a class="forum_header_menu" id="inbox_link" href="${pageContext.request.contextPath}/inbox">
            <spring:message code="label.inbox"/></a>
        <a class="forum_header_menu" id="outbox_link" href="${pageContext.request.contextPath}/outbox">
            <spring:message code="label.outbox"/></a>
        <a class="forum_header_menu" id="newmsg_link" href="${pageContext.request.contextPath}/pm/new">
            <spring:message code="label.new_pm"/></a>
        <a class="forum_header_menu" id="draft_link"
           href="${pageContext.request.contextPath}/drafts">
            <spring:message code="label.drafts"/></a>
        <span class="empty_cell"></span>
    </div>
</div>

