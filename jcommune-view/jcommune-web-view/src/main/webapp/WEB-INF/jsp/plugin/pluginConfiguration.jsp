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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<head>
    <title><c:out value="${pluginConfiguration.name}"/></title>
    <c:if test="${not empty error}">
        <script src="${pageContext.request.contextPath}/resources/javascript/app/dialog.js"></script>
        <script >
            $(function () {
                var errorAreaCollapse = function (e) {
                    e.preventDefault();
                    var errorArea = $("#errorArea");
                    if (errorArea.hasClass("hide-element")) {
                        $("#errorAreaControlButton").html("Hide details");
                        errorArea.removeClass("hide-element");
                    } else {
                        $("#errorAreaControlButton").html("Details");
                        errorArea.addClass("hide-element");
                    }
                };
                <c:set var="errorLines" value="${fn:split(errorInformation, newLineChar)}" />

                var bodyContent = '<b><h4><c:out value="${error}"/></h4></b> <div id="errorArea" class="hide-element">' +
                        <c:forEach var="errorLine" items="${errorLines}" varStatus="i">
                            <c:if test="${i.index < 50}">
                                '<c:out value="${errorLine}" escapeXml="false"/>' +
                            </c:if>
                        </c:forEach>
                '</div>';

                var footerContent = ' \
                <button id="errorCloseButton" class="btn">Close</button> \
                <button id="errorAreaControlButton" class="btn">Details</button>';

                jDialog.createDialog({
                    dialogId: 'pluginErrorDialog',
                    title: 'Plugin Configuration Error',
                    bodyContent: bodyContent,
                    footerContent: footerContent,
                    maxWidth: "70%",
                    maxHeight: 600,
                    'backdrop': false,
                    firstFocus: true,
                    tabNavigation: ['#errorCloseButton'],
                    handlers: {
                        '#errorCloseButton': {'static':'close'},
                        '#errorAreaControlButton' : {'click': errorAreaCollapse}
                    }
                });

                $("#pluginErrorDialog").draggable({
                    handle: ".modal-header"
                });

            });
        </script>
    </c:if>
</head>
<body>
<div class="container">
  <div id="plugins-properties-list-header">
    <h2><c:out value="${pluginConfiguration.name}"/></h2>
  </div>
  <span class="inline-block"></span>

  <form:form action="${pageContext.request.contextPath}/plugins/update" method="POST"
             modelAttribute="pluginConfiguration">
    <%-- Plugin configuration values --%>
    <form:hidden path="id" value="${pluginConfiguration.id}"/>
    <form:hidden path="name" value="${pluginConfiguration.name}"/>
    <form:hidden path="active" value="${pluginConfiguration.active}"/>
    <%-- Plugin configuration properties. --%>
    <table id="plugins-table" class="table table-row table-bordered">
      <c:choose>
        <c:when test="${!(empty pluginConfiguration.properties)}">
          <%-- Header --%>
          <thead>
          <tr>
            <th id="property-name">
              <spring:message code="label.plugins.plugin.property.name"/>
            </th>
            <th id="property-type">
              <spring:message code="label.plugins.plugin.property.type"/>
            </th>
            <th id="property-value">
              <spring:message code="label.plugins.plugin.property.value"/>
            </th>
          </tr>
          </thead>

          <%-- Content --%>
          <tbody>
          <c:forEach var="property" items="${pluginConfiguration.properties}" varStatus="status">
            <%-- Property --%>
            <form:hidden path="properties[${status.index}].id" value="${property.id}"/>
            <tr>
              <td>
                <form:hidden path="properties[${status.index}].name" value="${property.name}"/>
                <c:out value="${property.name}"/>
              </td>
              <td>
                <form:hidden path="properties[${status.index}].type" value="${property.type}"/>
                <c:out value="${property.type}"/>
              </td>
              <td>
                <form:input path="properties[${status.index}].value" value="${property.value}"/>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </c:when>
      </c:choose>
    </table>
    <c:if test="${!(empty pluginConfiguration.properties)}">
      <input type="submit" value="<spring:message code="label.plugins.save"/>"/>
    </c:if>
  </form:form>
</div>
</body>
