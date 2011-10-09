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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.user"/> - ${user.username}</title>
    <link href="${pageContext.request.contextPath}/resources/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>
<sec:authentication property="principal.username" var="auth" scope="request"/>

<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

<div id="userdetails">
    <ul>
        <li>
            <label><spring:message code="label.username"/></label>
            <span><c:out value="${user.username}"/></span>
        </li>
        <li>
            <label>Email</label>
            <c:choose>
                <c:when test="${user.username == auth}">
                    <span><c:out value="${user.email}"/></span>
                </c:when>
                <c:otherwise>
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <span><c:out value="${user.email}"/></span>
                    </sec:authorize>
                </c:otherwise>
            </c:choose>
        </li>
        <li>
            <label><spring:message code="label.firstname"/></label>
            <span><c:out value="${user.firstName}"/></span>
        </li>
        <li>
            <label><spring:message code="label.lastname"/></label>
            <span><c:out value="${user.lastName}"/></span>
        </li>
        <c:if test="${user.signature != null}">
            <li>
                <label><spring:message code="label.signature"/></label>
                <span><c:out value="${user.signature}"/></span>
            </li>
        </c:if>
        <li>
            <label><spring:message code="label.lastlogin"/></label>
            <span>
                <joda:format value="${user.lastLogin}"
                             locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                             pattern="dd MMM yyyy HH:mm"/>
            </span>
        </li>
        <c:if test="${user.avatar != null}">
            <li>
                <table>
                    <tr>
                        <td width="100" height="100" align="center" valign="middle">
                            <img src="${pageContext.request.contextPath}/${user.username}/avatar"/>
                        </td>
                    </tr>
                </table>
            </li>
        </c:if>
        <li>
            <label><spring:message code="label.postcount"/></label>
            <c:out value="${user.userPostCount}"/>
        </li>
        <li>
            <c:if test="${user.username == auth}">
                <a href="${pageContext.request.contextPath}/users/edit">
                    <label>Edit</label>
                </a>
            </c:if>
        </li>
    </ul>
</div>
</body>
</html>
