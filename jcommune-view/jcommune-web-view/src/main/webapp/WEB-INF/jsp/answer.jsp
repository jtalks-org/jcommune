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
    <div id="answer">
        <c:if test="${validationError==true}">
            <div id="error"><spring:message code="label.answer_error"/></div>
        </c:if>
        <jtalks:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/answer.html"
                       method="POST">
            <div>
                <h2><spring:message code="label.answer_to" />: <c:out value="${topic.title}"/></h2>
                <label for="bodytext">Write your message here:</label>
                <textarea name="bodytext" id="bodytext" cols="30" rows="10"></textarea>

                <div class="clear"></div>
                <button type="submit" class="coolbutton"><spring:message code="label.answer"/></button>
            </div>
        </jtalks:form>
    </div>
  </body>
</html>