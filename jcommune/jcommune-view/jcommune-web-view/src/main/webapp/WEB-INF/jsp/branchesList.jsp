<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head><title>Simple jsp page</title></head>
<body>
<form:form method="POST">
    <table border="1" width="100%">
        <c:forEach var="branch" items="${topicsBranchList}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/branch/${branch.id}.html"> <c:out
                        value="${branch.name}"/></a><br>
                    <span style="font-size: xx-small; "><c:out value="${branch.description}"/> </span>
                </td>
            </tr>
        </c:forEach>
    </table>
</form:form>
</body>
</html>
