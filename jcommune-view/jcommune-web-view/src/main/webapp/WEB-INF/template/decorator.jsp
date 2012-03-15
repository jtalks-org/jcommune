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
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<fmt:setBundle basename="org.jtalks.jcommune.web.view.messages"/>
<fmt:setLocale value="en"/>
<!DOCTYPE HTML>
<html>
<script>
    <%--Defines URL mapping root to be used in JS--%>
    $root = "${pageContext.request.contextPath}";
    <%--Include i18n resources for JS scripts--%>
    <jsp:include page="jsMessages.jsp"/>
</script>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0"/>
    <meta http-equiv="Expires" content="-1"/>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/screen.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/i18n/<spring:message code="locale.code"/>.css"/>
    <link rel="shortcut icon" type="image/x-icon"
          href="${pageContext.request.contextPath}/resources/images/favicon.ico"/>
    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/resources/images/favicon.png"/>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-1.7.min.js"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.prettyPhoto.js"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.truncate.js"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jqery.impromptu.js"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/custom/URLBuilder.js"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/custom/registration.js"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/custom/global.js"></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/licensed/xregexp-min.js'
            type='text/javascript'></script>
    <decorator:head/>
    <title><decorator:title/></title>
</head>
<body>
<jsp:include page="../template/bbEditorScripts.jsp"/>
<decorator:body/>
<div class="footer">
    <div class="copyright">
        <br/>
        <span class="version">Powered by JCommune ${project.version}</span>
        <br/>
        &copy; 2012 www.jtalks.org
    </div>
</div>
</body>
<script>SyntaxHighlighter.all()</script>
<%--Online surveys to ask users for a feature--%>
<script type="text/javascript">var _kiq = _kiq || [];</script>
<script type="text/javascript" src="//s3.amazonaws.com/ki.js/29591/5N_.js" async="true"></script>
</html>