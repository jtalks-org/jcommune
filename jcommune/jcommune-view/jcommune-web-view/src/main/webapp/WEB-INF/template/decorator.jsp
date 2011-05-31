<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
           prefix="decorator" %>
<html>
<head>
    <title><decorator:title default="JCommune"/></title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta http-equiv="Content-type" value="text/html; charset=utf-8" content="">
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>
<div align="center">
    <table cellspacing=0 cellpadding=0 width=100% border=0>
        <tr>
            <td><span class="textlogo1"><b>JTalks Developers</b>&nbsp</span>
            </td>
        </tr>
    </table>
    &nbsp
    <table cellspacing=0 cellpadding=5 width=100% border=0>
        <tr>
            <td bgcolor="#003366"></td>
        </tr>
        <tr>
            <td class="background"><a
                    href="${pageContext.request.contextPath}/main.html">&nbsp&nbsp<span
                    class="nav">Forum</span> </a> | <span style="float: right">
                <sec:authorize access="isAuthenticated()">
                    <sec:authentication property="principal.username"/>&nbsp
                    <a href="${pageContext.request.contextPath}/logout.html">Logout</a>
                </sec:authorize>
                &nbsp
                <sec:authorize access="isAnonymous()">
                    <a href="${pageContext.request.contextPath}/login.html">Sign In</a>
                    <a href="${pageContext.request.contextPath}/registration.html">Sign Up</a>
                </sec:authorize>
                &nbsp
                <a href="?lang=en">En</a> | <a href="?lang=ru">Ru</a> </span></td>
        </tr>
        <tr>
            <td bgcolor="003366"></td>

        </tr>

    </table>
    &nbsp

</div>
<decorator:body/>
<hr width=100% size=2>
<span class="text">Copyright 2011. JTalks</span>
<br>
</body>
</html>