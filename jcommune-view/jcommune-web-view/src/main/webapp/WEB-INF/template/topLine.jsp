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
<%@ page import="org.jtalks.jcommune.web.util.Language" %>
<%@ page import="org.springframework.web.servlet.i18n.CookieLocaleResolver" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<div class="top_line">
    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
        <fmt:message key="label.welcomeMessage"/>
        <a class="currentusername"
           href="${pageContext.request.contextPath}/users/${encodedUserName}"
           title="Имя пользователя"><sec:authentication
                property="principal.username"/></a>!
    </sec:authorize>
    <ul class="top_menu">
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <li class="no_border"><a href="${pageContext.request.contextPath}/users/${encodedUserName}"
                                     title="Профиль"><fmt:message key="label.profile"/></a></li>
            <li><a href="#" title="Настройки"><fmt:message
                    key="label.settings"/></a></li>
            <li><a href="${pageContext.request.contextPath}/inbox" title="Сообщения"><fmt:message
                    key="label.pm"/>(${newPmCount})</a></li>
            <li><a href="#" title="Пользователи"><fmt:message
                    key="label.users"/></a></li>
            <li><a href="#" title="Группы"><fmt:message key="label.groups"/></a></li>
            <li><a href="#" title="Для чайников"><fmt:message key="label.newbies"/></a></li>
            <li><a href="${pageContext.request.contextPath}/logout" title="На выход"><fmt:message
                    key="label.logout"/></a></li>
        </sec:authorize>
        <sec:authorize access="hasRole('ROLE_ANONYMOUS')">
            <li class="no_border"><a href="${pageContext.request.contextPath}/login"><fmt:message
                    key="label.signin"/></a>
            </li>
            <li><a href="${pageContext.request.contextPath}/users/new"><fmt:message
                    key="label.signup"/></a></li>
        </sec:authorize>

        <li class="flag no_border">
            <a href="#" onclick="window.location = getLanguageLink('ru')">
                <img src="${pageContext.request.contextPath}/resources/images/flag_russia.png" alt=""/></a></li>
        <li class="flag">
            <a href="#" onclick="window.location = getLanguageLink('en')">
                <img src="${pageContext.request.contextPath}/resources/images/flag_great_britain.png" alt=""/></a></li>
    </ul>
</div>
<script type=text/javascript>
    function getLanguageLink(lang) {
        var href = window.location.toString().split("#", 1)[0];

        if (href.indexOf("?") == -1) {
            href = href + "?lang=";
        } else {
            if (href.indexOf("lang=") == -1) {
                href = href + "&lang=";
            } else {
                href = href.substring(0, href.length - 2);
            }
        }
        return href + lang;
    }
</script>
