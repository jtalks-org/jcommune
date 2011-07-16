<%--
  author: Vitaliy Kravchenko
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head> <title> Article Collection with the first ARticle </title> </head>
    <body>

        <div class="container">
               <a class="title" href=""><c:out value="${articleCollection.title}"/></a>
               <div class="small_block">
                  <p><c:out value="${articleCollection.description}"/></p>
               </div>
               <div class="big_block">
                  <p><c:out value="${firstArticle.articleContent}"/></p>
               </div>
        </div>
    </body>
</html>