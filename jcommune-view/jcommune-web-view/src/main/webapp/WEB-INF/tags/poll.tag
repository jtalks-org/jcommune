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
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag body-content="empty" %>
<%@ attribute name="pollOptions" required="true" type="java.util.List" %>
<%@ attribute name="poll" required="true" type="org.jtalks.jcommune.model.entity.Voting" %>
<%@ attribute name="pollEnabled" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div id="pollWrap">
	<form name="pollForm" method="post" action="">
		<!-- Poll title -->
		<h3><c:out value="${poll.title}"/></h3>
		<!-- List of poll options -->
		<ul>
			<c:forEach items="${pollOptions}" var="option">
				<li>
					<c:if test="${pollEnabled}">
						<sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
							<c:choose>
								<c:when test="${poll.single}">
									<input name="pollAnswer" id="pollRadioButton${option.id}"
							 	   		   type="radio" value="${option.id}">
								</c:when>
								<c:otherwise>
									<input name="pollAnswer + ${option.id}" id="pollRadioButton${option.id}"
							 	   		   type="checkbox" value="${option.id}">
								</c:otherwise>
							</c:choose>
						</sec:authorize>
					</c:if>
					<c:out value="${option.name} (${option.voteCount/poll.totalVoteCount*100}%-${option.voteCount})"/>
					<span id="pollAnswer${option.id}"></span>
				</li>
			    <li style="width:${option.voteCount/poll.totalVoteCount*100}%; background-color:#00ff00" 
			    	class="pollChart pollChart${option.id}"/>
			</c:forEach>
		</ul>
		<!-- Poll button -->
		<c:if test="${pollEnabled}">
			<sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
				<input type="submit" name="pollSubmit" id="pollSubmit" value="<fmt:message key="label.poll.vote"/>">
			</sec:authorize>
		</c:if>
		<!-- Additional components -->
		<span id="pollMessage" style="display: none; "></span>
		<img src="${pageContext.request.contextPath}/resources/images/ajaxLoader.gif"
			 alt="Ajax Loader" id="pollAjaxLoader" style="display: none; ">
	</form>
</div>
