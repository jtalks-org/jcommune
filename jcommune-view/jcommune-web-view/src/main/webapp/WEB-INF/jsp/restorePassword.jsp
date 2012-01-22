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
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<head>
    <title><spring:message code="label.restorePassword.header"/></title>
</head>
<div class="wrap registration_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>
    <div class="all_forums">
        <h2 class="heading"><spring:message code="label.restorePassword.header"/></h2>
        <br>
        <form:form id="form" name="form" modelAttribute="dto"
                   action='${pageContext.request.contextPath}/password/restore' method="POST">

            <p><spring:message code="label.restorePassword.text"/></p>

            <table>
                <tr>
                    <td>

                        <spring:message code="label.email"/>

                    </td>
                    <td>
                        <form:input path="email" type="text" size="20"/>
                        <br/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <form:errors path="email" cssClass="error"/>
                        <c:if test="${not empty message}">
                            <spring:message code="${message}"/>
                        </c:if>
                    </td>
                </tr>

            </table>

            <button type="submit"><spring:message code="label.send"/></button>
            <div class="spacer"></div>
        </form:form>
    </div>
</div>