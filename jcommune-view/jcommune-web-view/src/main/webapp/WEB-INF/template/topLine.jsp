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
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="navbar navbar-fixed-top">
<div class="navbar-inner">
<div class="container-fluid">

<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
  <span class="icon-bar"></span>
  <span class="icon-bar"></span>
  <span class="icon-bar"></span>
</a>

<span id="logoTooltipHolder" class="hidden"><c:out value="${logoTooltip}"/></span>
<span id="descriptionHolder" class="hidden"><c:out value="${cmpDescription}"/></span>
<span id="titlePrefixHolder" class="hidden"><c:out value="${cmpTitlePrefix}"/></span>
<span id="copyrightHolder" class="hidden"><c:out value="${copyrightTemplate}"/></span>

<c:set var="toolTip" value="${logoTooltip}"/>
<c:if test="${not empty forumComponent}">
  <jtalks:hasPermission permission="GeneralPermission.ADMIN"
                        targetId="${forumComponent.id}" targetType="COMPONENT">
    <c:if test="${empty toolTip}">
      <c:set var="toolTip"> <spring:message code="label.changeLogo"/> </c:set>
    </c:if>
  </jtalks:hasPermission>
</c:if>

<div class="logo-container">
  <a href="${pageContext.request.contextPath}/" title="${fn:escapeXml(toolTip)}"
     data-toggle="tooltip" data-placement="right">
    <c:choose>
      <c:when test="${sessionScope.adminMode == true}">
        <img id="forumLogo" class="forum-logo cursor-pointer" src='<c:url value="/admin/logo"/>'
             alt="${fn:escapeXml(toolTip)}"/>
      </c:when>
      <c:otherwise>
        <img class="forum-logo cursor-pointer" src='<c:url value="/admin/logo"/>' alt="${fn:escapeXml(toolTip)}"/>
      </c:otherwise>
    </c:choose>
  </a>
</div>

<c:choose>
  <c:when test="${cmpName==null}">
    <span class="brand"><fmt:message key="label.error"/></span>
  </c:when>
  <c:when test="${cmpName != null and sessionScope.adminMode == true}">
    <a class="brand cursor-pointer" id="cmpName"><c:out value="${cmpName}"/></a>
  </c:when>
  <c:otherwise>
    <a class="brand"
       href="${pageContext.request.contextPath}/"><c:out value="${cmpName}"/>
    </a>
  </c:otherwise>
</c:choose>

