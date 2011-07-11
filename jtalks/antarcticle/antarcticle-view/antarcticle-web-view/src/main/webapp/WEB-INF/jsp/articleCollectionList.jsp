<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head> <title> All article collections </title> </head>
    <body>
        <table border="1" width="100%">
            <c:forEach var="articleCollection" items="${articleCollectionList}">
                <tr> <td>
                    <h1> <a href="${pageContext.request.contextPath}/articleCollection/${articleCollection.id}.html">
                        <c:out value="${articleCollection.title}" />
                    </a> </h1>
                    <div> <c:out value="${articleCollection.description}" /> </div>
                </td></tr>
            </c:forEach>
        </table>
    </body>
</html>