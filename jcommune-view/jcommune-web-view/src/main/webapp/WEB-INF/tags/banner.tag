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
<%@ attribute name="position" required="true" type="org.jtalks.jcommune.model.entity.BannerPosition" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>

<div class="container">
  <c:if test="${not empty banner}">
    ${banner.content}
  </c:if>
  <c:if test="${not empty forumComponent and sessionScope.adminMode == true}">
    <jtalks:hasPermission targetId="${forumComponent.id}" targetType="COMPONENT" permission="GeneralPermission.ADMIN">
      <div class="pull-right">
        <a href="#uploadBannerModal${position}" role="button" class="btn" data-toggle="modal">
          <c:choose>
            <c:when test="${empty banner}">
              <fmt:message key="label.banner.add"/>
            </c:when>
            <c:when test="${empty banner.content}">
              <fmt:message key="label.banner.add"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="label.banner.edit"/>
            </c:otherwise>
          </c:choose>

        </a>
      </div>
      <!-- Upload banner modal dialog -->
      <form:form id="uploadBannerModal${position}" action="${pageContext.request.contextPath}/banners/upload"
                 modelAttribute="uploadedBanner" method="POST" enctype="multipart/form-data" class="modal hide fade"
                 tabindex="-1" role="dialog" aria-labelledby="myModalLabel${position}" aria-hidden="true"
                 style="margin-top: -129.5px;">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true" tabindex="-1">×</button>
          <h3 id="myModalLabel${position}"><fmt:message key="label.banner.upload.dialog.header"/></h3>
        </div>
        <input name="positionOnPage" value="${position}" type="hidden"/>
        <div class="modal-body">
          <jsp:setProperty name="uploadedBanner" property="content" value="${banner.content}"/>
          <form:textarea class="banner-textarea script-confirm-unsaved" path="content" id="bodyBanner${position}"
                        tabindex="1" style="width:100%;" rows="7"/>
        </div>
        <div class="modal-footer">
          <button class="btn cancel" data-dismiss="modal" aria-hidden="true" tabindex="3">
            <fmt:message key="label.banner.upload.dialog.cancel"/>
          </button>
          <button class="btn btn-primary" tabindex="2">
            <fmt:message key="label.banner.upload.dialog.save"/>
          </button>
        </div>
      </form:form>
    </jtalks:hasPermission>
  </c:if>
</div>
