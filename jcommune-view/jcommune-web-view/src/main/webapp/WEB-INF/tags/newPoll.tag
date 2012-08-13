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
<%@ attribute name="multipleName" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="multipleValue" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="endingDateNameValue" required="true" rtexprvalue="true" type="java.lang.String" %>

<div class='well'>
    <div id="editPoll">
        <legend><spring:message code="label.poll.header"/></legend>

        <div class='control-group'>
            <spring:message code='label.poll.title' var='pollTitlePlaceholder'/>
            <form:input path="${titleNameValue}" id="${titleNameValue}" type="text" name="${titleNameValue}"
                        size="45" maxlength="255" placeholder="${pollTitlePlaceholder}" class="post"/>
            <br>
            <form:errors path="${titleNameValue}" cssClass="help-inline"/>
        </div>

        <div class='control-group'>
            <spring:message code='label.poll.options.title' var='optionsPlaceholder'/>
            <form:textarea path="${pollOptionsNameValue}" rows="8" class="post" placeholder="${optionsPlaceholder}"/>
            <br>
            <form:errors path="${pollOptionsNameValue}" cssClass="help-inline"/>
        </div>

        <div class='control-group left-aligned'>
            <form:checkbox path="${multipleName}" id="multipleChecker" value="${multipleValue}"/>
            <spring:message code="label.poll.multiple.title"/>
        </div>

        <div class="control-group right-aligned">
            <spring:message code="label.poll.date"/>
            <spring:message code='label.poll.date.set' var='datePlaceholder'/>
            <form:input path="${endingDateNameValue}" id="datepicker" type="text" readonly="true"
                        placeholder="${datePlaceholder}" class="cursor-pointer"/>
            &nbsp;<i class="icon-trash cursor-pointer" id="deleteEndingDate"></i>
            <br>
            <form:errors path="${endingDateNameValue}" cssClass="help-inline"/>
        </div>
        <%--Make parent div include floated divs explicitly, or they'll be shown out of parent container--%>
        <div class="cleared"></div>
    </div>
    <div id="previewPoll">
    </div>
</div>