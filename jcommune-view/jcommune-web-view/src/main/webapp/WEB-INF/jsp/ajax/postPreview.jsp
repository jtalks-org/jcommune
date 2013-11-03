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
<%@ page contentType="application/json" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:hasBindErrors name="${beanName}">
    <c:set var="isInvalid" value="${errors.hasFieldErrors('bodyText')}" />
    <c:set var="errors" value="${errors}" />
</spring:hasBindErrors>
<json:object>
    <json:property name="is_invalid" value="${isInvalid}" />
    <c:choose>
        <c:when test="${isInvalid}">
        <json:array name="errors" var="message" escapeXml="false" items="${errors.getFieldErrors('bodyText')}">
            <json:object><json:property name="defaultMessage" value="${message.defaultMessage}" /></json:object>
        </json:array>
        </c:when>
        <c:otherwise>
            <json:property name="html" escapeXml="false">
                <jtalks:postContent text="${data.bodyText}" signature="${signature}"/>
            </json:property>
        </c:otherwise>
    </c:choose>
</json:object>