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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<c:set value="false" var="pollEditing"/>
<c:if test ="${topicId != null}">
  <c:set value="true" var="pollEditing"/>
</c:if>
<head>
  <meta name="description" content="<c:out value="${topicDto.topic.branch.name}"/>">
  <title>
    <c:out value="${cmpTitlePrefix}"/>
    <c:out value="${topicDto.topic.branch.name}"/> - <spring:message code="h.new_topic"/>
  </title>
</head>
<body>
<div class="container">
  <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

  <div id="previewPoll" class="well">
  </div>
  <form:form action="${pageContext.request.contextPath}${submitUrl}"
             method="POST" modelAttribute="topicDto" class="well anti-multipost submit-form" enctype="multipart/form-data">
    <div class='control-group hide-on-preview'>
      <div class='controls'>
        <spring:message code='label.topic.topic_title' var='topicTitlePlaceholder'/>
        <form:input path="topic.title" id="subject" type="text" size="45"
                    maxlength="255" tabindex="100"
                    class="span11 script-confirm-unsaved" placeholder="${topicTitlePlaceholder}"/>
        <form:errors path="topic.title" id="subjectError" type="text" name="subjectError" size="45"
                     maxlength="255"
                     class="post" cssClass="help-inline focusToError"/>
      </div>
    </div>
    <jtalks:hasPermission targetId='${branchId}' targetType='BRANCH'
                          permission='BranchPermission.CREATE_STICKED_TOPICS'>
      <div class='control-group hide-on-preview'>
        <form:checkbox id="sticked" path="topic.sticked" value="true" tabindex="101"
                       class="confirm-unsaved form-check-radio-box"/>
        <label for='sticked' class='string optional'>
          <spring:message code="label.sticked"/>
        </label>

        <form:errors path="topic.sticked"/>
      </div>
    </jtalks:hasPermission>

    <jtalks:hasPermission targetId='${branchId}' targetType='BRANCH' permission='BranchPermission.CREATE_ANNOUNCEMENTS'>
      <div class='control-group hide-on-preview'>
        <form:checkbox id="announcement" path="topic.announcement" value="true" tabindex="102"
                       class="script-confirm-unsaved form-check-radio-box"/>
        <label for='announcement' class='string optional'>
          <spring:message code="label.announcement"/>
        </label>
        <form:errors path="topic.announcement"/>
      </div>
    </jtalks:hasPermission>
    <jtalks:bbeditor labelForAction="label.save"
                     postText="${topicDto.bodyText}"
                     bodyParameterName="bodyText"
                     back="${pageContext.request.contextPath}/branches/${branchId}"/>
    <c:if test="${topicId eq null}">
      <input id="pollButton" type="button" rel="add" class="btn btn-success space-left-medium-nf" name="pollButton"
             tabindex="300" value="<spring:message code="label.addPoll"/>"/>
    </c:if>
    <c:if test="${topicId eq null || topicDto.poll != null}">
    <div class="hide-on-preview">
    <br/>
    <br/>
    <div id="editPollHolder" class='well <c:if test="${topicId eq null}">hide</c:if>'>
      <fieldset id="editPoll">
        <legend>
          <c:choose>
            <c:when test="${pollEditing}">
              <spring:message code="label.poll.title"/>
            </c:when>
            <c:otherwise>
              <spring:message code="label.poll.header"/>
            </c:otherwise>
          </c:choose>
        </legend>
        <div class='control-group'>
          <spring:message code='label.poll.title' var='pollTitlePlaceholder'/>
          <form:input path="topic.poll.title" tabindex="600" type="text" id="pollTitle"
                      size="45" maxlength="255" placeholder="${pollTitlePlaceholder}"
                      class="post script-confirm-unsaved input-xlarge" disabled="${pollEditing}"/>
          <br>
          <form:errors path="topic.poll.title" cssClass="help-inline focusToError"/>
        </div>
        <div class='control-group'>
          <spring:message code='label.poll.options.title' var='optionsPlaceholder'/>
          <spring:message code='label.poll.options.addPollItem' var='addPollItemLabel'/>
          <div class="left-aligned">
            <c:choose>
              <c:when test="${pollEditing}">
                <c:forEach items="${topicDto.poll.pollItems}" varStatus="i">
                    <div class="pollItemsValue">
                        <form:input path="poll.pollItems[${i.index}]" placeholder="${optionsPlaceholder}"
                                class="input-medium" disabled="${pollEditing}"/></div>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <script type="text/javascript">
                   var minPollItems = ${pollConfig.minPollItems};
                   var maxPollItems = ${pollConfig.maxPollItems};
                </script>
                <div id="sortable" rel="pollItemsValue[__index__]">
                  <c:choose>
                    <c:when test="${not empty topicDto.pollItemsValue}">
                      <c:set value="${topicDto.pollItemsValue.size()-1}" var="pollItemsUpperBound" />
                    </c:when>
                    <c:otherwise>
                      <c:set value="${pollConfig.minPollItems-1}" var="pollItemsUpperBound" />
                    </c:otherwise>
                  </c:choose>
                  <c:forEach begin="0" end="${pollItemsUpperBound}" varStatus="i">
                    <div class="pollItemsValue">
                      <div class="input-append">
                        <form:input path="pollItemsValue[${i.index}]" placeholder="${optionsPlaceholder}"
                                      class="input-xlarge" disabled="${pollEditing}"/>
                        <a href="#" class="icon-move poll-icon-pull"></a>
                        <a href="#" class="icon-remove remove poll-icon-pull"></a>
                      </div>
                    </div>
                  </c:forEach>
                  <a href="#" id="add" class="btn btn-primary btn-xs">${addPollItemLabel}</a>
                </div>
                <form:errors path="topic.poll.pollItems" cssClass="help-inline focusToError"/>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="right-aligned">
            <spring:message code="label.poll.date"/>
            <spring:message code='label.poll.date.set' var='datePlaceholder'/>
            <form:input path="poll.endingDate" tabindex="900" id="datepicker" type="text"
                        readonly="true" placeholder="${datePlaceholder}"
                        class="cursor-pointer script-confirm-unsaved space-left-small"/>
            <c:if test="${topicId eq null}">
                &nbsp;<i class="icon-trash cursor-pointer" id="deleteEndingDate"></i>
            </c:if>
            <br>
            <form:errors path="poll.endingDate" cssClass="help-inline"/>
          </div>
        </div>
        <div class="cleared"></div>
        <div class='control-group'>
          <br/>
          <form:checkbox path="poll.multipleAnswer" id="multipleChecker"
                         class="form-check-radio-box script-confirm-unsaved"
                         tabindex="800" value="${poll.multipleAnswer}" disabled="${pollEditing}"/>
          <label for='multipleChecker' class='string optional'>
            <spring:message code="label.poll.multiple.title"/>
          </label>
        </div>
        <%--Make parent div include floated divs explicitly, or they'll be shown out of parent container--%>
        <div class="cleared"></div>
      </fieldset>
    </div>
    </div>
    </c:if>
  </form:form>

  <a href="${pageContext.request.contextPath}/branches/${branchId}" tabindex="1000" class='back-btn'>
    <i class="icon-arrow-left"></i>
    <spring:message code="label.back"/>
  </a>
</div>
</body>
