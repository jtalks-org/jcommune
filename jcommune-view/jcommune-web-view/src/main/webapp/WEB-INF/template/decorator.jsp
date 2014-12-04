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
<meta name="viewport" content="width=800, maximum-scale=0.8">
<meta property="og:image" content="${pageContext.request.contextPath}/admin/logo"/>
<%-- Twitter card --%>
<meta name="twitter:card" content="summary"/>
<meta name="twitter:description" content="<decorator:title/>"/>
<meta name="twitter:title" content="<decorator:title/>"/>
<script>
  <%--Defines URL mapping root to be used in JS--%>
  $root = "${pageContext.request.contextPath}";
  <%--Include i18n resources for JS scripts--%>
  <jsp:include page="jsMessages.jsp"/>
</script>

<%--
These are favicons, that are shown on the browser tab near the site name. This image represents the web resource.
According to this: http://www.whatwg.org/specs/web-apps/current-work/multipage/links.html#rel-icon there migth be
several favicons of different types and user agents (browsers) should decide what icon to choose (there is no standard).
I'm not quite sure whether we should add attributes like sizes though it looks like it's encouraged. To be discussed
in the future.
--%>
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/admin/icon/ico?v=${infoChangeDate}"/>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/admin/icon/png?v=${infoChangeDate}"/>

<c:if test="${cmpName == null}">
  <spring:message code="label.error" var="cmpName"/>
</c:if>
<spring:message code="label.rssFeed" var="rssTitle" arguments="${cmpName}" htmlEscape="true" javaScriptEscape="true"/>
<link rel="alternate" type="application/rss+xml" title="${rssTitle}"
      href="${pageContext.request.contextPath}/topics/recent.rss"/>

<link rel="stylesheet" type="text/css" media="screen, projection"
      href="${pageContext.request.contextPath}/resources/css/manual/i18n/<spring:message code="locale.code"/>.css"/>

<%--
    Adding new scripts (js) or files with styles (css).
      1 - Add to wro.xml to group
        a - if group exists, then add
        b - if group not exists, then add new group. Add file to this group. Add this group to pom.xml (plugin).
            Add this group to block (<c:when  ...  <c:when>) which is located below the text.
            Add import files which contains in group to block (<c:otherwise> ... <c:otherwise>)
      2 - Add new imports of file to block (<c:otherwise> ... <c:otherwise>)
