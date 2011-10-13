<%@ tag body-content="empty" %>
<%@ attribute name="breadcrumbList" required="true" rtexprvalue="true" type="java.util.ArrayList" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:forEach var="breadcrumb" items="${breadcrumbList}">
    <c:choose>
        <%--create root breadcrumb--%>
        <c:when test="${breadcrumb.breadcrumbLocationValue == 'Forum'}">
            <a class="forums_list" href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}">
                <fmt:message key="label.forum"/>
            </a>
        </c:when>
        <%--create inbox, outbox, drafts breadcrumbs--%>
        <c:when test="${breadcrumb.breadcrumbLocation.name == '/inbox'
                        || breadcrumb.breadcrumbLocation.name == '/outbox'
                        || breadcrumb.breadcrumbLocation.name == '/drafts'
                        || breadcrumb.breadcrumbLocation.name == '/topics/recent'}">
            <%--TODO Need to define standard URI for most location - ${Entity type}/${Entity ID}.html--%>
            <%--TODO Need to remove '/pm/' from controller mapping.html--%>
            <span class="arrow"> > </span>
            <a class="forums_list"
               href="${pageContext.request.contextPath}${breadcrumb.breadcrumbLocation.name}">
                <c:out value="${breadcrumb.breadcrumbLocationValue}"/>
            </a>
        </c:when>
        <%--create section, topic, branch, post breadcrumb--%>
        <c:otherwise>
            <span class="arrow"> > </span>
            <a class="forums_list"
               href="${pageContext.request.contextPath}/${breadcrumb.breadcrumbLocation.name}/${breadcrumb.id}">
                <c:out value="${breadcrumb.breadcrumbLocationValue}"/>
            </a>
        </c:otherwise>
    </c:choose>
</c:forEach>