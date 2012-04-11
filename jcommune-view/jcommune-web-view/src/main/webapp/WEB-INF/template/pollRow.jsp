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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<c:if test="${poll != null}">
    <div class="forum_row">
        <div class="forum_userinfo">
            <a class="username"
               href="${pageContext.request.contextPath}/users/${topic.topicStarter.encodedUsername}">
                <c:out value="${topic.topicStarter.username}"/>
            </a>

            <div class="status">
                <spring:message var="online" code="label.topic.online_users"/>
                <spring:message var="offline" code="label.topic.offline_users"/>
                <jtalks:ifContains collection="${usersOnline}" object="${topic.topicStarter}"
                                   successMessage="${online}" failMessage="${offline}"/>
            </div>
            <img src="${pageContext.request.contextPath}/${topic.topicStarter.encodedUsername}/avatar"
                 class="avatar"/>
            <br/>

            <div class="user_misc_info">
                <span class="status"><spring:message code="label.topic.registered"/></span>
                <jtalks:format pattern="dd.MM.yy" value="${topic.topicStarter.registrationDate}"/><br/>
                <span class="status"><spring:message code="label.topic.message_count"/></span>
                <c:out value="${topic.topicStarter.postCount}"/><br/>
                <c:if test="${topic.topicStarter.location != null}">
                    <span class="status"><spring:message code="label.topic.from_whence"/></span>
                    <span class="break_word"><c:out value="${topic.topicStarter.location}"/></span>
                </c:if>
            </div>
        </div>
        <div class="forum_message_cell">
            <jtalks:poll isVoteButtonEnabled="true" pollOptions="${pollOptions}" poll="${poll}"/>
        </div>
    </div>
</c:if>