<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  author: Pavel Karpukhin
--%>
<html>
    <head> <title> All article collections </title> </head>
    <body>
        <c:forEach var="articleCollection" items="${articleCollectionList}">
            <h2> <a href="${pageContext.request.contextPath}/articleCollection/${articleCollection.id}.html">
                <c:out value="${articleCollection.title}" />
            </a> </h2>
            <div> <c:out value="${articleCollection.description}" /> </div>
        </c:forEach>
    </body>
</html>