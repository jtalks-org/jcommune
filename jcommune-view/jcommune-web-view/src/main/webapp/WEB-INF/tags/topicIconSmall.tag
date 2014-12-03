<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag body-content="empty" %>
<%@ attribute name="topicDto" required="true" type="org.jtalks.jcommune.plugin.api.web.dto.TopicDto" %>
<%@ attribute name="authenticated" required="false" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--actual icon depends on both new posts presence and topic closed status--%>
<c:if test="${topicDto.topic.hasUpdates}">
  <%--if there are new posts this icon should be a link--%>
  <a href="${pageContext.request.contextPath}/posts/${topicDto.topic.firstUnreadPostId}">
</c:if>

<c:choose>
  <c:when test="${topicDto.topic.hasUpdates && authenticated}">
    <c:set var="iconUrl" value="${topicDto.unreadIconUrl}"/>
     <c:set var="titleCode" value="label.topic.new_posts"/>
  </c:when>
  <c:otherwise>
    <c:set var="iconUrl" value="${topicDto.readIconUrl}"/>
    <c:set var="titleCode" value="label.topic.no_new_posts"/>
  </c:otherwise>
</c:choose>


<img class="status-img-small"
     src="${pageContext.request.contextPath}${iconUrl}"
     data-original-title="<spring:message code="${titleCode}" htmlEscape="true" javaScriptEscape="true"/>"
     alt="<spring:message code="${titleCode}" htmlEscape="true" javaScriptEscape="true"/>"/>
<c:if test="${topicDto.topic.hasUpdates}">
  </a>
</c:if>
