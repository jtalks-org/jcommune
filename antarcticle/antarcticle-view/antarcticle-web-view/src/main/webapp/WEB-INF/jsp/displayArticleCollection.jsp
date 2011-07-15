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
        <table border="1" width="100%">
                 <h2> <c:out value="${articleCollection.title}"/> </h2><br>
                 <tr>
                   <td >              
                    </tr><c:out value="${articleCollection.description}" />
                   </td>
                 </tr>


                <tr>  
                 <td>
                    <c:out value="${firstArticle.articleTitle}"/>
                 </td>
                </tr>
               <tr>
                 <td>                                   
                    <c:out value="${firstArticle.articleContent}"/>
                 </td>
                </tr>

        </table>
    </body>
</html>