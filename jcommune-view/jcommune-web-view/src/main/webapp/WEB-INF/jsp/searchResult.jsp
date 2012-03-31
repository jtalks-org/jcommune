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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
   <title><spring:message code="label.section.jtalks_forum"/></title>
</head>
<body>
   <div class="wrap section_page">
       <jsp:include page="../template/topLine.jsp"/>
       <jsp:include page="../template/logo.jsp"/>
       <div class="all_forums">
	   		<div class="forum_info_top">
		  		<span class="nav_top">
		         	<jtalks:pagination uri="${uri}" pagination="${pagination}" list="${topics}"/>
		       	</span>
	 		</div>
           <div class="forum_header_table">
	            <div class="forum_header">
	                <h3 class="forum_header_link"><spring:message code="label.search.header.topic"/></h3>
	                <span class="forum_header_branches"><spring:message code="label.search.header.branch"/></span>
	                <span class="forum_header_author"><spring:message code="label.search.header.author"/></span>
	                <span class="forum_header_last_message"><spring:message code="label.search.header.date"/></span>
	            </div>
           </div>
 		   <div>
 		   	   <c:choose>
	 		   	   <c:when test="${!(empty topics)}">
			           <ul class="forum_table">
			               <c:forEach var="topic" items="${list}" varStatus="i">
			                   <li class="forum_row">
			                       <div class="forum_info">
										<h4>
											<a class="forum_link" href="${pageContext.request.contextPath}/topics/${topic.id}"> 
												<c:out value="${topic.title}"/>
											</a>
										</h4>
			                       </div>
			                       <div class="forum_branches">
			                       		<a class="forum_link" href="${pageContext.request.contextPath}/branches/${topic.branch.id}"> 
			                       			<c:out value="${topic.branch.name}"/>
			                       		</a>
			                       </div>
								   <div class="forum_author">
										<a href="${pageContext.request.contextPath}/users/${search_header.encodedUsername}"
											title="<spring:message code="label.topic.header.author"/>"><c:out
												value="${topic.topicStarter.username}"/></a>
								   </div>
								   <div class="forum_last_message">
								   		<a href="${pageContext.request.contextPath}/topics/${post.topic.id}">
	                                    <jtalks:format value="${topic.creationDate}"/></a>
								   </div>
								</li>
			               </c:forEach>
			           </ul>
		           </c:when>
		           <c:otherwise>
		           		<ul class="forum_table">
		                    <li class="forum_row empty_container">
		                        <div>
		                            <span class="empty">
		                                <spring:message code="label.search.empty"/>
		                            </span>
		                        </div>
		                    </li>
                		</ul>
		           </c:otherwise>
	           </c:choose>
           </div>
           <div class="forum_info_bottom">
		        <span class="nav_bottom">
		            <jtalks:pagination uri="${uri}" pagination="${pagination}" list="${topics}"/>
		        </span>	
           </div>
       </div>
       <div class="footer_buffer"></div>
   </div>
</body>