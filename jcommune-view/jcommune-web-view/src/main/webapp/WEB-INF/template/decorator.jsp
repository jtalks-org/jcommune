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
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate, max-age=0"/>
    <meta http-equiv="Expires" content="-1"/>

    <script>
        <%--Defines URL mapping root to be used in JS--%>
        $root = "${pageContext.request.contextPath}";
        <%--Include i18n resources for JS scripts--%>
        <jsp:include page="jsMessages.jsp"/>
    </script>

    <%-- support of HTML5 elements for IE6-8 --%>
    <%--[if lt IE 9]>
      <script src="${pageContext.request.contextPath}/resources/javascript/licensed/html5.js"></script>
    <![endif]--%>

    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/screen.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/i18n/<spring:message code="locale.code"/>.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection" 
          href='${pageContext.request.contextPath}/resources/css/fonts-googleapis-com.css'/>
    <link rel="shortcut icon" type="image/x-icon"
          href="${pageContext.request.contextPath}/resources/images/favicon.ico"/>
    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/resources/images/favicon.png"/>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-1.7.min.js"></script>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.prettyPhoto.js"></script>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.truncate.js"></script>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jqery.impromptu.js"></script>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.offtmp.js"></script>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/custom/URLBuilder.js"></script>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/custom/registration.js"></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/custom/signin.js'
            type='text/javascript'></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/global.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/antimultipost.js"
            type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/licensed/xregexp-min.js'
            type='text/javascript'></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/licensed/bootstrap.min.js'
            type='text/javascript'></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/licensed/bootbox.min.js'
            type='text/javascript'></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/custom/errorUtils.js'
            type='text/javascript'></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/custom/utils.js'
            type='text/javascript'></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/custom/dropdown.js'
            type='text/javascript'></script>
    <script src='${pageContext.request.contextPath}/resources/javascript/custom/forumEffects.js'
            type='text/javascript'></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/prettify/prettify.js"
            type='text/javascript'></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/topline.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/search.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/componentClickEventDisabler.js"
            type="text/javascript"></script>
    <decorator:head/>
    <title><decorator:title/></title>
</head>
<body>
<jsp:include page="../template/topLine.jsp"/>
<decorator:body/>
<div class="container">
    <footer>
        Powered by JCommune ${project.version}<br/>
        &copy; 2012 <a href="http://jtalks.org">jtalks.org</a><br/>
        Design with <a href="http://twitter.github.com/bootstrap">Twitter Bootstrap</a>
    </footer>
</div>
</body>
</html>