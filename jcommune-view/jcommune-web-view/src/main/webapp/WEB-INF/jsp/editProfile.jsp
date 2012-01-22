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
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/javascript/licensed/fileuploader.js"></script>
</head>
<body>
<div class="wrap userdetails_page">
    <jsp:include page="../template/topLine.jsp"/>
    <jsp:include page="../template/logo.jsp"/>

    <div class="all_forums">
        <div id="editUserDetails">
            <form:form id="editProfileForm" name="editProfileForm"
                       action="${pageContext.request.contextPath}/users/edit"
                       modelAttribute="editedUser" method="POST" enctype="multipart/form-data">

                <form:hidden id="avatar" path="avatar"/>
                <input id="avatarTempValue" type="hidden"/>

                <div class="forum_header_table">
                    <div class="forum_header">
                        <span class="forum_header_generic"><spring:message code="label.profile"/></span>
                        <span class="empty_cell"></span>
                    </div>
                </div>
                <ul class="forum_table">
                    <li class="forum_row">
                        <label><spring:message code="label.username"/></label>
                        <span><c:out value="${auth}"/></span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.email"/></label>
                        <span>
                            <form:input path="email" size="25" value="${editedUser.email}"/>
                            <br/>
                            <form:errors path="email" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.firstname"/></label>
                        <span>
                            <form:input path="firstName" size="25" value="${editedUser.firstName}"/>
                            <br/>
                            <form:errors path="firstName" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.lastname"/></label>
                        <span>
                            <form:input path="lastName" size="25" value="${editedUser.lastName}"/>
                            <br/>
                            <form:errors path="lastName" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.currentPassword"/></label>
                        <span>
                            <form:input path="currentUserPassword" size="25" type="password"/>
                            <br/>
                            <form:errors path="currentUserPassword" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.newPassword"/></label>
                        <span>
                            <form:input path="newUserPassword" size="25" type="password"/>
                            <br/>
                            <form:errors path="newUserPassword" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.newPasswordConfirmation"/></label>
                        <span>
                            <form:input path="newUserPasswordConfirm" size="25" type="password"/>
                            <br/>
                            <form:errors path="newUserPasswordConfirm" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.signature"/></label>
                        <span>
                            <form:input path="signature" size="40" value="${editedUser.signature}"/>
                            <br/>
                            <form:errors path="signature" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.language"/></label>
                        <span>
                            <form:select path="language" value="${editedUser.language}">
                                <c:forEach items="${editedUser.languagesAvailable}" var="language">
                                    <form:option value="${language}">
                                        <spring:message code="${language.languageNameLabel}"/>
                                    </form:option>
                                </c:forEach>
                            </form:select>
                            <br/>
                            <form:errors path="language" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.numberOfTopicsOnPage"/></label>
                        <span>
                            <form:select path="pageSize" value="${editedUser.pageSize}"
                                         items="${editedUser.pageSizesAvailable}"/>
                            <br/>
                            <form:errors path="pageSize" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.location"/></label>
                        <span>
                            <form:input path="location" size="40" value="${editedUser.location}"/>
                            <br/>
                            <form:errors path="location" cssClass="error"/>
                        </span>
                    </li>
                    <li class="forum_row">
                        <label><spring:message code="label.avatar"/></label>
                        <span class="avatar">
                            <img id="avatarPreview" src="${editedUser.avatar}" alt=""/>
                        </span>
                    </li>
                    <li class="forum_row"></li>
                </ul>
                <div class="form_controls">
                    <span id="upload"><input type="button" value="<spring:message code="label.avatar.load"/>"/></span>
                    <input type="submit" value="<spring:message code="label.save_changes"/>"
                           onclick="submitForm('editProfileForm')"/>
                    <a href="${pageContext.request.contextPath}/users/${auth}">
                        <button><spring:message code="label.back"/></button>
                    </a>
                </div>
            </form:form>
            <c:if test="${editedUser.avatar != null}">
                <form action="${pageContext.request.contextPath}/users/edit/avatar" id="removeAvatarForm"
                      name="removeAvatarForm" method="POST">
                    <div class="form_controls">
                        <input type="submit" value="<spring:message code="label.avatar.remove"/>"
                               onclick="submitForm('removeAvatarForm')"/>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
    <div class="footer_buffer"></div>
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
                if (responseJSON.success == "true") {
                    document.getElementById('avatarPreview').setAttribute('src', responseJSON.srcPrefix
                            + responseJSON.srcImage);
                    document.getElementById('avatarTempValue').setAttribute('value', responseJSON.srcImage);
                } else {
                    document.getElementById('avatarPreview').setAttribute('src', "");
                    document.getElementById('avatarPreview').setAttribute('alt', responseJSON.message);
                }

            },
            debug:false
        });

    }

    $(document).ready(createUploader());
</script>
</body>
