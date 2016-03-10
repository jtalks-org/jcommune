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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
  <head>
    <title>
      <c:out value="${cmpTitlePrefix}"/>
      <spring:message code="label.search.user"/>
    </title>
  </head>
  <body>
    <div class="container">
      <h2><spring:message code="label.search.user"/></h2>

      <div class="input-append">
        <form method="GET" action='${pageContext.request.contextPath}/users/list'>
          <input id="searchKey" name="searchKey" type="text" value="${param.searchKey}"/>
          <button type="submit" class="btn">
            <spring:message code="label.search"/>
          </button>
        </form>
      </div>

      <div class="alert alert-error" style="display: none">
        <span class="close">&times;</span>
        <span id="alertMessagePopup"></span>
      </div>

      <c:if test="${users!=null}">
        <div>
          <c:choose>
            <c:when test="${empty users}">
              <table class="table grid-table">
                <tr>
                  <td>
                    <spring:message code="label.search.user.empty"/>
                  </td>
                </tr>
              </table>
            </c:when>
            <c:otherwise>
              <table class="table table-bordered grid-table">
                <c:forEach var="user" items="${users}">
                  <tr class="grid-row">
                    <td><c:out value="${user.username}"/></td>
                    <td><c:out value="${user.email}"/></td>
                    <td>
                      <a href="${pageContext.request.contextPath}/users/${user.id}">
                        <spring:message code="label.profile"/>
                      </a>
                    </td>
                    <td>
                      <span class="groups-button" onclick="return window.userSearch.toggleUserGroups(event, ${user.id});">
                        <spring:message code="label.user.groups"/>
                      </span>
                    </td>
                  </tr>
                  <tr class="grid-row" style="display: none;" id="user-groups-table-${user.id}" >
                    <td colspan="4">
                      <select data-placeholder="Choose a groups..." class="user-groups-select" data-user-id="${user.id}" multiple="multiple" style="width: 100%">
                        <c:forEach var="group" items="${groups}">
                          <option value="${group.id}">${group.name}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                </c:forEach>
              </table>
            </c:otherwise>
          </c:choose>
        </div>
      </c:if>
    </div>
  </body>
</html>