--%>
<c:set var="mode" value="${isJsCompressed}"/>
<c:set var="uriString" value="${pageContext.request.requestURI}"/>
<c:choose>
  <c:when test="${mode eq 'true'}">
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/wro/main.css?${project.version}"/>

    <script src="${pageContext.request.contextPath}/resources/wro/main.js?${project.version}"></script>

    <c:if test="${fn:contains(uriString, 'inbox') or fn:contains(uriString, 'outbox')
      or fn:contains(uriString, 'drafts') or fn:contains(uriString, 'pm') or fn:contains(uriString, 'reply')
      or fn:contains(uriString, 'quote')}">
      <script language="javascript"
              src="${pageContext.request.contextPath}/resources/wro/pm.js?${project.version}"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'posts')}">
      <script type="text/javascript"
              src="${pageContext.request.contextPath}/resources/wro/post.js?${project.version}"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'reviews')}">
      <script type="text/javascript"
              src="${pageContext.request.contextPath}/resources/wro/cr.js?${project.version}"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'user')}">
      <link rel="stylesheet" type="text/css" media="screen, projection"
            href="${pageContext.request.contextPath}/resources/wro/user.css?${project.version}"/>

      <script type="text/javascript"
              src="${pageContext.request.contextPath}/resources/wro/user.js?${project.version}"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'plugins')}">
      <script type="text/javascript"
              src="${pageContext.request.contextPath}/resources/wro/plugin.js?${project.version}"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'topics') or fn:contains(uriString, 'branches')}">
      <script type="text/javascript"
              src="${pageContext.request.contextPath}/resources/wro/topic.js?${project.version}"></script>
    </c:if>
  </c:when>

  <c:otherwise>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/app/editor.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/lib/fonts-googleapis-com.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/lib/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/lib/bootstrap-responsive.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/lib/prettify.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/lib/prettyPhoto.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/lib/inline.css"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/app/application.css"/>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/lib/jquery.contextMenu.css"
          type="text/css" media="all"/>
    <link rel="stylesheet" type="text/css" media="all"
          href="${pageContext.request.contextPath}/resources/css/lib/jquery-ui.css"/>

    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/jquery-1.7.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/jquery.truncate.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/jquery-ui-i18n.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/jquery.prettyPhoto.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/contextmenu/jquery.contextMenu.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/contextmenu/jquery-fieldselection.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/jquery/contextmenu/textarea-helper.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/prettify.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-apollo.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-clj.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-css.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-go.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-hs.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-lisp.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-lua.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-ml.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-n.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-proto.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-scala.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-sql.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-tex.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-vb.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-vhdl.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-wiki.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-xq.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/prettify/lang-yaml.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/wysiwyg-bbcode/editor.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/purl.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/pollPreview.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/fileuploader.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/html5.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/html5placeholder.jquery.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/lib/json2.js"></script>

    <script src="${pageContext.request.contextPath}/resources/javascript/app/keymaps.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/dialog.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/mainLinksEditor.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/URLBuilder.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/URLBuilder.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/registration.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/signin.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/global.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/antimultipost.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/errorUtils.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/utils.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/dropdown.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/forumEffects.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/topline.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/search.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/events.js"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/banner.js"></script>


    <%--Should be added to every page because we can use Administrator mode on each page to
        change forum name, description and icon --%>
    <script src="${pageContext.request.contextPath}/resources/javascript/app/forumAdministration.js"></script>
    <c:if test="${fn:contains(uriString, 'admin') or fn:contains(uriString, 'permissions')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/editPermissions.js"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'inbox') or fn:contains(uriString, 'outbox')
      or fn:contains(uriString, 'drafts') or fn:contains(uriString, 'pm')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/privateMessages.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/updateSaveButtonStateOnPmForm.js">
      </script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/leaveConfirm.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/contextMenu.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/lib/purl.js"></script>
    </c:if>

    <%--Should be added to compose pm pages because common code performes preview of topics, and private messages--%>
    <c:if test="${fn:contains(uriString, 'pm') or fn:contains(uriString, 'reply') or fn:contains(uriString, 'quote')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/pollPreview.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/lib/purl.js"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'posts')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/leaveConfirm.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/bbeditorEffects.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/lib/purl.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/contextMenu.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/pollPreview.js"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'reviews')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/leaveConfirm.js"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'user')}">
      <link rel="stylesheet" type="text/css" media="screen, projection"
            href="${pageContext.request.contextPath}/resources/css/app/profile.css"/>

      <script src="${pageContext.request.contextPath}/resources/javascript/app/avatarUpload.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/contacts.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/userProfileEffects.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/contextMenu.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/codeHighlighting.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/registration.js"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'plugins')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/utils.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/permissionService.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/pluginConfiguration.js"></script>
    </c:if>

    <c:if test="${fn:contains(uriString, 'topics') or fn:contains(uriString, 'branches')}">
      <script src="${pageContext.request.contextPath}/resources/javascript/app/datepicker.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/pollPreview.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/leaveConfirm.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/contextMenu.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/bbeditorEffects.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/utils.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/subscription.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/moveTopic.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/poll.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/codeHighlighting.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/app/permissionService.js"></script>
      <script src="${pageContext.request.contextPath}/resources/javascript/lib/purl.js"></script>
    </c:if>

  </c:otherwise>
</c:choose>


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
        <c:choose>
          <c:when test="${sessionScope.adminMode == true}">
            <span id="userDefinedCopyright" class="cursor-pointer"><c:out value='${userDefinedCopyright}'/></span>
          </c:when>
          <c:otherwise>
            <c:out value='${userDefinedCopyright}'/>
          </c:otherwise>
        </c:choose><br/>
        Powered by JCommune ${project.version} by <a class="space-left-small" href="http://jtalks.org">jtalks.org</a><br/>
        Design with <a class="space-left-small" href="http://getbootstrap.com">Twitter Bootstrap</a>
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