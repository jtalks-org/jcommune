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
<%@ attribute name="banner" required="true" type="org.jtalks.jcommune.model.entity.Banner" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div id="banner">
	<c:if test="${not empty banner}">
		${banner.content}
		<a href="#uploadBannerModal${banner.id}" role="button" class="btn" data-toggle="modal">Change Me</a>
		
		<!-- Upload banner modal dialog -->
		<div id="uploadBannerModal${banner.id}" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h3 id="myModalLabel">Modal header</h3>
			</div>
			<form:form action="${pageContext.request.contextPath}/banners/upload" modelAttribute="uploadedBanner" method="POST" 
					   class="well anti-multipost" enctype="multipart/form-data">
				<form:input type="hidden" path="positionOnPage" value="${banner.positionOnPage}"/>
				<div class="modal-body">
					<form:textarea path="content" id="body" name="body" tabindex="200"
					 			   style="width:100%;" class="script-confirm-unsaved"/>
				</div>
				<div class="modal-footer">
					<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
					<button class="btn btn-primary">upload</button>
				</div>
			</form:form>
		</div>
	</c:if>
</div>
