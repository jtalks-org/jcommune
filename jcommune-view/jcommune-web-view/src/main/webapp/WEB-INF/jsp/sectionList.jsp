<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
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
  <c:forEach var="section" items="${sectionList}">
    <h3><a href="${pageContext.request.contextPath}/section/${section.id}.html"> 
      <c:out value="${section.name}"/></a></h3>
      <span style="font-size: xx-small;"><c:out value="${section.description}"/></span>
    <br />
    <table border="1" width="100%">
        <c:forEach var="branch" items="${section.branches}" varStatus="i">
            <tr>
                <td><a href="${pageContext.request.contextPath}/branch/${branch.id}.html">
                    <c:out value="${branch.name}"/></a><br>
                    <span style="font-size: xx-small;"><c:out value="${branch.description}"/> </span>
                </td>
            </tr>
        </c:forEach>
    </table>
    <br />
  </c:forEach>
</form:form>
</body>
</html>