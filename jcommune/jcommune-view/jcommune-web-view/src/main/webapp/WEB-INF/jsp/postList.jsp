<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head></head>
<body>
<table border="1" width="100%" name="messagesTable">

    <sec:authentication property="name" var="username" scope="request"/>    
    
    <h2>
        <spring:message code="label.topic"/>
        :
        <c:out value="${topicTitle}"/>
    </h2>    
    <c:forEach var="post" items="${posts}">
        <tr>
            <td width="20%"><spring:message code="label.author"/>: <c:out
                    value="${post.userCreated.username}"/>
            </td>
            <td width="80%"><spring:message code="label.text"/>: <c:out
                    value="${post.postContent}"/>
            </td>
            
            <c:if test="${username==post.userCreated.username}">
                <td>
                    <form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/post/${post.id}/delete.html"
                               method="GET">
                        <input type="submit" value="<spring:message code="label.delete"/>"/>
                    </form:form>
                </td>
            </c:if>
        </tr>
    </c:forEach>
</table>
<table>
    <tr>
        <td>
          <form:form action="${pageContext.request.contextPath}/branch/${branchId}.html" method="GET">
            <input type="submit" value="<spring:message code="label.back"/>"/>             
          </form:form>
        </td>
        <sec:authorize access="isAuthenticated()">
          <td>
            <form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/answer.html" name="answerButtonForm" method="GET">
              <input type="submit" name="addAnswerButton" value="<spring:message code="label.answer"/>"/>
            </form:form>
          </td>
        </sec:authorize>
    </tr>
</table>

<div id="pagination">
    <c:if test="${maxPages > 1}">
        <c:if test="${page > 1}">
            <c:url value="/branch/${branchId}/topic/${topicId}.html" var="prev">
                <c:param name="page" value="${page - 1}"/>
            </c:url>
            <a href='<c:out value="${prev}" />' class="pn next"><spring:message code="pagination.prev"/></a>
        </c:if>
        <c:forEach begin="1" end="${maxPages}" step="1" varStatus="i">
            <c:choose>
                <c:when test="${page == i.index}">
                    <span>${i.index}</span>
                </c:when>
                <c:otherwise>
                    <c:url value="/branch/${branchId}/topic/${topicId}.html" var="url">
                        <c:param name="page" value="${i.index}"/>
                    </c:url>
                    <a href='<c:out value="${url}" />'>${i.index}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page + 1 < maxPages+1}">
            <c:url value="/branch/${branchId}/topic/${topicId}.html" var="next">
                <c:param name="page" value="${page + 1}"/>
            </c:url>
            <a href='<c:out value="${next}" />' class="pn next"><spring:message code="pagination.next"/></a>
        </c:if>
    </c:if>
</div>

</body>
</html>