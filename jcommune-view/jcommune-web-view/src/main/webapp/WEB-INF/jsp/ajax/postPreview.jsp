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
<%@ page contentType="application/json" language="java" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<json:object>
    <json:property name="is_errors" value="${result.fieldErrorCount['bodyText']}" />
    <json:property name="html" escapeXml="false">
        <c:choose>
            <c:when test="${result.getFieldErrorCount('bodyText') == 0}"><jtalks:postContent text="${text}" signature="${signature}"/></c:when>
            <c:when test="${result.getFieldErrorCount('bodyText') > 0}">
                <div class="errors">
                <c:forEach var="message" items="${result.getFieldErrors('bodyText')}">
                    <div class="help-inline">${message}</div>
                </c:forEach>
                </div>
            </c:when>
        </c:choose>
    </json:property>
</json:object>