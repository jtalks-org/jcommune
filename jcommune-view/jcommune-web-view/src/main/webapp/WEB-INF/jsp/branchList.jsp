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
<head><title>Branch List</title></head>
<body>
<form:form method="POST">
    <c:out value="${section.name}"/><br>
    <span style="font-size: xx-small; "><c:out value="${section.description}"/> </span>
    <br />
    <table border="1" width="100%">
        <c:forEach var="branch" items="${section.branches}" varStatus="i">
            <tr>
                <td><a href="${pageContext.request.contextPath}/branches/${branch.id}.html"> <c:out
                        value="${branch.name}"/></a><br>
                    <span style="font-size: xx-small; "><c:out value="${branch.description}"/> </span>
                </td>
            </tr>
        </c:forEach>
    </table>
</form:form>
</body>
</html>
