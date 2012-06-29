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

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">

            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a class="brand" href="${pageContext.request.contextPath}/">JTalks - JCommune</a>
            
            <div class="nav-collapse">
                <form action='<c:url value="/search/"/>' method="GET" name="form" id="form" class="navbar-search pull-left dropdown">
                    <input id="searchText" name="searchText" type="text" class="search-query dropdown-toggle" 
                        placeholder='<fmt:message key="label.search"/>' maxlength="50"
                        value='<c:out value="${searchText}"/>'/>
                </form>
                <ul class="nav pull-right">
                <!-- Not logged in block -->
                <sec:authorize access="isAnonymous()">
                    <li>
                        <a id="signup" href="${pageContext.request.contextPath}/user/new">
                            <fmt:message key="label.signup"/>
                        </a>
                    </li>
                    <li class="divider-vertical"></li>
                    <li>
                        <a id="signin" href="${pageContext.request.contextPath}/login">
                            <fmt:message key="label.signin"/> 
                            <strong class="caret"></strong>
                        </a>
                    </li>
                </sec:authorize>
                <!-- END OF Not logged in block -->

                <!-- Logged in block -->
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
                    <li>
                        <a href="#">
                            <fmt:message key="label.newbies"/>
                        </a>
                    <li class="divider-vertical"></li>
                    <li>
                        <div>
                            <a class='btn btn-inverse btn-small' href="${pageContext.request.contextPath}/logout">
                                <fmt:message key="label.logout"/>
                            </a>
                        </div>
                    </li>
                </sec:authorize>
                <!-- END OF Logged in block -->

                    <!-- Language chooser -->
                    <li class="dropdown">
                        <div id="lang-selector-toggle" class="dropdown-toggle language-selector-container" data-toggle="dropdown" 
                            title="<fmt:message key='label.click_language'/>">
                            <a href="#">
                                <img src="${pageContext.request.contextPath}/resources/images/flags/<fmt:message key='locale.code'/>.png" />
                            </a>
                            <b class="caret"></b>
                        </div>
                        <ul class="dropdown-menu lang-menu">
                            <li id='lang-en' >
                                <a href="#" onclick="window.location = getLanguageLink('en')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/en.png"/> 
                                    <fmt:message key='label.english'/>
                                </a>
                            </li>
                            <li  id='lang-ru'>
                                <a href="#" onclick="window.location = getLanguageLink('ru')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/ru.png"/> 
                                    <fmt:message key='label.russian'/>
                                </a>
                            </li>
                            <li id='lang-uk' >
                                <a href="#" onclick="window.location = getLanguageLink('uk')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/uk.png" /> 
                                    <fmt:message key='label.ukrainian'/>
                                </a>
                            </li>
                            <li id='lang-es'>
                                <a href="#"  onclick="window.location = getLanguageLink('es')">
                                    <img src="${pageContext.request.contextPath}/resources/images/flags/es.png" /> 
                                    <fmt:message key='label.spanish'/>
                                </a>
                            </li>
                        </ul>
                    </li>
                    <!-- END OF Language chooser -->

                </ul>
            </div>
        </div>
    </div>
</div>