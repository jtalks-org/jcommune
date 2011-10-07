<%@ tag body-content="empty" %>
<%@ attribute name="breadcrumbList" required="true" rtexprvalue="true" type="java.util.ArrayList" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:forEach var="breadcrumb" items="${breadcrumbList}">
    <c:choose>
        <%--create root breadcrumb--%>
        <c:when test="${breadcrumb.breadcrumbLocationValue == 'Forum'}">
            <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}">
                <span class="nav"> <fmt:message key="label.forum"/> </span>
            </a>
        </c:when>
        <%--create inbox, outbox, drafts breadcrumbs--%>
        <c:when test="${breadcrumb.breadcrumbLocation.name == '/inbox'
                        || breadcrumb.breadcrumbLocation.name == '/outbox'
                        || breadcrumb.breadcrumbLocation.name == '/drafts'
                        || breadcrumb.breadcrumbLocation.name == '/topics/recent'}">
            <%--TODO Need to define standard URI for most location - ${Entity type}/${Entity ID}.html--%>
            <%--TODO Need to remove '/pm/' from controller mapping.html--%>
            <a href="${pageContext.request.contextPath}${breadcrumb.breadcrumbLocation.name}">
                <span class="nav"> <c:out value="${breadcrumb.breadcrumbLocationValue}"/> </span>
            </a>
        </c:when>
        <%--create section, topic, branch, post breadcrumb--%>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}/${breadcrumb.id}">
                <span class="nav"> <c:out value="${breadcrumb.breadcrumbLocationValue}"/> </span>
            </a>
        </c:otherwise>
    </c:choose>
</c:forEach>