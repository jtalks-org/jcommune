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
       		<div class="info_top_lower">
	 	   		<span class="nav_top">
	            	<jtalks:pagination uri="${uri}" pagination="${pagination}" list="${posts}"/>
	          	</span>
 	   		</div>
			<div class="forum_header_table">
               <div class="forum_header">
                   <h3><fmt:message key="label.search.result"/></h3>
                   <span class="empty_cell"></span>
               </div>
           </div>
 		   <div>
 		   	   <c:choose>
	 		   	   <c:when test="${!(empty posts)}">
			           <ul class="forum_table">
			               <c:forEach var="post" items="${list}" varStatus="i">
			                   <li class="forum_row">
			                       <div class="forum_info">
										<h4>
											<a class="forum_link" href="${pageContext.request.contextPath}/topics/${post.topic.id}"> 
												<c:out value="${post.topic.title}"/>
											</a>
										</h4>
										<div class="forum_message_cell_text">
											<span class="truncated break_word">
												<c:out value="${post.postContent}"/>
											</span>
			                           </div>
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
		            <jtalks:pagination uri="${uri}" pagination="${pagination}" list="${posts}"/>
		        </span>	
           </div>
       </div>
       <div class="footer_buffer"></div>
   </div>
</body>