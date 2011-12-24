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
<html>
<head>
    <title><spring:message code="label.signup"/></title>
</head>

<body>
<div class="wrap registration_page">
    <jsp:include page="../template/topLine.jsp"/>
    <h1><a href="${pageContext.request.contextPath}">
        <img src="${pageContext.request.contextPath}/resources/images/jtalks.png"/>
    </a></h1>
    <div class="all_forums">
        <jtalks:form id="form" name="form" action='${pageContext.request.contextPath}/user/new'
                     modelAttribute="newUser" method="POST">
            <div class="forum_header_table">
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="label.fillmessage"/></span>
                    <span class="empty_cell"></span> <!-- Необходима для корректного отображения псевдотаблицы -->
                </div>
            </div>
            <div class="forum_table" id="stylized">

                <div class="forum_row">
                    <form:label path="username"> <spring:message code="label.username"/></form:label>
                    <div>
                        <form:input path="username" class="reg_input" type="text"/>
                        <form:errors path="username" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.username"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="email"><spring:message code="label.email"/></form:label>
                    <div>
                        <form:input path="email" class="reg_input" type="text"/>
                        <form:errors path="email" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.email"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="password"><spring:message code="label.password"/></form:label>

                    <div>
                        <form:input path="password" class="reg_input" type="password"/>
                        <form:errors path="password" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.password"/></span>
                </div>
                <div class="forum_row">
                    <form:label path="passwordConfirm"><spring:message code="label.confirmation"/></form:label>

                    <div>
                        <form:input path="passwordConfirm" class="reg_input" type="password"/>
                        <form:errors path="passwordConfirm" cssClass="error"/>
                    </div>
                    <span class="reg_info"><spring:message code="label.tip.confirmation"/></span>
                </div>
                    <%--                <div class="forum_row">
                        <div><input type="checkbox" name="iagree" id="iagree"/>
                            Я принимаю условия <a href="#">пользовательского соглашения</a>.
                            </input></div>
                    </div>--%>
            </div>
            <div class="form_controls">
                <button type="submit"><spring:message code="label.signup"/></button>
            </div>
        </jtalks:form>
    </div>
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>

</body>
</html>
