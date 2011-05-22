<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head></head>
<body>
<table border="1" width="100%">    

    <sec:authentication property="name" var="username" scope="request"/>    
    
    <h2>
        <spring:message code="label.topic"/>
        :
        <c:out value="${selectedTopic.title}"/>
    </h2>    
    <c:forEach var="posts" items="${selectedTopic.posts}">
        <tr>
            <td width="20%"><spring:message code="label.author"/>: <c:out
                    value="${posts.userCreated.username}"/>
            </td>
            <td width="80%"><spring:message code="label.text"/>: <c:out
                    value="${posts.postContent}"/>
            </td>
            
            <c:if test="${username==posts.userCreated.username}">
                <td>
                    <form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${selectedTopic.id}/deletePost.html" 
                               method="GET">
                        <input name="topicId" type="hidden" value="${selectedTopic.id}"/>
                        <input name="postId" type="hidden" value="${posts.id}"/>
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
            <form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${selectedTopic.id}/answer.html" method="GET">
              <input name="topicId" type="hidden" value="${selectedTopic.id}"/>
              <input type="submit" value="<spring:message code="label.answer"/>"/>
            </form:form>
          </td>
        </sec:authorize>
    </tr>
</table>

</body>
</html>