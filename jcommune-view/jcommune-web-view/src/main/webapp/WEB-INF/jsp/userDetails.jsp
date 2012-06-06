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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<head>
    <title><spring:message code="label.user"/> - ${user.username}</title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/json2.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/resources/javascript/custom/userProfileEffects.js"
            type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/profile.css"/>
    
</head>
<body>
    <sec:authentication property="principal.username" var="auth" scope="request"/>
    <jsp:include page="../template/topLine.jsp"/>

    <div class="container">
        <div id="userdetails" class="userprofile" style="max-width: 480px; margin: 0 auto; position: relative;">
            <div style="line-height:100px; " >
                <span class="pull-left thumbnail">
                    <div class="wraptocenter">
                        <span></span>
                        <c:if test="${user.avatar != null}">
                            <img src="${pageContext.request.contextPath}/${user.encodedUsername}/avatar" alt="" />
                        </c:if>
                    </div>
                </span>
                <h2 class="pull-right" style="vertical-align: middle; line-height:100px;">
                    <c:out value="${user.username}"/>
                </h2>
            </div>
            
            <div class="clearfix"></div>
                <div style="padding-top: 4px;">         
                    <c:if test="${user.username != auth}">
                        <div style="width: 100px; display:inline-block;text-align: center">
                            <a class="btn btn-mini btn-info" 
                                href="${pageContext.request.contextPath}/pm/new/${user.encodedUsername}" >
                                <spring:message code="label.pm.send"/>
                            </a>
                        </div>
                    </c:if>
                    
                    <a class="btn btn-mini pull-right" style="margin-left: 5px;"
                        href="${pageContext.request.contextPath}/users/${user.encodedUsername}/postList">
                        <spring:message code="label.postList"/>
                    </a>           
            </div>
            
            <div class="clearfix"></div>
            <hr style="margin: 6px 0 6px 0;"/>
            
            <div>
                <form class="form-horizontal">
                    <fieldset>
                        <div class="control-group">
                            <label for="" class="control-label">
                                <spring:message code="label.firstname"/>
                            </label>
                            <div class="controls">
                                <input type="text" class="input-xlarge uneditable-input" 
                                        readonly="readonly" value="<c:out value='${user.firstName}'/>"  
                                        autocomplete="off" />
                            </div>
                        </div>
                        
                        <div class="control-group">
                            <label for="" class="control-label">
                                <spring:message code="label.lastname"/>
                            </label>
                            <div class="controls">
                                <input type="text" class="input-xlarge uneditable-input" 
                                        readonly="readonly" value="<c:out value='${user.lastName}'/>"  
                                        autocomplete="off" />
                            </div>
                        </div>
                
                        <div class="control-group">
                            <label for="" class="control-label">
                                <spring:message code="label.signature"/>
                            </label>
                            <div class="controls">
                                <input type="text" class="input-xlarge uneditable-input" 
                                        readonly="readonly" value="<c:out value='${user.signature}'/>"  
                                        autocomplete="off" />
                            </div>
                        </div>
                        
                        <c:choose>
                            <%--Do not show my email to other users--%>
                            <c:when test="${user.username == auth}">
                                <div class="control-group">
                                    <label for="" class="control-label">
                                        <spring:message code="label.email"/>
                                    </label>
                                    <div class="controls">
                                        <input type="text" class="input-xlarge uneditable-input" 
                                            readonly="readonly" value="<c:out value='${user.email}'/>"  
                                            autocomplete="off" />
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label for="" class="control-label">
                                        <spring:message code="label.language"/>
                                    </label>
                                    <div class="controls">
                                        <input type="text" class="input-xlarge uneditable-input" 
                                            readonly="readonly" value="<c:out value='${language.languageNameLabel}'/>"  
                                            autocomplete="off" />
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label for="" class="control-label">
                                        <spring:message code="label.pageSize"/>
                                    </label>
                                    <div class="controls">
                                        <input type="text" class="input-xlarge uneditable-input" 
                                            readonly="readonly" value="<c:out value='${pageSize}'/>"  
                                            autocomplete="off" />
                                    </div>
                                </div>
                            </c:when>
                        </c:choose>
                        <div class="control-group">
                            <label for="" class="control-label">
                                <spring:message code="label.location"/>
                            </label>
                            <div class="controls">
                                <input type="text" class="input-xlarge uneditable-input" 
                                    readonly="readonly" value="<c:out value='${user.location}'/>"  
                                    autocomplete="off" />
                            </div>
                        </div>
                        
                        <div class="control-group">
                            <label for="" class="control-label">
                                <spring:message code="label.registrationDate"/>
                            </label>
                            <div class="controls">
                                <input type="text" class="input-xlarge uneditable-input" 
                                    readonly="readonly" value="<jtalks:format value='${user.registrationDate}'/>"  
                                    autocomplete="off" />
                            </div>
                        </div>
                        
                        <div class="control-group">
                            <label for="" class="control-label">
                                <spring:message code="label.lastlogin"/>
                            </label>
                            <div class="controls">
                                <input type="text" class="input-xlarge uneditable-input" 
                                    readonly="readonly" value="<jtalks:format value='${user.lastLogin}'/>"  
                                    autocomplete="off" />
                            </div>
                        </div>
                        
                        <div class="control-group">
                            <label for="" class="control-label" style="margin-top: -3px">   
                                <spring:message code="label.postcount"/>
                            </label>
                            <div class="controls">
                                <span class="label label-info"><c:out value="${user.postCount}"/></span>
                            </div>
                        </div>
                        
                        <c:if test="${!empty user.userContacts}"> 
                            <hr style="margin: 6px 0 6px 0;"/>
                        
                            <h4><spring:message code="label.contacts.header"/></h4>
                            <ul id="contacts" class="contacts">
                                <c:forEach var="contact" items="${user.userContacts}">
                                    <li>
                                        <span class="contact" title="<c:out value='${contact.type.typeName}'/>">
                                            <img src="${pageContext.request.contextPath}${contact.type.icon}" title="<c:out value="${contact.type.typeName}"/>">
                                            ${contact.displayValue}
                                        </span>
                                    </li>
                                </c:forEach>                        
                            </ul>
                        </c:if>
                        
                        <c:if test="${user.username == auth}">
                            <div style="padding-left: 160px;">
                                <a class="btn btn-primary" type="submit" 
                                    title="<spring:message code='label.edit_profile'/>"
                                    href="${pageContext.request.contextPath}/users/edit">
                                    <spring:message code="label.edit_profile"/>
                                </a>
                            </div>
                        </c:if>
                    </fieldset>
                </form>
            </div>
            <div class="clearfix"></div>
        </div>
    </div>
</body>
