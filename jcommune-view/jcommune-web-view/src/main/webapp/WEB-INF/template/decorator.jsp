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
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<fmt:setBundle basename="org.jtalks.jcommune.web.view.messages"/>
<fmt:setLocale value="en"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0"/>
    <meta http-equiv="Expires" content="-1"/>

    <title><decorator:title default="JCommune"/></title>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/screen.css"/>
</head>
<!--apply timezone settings-->
<joda:dateTimeZone value='<%=session.getAttribute("GMT")%>'>
    <!--Define timezone to adjust date and time values-->
    <body onload='document.cookie="GMT=" + new Date().getTimezoneOffset()'>

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
            <li class="flag no_border"><a href="?lang=ru"><img
                    src="${pageContext.request.contextPath}/resources/images/flag_russia.png" alt=""/></a></li>
            <li class="flag"><a href="?lang=en"><img
                    src="${pageContext.request.contextPath}/resources/images/flag_great_britain.png" alt=""/></a></li>
        </ul>
    </div>


    <decorator:body/>
    <div class="footer"> <!-- Подвал -->
        <div class="copyright">
            Java and all Java-related trademarks and logos are trademarks or registered tradermarks of Oracle
            Corporation
            in the United States and other countries.
            <br/>
            Этот сайт не относится к фирме Oracle Corporation и не поддерживается ею.
            <br/>
            © 2011 www.jtalks.org: <a href="#">форум java программистов</a>
        </div>
    </div>
    </body>
</joda:dateTimeZone>
</html>