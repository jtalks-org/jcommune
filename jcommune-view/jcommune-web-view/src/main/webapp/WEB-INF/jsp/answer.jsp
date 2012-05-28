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

<head>
    <title><spring:message code="label.answer_to"/>: <c:out value="${topic.title}"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
    <script language="javascript"
            src="${pageContext.request.contextPath}/resources/javascript/custom/leaveConfirm.js"></script>
</head>
<body>
<div class="wrap answer_page">
   <jsp:include page="../template/topLine.jsp"/>

    <div class="container">
        <h2><a class="heading" href="${pageContext.request.contextPath}/topics/${topic.id}"><c:out value="${topic.title}"/></a></h2>
               
        <form:form action="${pageContext.request.contextPath}/posts/new?topicId=${topicId}&page=${page}"
                   method="POST" modelAttribute="postDto" class='well'>
            <form:hidden path="topicId"/>
            
            <jtalks:bbeditor labelForAction="label.answer"
                             postText="${postDto.bodyText}"
                             bodyParameterName="bodyText"
                             back="${pageContext.request.contextPath}/topics/${topicId}"/>
        </form:form>

        <a href="${back}" style="font-weight: bold"><i class="icon-arrow-left"></i> <spring:message code="label.back"/></a>
    </div>
</div>
<script type="text/javascript">
      jQuery(document).ready(function(){
        // Setup drop down menu
        jQuery('.dropdown-toggle').dropdown();
       
        // Fix input element click problem
        jQuery('.dropdown input, .dropdown label').click(function(e) {
          e.stopPropagation();
        });

        // Tooltips on status images
        jQuery('.btn-toolbar a').tooltip();

        // jQuery('#color-picker').colourPicker();
      });
    </script>
</body>