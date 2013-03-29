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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">

            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <c:choose>
                <c:when test="${cmpName==null}">
                    <span class="brand"><fmt:message key="label.error"/></span>
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
                    <input id="searchText" name="searchText" type="text" class="search-query dropdown-toggle"
                           placeholder='<fmt:message key="label.search"/>' maxlength="50"
                           value='<c:out value="${searchText}"/>'/>
                    <span id='searchClear' class='search-clear'>×</span>
                </form>

                <ul class="nav pull-right">
                    <%--External links start--%>
                    <li class="dropdown top-line-links">
                        <span id="links-toggle" class="dropdown-toggle topline-links links-selector-container"
                              data-toggle="dropdown"
                              title='<fmt:message key="label.links"/>'>
                            <fmt:message key="label.links"/>
                            <c:if test="${not empty forumComponent}">
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
                                    <li><a id="small-screen-external-link-${link.id}" title="${link.hint}" href="${link.url}">
                                        <c:out value="${link.title}"/>
                                    </a></li>
                                </c:forEach>
                            </c:if>
                        </ul>
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
                            <a id="signin" href="${pageContext.request.contextPath}/login">
                                <fmt:message key="label.signin"/>
                            </a>
                        </li>
                    </sec:authorize>
                    <%-- END OF Not logged in block --%>

                    <%-- Logged in block --%>
                    <sec:authorize access="isAuthenticated()">
                        <li>
                            <a href="${pageContext.request.contextPath}/user">
                                <fmt:message key="label.profile"/>
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/inbox">
                                <fmt:message key="label.pm"/>
                                <c:if test="${newPmCount != null}">
                                <span id="new-pm-count" title="
                                    <fmt:message key='label.tips.pm_count'>
                                        <fmt:param>${newPmCount}</fmt:param>
                                    </fmt:message>
                                    ">
                                    (
                                    <i class="icon-envelope icon-white" style="vertical-align:middle;"></i>
                                    <span class='test-pm-count'>${newPmCount}</span>
                                    )
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
                        <li class="divider-vertical"></li>
                        <li>
                            <div>
                                <a class='btn btn-inverse btn-small' href="${pageContext.request.contextPath}/logout">
                                    <fmt:message key="label.logout"/>
                                </a>
                            </div>
                        </li>
                    </sec:authorize>
                    <%-- END OF Logged in block --%>

                    <%-- Language chooser --%>
                    <li class="dropdown">
                        <div id="lang-selector-toggle" class="dropdown-toggle language-selector-container"
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
                                <a href="#" onclick="window.location = getLanguageLink('en')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/en.png"
                                         alt="<fmt:message key='label.english'/>"/>
                                    <fmt:message key='label.english'/>
                                </a>
                            </li>
                            <li id='lang-ru'>
                                <a href="#" onclick="window.location = getLanguageLink('ru')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/ru.png"
                                         alt="<fmt:message key='label.russian'/>"/>
                                    <fmt:message key='label.russian'/>
                                </a>
                            </li>
                            <li id='lang-uk'>
                                <a href="#" onclick="window.location = getLanguageLink('uk')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/uk.png"
                                         alt="<fmt:message key='label.ukrainian'/>"/>
                                    <fmt:message key='label.ukrainian'/>
                                </a>
                            </li>
                            <%-- Spanish is disabled since we are not able make full translation --%>
                            <!--                             <li id='lang-es'> -->
                            <!--                                 <a href="#"  onclick="window.location = getLanguageLink('es')"> -->
                            <%--                                     <img src="${pageContext.request.contextPath}/resources/images/flags/es.png" />  --%>
                            <%--                                     <fmt:message key='label.spanish'/> --%>
                            <!--                                 </a> -->
                            <!--                             </li> -->
                        </ul>
                    </li>
                    <%-- END OF Language chooser --%>
                </ul>
            </div>
        </div>
    </div>
</div>


