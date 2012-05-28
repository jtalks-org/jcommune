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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/xml_rt" %>
<%@ tag body-content="empty" %>
<%@ attribute name="titleNameValue" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="pollOptionsNameValue" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="singleNameValue" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="singleValue" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="multipleValue" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="endingDateNameValue" required="true" rtexprvalue="true" type="java.lang.String" %>

<div class='well' id="editPoll">
        <legend><spring:message code="label.poll.header"/></legend>
        
        <div class='control-group'>
        <label class='control-label'>
            <spring:message code="label.poll.title"/>
        </label>
        <form:input path="${titleNameValue}" id="${titleNameValue}" type="text" name="${titleNameValue}"
                            size="45"
                            maxlength="255" tabindex="1"
                            class="post"/>
                <br>
                <form:errors path="${titleNameValue}" cssClass="error"/>
        </div>
        
        <div class='control-group'>
            <label class='control-label'>
                <spring:message code="label.poll.options.title"/>
            </label>
            
            <form:textarea path="${pollOptionsNameValue}" rows="8"
                        class="post"/>
            <br>
            <form:errors path="${pollOptionsNameValue}" cssClass="error"/>
        </div>
        
        <div class='control-group'>            
                <input type="radio" name="${singleNameValue}" checked="checked"
                       value="${singleValue}"/>
                <spring:message code="label.poll.single.title"/>
                <br/>
            
                <input id="multipleBtn" type="radio" name="${singleNameValue}"
                       value="${multipleValue}"/>
                <spring:message code="label.poll.multiple.title"/>
        </div>
        
        <div class='control-group'>
            <label class='control-label'>
                <spring:message code="label.poll.date"/>
            </label>
            
                <input id="datepicker" type="text" name="${endingDateNameValue}">
                <br>
                <form:errors path="${endingDateNameValue}" cssClass="error"/>
        </div>

    <div id="previewPoll">

    </div>


</div>