<%--

    Copyright (C) 2011  jtalks.org Team
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
    Also add information on how to contact you by electronic and paper mail.
    Creation date: Apr 12, 2011 / 8:05:19 PM
    The jtalks.org Project

--%>
<%@ page import="org.jtalks.jcommune.model.entity.Topic" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Forum</title>
</head>
<body>
    <table border="1" width="100%">
        <tr>
            <td width="80%"><spring:message code="label.topic"/></td>
            <td width="10%"><spring:message code="label.author"/></td>
            <td width="10%"><spring:message code="label.date"/></td>
        </tr>

        <c:forEach var="topic" items="${topics}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${topic.announcement=='true'}">
                            <div class="announcement"><a href="${pageContext.request.contextPath}/topic/${topic.id}.html">
                            <spring:message code="label.marked_as_announcement"/><c:out value="${topic.title}"/></a></div>
                        </c:when>
                        <c:when test="${topic.sticked=='true'}">
                            <div class="sticked"><a href="${pageContext.request.contextPath}/topic/${topic.id}.html">
                            <spring:message code="label.marked_as_sticked"/><c:out value="${topic.title}"/></a></div>
                        </c:when>
                        <c:otherwise><a href="${pageContext.request.contextPath}/topic/${topic.id}.html">
                            <c:out value="${topic.title}"/></a>
                        </c:otherwise>
                    </c:choose>
                    <div id="shortContent">
                    <c:out value="${topic.lastPostShortContent}"/>
                    </div>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/user/${topic.lastPost.userCreated.encodedUsername}.html">
                        <c:out value="${topic.lastPost.userCreated.username}"/>
                    </a>
                </td>
                <td><joda:format value="${topic.modificationDate}"
                                 locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                                 pattern="dd MMM yyyy HH:mm"/></td>
            </tr>
        </c:forEach>
    </table>
    <br>



<div id="pagination">
    <c:if test="${maxPages > 1}">

        <c:if test="${page > 2}">
            <c:url value="/recent.html" var="first">
                <c:param name="page" value="1"/>
            </c:url>
            <a href='<c:out value="${first}" />' class="pn next"><spring:message code="pagination.first"/></a>...
        </c:if>

        <c:choose>
            <c:when test="${page > 1}">
                <c:set var="begin" value="${page - 1}"/>
            </c:when>
            <c:otherwise>
                <c:set var="begin" value="1"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${page + 1 < maxPages}">
                <c:set var="end" value="${page + 1}"/>
            </c:when>
            <c:otherwise>
                <c:set var="end" value="${maxPages}"/>
            </c:otherwise>
        </c:choose>

        <c:forEach begin="${begin}" end="${end}" step="1" varStatus="i">
            <c:choose>
                <c:when test="${page == i.index}">
                    <span>${i.index}</span>
                </c:when>
                <c:otherwise>
                    <c:url value="/recent.html" var="url">
                        <c:param name="page" value="${i.index}"/>
                    </c:url>
                    <a href='<c:out value="${url}" />'>${i.index}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page + 2 < maxPages+1}">
            <c:url value="/recent.html" var="last">
                <c:param name="page" value="${maxPages}"/>
            </c:url>
            ...<a href='<c:out value="${last}"/>' class="pn next"><spring:message code="pagination.last"/></a>
        </c:if>

    </c:if>
</div>
</body>
</html>