<%@ page import="org.jtalks.jcommune.model.entity.Topic"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head></head>
<body>
	<table border="1" width="100%">
		<h2>
			<spring:message code="label.topic" />
			:
			<c:out value="${selectedTopic.title}" />
		</h2>
		<br>
		<c:forEach var="posts" items="${selectedTopic.posts}">
			<tr>
				<td width="20%"><spring:message code="label.author" />: <c:out
						value="${posts.userCreated.username}" /></td>
				<td width="80%"><spring:message code="label.text" />: <c:out
						value="${posts.postContent}" /></td>
			</tr>
		</c:forEach>
	</table>
	<table>
		<tr>
			<td><form:form
					action="${pageContext.request.contextPath}/forum.html" method="GET">
					<input type="submit" value="<spring:message code="label.back"/>" />
				</form:form></td>
			<td><form:form
					action="${pageContext.request.contextPath}/answer.html"
					method="POST">
					<input name="topicId" type="hidden" value="${selectedTopic.id}" />
					<input type="submit" value="<spring:message code="label.answer"/>" />
				</form:form></td>
		</tr>
	</table>

</body>
</html>