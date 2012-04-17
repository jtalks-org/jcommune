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
<html>
<head>
    <title><spring:message code="label.signin"/></title>
</head>
<body>
<div class="wrap login_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <form action='<c:url value="/j_spring_security_check"/>' method="POST" name="form" id="form">
        <div class="all_forums">
            <div class="forum_header_table">
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="label.signin"/></span>
                    <span class="empty_cell"></span>
                </div>
            </div>
            <div class="forum_table" id="stylized">
                <div class="forum_row">
                    <label for="j_username"><spring:message code="label.username"/> </label>

                    <div>
                        <input class="reg_input" type="text" name="j_username" id="j_username">
                    </div>
                </div>
                <div class="forum_row">
                    <label for="j_password"><spring:message code="label.password"/> </label>

                    <div>
                        <input class="reg_input" type="password" name="j_password" id="j_password">
                        <c:if test="${not empty param.login_error}">
                            <span class="error">
                                <spring:message code="label.login_error"/>
                            </span>
                        </c:if>
                    </div>
                </div>
                <div class="forum_row">
                  <label class="auto_logon">
                    <input type="checkbox" name="_spring_security_remember_me"><spring:message code="label.auto_logon"/>
                  </label>
                    <span class="empty_cell"></span>
                </div>
            </div>

            <div class="form_controls">
                <input type="submit" class="button" value="<spring:message code="label.signin"/>"></input><br/>
                <a href='<c:url value="/password/restore"/>'><spring:message
                        code="label.restorePassword.prompt"/></a>
            </div>
        </div>
    </form>
    <div class="footer_buffer"></div>
</div>

</body>
</html>