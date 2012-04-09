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
<%@ attribute name="pollEnabled" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<div id="pollWrap">
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
					<c:if test="${pollEnabled && poll.active}">
						<sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
							<c:choose>
								<c:when test="${poll.single}">
									<input name="pollAnswer" id="pollRadioButton${option.id}"
							 	   		   type="radio" value="${option.id}">
								</c:when>
								<c:otherwise>
									<input name="pollAnswer" id="pollCheckBox${option.id}"
							 	   		   type="checkbox" value="${option.id}">
								</c:otherwise>
							</c:choose>
						</sec:authorize>
					</c:if>
					<c:out value="${option.name}"/>
					<!-- Available to anonymous users and voted users. -->
					<sec:authorize access="isAnonymous()">
						<fmt:message key="label.poll.option.vote.info">
							<fmt:param>
								<fmt:formatNumber value="${option.voteCount/poll.totalVoteCount*100}" maxFractionDigits="2"/>
							</fmt:param>
							<fmt:param>${option.voteCount}</fmt:param>
						</fmt:message>
					</sec:authorize>
					<span id="pollAnswer${option.id}"></span>
				</li>
				<!-- Available to anonymous users and voted users. -->
				<sec:authorize access="isAnonymous()">
				    <li style="width:${option.voteCount/poll.totalVoteCount*100}%; background-color:#00ff00" 
				    	class="pollChart pollChart${option.id}"/>
			    </sec:authorize>
			</c:forEach>
		</ul>
		<!-- Poll button. Available when poll is active and user not voted. -->
		<c:if test="${pollEnabled && poll.active}">
			<sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
				<input type="submit" name="pollSubmit" id="pollSubmit" value="<fmt:message key="label.poll.vote"/>">
			</sec:authorize>
		</c:if>
		<!-- Additional components -->
		<span id="pollMessage" style="display: none; "><fmt:message key="label.poll.message.error"/></span>
		<img src="${pageContext.request.contextPath}/resources/images/ajaxLoader.gif"
			 alt="Ajax Loader" id="pollAjaxLoader" style="display: none; ">
		<input type="hidden" name="pollId" value = "${poll.id}"/>
	</form>
</div>
