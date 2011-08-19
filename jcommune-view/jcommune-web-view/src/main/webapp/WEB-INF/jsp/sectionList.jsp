<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Section List</title>
</head>
<body>
<form:form method="POST">
  <c:forEach var="section" items="${brachesSectionList}">
    <c:set var="branches" value="${section.branches}"/>
    <a href="${pageContext.request.contextPath}/section/${section.id}.html"> 
      <h3><c:out value="${section.name}"/></h3>
      <h3><c:out value="${section.description}"/></h3></a>
    <br />
    <table border="1" width="100%">
        <c:forEach var="branch" items="${branches}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/branch/${branch.id}.html"> <c:out
                        value="${branch.name}"/></a><br>
                    <span style="font-size: xx-small;"><c:out value="${branch.description}"/> </span>
                </td>
                <td><c:out value="${branch.size}"/></td>
                <td><c:out value="${branch.topics.size}"/></td>
            </tr>
        </c:forEach>
    </table>
    <br />
  </c:forEach>
</body>
</html>