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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="fmt" uri="http://www.springframework.org/tags" %>
<sec:authentication property="principal.username" var="auth" scope="request"/>
<head>
    <title><spring:message code="label.user"/> - "${auth}"</title>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/jquery/jquery-1.7.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/fileuploader.js"></script>
</head>
<body>
<jsp:include page="../template/topLine.jsp"/>
<div id="editUserDetails">
    <form:form id="editProfileForm" name="editProfileForm"
               action="${pageContext.request.contextPath}/users/edit"
               modelAttribute="editedUser" method="POST" enctype="multipart/form-data">

        <form:hidden id="avatar" path="avatar"/>
        <input id="avatarTempValue" type="hidden"/>

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
                <td><label><spring:message code="label.newPasswordConfirmation"/></label></td>
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
                <td><label><spring:message code="label.language"/></label></td>
                <td>
                    <form:select path="language" value="${editedUser.language}">
                        <c:forEach items="${editedUser.languagesAvailable}" var="language">
                            <form:option value="${language}">
                                <spring:message code="${language.languageNameLabel}"/>
                            </form:option>
                        </c:forEach>
                    </form:select>
                </td>
                <td><form:errors path="language" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.numberOfTopicsOnPage"/></label></td>
                <td>
                    <form:select path="pageSize" value="${editedUser.pageSize}"
                                 items="${editedUser.pageSizesAvailable}"/>
                </td>
                <td><form:errors path="pageSize" cssClass="error"/></td>
            </tr>
            <tr>
                <td><label><spring:message code="label.avatar.preview"/></label></td>
                <td width="100" height="100" align="center" valign="middle">
                    <img id="avatarPreview" src="" alt=""/>
                </td>
            </tr>
            <tr>
                <td>
                    <div id="upload"><input type="button" value="<spring:message code="label.avatar.load"/>"/></div>
                </td>
            </tr>
        </table>
    </form:form>

    <form action="${pageContext.request.contextPath}/users/edit/avatar" id="removeAvatarForm"
          name="removeAvatarForm" method="POST">
        <c:if test="${editedUser.avatar != null}">
            <table>
                <tr>
                    <td><label><spring:message code="label.avatar.current"/></label></td>
                    <td width="100" height="100" align="center" valign="middle">
                        <img src="${editedUser.avatar}" alt=""/><br>
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

        if (formName == "editProfileForm") {
            document.getElementById('avatar').setAttribute('value',
                    document.getElementById('avatarTempValue').value);
        } else {
            document.getElementById('avatar').setAttribute('value', null);
        }

        document.forms[formName].submit();

    }

    function createUploader() {
        var action;
        if (navigator.appName.indexOf("Microsoft") != -1 ||
                navigator.appName.indexOf("Opera") != -1) {
            action = '${pageContext.request.contextPath}/users/IFrameAvatarpreview';
        }
        else {
            action = '${pageContext.request.contextPath}/users/XHRavatarpreview';
        }

        console.log('Action: %s', action);
        var uploader = new qq.FileUploaderBasic({
            button:$("#upload").get(0),
            action:action,
            multiple:false,
            allowedExtensions:['jpg', 'jpeg', 'png', 'gif'],
            sizeLimit:4194304, // max size
            onSubmit:function (id, filename) {
                console.log('File upload: %s, ID: %s', filename, id);
            },
            onProgress:function (id, filename, loaded, total) {
                console.log('Progress for file: %s, ID: %s, loaded: %s, total: %s', filename, id, loaded, total);
            },
            onComplete:function (id, filename, responseJSON) {
                console.log('File upload for file %s, id %s done with status %s', filename, id, responseJSON);
                document.getElementById('avatarPreview').setAttribute('src', responseJSON.srcPrefix + responseJSON.srcImage);
                document.getElementById('avatarTempValue').setAttribute('value', responseJSON.srcImage);
            },
            debug:true
        });

    }

    $(document).ready(createUploader());
</script>

</body>
