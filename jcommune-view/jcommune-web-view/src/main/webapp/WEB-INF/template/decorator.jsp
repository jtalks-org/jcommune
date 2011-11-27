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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="org.jtalks.jcommune.web.view.messages"/>
<fmt:setLocale value="en"/>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0"/>
    <meta http-equiv="Expires" content="-1"/>
    <meta charset="UTF-8">

    <title><decorator:title default="JCommune"/>
    </title>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/screen.css"/>
    <link rel="shortcut icon" type="image/x-icon"
          href="${pageContext.request.contextPath}/resources/images/favicon.ico"/>
    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/resources/images/favicon.png"/>

    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-1.7.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/fileuploader.js"></script>
</head>
<!--Define timezone to adjust date and time values-->
<body onload='document.cookie="GMT=" + new Date().getTimezoneOffset()+"; path=/"'>
<decorator:body/>
<div class="footer"> <!-- Подвал -->
    <div class="copyright">
        Java and all Java-related trademarks and logos are trademarks or registered trademarks of Oracle
        Corporation
        in the United States and other countries.
        <br/>
        Этот сайт не относится к фирме Oracle Corporation и не поддерживается ею.
        <br/>
        © 2011 www.jtalks.org: <a href="#">форум java программистов</a>
        <br/>
        <span class="version">Powered by JCommune ${project.version}</span>
    </div>
</div>
</body>
</html>