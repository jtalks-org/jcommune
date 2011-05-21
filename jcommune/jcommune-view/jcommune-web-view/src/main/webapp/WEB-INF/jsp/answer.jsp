<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
  <head>
    <title><spring:message code="label.answer_to"/>: <c:out value="${topic.title}"/></title>
    <link href="${pageContext.request.contextPath}/css/main.css"
          type=text/css rel=stylesheet>
  </head>
  <body>
    <div id="stylized">
      <h1 style="margin:10px">
        <spring:message code="label.answer_to" />: <c:out value="${topic.title}"/>
      </h1>
      <form:form action="${pageContext.request.contextPath}/addAnswer.html" method="POST">
        <input type="hidden" name="topicId" value="${topic.id}" />
        <c:if test="${validationError==true}">
          <div style="margin: 10px; color: #e43131"><spring:message code="label.answer_error"/></div>
        </c:if>
        <textarea style="margin:10px" name="bodytext" cols="40" rows="10"></textarea>
        <br>
        <input type="submit" value="<spring:message code="label.answer"/>" />
        <div class="spacer"></div>
      </form:form>
    </div>
  </body>
</html>