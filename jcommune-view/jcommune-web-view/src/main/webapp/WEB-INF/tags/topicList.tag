<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ attribute name="topics" required="true" type="java.util.List" %>
<%@ attribute name="messageToShowIfNoTopics" required="true" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="showBranchColumn" required="false" type="java.lang.Boolean" rtexprvalue="true" %>
<c:set var="authenticated" value="false"/>
<sec:authorize access="isAuthenticated()">
    <c:set var="authenticated" value="true"/>
</sec:authorize>
<table id="topics-table" class="table table-row table-bordered">
  <c:choose>
    <c:when test="${!(empty topics)}">
      <thead>
      <tr>
        <th class="status-col-small"></th>
        <th><spring:message code="label.branch.header.topics"/></th>
        <th class="latest-by forum-latest-by-header">
          <spring:message code="label.branch.header.lastMessage"/>
        </th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="topicDto" items="${topics}">
        <tr>
          <td class="status-col-small">
            <jtalks:topicIconSmall topicDto="${topicDto}" authenticated="${authenticated}"/>
          </td>
          <td class="posts-td-small posts-td-small_2">
            <h2 class="h-nostyle">
                <%--Some topic types should have a special prefix when displayed--%>
              <c:if test="${topicDto.topic.announcement=='true'}">
                <span class="sticky space-left-small-nf"><spring:message code="label.marked_as_announcement"/></span>
              </c:if>
              <c:if test="${topicDto.topic.sticked=='true'}">
                <span class="sticky space-left-small-nf"><spring:message code="label.marked_as_sticked"/></span>
              </c:if>
              <c:if test="${topicDto.topic.hasPoll}">
                <span class="sticky space-left-small-nf"><spring:message code="label.marked_as_poll"/></span>
              </c:if>
              <a class="space-left-small-nf" href="${pageContext.request.contextPath}${topicDto.topicUrl}">
                <c:out value="${topicDto.topic.title}"/>
              </a>
            </h2>
            <div class="created-by">
              <span><spring:message code="label.topic.created_by"/>&nbsp;</span>
              <a href="${pageContext.request.contextPath}/users/${topicDto.topic.topicStarter.id}"
                 data-original-title="<spring:message code="label.tips.view_profile"/>" data-placement="right">
                <c:out value="${topicDto.topic.topicStarter.username}"/>
              </a>&nbsp;&nbsp;
              <c:if test="${showBranchColumn}">
                <span><spring:message code="label.topic.section.in"/>&nbsp;</span>
                <a href="${pageContext.request.contextPath}/branches/${topicDto.topic.branch.id}">
                  <c:out value="${topicDto.topic.branch.name}"/>
                </a>
              </c:if>
            </div>
          </td>
          <td class="latest-by shrink-to-fit">
            <div>
              <i class="icon-calendar"></i>
              <a class="date margin-right-big" href="${pageContext.request.contextPath}/posts/${topicDto.topic.lastDisplayedPost.id}"
                 data-original-title="<spring:message code="label.branch.header.lastMessage.tooltip"/>">
                <jtalks:format value="${topicDto.topic.lastDisplayedPost.creationDate}"/>
              </a>
              <i class="icon-envelope margin-left-big margin-right-big"></i>
              <span class='test-views' data-original-title="<spring:message code="label.branch.header.posts"/>">
                <c:out value="${topicDto.topic.displayedPostsCount}"/>
              </span>
            </div>
            <div>
              <i class="icon-user"></i>
              <a class="space-left-small"
                 href="${pageContext.request.contextPath}/users/${topicDto.topic.lastDisplayedPost.userCreated.id}"
                 title="<spring:message code='label.tips.view_profile'/>">
                <c:out value="${topicDto.topic.lastDisplayedPost.userCreated.username}"/>
              </a>
            </div>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </c:when>
    <c:otherwise>
      <tbody>
      <tr>
        <td><c:out value="${messageToShowIfNoTopics}"/></td>
      </tr>
      </tbody>
    </c:otherwise>
  </c:choose>
</table>
