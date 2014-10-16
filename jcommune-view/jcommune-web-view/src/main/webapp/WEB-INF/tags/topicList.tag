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
      <c:forEach var="topic" items="${topics}">
        <tr>
          <td class="status-col-small">
            <jtalks:topicIconSmall topic="${topic}" authenticated="${authenticated}"/>
          </td>
          <td class="posts-td-small posts-td-small_2">
            <h2 class="h-nostyle">
              <span class="badge badge-inverse" data-original-title="<spring:message code="label.postcount"/>">
                <c:out value="${topic.postCount}"/>
              </span>
                <%--Some topic types should have a special prefix when displayed--%>
              <c:if test="${topic.announcement=='true'}">
                <span class="sticky space-left-small-nf"><spring:message code="label.marked_as_announcement"/></span>
              </c:if>
              <c:if test="${topic.sticked=='true'}">
                <span class="sticky space-left-small-nf"><spring:message code="label.marked_as_sticked"/></span>
              </c:if>
              <c:if test="${topic.hasPoll}">
                <span class="sticky space-left-small-nf"><spring:message code="label.marked_as_poll"/></span>
              </c:if>
              <a class="space-left-small-nf" href="${pageContext.request.contextPath}/topics/${topic.id}">
                <c:out value="${topic.title}"/>
              </a>
            </h2>
            <sub class="created-by"><spring:message code="label.topic.created_by"/>
              <a class="space-left-small" href="${pageContext.request.contextPath}/users/${topic.topicStarter.id}">
                <c:out value="${topic.topicStarter.username}"/>
              </a>
              <c:if test="${showBranchColumn}">
                in <a href="${pageContext.request.contextPath}/branches/${topic.branch.id}">
                  <c:out value="${topic.branch.name}"/>
                </a>
              </c:if>
            </sub>
          </td>
          <td class="latest-by shrink-to-fit">
            <div>
              <i class="icon-calendar"></i>
              <a class="date" href="${pageContext.request.contextPath}/posts/${topic.lastPost.id}"
                 data-original-title="<spring:message code="label.branch.header.lastMessage.tooltip"/>">
                <jtalks:format value="${topic.lastPost.creationDate}"/>
              </a>
            </div>
            <div>
              <i class="icon-user"></i>
              <a class="space-left-small margin-right-big"
                 href="${pageContext.request.contextPath}/users/${topic.lastPost.userCreated.id}"
                 title="<spring:message code='label.tips.view_profile'/>">
                <c:out value="${topic.lastPost.userCreated.username}"/>
              </a>
              <i class="icon-eye-open margin-left-big"></i>
              <span class='test-views' data-original-title="<spring:message code="label.branch.header.views"/>">
                <c:out value="${topic.views}"/>
              </span>
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