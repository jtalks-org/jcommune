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
<%@ attribute name="pollOptions" required="true" type="java.util.List" %>
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
            <c:choose>
                <c:when test="${poll.endingDate==null}">
                    <c:out value="${poll.title}"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="label.poll.title.with.ending">
                        <fmt:param>${poll.title}</fmt:param>
                        <fmt:param><jtalks:format value="${poll.endingDate}"/></fmt:param>
                    </fmt:message>
                </c:otherwise>
            </c:choose>
        </h3>
        <!-- List of poll options -->
        <ul>
            <c:forEach items="${pollOptions}" var="option">
                <li>
                    <!-- RadioButton/CheckBox. Available when poll is active and user not voted. -->
                    <c:if test="${poll.active && votingAvailable}">
                        <c:choose>
                            <c:when test="${poll.singleAnswer}">
                                <input name="pollAnswer" id="pollRadioButton${option.id}"
                                       type="radio" value="${option.id}">
                            </c:when>
                            <c:otherwise>
                                <input name="pollAnswer" id="pollCheckBox${option.id}"
                                       type="checkbox" value="${option.id}">
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <c:out value="${option.name}"/>
                    <!-- Available to anonymous users and voted users. -->
                    <c:choose>
                        <c:when test="${!votingAvailable}">
							<span id="pollAnswer${option.id}">
								<fmt:message key="label.poll.option.vote.info">
                                    <fmt:param>${option.votesCount}</fmt:param>
                                    <fmt:param>
                                        <c:choose>
                                            <c:when test="${poll.totalVotesCount > 0}">
                                                <fmt:formatNumber maxFractionDigits="2"
                                                                  value="${option.votesCount/poll.totalVotesCount*100}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:param value="0"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </fmt:param>
                                </fmt:message>
							</span>
                        </c:when>
                        <c:otherwise>
                            <span id="pollAnswer${option.id}"></span>
                        </c:otherwise>
                    </c:choose>
                </li>
                <!-- Available to anonymous users and voted users. -->
                <c:choose>
                    <c:when test="${!votingAvailable}">
                        <li style="width:${option.votesCount/poll.totalVotesCount*100}%"
                            class="pollChart pollChart${option.id}"/>
                    </c:when>
                    <c:otherwise>
                        <li class="pollChart pollChart${option.id}"/>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ul>
        <!-- Poll button. Available when poll is active and user not voted and is button not disabled. -->
        <c:if test="${isVoteButtonEnabled && poll.active && votingAvailable}">
            <input type="submit" name="pollSubmit" id="pollSubmit"
                   value="<fmt:message key="label.poll.vote"/>">
        </c:if>
        <!-- Additional components -->
        <span id="pollMessage" style="display: none; "><fmt:message key="label.poll.message.error"/></span>
        <img src="${pageContext.request.contextPath}/resources/images/ajaxLoader.gif"
             alt="Ajax Loader" id="pollAjaxLoader" style="display: none; ">
        <input type="hidden" name="pollId" value="${poll.id}"/>
    </form>
</div>
