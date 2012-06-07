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
<div class="top_line">
    <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
        <fmt:message key="label.welcomeMessage"/>
        <a class="currentusername" href="${pageContext.request.contextPath}/user">
            <sec:authentication property="principal.username"/>
        </a>!
    </sec:authorize>
    <ul class="top_menu">
        <li class="no_border">
            <a href="${pageContext.request.contextPath}/">
                <fmt:message key="label.forum"/>
            </a>
        </li>
        <sec:authorize access="hasAnyRole('ROLE_ADMIN,ROLE_USER')">
            <li>
                <a href="${pageContext.request.contextPath}/user">
                    <fmt:message key="label.profile"/>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/inbox">
                    <fmt:message key="label.pm"/><c:if test="${newPmCount != null}">(${newPmCount})</c:if>
                </a>
            </li>
            <li>
                <a href="#">
                    <fmt:message key="label.newbies"/>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/logout">
                    <fmt:message key="label.logout"/>
                </a>
            </li>
        </sec:authorize>
        <sec:authorize access="hasRole('ROLE_ANONYMOUS')">
            <li>
                <a id="signin" href="${pageContext.request.contextPath}/login">
                    <fmt:message key="label.signin"/>
                </a>
            </li>
            <li>
                <a id="signup" href="${pageContext.request.contextPath}/user/new">
                    <fmt:message key="label.signup"/>
                </a>
            </li>
        </sec:authorize>
        <li class="no_border">
            <div class="lighter">
                <form class="searchform" action='<c:url value="/search"/>' method="GET" name="form" id="form">
					<span><input type="text" name="searchText" class="search rounded"
                                 placeholder="<fmt:message key="label.search"/>" maxlength="50"
                                 value="<c:out value="${searchText}"/>"></span>
                </form>
            </div>
        </li>
        <li class="flag no_border">
            <a href="#" onclick="window.location = getLanguageLink('en')">
                <img src="${pageContext.request.contextPath}/resources/images/flags/great britain.png" alt=""/>
            </a>
        </li>
        <li class="flag">
            <a href="#" onclick="window.location = getLanguageLink('ru')">
                <img src="${pageContext.request.contextPath}/resources/images/flags/russia.png" alt=""/>
            </a>
        </li>
        <li class="flag">
            <a href="#" onclick="window.location = getLanguageLink('uk')">
                <img src="${pageContext.request.contextPath}/resources/images/flags/ukraine.png" alt=""/>
            </a>
        </li>
        <li class="flag">
            <a href="#" onclick="window.location = getLanguageLink('es')">
                <img src="${pageContext.request.contextPath}/resources/images/flags/spain.png" alt=""/>
            </a>
        </li>
    </ul>
</div>
