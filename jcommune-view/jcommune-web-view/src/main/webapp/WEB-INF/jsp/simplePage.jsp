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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<head>
    <title>
        <c:out value="${simplePageDto.nameText}"/>
    </title>

</head>
<body>
<div class="wrap answer_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <div class="forum_info_top">
            <div>
                <div> <!-- top left -->

                </div>
                <div> <!-- top right -->

                </div>
            </div>
            <div class="info_top_lower">
                <div> <!-- bottom left -->
                    <h2 class="heading"><c:out value="${simplePageDto.nameText}"/></h2>
                </div>
                <div> <!-- bottom right -->

                </div>
            </div>
        </div>

        <ul class="forum_table" id="stylized">

            <li class="forum_row">
                <jtalks:bb2html bbCode="${simplePageDto.contentText}"/>
            </li>
        </ul>
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <div>
                <a class="button" href="${pageContext.request.contextPath}/pages/${simplePageDto.pathName}/edit">
                    <spring:message code="label.edit"/>
                </a>
            </div>
        </sec:authorize>
    </div>
</div>

<div class="footer_buffer">

</div>
</div>
</body>