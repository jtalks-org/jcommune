<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head></head>
<body>
<div id="content">
    <div id="posts">
        <h2>
            <spring:message code="label.topic"/>: <c:out value="${topic.title}"/>
        </h2>
        <table align="center" cellpadding="0" cellspacing="0">
            <colgroup>
                <col class="oce-first"/>
            </colgroup>
            <thead>
                <tr>
                    <th scope="col">Author</th>
                    <th scope="col">Message</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="post" items="${posts}" varStatus="i">
                    <tr>
                        <td class="author">
                            <a href="${pageContext.request.contextPath}/user/${post.userCreated.encodedUsername}.html">
                                    <c:out value="${post.userCreated.username}"/>
                            </a>
                        </td>
                        <td class="content" valign="top">
                            <table>
                                <tr>
                                    <td class="link">
                                        <a href="javascript:copyLink(${post.id})">
                                            <spring:message code="label.link"/>
                                        </a>
                                        <span class="modification-date">
                                            <joda:format value="${post.creationDate}"
                                                 locale="${sessionScope['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']}"
                                                 pattern="dd MMM yyyy HH:mm"/>
                                        </span>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text">
                                        <c:out value="${post.postContent}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="buttons">
                                        <sec:accesscontrollist hasPermission="8,16" domainObject="${post}">
                                            <c:choose>
                                                <c:when test="${page == 1 && i.index == 0}">
                                                    <%-- first post - url to delete topic --%>
                                                    <c:set var="delete_url" value="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/delete.html"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <%-- url to delete post --%>
                                                    <c:set var="delete_url" value="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/post/${post.id}/delete.html"/>
                                                </c:otherwise>
                                            </c:choose>
                                            <a class="coolbutton" href="${delete_url}"><spring:message code="label.delete"/></a>
                                        </sec:accesscontrollist>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div id="buttons">
            <a class="coolbutton" href="${pageContext.request.contextPath}/branch/${branchId}.html">
                <spring:message code="label.back"/>
            </a>
            <sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <a class="coolbutton" href="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/answer.html">
                   <spring:message code="label.answer"/>
                </a>
            </sec:authorize>
            <sec:accesscontrollist hasPermission="8,16" domainObject="${topic}">
                <a class="coolbutton" href="${pageContext.request.contextPath}/branch/${branchId}/topic/${topicId}/edit.html">
                    <spring:message code="label.edit"/>
                </a>
            </sec:accesscontrollist>
        </div>
    </div> <!-- postList -->
    <c:if test="${maxPages > 1}">
        <div id="pagination">
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
        </div> <!-- pagination -->
    </c:if>
</div> <!-- content -->
<script type="text/javascript">
    function copyLink(postId) {
        prompt("Link to copy", document.location.href+"#"+postId);
    }
</script>
</body>
</html>