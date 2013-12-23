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
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
  <title>
    <c:out value="${cmpTitlePrefix}"/>
    <spring:message code="label.edit"/>
  </title>
</head>
<body>
<div class="container">

  <div id="answer">
    <form:form action="${pageContext.request.contextPath}/pages/${simplePageDto.pathName}/edit"
               method="POST" modelAttribute="simplePageDto" class='well anti-multipost'>

      <h2>
        <spring:message code="label.edit"/>
      </h2>
      <form:hidden path="id"/>


      <div>
          <form:input path="nameText" class="reg_input" type="text"/></br>
        <form:errors path="nameText" cssClass="error"/>
      </div>

      <form:hidden path="pathName"/>
      <jtalks:bbeditor labelForAction="label.save"
                       postText="${simplePageDto.contentText}"
                       bodyParameterName="contentText"
                       back="${pageContext.request.contextPath}/pages/${simplePageDto.pathName}"/>
    </form:form>
    <a href="${pageContext.request.contextPath}/pages/${simplePageDto.pathName}" class="back-btn">
      <i class="icon-arrow-left"></i>
      <spring:message code="label.back"/>
    </a>
  </div>

  <div class="footer_buffer"></div>
</div>
</div>
</body>
</html>