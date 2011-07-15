<%-- 
    Document   : articleWithComments
    Created on : Jul 15, 2011, 2:29:09 AM
    Author     : Dmitry Sokolov
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head> <title>Article: ${article.articleTitle}</title> </head>
    <body>
        <div id="articleTitle">
            ${article.articleTitle}
        </div>
        <div id="articleComtent">
            ${article.articleContent}
        </div>
        <div id="comments">
            <c:forEach var="comment" items="${comments}">
                <div class="userCommented">
                    <c:out value="${comment.userCommented.username}" />
                </div>
                <div class="commentContent">
                    <c:out value="${comment.commentContent}" /> 
                </div>
            </c:forEach>
        </div>
    </body>
</html>