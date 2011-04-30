<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title><decorator:title default="JCommune"/>></title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <META content="text/html; charset=windows-1251" http-equiv=Content-Type>       
    <LINK href="${pageContext.request.contextPath}/css/main.css" type=text/css rel=stylesheet>
</head>
<body>
<div align="center">
    <table cellspacing=0 cellpadding=0 width=100% border=0>
        <tr>
            <td>
                <span class="textlogo1"><b>JTalks Developers</b>&nbsp</span>
            </td>
        </tr>
    </table>
    &nbsp
    <table cellspacing=0 cellpadding=5 width=100% border=0>
        <tr>
            <td bgcolor="#003366"></td>
        </tr>
        <tr>
            <td class="background">

                
                <a href="forum.html">&nbsp&nbsp<span class="nav">Forum</span></a> |
               <span style="float: right">
                <a href="?lang=en">En</a>
                |
                <a href="?lang=ru">Ru</a>
                </span>
            </td>
        </tr>
        <tr>
            <td bgcolor="003366"></td>

        </tr>

    </table>
    &nbsp

</div>
<decorator:body/>
<hr width=100% size=2>
<span class="text">Copyright 2011. JTalks</span> <br>
</body>
</html>