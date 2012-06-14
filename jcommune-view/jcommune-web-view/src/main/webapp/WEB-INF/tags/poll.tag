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
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag body-content="empty" %>
<%@ attribute name="pollItems" required="true" type="java.util.List" %>
<%@ attribute name="poll" required="true" type="org.jtalks.jcommune.model.entity.Poll" %>
<%@ attribute name="isVoteButtonEnabled" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<div id="pollWrap">
    <!-- Determination of whether the user can vote in the topic. -->
    <c:set var="votingAvailable" value="true" scope="request"/>
    <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
        <sec:accesscontrollist domainObject="${poll}" hasPermission="2">
            <c:set var="votingAvailable" value="false" scope="request"/>
        </sec:accesscontrollist>
    </sec:authorize>
    <sec:authorize access="isAnonymous()">
        <c:set var="votingAvailable" value="false" scope="request"/>
    </sec:authorize>
    <!-- General form. -->
    <form name="pollForm" action="#">
        <!-- Poll title -->
        <h3>
            <c:out value="${poll.title}"/>
            <c:if test="${poll.endingDate!=null}">
                <fmt:message key="label.poll.title.with.ending">
                    <fmt:param><jtalks:format pattern="dd MMM yyyy" value="${poll.endingDate}"/></fmt:param>
                </fmt:message>
            </c:if>
        </h3>
        <!-- List of poll options -->
        <c:if test="${poll.active && votingAvailable}">
            
            <c:forEach items="${pollOptions}" var="option">
                <div class="control-group">
                    <%-- RadioButton/CheckBox. Available when poll is active and user not voted. --%>
                         <c:choose>
                            <c:when test="${poll.multipleAnswer}">
                                <input name="pollAnswer" id="pollCheckBox${option.id}"
                                       type="checkbox" value="${option.id}">
                            </c:when>
                            <c:otherwise>
                                <input name="pollAnswer" id="pollRadioButton${option.id}"
                                       type="radio" value="${option.id}">
                            </c:otherwise>
                        </c:choose>
                    
                    <c:out value="${option.name}"/>
                    <span id="pollAnswer${option.id}" style="display:none">
                        <div class="progress" style="margin-bottom: 3px;">
                            <div class="bar chart" style="width: 0;"></div>
                        </div>
                    </span>
                </div>
            </c:forEach>
            
            <!-- Poll button. Available when poll is active and user not voted and is button not disabled. -->
            <c:if test="${isVoteButtonEnabled}">
                <input type="submit" name="pollSubmit" id="pollSubmit"
                        class="btn btn-primary"
                        value="<fmt:message key="label.poll.vote"/>">
            </c:if>
        </c:if>
        
        <!-- Available to anonymous users and voted users. -->
        <c:if test="${!poll.active || !votingAvailable}">
            <c:forEach items="${pollOptions}" var="option">                   
                    <c:out value="${option.name}"/>
                            <span id="pollAnswer${option.id}">
                                <div class="progress" style="margin-bottom: 3px;">
                                        <c:choose>
                                            <c:when test="${poll.totalVotesCount > 0 && option.votesCount > 0}">
                                                <div class="bar" style="width: ${option.votesCount / poll.totalVotesCount * 100}%;">${option.votesCount}</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="bar" style="width: 0;"></div>
                                            </c:otherwise>
                                        </c:choose>                                 
                                </div>
                            </span>
            </c:forEach>
        </c:if>
        
        
        <!-- Additional components -->
        <span id="pollMessage" style="display: none; " class="label label-important"><fmt:message key="label.poll.message.error"/></span>
        <img src="${pageContext.request.contextPath}/resources/images/ajaxLoader.gif"
             alt="Ajax Loader" id="pollAjaxLoader" style="display: none; ">
        <input type="hidden" name="pollId" value="${poll.id}"/>
    </form>
</div>
