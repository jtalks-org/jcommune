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
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<fmt:setBundle basename="org.jtalks.jcommune.web.view.messages"/>
<fmt:setLocale value="en"/>
<!DOCTYPE HTML>
<html>
<head>
  <meta charset="UTF-8">

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
  <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/admin/icon/ico?v=${infoChangeDate}"/>
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/admin/icon/png?v=${infoChangeDate}"/>
  <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-1.7.min.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/keymaps.js"></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/dialog.js'></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.prettyPhoto.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery.truncate.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/URLBuilder.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/registration.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/mainLinksEditor.js"></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/signin.js'></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/global.js'></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/antimultipost.js"></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/licensed/xregexp-min.js'></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/licensed/bootstrap.min.js'></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/errorUtils.js'></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/utils.js'></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/dropdown.js'></script>
  <script src='${pageContext.request.contextPath}/resources/javascript/custom/forumEffects.js'></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/licensed/prettify/prettify.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/topline.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/search.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/banner.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/licensed/html5placeholder.jquery.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/events.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/custom/forumAdministration.js"></script>
  <script src="${pageContext.request.contextPath}/resources/javascript/licensed/fileuploader.js"></script>
  <decorator:head/>
  <title><decorator:title/></title>
</head>
<body>
<jsp:include page="../template/topLine.jsp"/>
<jsp:include page="../template/externalLinkBar.jsp"/>
<jtalks:banner banner="${banners['TOP']}" position="${'TOP'}"/>
<decorator:body/>
<div class="container">
  <footer>
    <jtalks:banner banner="${banners['BOTTOM']}" position="${'BOTTOM'}"/>
    <div>
      <div class="pull-left">
        Powered by JCommune ${project.version}<br/>
        &copy; 2013 <a href="http://jtalks.org">jtalks.org</a><br/>
        Design with <a href="http://twitter.github.com/bootstrap">Twitter Bootstrap</a>
      </div>
      <div>
        <jtalks:banner banner="${banners['BOTTOM_FOOTER']}" position="${'BOTTOM_FOOTER'}"/>
      </div>
    </div>
    <c:if test="${not empty sapeLinks}">
      <div class="sape-div">
          <%--this shouldn't be escaped because we receive HTML elements from SAPE which should be shown as is--%>
          ${sapeLinks}
      </div>
    </c:if>
  </footer>
</div>
</body>
</html>
