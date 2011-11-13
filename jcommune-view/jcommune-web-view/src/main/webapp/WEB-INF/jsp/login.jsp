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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="label.signin"/></title>
</head>
<body>
<div class="wrap login_page">
    <jsp:include page="../template/topLine.jsp"/>
    <h1>JTalks</h1>
    <c:if test="${not empty param.login_error}">
    <span style="color: red; ">
        <spring:message code="label.login_error"/>
    </span>
    </c:if>
    <form action='<c:url value="/j_spring_security_check"/>' method="POST" name="form" id="form">
        <div class="all_forums">
           <div style="width: 400px">
            <div class="forum_header">
                <span class="forum_header_answer"><spring:message code="label.signin"/></span>
                <span class="empty_cell"></span> <!-- Необходима для корректного отображения псевдотаблицы -->
            </div>
            <div class="forum_row">
                <label for="j_username"><spring:message code="label.username"/> </label>
                <input type="text" size="30" name="j_username" id="j_username">

            </div>
            <div class="forum_row">
                <label for="j_password"><spring:message code="label.password"/> </label>
                <input type="password" size="30" name="j_password" id="j_password">

            </div>
            <div class="forum_row">

                <input type="checkbox" name="staylogged">Автоматически входить
                при каждом посещении
                <span class="empty_cell"></span> <!-- Необходима для корректного отображения псевдотаблицы -->
            </div>

            <div class="form_controls">
                <input type="submit" value="<spring:message code="label.signin"/>"></input><br/><br/>
                <a href='<c:url value="/password/restore"/>'><spring:message code="label.restorePassword.prompt"/></a>
            </div>
           </div>
        </div>
    </form>
    <!-- Конец всех форумов -->
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>

</body>
</html>