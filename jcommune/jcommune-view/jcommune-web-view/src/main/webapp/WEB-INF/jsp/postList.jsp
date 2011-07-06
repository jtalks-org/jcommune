<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head></head>
<body>
<table border="1" width="100%">

    <h2>
        <spring:message code="label.topic"/>
        :
        <c:out value="${topicTitle}"/>
    </h2>
    <c:forEach var="post" items="${posts}" varStatus="i">
        <tr>
            <td width="20%"><spring:message code="label.author"/>:
                <a href="${pageContext.request.contextPath}/user/${post.userCreated.id}.html">
                    <c:out value="${post.userCreated.username}"/>
                </a>
            </td>
            <td width="80%"><spring:message code="label.text"/>:
                <c:out value="${post.postContent}"/>
            </td>
            <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                <c:choose>
                    <c:when test="${page == 1 && i.index == 0}">
                        <td>
                            <form:form
                                    action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/delete.html"
                                    method="GET">
                                <input type="submit" value="<spring:message code="label.delete"/>"/>
                            </form:form>
                        </td>
                    </c:when>

                    <c:otherwise>
                        <td>
                            <form:form
                                    action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/post/${post.id}/delete.html"
                                    method="GET">
                                <input type="submit" value="<spring:message code="label.delete"/>"/>
                            </form:form>
                        </td>
                    </c:otherwise>
                </c:choose>
            </sec:accesscontrollist>
        </tr>
    </c:forEach>
</table>
<table>
    <tr>
        <td>
            <form:form action="${pageContext.request.contextPath}/branch/${branchId}.html" method="GET">
                <input type="submit" value="<spring:message code="label.back"/>"/>
            </form:form>
        </td>
        <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
            <td>
                <form:form action="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/answer.html"
                           method="GET">
                    <input type="submit" value="<spring:message code="label.answer"/>"/>
                </form:form>
            </td>
        </sec:authorize>
    </tr>
</table>

<div id="pagination">
    <c:if test="${maxPages > 1}">

        <c:if test="${page > 2}">
            <c:url value="/branch/${branchId}/topic/${topicId}.html" var="first">
                <c:param name="page" value="1"/>
            </c:url>
            <a href='<c:out value="${first}" />' class="pn next"><spring:message code="pagination.first"/></a>...
        </c:if>

        <c:choose>
            <c:when test="${page > 1}">
                <c:set var="begin" value="${page - 1}"/>
            </c:when>
            <c:otherwise>
                <c:set var="begin" value="1"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${page + 1 < maxPages}">
                <c:set var="end" value="${page + 1}"/>
            </c:when>
            <c:otherwise>
                <c:set var="end" value="${maxPages}"/>
            </c:otherwise>
        </c:choose>

        <c:forEach begin="${begin}" end="${end}" step="1" varStatus="i">
            <c:choose>
                <c:when test="${page == i.index}">
                    <span>${i.index}</span>
                </c:when>
                <c:otherwise>
                    <c:url value="/branch/${branchId}/topic/${topicId}.html" var="url">
                        <c:param name="page" value="${i.index}"/>
                    </c:url>
                    <a href='<c:out value="${url}" />'>${i.index}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page + 2 < maxPages+1}">
            <c:url value="/branch/${branchId}/topic/${topicId}.html" var="last">
                <c:param name="page" value="${maxPages}"/>
            </c:url>
            ...<a href='<c:out value="${last}"/>' class="pn next"><spring:message code="pagination.last"/></a>
        </c:if>

    </c:if>
</div>

</body>
</html>