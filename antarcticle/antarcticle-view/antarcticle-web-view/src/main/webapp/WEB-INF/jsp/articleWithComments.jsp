<%-- 
    Document   : articleWithComments
    Created on : Jul 15, 2011, 2:29:09 AM
    Author     : Dmitry Sokolov
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<html>
    <head> <title>Article: ${article.articleTitle}</title> </head>
    <body>
        <div class="div-table">
            <div class="div-table-caption">
                <h1>${article.articleTitle}</h1>
                ${article.articleContent}
            </div>
            <c:forEach var="comment" items="${comments}">
                <div class="div-table-row">
                    <div class="div-table-col" style="width: 200px;">
                        User: <c:out value="${comment.userCommented.username}" /><br/>
                        Date: <joda:format value="${comment.creationDate}" style="SM" />
                    </div>
                    <div class="div-table-col" style="width: 500px;">
                        <c:out value="${comment.commentContent}" /> 
                    </div>
                </div>
            </c:forEach>
        </div>
    </body>
</html>