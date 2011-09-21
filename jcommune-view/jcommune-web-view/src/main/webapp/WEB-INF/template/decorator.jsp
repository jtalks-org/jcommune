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
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" content=""/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0"/>
    <meta http-equiv="Expires" content="-1"/>

    <title><decorator:title default="JCommune"/></title>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/css/screen.css"/>
</head>
<!--apply timezone settings-->
<joda:dateTimeZone value='<%=session.getAttribute("GMT")%>'>
    <!--Define timezone to adjust date and time values-->
    <body onload='document.cookie="GMT=" + new Date().getTimezoneOffset()'>

    <div class="top_line">
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            Привет, <a class="currentusername"
                       href="${pageContext.request.contextPath}/user/${encodedUserName}.html"
                       title="Имя пользователя"><sec:authentication
                property="principal.username"/></a>!
        </sec:authorize>
        <ul class="top_menu">
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <li class="no_border"><a href="${pageContext.request.contextPath}/user/${encodedUserName}.html"
                                         title="Профиль"><fmt:message key="label.profile"/></a></li>
                <li><a href="#" title="Настройки"><fmt:message
                        key="label.settings"/></a></li>
                <li><a href="${pageContext.request.contextPath}/pm/inbox.html" title="Сообщения"><fmt:message
                        key="label.pm"/>(${newPmCount})</a></li>
                <li><a href="#" title="Пользователи"><fmt:message
                        key="label.users"/></a></li>
                <li><a href="#" title="Группы"><fmt:message key="label.groups"/></a></li>
                <li><a href="#" title="Для чайников"><fmt:message key="label.newbies"/></a></li>
                <li><a href="${pageContext.request.contextPath}/logout.html" title="На выход"><fmt:message
                        key="label.logout"/></a></li>
            </sec:authorize>
            <sec:authorize access="hasRole('ROLE_ANONYMOUS')">
                <li><a href="${pageContext.request.contextPath}/login.html"><fmt:message key="label.signin"/></a>
                </li>
                <li><a href="${pageContext.request.contextPath}/registration.html"><fmt:message
                        key="label.signup"/></a></li>
            </sec:authorize>
            <li class="flag no_border"><a href="?lang=ru"><img
                    src="${pageContext.request.contextPath}/images/flag_russia.png" alt=""/></a></li>
            <li class="flag"><a href="?lang=en"><img
                    src="${pageContext.request.contextPath}/images/flag_great_britain.png" alt=""/></a></li>
        </ul>
    </div>


    <div align="center">
        <table cellspacing=0 cellpadding=5 width=100% border=0>
            <tr>
                <td bgcolor="#003366"></td>
            </tr>
            <tr>
                <td class="background">
                    <c:forEach var="breadcrumb" items="${breadcrumbList}">
                        <c:choose>
                            <%--create root breadcrumb--%>
                            <c:when test="${breadcrumb.breadcrumbLocation.name == 'main'}">
                                <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}.html">
                                    <span class="nav"> <fmt:message key="label.forum"/> </span>
                                </a>
                            </c:when>
                            <%--create inbox, outbox, drafts breadcrumbs--%>
                            <c:when test="${breadcrumb.breadcrumbLocation.name == '/pm/inbox'
                        || breadcrumb.breadcrumbLocation.name == '/pm/outbox'
                        || breadcrumb.breadcrumbLocation.name == '/pm/drafts'
                        || breadcrumb.breadcrumbLocation.name == '/recent'}">
                                <%--TODO Need to define standard URI for most location - ${Entity type}/${Entity ID}.html--%>
                                <%--TODO Need to remove '/pm/' from controller mapping.html--%>
                                <a href="${pageContext.request.contextPath}${breadcrumb.breadcrumbLocation.name}.html">
                                    <span class="nav"> <c:out value="${breadcrumb.breadcrumbLocationValue}"/> </span>
                                </a>
                            </c:when>
                            <%--create section, topic, branch, post breadcrumb--%>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}/${breadcrumb.id}.html">
                                    <span class="nav"> <c:out value="${breadcrumb.breadcrumbLocationValue}"/> </span>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <td bgcolor="003366"></td>

            </tr>

        </table>
        &nbsp;

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