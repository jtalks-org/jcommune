<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag body-content="empty" %>
<%@ attribute name="topic" required="true" type="org.jtalks.jcommune.model.entity.Topic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--Users with this grant can post even in closed topics.--%>
<c:set var="hasCloseTopicPermission" value="false"/>
<c:if test='${topic.codeReview == null}'>
  <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                        permission='BranchPermission.CLOSE_TOPICS'>
    <c:set var="hasCloseTopicPermission" value="true"/>
  </jtalks:hasPermission>
</c:if>
<%--User can post either if the topic is open, or he has a permission to close/open it--%>
<c:if test="${(!topic.closed || hasCloseTopicPermission) && topic.codeReview == null}">
  <jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                        permission='BranchPermission.CREATE_POSTS'>
    <a class="new-topic-btn btn btn-primary space-left-medium-nf"
       href="${pageContext.request.contextPath}/posts/new?topicId=${topic.id}&branchId=${topic.branch.id}"
       title="<spring:message code='label.tips.create_new_post'/>">
      <spring:message code="label.answer"/>
    </a>
  </jtalks:hasPermission>
</c:if>

<jtalks:hasPermission targetId='${topic.branch.id}' targetType='BRANCH'
                      permission='BranchPermission.MOVE_TOPICS'>
  <a href="#" class="move_topic btn space-left-medium-nf" title="<spring:message code='label.tips.move_topic'/>"
     data-topicId="${topic.id}">
    <spring:message code="label.topic.move"/>
  </a>
</jtalks:hasPermission>

<c:if test='${topic.codeReview == null && hasCloseTopicPermission}'>
  <c:choose>
    <c:when test="${topic.closed}">
      <a href="${pageContext.request.contextPath}/topics/${topic.id}/open" class="open_topic btn space-left-medium-nf">
        <spring:message code="label.topic.open"/>
      </a>
    </c:when>
    <c:otherwise>
      <a href="${pageContext.request.contextPath}/topics/${topic.id}/close" class="open_topic btn space-left-medium-nf">
        <spring:message code="label.topic.close"/>
      </a>
    </c:otherwise>
  </c:choose>
</c:if>