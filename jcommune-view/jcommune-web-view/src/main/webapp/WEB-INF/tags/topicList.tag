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
<div id="topics-table" class="table table-bordered">
  <c:choose>
    <c:when test="${!(empty topics)}">
      <div class="topic-table-header">
        <div class="status-col-small pull-left topic-table-column-header topic-table-header-first"></div>
        <div class="latest-by forum-latest-by-header pull-right topic-table-column-header topic-table-header-last">
          <spring:message code="label.branch.header.lastMessage"/>
        </div>
        <div class="messages-info pull-right topic-table-column-header">
          Messages
        </div>
        <div class="topic-info-column topic-table-column-header">
          <spring:message code="label.branch.header.topics"/>
        </div>
      </div>
      <div>
      <c:forEach var="topic" items="${topics}">
        <div class="topic-table-row">
          <div class="status-col-small pull-left topic-table-td topic-table-cell-first">
              <jtalks:topicIconSmall topic="${topic}" authenticated="${authenticated}"/>
          </div>
          <div class="latest-by shrink-to-fit pull-right topic-table-td">
            <div class="last-message-div">
              <i class="icon-calendar"></i>
              <a class="date margin-right-big" href="${pageContext.request.contextPath}/posts/${topic.lastPost.id}"
                 data-original-title="<spring:message code="label.branch.header.lastMessage.tooltip"/>">
                <jtalks:format value="${topic.lastPost.creationDate}"/>
              </a>
            </div>
            <div class="last-message-div">
              <i class="icon-user"></i>
              <a class="space-left-small"
                 href="${pageContext.request.contextPath}/users/${topic.lastPost.userCreated.id}"
                 title="<spring:message code='label.tips.view_profile'/>">
                <c:out value="${topic.lastPost.userCreated.username}"/>
              </a>
            </div>
          </div>
          <div class="messages-info pull-right topic-table-td">
            <i class="icon-envelope margin-left-big margin-right-big"></i>
            <span class='test-views' data-original-title="<spring:message code="label.branch.header.posts"/>">
              <c:out value="${topic.postCount}"/>
            </span>
            <i class="icon-eye-open margin-left-big"></i>
            <span class='test-views' data-original-title="<spring:message code="label.branch.header.posts"/>">
              <c:out value="${topic.views}"/>
            </span>
          </div>
          <div class="topic-info-column posts-td-small topic-table-td">
            <div class="h-nostyle ellipsis-text">
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
            </div>
            <div class="pull-right">
              <spring:message code="label.topic.created_by"/>
              <a class="space-left-small margin-right-big" href="${pageContext.request.contextPath}/users/${topic.topicStarter.id}">
                <c:out value="${topic.topicStarter.username}"/>
              </a>
            </div>
            <c:if test="${showBranchColumn}">
            <div class="created-by ellipsis-text ellipsis-text">
                in <a class="margin-left-small margin-right-small" href="${pageContext.request.contextPath}/branches/${topic.branch.id}">
                  <c:out value="${topic.branch.name}"/>
                </a>
            </div>
            </c:if>
          </div>
        </div>
      </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div>
        <td><c:out value="${messageToShowIfNoTopics}"/></td>
      </div>
    </c:otherwise>
  </c:choose>
</div>