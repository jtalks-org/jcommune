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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<sec:authentication property="principal.username" var="auth" scope="request"/>
<head>
    <title><spring:message code="label.user"/> - "${auth}"</title>
    <link href="${pageContext.request.contextPath}/resources/css/main.css"
          type=text/css rel=stylesheet>
</head>
<body>

<jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

<div id="editUserDetails">
    <form:form id="editProfileForm" name="editProfileForm"
               action="${pageContext.request.contextPath}/users/update"
               modelAttribute="editedUser" method="POST" enctype="multipart/form-data">

        <table>
            <tr>
                <td><label><spring:message code="label.username"/></label></td>
                <td><span><c:out value="${auth}"/></span></td>
            </tr>

            <tr>
                <td><label><spring:message code="label.email"/></label></td>
                <td><form:input path="email" size="25" value="${editedUser.email}"/></td>
                <td><form:errors path="email" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.firstname"/></label></td>
                <td><form:input path="firstName" size="25" value="${editedUser.firstName}"/></td>
                <td><form:errors path="firstName" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.lastname"/></label></td>
                <td><form:input path="lastName" size="25" value="${editedUser.lastName}"/></td>
                <td><form:errors path="lastName" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.newPassword"/></label></td>
                <td><form:input path="newUserPassword" size="25" type="password"/></td>
                <td><form:errors path="newUserPassword" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.newPasswordConfiration"/></label></td>
                <td><form:input path="newUserPasswordConfirm" size="25" type="password"/></td>
                <td><form:errors path="newUserPasswordConfirm" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.currentPassword"/></label></td>
                <td><form:input path="currentUserPassword" size="25" type="password"/></td>
                <td><form:errors path="currentUserPassword" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.signature"/></label></td>
                <td><form:input path="signature" size="50" value="${editedUser.signature}"/></td>
                <td><form:errors path="signature" cssClass="error"/></td>
            </tr>
            <tr>
                <td></td>
                <td><form:input path="avatar" type="file"/></td>
                <td><form:errors path="avatar" cssClass="error"/></td>
            </tr>
        </table>
    </form:form>

    <form action="${pageContext.request.contextPath}/users/edit/avatar" id="removeAvatarForm"
          name="removeAvatarForm" method="POST">
        <c:if test="${editedUser.avatar.size>0}">
            <table>
                <tr>
                    <td width="100" height="100" align="center" valign="middle">
                        <img src="${pageContext.request.contextPath}/show/${auth}/avatar"/><br>
                    </td>
                </tr>
                <tr>
                    <td>
                        <a href="javascript:submitForm('removeAvatarForm')"><spring:message
                                code="label.avatar.remove"/></a>
                    </td>
                </tr>
            </table>
        </c:if>
    </form>

    <table>
        <tr>
            <td>
                <form:form action='${pageContext.request.contextPath}/users/${auth}' method="GET">
                    <input type="submit" value="<spring:message code="label.back"/>"/>
                </form:form>
            </td>
            <td>
                <input type="submit" value="<spring:message code="label.save_changes"/>"
                       onclick="submitForm('editProfileForm')"/>
            </td>
        </tr>
    </table>

</div>


<script type="text/javascript">
    function submitForm(formName) {
        document.forms[formName].submit();
    }
</script>

</body>
</html>
