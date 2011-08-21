<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.jtalks.jcommune.web.view.messages" />
<fmt:setLocale value="en"/>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" content=""/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0"/>
    <meta http-equiv="Expires" content="-1"/>

    <title><decorator:title default="JCommune"/></title>
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet />
</head>
<body>
<div align="center">
    <table cellspacing=0 cellpadding=0 width=100% border=0>
        <tr>
            <td><span class="textlogo1"><b>JTalks Developers</b>&nbsp;</span>
            </td>
        </tr>
    </table>
    &nbsp;
    <table cellspacing=0 cellpadding=5 width=100% border=0>
        <tr>
            <td bgcolor="#003366"></td>
        </tr>
        <tr>
            <td class="background">
                <c:forEach var="breadcrumb" items="${breadcrumbList}">
                    <c:choose>
                        <c:when test="${breadcrumb.breadcrumbLocation.name == 'main'}">
                            <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}.html">
                                <span class="nav"> <fmt:message key="label.forum"/> </span>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}/${breadcrumb.id}.html">
                                <span class="nav"> <c:out value="${breadcrumb.breadcrumbLocationValue}"/> </span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <span style="float: right">
                <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                    <a href="${pageContext.request.contextPath}/user/${encodedUserName}.html"><sec:authentication property="principal.username"/></a>&nbsp;                    
                    <a href="${pageContext.request.contextPath}/pm/inbox.html"><fmt:message key="label.pm"/> (${newPmCount})</a>
                    <a href="${pageContext.request.contextPath}/logout.html"><fmt:message key="label.logout"/></a>
                </sec:authorize>
                &nbsp;
                <sec:authorize access="hasRole('ROLE_ANONYMOUS')">
                    <a href="${pageContext.request.contextPath}/login.html"><fmt:message key="label.signin"/></a>
                    <a href="${pageContext.request.contextPath}/registration.html"><fmt:message key="label.signup"/></a>
                </sec:authorize>
                &nbsp;
                <a href="?lang=en">En</a> | <a href="?lang=ru">Ru</a> </span></td>
        </tr>
        <tr>
            <td bgcolor="003366"></td>

        </tr>

    </table>
    &nbsp;

</div>
<decorator:body/>
<div style="clear:both;"></div>
<hr width=100% size=2>
<span class="text">Copyright 2011. JTalks</span>
<br>
</body>
</html>