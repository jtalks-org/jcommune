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
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<head>
    <title>
        <c:out value="${cmpTitlePrefix}"/>
        <spring:message code="label.pm_title"/>
    </title>
</head>
<body>

<div class="container">
    <c:set var="textLength" value="60"/>
    <h2><c:out value="${fn:substring(pm.title,0, textLength)}"/></h2>
    <h2><c:out value="${fn:substring(pm.title, textLength, fn:length(pm.title))}"/></h2>
    <hr/>
    <div class="row">
        <div class="span2">
            <jsp:include page="../../template/newPrivateMessage.jsp"/>
            <jsp:include page="../../template/pmFolders.jsp"/>
        </div>
        <!-- /span2 -->
        <div class="span9">
            <div class="pm_buttons">
                <jtalks:hasPermission targetId='${user.id}' targetType='USER'
                                      permission='ProfilePermission.SEND_PRIVATE_MESSAGES'>
                    <c:if test="${jtalks:isExists(pm.userTo)}">
                        <c:if test="${(pm.userTo eq user)}">
                            <a class="btn btn-primary"
                               href="${pageContext.request.contextPath}/reply/${pm.id}?userId=${user.id}">
                                <i class="icon-share-alt icon-white"></i>
                                <spring:message code="label.reply"/>
                            </a>

                            <a class="btn margin-left-big"
                               href="${pageContext.request.contextPath}/quote/${pm.id}?userId=${user.id}">
                                <i class="icon-quote"></i>
                                <spring:message code="label.quote"/>
                            </a>
                        </c:if>
                    </c:if>
                </jtalks:hasPermission>

                <span class="del">
                    <a id="deleteOnePM"
                       class="btn btn-danger delete margin-left-big"
                       href="${pageContext.request.contextPath}/pm"
                       data-confirmationMessage="<spring:message code="label.deletePMConfirmation"/>">

                        <i class="icon-trash icon-white"></i>
                        <spring:message code="label.delete"/>
                    </a>
                    <input id="pmId" type="hidden" value="${pm.id}"/>
                    <form:form id="deleteForm" method="DELETE"/>
                </span>
                <!-- del -->

            </div>
            <!-- pm_buttons -->

            <div class="well pm_message_view">
                <div class="row pm_message_detail">
                    <div class="pull-left thumbnail pm_message_avatar">
                        <img src="${pageContext.request.contextPath}/users/${pm.userFrom.id}/avatar?version=${pm.userFrom.version}" alt=""/>
                    </div>
                    <div class="pm_message_userTo_link">
                        <a href="${pageContext.request.contextPath}/users/${pm.userFrom.id}">
                            <i class="icon-user"></i><c:out value="${pm.userFrom.username}"/>
                        </a>
                        <br/>
                        <span><i class="icon-calendar"></i><jtalks:format value="${pm.creationDate}"/></span>
                    </div>
                </div>
                <div class="pm-text-box">
                    <jtalks:postContent text="${pm.body}" signature="${pm.userFrom.signature}"/>
                </div>
            </div>

        </div>

    </div>
    <!-- /row -->

</div>
<!-- /container -->

<div class="footer_buffer"></div>
</body>
