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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<jsp:useBean id="topicsPage" type="org.springframework.data.domain.Page" scope="request"/>
<head>
  <title>
      <c:out value="${cmpTitlePrefix}"/>
      <spring:message code="label.messagesWithoutAnswers"/>
  </title>
</head>
<body>
<div class="container">
  <div class="row-fluid upper-pagination forum-pagination-container">
    <div class="span3">
      <h3>
        <spring:message code="label.messagesWithoutAnswers"/>
      </h3>
    </div>

    <div class="span9">
      <div class="pagination pull-right forum-pagination">
        <ul>
          <jtalks:pagination uri="" page="${topicsPage}"/>
        </ul>
      </div>
    </div>
  </div>

  <%--you cannot use <spring> tag inside of an attribute, thus defining it as a separate var--%>
  <spring:message code="label.messagesWithoutAnswers.empty" var="messageToShowIfNoTopics"/>
  <jtalks:topicList topics="${topicsPage.content}" messageToShowIfNoTopics='${messageToShowIfNoTopics}'
                    showBranchColumn="true"/>

  <div class="row-fluid upper-pagination forum-pagination-container">
    <div class="span12">
      <div class="pagination pull-right forum-pagination-container">
        <ul>
          <jtalks:pagination uri="" page="${topicsPage}"/>
        </ul>
      </div>
    </div>
  </div>
</div>
</body>
