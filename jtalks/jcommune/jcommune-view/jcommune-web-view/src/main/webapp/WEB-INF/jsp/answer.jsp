<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<html>
  <head>
    <title><spring:message code="label.answer_to"/>: <c:out value="${topic.title}"/></title>
    <link href="${pageContext.request.contextPath}/css/main.css" type=text/css rel=stylesheet>
  </head>
  <body>
    <div id="stylized">
      <h1 style="margin:10px">
        <spring:message code="label.answer_to" />: <c:out value="${topic.title}"/>
      </h1>
      <jtalks:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/answer.html" 
                   method="POST">
        <c:if test="${validationError==true}">
          <div style="margin: 10px; color: #e43131"><spring:message code="label.answer_error"/></div>
        </c:if>
        <textarea style="margin:10px" name="bodytext" cols="40" rows="10"></textarea>
        <br>
        <input type="submit" value="<spring:message code="label.answer"/>" />
        <div class="spacer"></div>
      </jtalks:form>
    </div>
  </body>
</html>