<div class="nav-collapse">
  <form action='<c:url value="/search/"/>' method="GET" name="search-form" id="search-form"
        class="navbar-search pull-left dropdown">
    <input id="searchText" name="text" type="text" class="search-query"
           placeholder='<fmt:message key="label.search"/>' maxlength="50"
           value='<c:out value="${searchText}"/>'/>
    <span id='searchClear' class='search-clear'>×</span>
  </form>

  <ul class="nav pull-right">
    <%--External links start--%>
    <li class="dropdown topline-links">
                        <span id="links-toggle" class="dropdown-toggle links-selector-container"
                              data-toggle="dropdown"
                              title='<fmt:message key="label.links"/>'>
                            <fmt:message key="label.links"/>
                            <c:if test="${not empty forumComponent and sessionScope.adminMode == true}">
                              <jtalks:hasPermission targetId="${forumComponent.id}" targetType="COMPONENT"
                                                    permission="GeneralPermission.ADMIN">
                                   <span id="links_editor_top" title='<fmt:message key="label.linksEditor"/>'
                                         class="icon-white-cog cursor-hand links_editor"></span>
                              </jtalks:hasPermission>
                            </c:if>
                        </span>
      <ul class="dropdown-menu links-menu">
        <c:if test="${not empty externalLinks}">
          <c:forEach var="link" items="${externalLinks}">
            <li><a id="small-screen-external-link-<c:out value='${link.id}'/>" 
            data-original-title="<c:out value='${link.hint}'/>" href="<c:out value='${link.url}'/>">
              <c:out value="${link.title}"/>
            </a></li>
          </c:forEach>
        </c:if>
      </ul>
    </li>

    <%-- Not logged in block --%>
    <sec:authorize access="isAnonymous()">
      <%--Temporary disabled, cause we need more requirements for "newbies" page
          <li>
              <a href="${pageContext.request.contextPath}/pages/for_newbies">
                  <fmt:message key="label.newbies"/>
              </a>
          </li>
      --%>
      <li>
        <a id="signup" href="${pageContext.request.contextPath}/user/new">
          <fmt:message key="label.signup"/>
        </a>
      </li>
      <li class="divider-vertical"></li>
      <li>
        <a id="signin" rel="${sessionScope.customReferer}" href="${pageContext.request.contextPath}/login">
          <fmt:message key="label.signin"/>
        </a>
      </li>
    </sec:authorize>
    <%-- END OF Not logged in block --%>

    <%-- Logged in block --%>
    <sec:authorize access="isAuthenticated()">
      <c:if test="${not empty forumComponent}">
        <jtalks:hasPermission permission="GeneralPermission.ADMIN"
                            targetId="${forumComponent.id}" targetType="COMPONENT">
          <%-- Administration functions chooser --%>
          <li class="dropdown">
            <div class="dropdown-toggle topline-dropdown-menu" data-toggle="dropdown">
              <a id="user-dropdown-administration-link" href="#">
                <fmt:message key="label.administration"/>
              </a>
              <b class="caret"></b>
            </div>
            <ul class="dropdown-menu">
              <li>
                <c:choose>
                  <c:when test="${sessionScope.adminMode == true}">
                    <a id="Administration" href="${pageContext.request.contextPath}/admin/exit">
                      <fmt:message key="label.administration.exit"/>
                    </a>
                  </c:when>

                  <c:otherwise>
                    <a id="Administration" href="${pageContext.request.contextPath}/admin/enter">
                      <fmt:message key="label.administration.enter"/>
                    </a>
                  </c:otherwise>
                </c:choose>
              </li>
              <li>
                <a id="PluginPage" href="${pageContext.request.contextPath}/plugins/list">
                  <fmt:message key="label.plugins"/>
                </a>
              </li>
            </ul>
          </li>
        </jtalks:hasPermission>
      </c:if>

      <sec:authentication property="principal.username" var="username" scope="request"/>
      <li class="dropdown">
        <div class="dropdown-toggle topline-dropdown-menu" data-toggle="dropdown">
          <a id="user-dropdown-menu-link" href="#">
            <c:out value="${username}"/>
            <c:if test="${newPmCount != null}">
              <span class="margin-left-small">(</span>
              <i class="margin-left-small icon-envelope icon-white" style="vertical-align:middle;"></i>
              <span class='margin-left-small test-pm-count'>${newPmCount}</span>
              <span class="margin-left-small">)</span>
            </c:if>
          </a>
          <b class="caret"></b>
        </div>
        <ul class="dropdown-menu">
          <li>
            <a href="${pageContext.request.contextPath}/user">
              <fmt:message key="label.profile"/>
            </a>
          </li>
            <%--External links end--%>
          <c:if test="${not empty forumComponent}">
            <jtalks:hasPermission permission="GeneralPermission.ADMIN"
                                  targetId="${forumComponent.id}" targetType="COMPONENT">
              <li>
                <a href='<c:url value="/configuration/sape"/>'>
                  <spring:message code="label.sapeConfiguration"/>
                </a>
              </li>
            </jtalks:hasPermission>
          </c:if>
          <li>
            <a href="${pageContext.request.contextPath}/inbox">
              <fmt:message key="label.pm"/>
              <c:if test="${newPmCount != null}">
                <span id="new-pm-count" title="<fmt:message key='label.tips.pm_count'>
                                                <fmt:param>${newPmCount}</fmt:param>
                                              </fmt:message>
                                            ">
                                        <span class="space-left-small">(</span>
                                        <span class='test-pm-count space-left-small'>${newPmCount}</span>
                                        <span class="space-left-small">)</span>
                                    </span>
              </c:if>
            </a>
          </li>
            <%--Temporary disabled, cause we need more requirements for "newbies" page
                <li>
                    <a href="${pageContext.request.contextPath}/pages/for_newbies">
                        <fmt:message key="label.newbies"/>
                    </a>
                </li>
            --%>
          <li>
            <a href="${pageContext.request.contextPath}/logout">
              <fmt:message key="label.logout"/>
            </a>
          </li>
        </ul>
      </li>
    </sec:authorize>
    <%-- END OF Logged in block --%>

    <%-- Language chooser --%>
        <%-- Language chooser --%>
        <li class="dropdown">
            <div id="lang-selector-toggle" class="dropdown-toggle topline-dropdown-menu"
                 data-toggle="dropdown"
                 title="<fmt:message key='label.click_language'/>">
                <a href="#">
                    <img src="${pageContext.request.contextPath}/resources/images/flags/<fmt:message key='locale.code'/>.png"
                         alt="<fmt:message key='locale.name'/>"/>
                </a>
                <b class="caret"></b>
            </div>
            <ul class="dropdown-menu lang-menu">
                <li id='lang-en'>
                    <a href="${pageContext.request.requestURL}/language/?lang=en">
                        <img src="${pageContext.request.contextPath}/resources/images/flags/en.png"
                             alt="<fmt:message key='label.english'/>"/>
                        <fmt:message key='label.english'/>
                    </a>
                </li>
                <li id='lang-ru'>
                    <a href="${pageContext.request.requestURL}/language/?lang=ru">
                        <img src="${pageContext.request.contextPath}/resources/images/flags/ru.png"
                             alt="<fmt:message key='label.russian'/>"/>
                        <fmt:message key='label.russian'/>
                    </a>
                </li>
                <li id='lang-uk'>
                    <a href="${pageContext.request.requestURL}/language/?lang=uk">
                        <img src="${pageContext.request.contextPath}/resources/images/flags/uk.png"
                             alt="<fmt:message key='label.ukrainian'/>"/>
                        <fmt:message key='label.ukrainian'/>
                    </a>
                </li>
            </ul>
        </li>
        <%-- END OF Language chooser --%>
  </ul>
</div>
</div>
</div>
</div>


