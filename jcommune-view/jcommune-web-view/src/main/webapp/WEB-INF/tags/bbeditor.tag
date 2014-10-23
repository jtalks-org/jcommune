<%--

    Copyright (C) 2011  JTalks.org Team
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%>
<%@ tag body-content="empty" %>
<%@ attribute name="back" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="postText" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="bodyParameterName" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="labelForAction" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="showSubmitButton" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${empty showSubmitButton}">
  <c:set var="showSubmitButton" value="true"/>
</c:if>

<div class="btn-toolbar hide-on-preview">
  <div class="btn-group">
    <a id="format_b" class="btn" accesskey="b" href="#fake"
       title="<spring:message code='label.answer.bold'/>">
      <i class="icon-bold"></i>
    </a>
    <a id="format_i" class="btn" accesskey="i" href="#fake"
       title="<spring:message code='label.answer.italic'/>">
      <i class="icon-italic"></i>
    </a>
    <a id="format_u" class="btn" accesskey="u" href="#fake"
       title="<spring:message code='label.answer.underline'/>">
      <i class="icon-underline"></i>
    </a>
    <a id="format_s" class="btn" accesskey="s" href="#fake"
       title="<spring:message code='label.answer.striked'/>">
      <i class="icon-strike"></i>
    </a>
    <a id="format_highlight" class="btn" accesskey="h" href="#fake"
       title='<spring:message code='label.answer.highlight'/>'>
      &nbsp;Highlight&nbsp;
    </a>
  </div>
  <div class='btn-group'>
    <a id="format_left" class="btn" href="#fake"
       title="<spring:message code='label.answer.align_left'/>">
      <i class="icon-align-left"></i>
    </a>
    <a id="format_center" class="btn" accesskey="c" href="#fake"
       title="<spring:message code='label.answer.align_center'/>">
      <i class="icon-align-center"></i>
    </a>
    <a id="format_right" class="btn" href="#fake"
       title="<spring:message code='label.answer.align_right'/>">
      <i class="icon-align-right"></i>
    </a>
  </div>
  <div class='btn-group'>
    <a id="format_list" class="btn" accesskey="l" href="#fake"
       title="<spring:message code='label.answer.list'/>">
      <i class="icon-list"></i>
    </a>
    <a id="format_listeq" class="btn" accesskey="o" href="#fake"
       title="<spring:message code='label.answer.list_item'/>">
      <i class="icon-add-row"></i>
    </a>
  </div>
  <div class='btn-group'>
    <a id="select_color" class="btn" href="#fake"
       title="<spring:message code='label.answer.font_color'/>">
      <i class="icon-palette"></i>
    </a>
    <span id="colorpicker201" class="colorpicker201"></span>

    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#fake"
       title="<spring:message code="label.answer.font_size"/>">
      <i class="icon-text-height"></i>
      <span class="caret"></span>
    </a>
    <ul class="dropdown-menu" id='select_size'>
      <li data-value='7'><a href="#fake"><spring:message code="label.answer.font_size.tiny"/></a></li>
      <li data-value='9'><a href="#fake"><spring:message code="label.answer.font_size.small"/></a></li>
      <li data-value='12'><a href="#fake"><spring:message code="label.answer.font_size.normal"/></a></li>
      <li data-value='18'><a href="#fake"><spring:message code="label.answer.font_size.large"/></a></li>
      <li data-value='24'><a href="#fake"><spring:message code="label.answer.font_size.huge"/></a></li>
    </ul>
  </div>
  <div class='btn-group'>
    <a id="format_img" class="btn" accesskey="p" href="#fake"
       title="<spring:message code='label.answer.insert_picture'/>">
      <i class="icon-picture"></i>
    </a>
    <a id="format_url" class="btn" accesskey="w" href="#fake"
       title="<spring:message code='label.answer.insert_link'/>">
      <i class="icon-link"></i>
    </a>

    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#fake">
      <spring:message code="label.answer.font_code"/>
      <span class="caret"></span>
    </a>
    <ul class="dropdown-menu" id='select_code'>
      <li data-value="cpp"><a href="#fake">C/C++</a></li>
      <li data-value="csharp"><a href="#fake">C#</a></li>
      <li data-value="java"><a href="#fake">Java</a></li>
      <li data-value="php"><a href="#fake">PHP</a></li>
      <li data-value="python"><a href="#fake">Python</a></li>
      <li data-value="pascal"><a href="#fake">Pascal</a></li>
      <li data-value="bash"><a href="#fake">Bash</a></li>
      <li data-value="js"><a href="#fake">JavaScript</a></li>
      <li data-value="html"><a href="#fake">HTML</a></li>
      <li data-value="css"><a href="#fake">CSS</a></li>
      <li data-value="sql"><a href="#fake">SQL</a></li>
      <li data-value="xml"><a href="#fake">XML</a></li>
    </ul>

    <a id="format_quote" class="btn" accesskey="q" href="#fake"
       title="<spring:message code='label.answer.quote'/>">
      <i class="icon-quote"></i>
    </a>
  </div>
  <div class='btn-group'>
    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#fake"
       title="<spring:message code="label.answer.indent"/>">
      <spring:message code="label.answer.indent"/>
      <span class="caret"></span>
    </a>
    <ul class="dropdown-menu" id='select_indent'>
      <li data-value="15"><a href="#fake">15</a></li>
      <li data-value="20"><a href="#fake">20</a></li>
      <li data-value="25"><a href="#fake">25</a></li>
    </ul>
  </div>
</div>

<div class='control-group controls'>
  <%-- <div id="helpline"><spring:message code="label.answer.tooltip"/></div>--%>
  <div id="editorBBCODEdiv">
    <pre id="htmlContent"></pre>
    <spring:message code="placeholder.editor.content" var="placeholderEditorContent"/>
    <textarea id="postBody" name="${bodyParameterName}" tabindex="200" style="width:100%;height: 350px"
              placeholder="${placeholderEditorContent}" class="script-confirm-unsaved"><c:out
            value="${postText}"/></textarea>
  </div>
  <br>
  <c:if test="${showSubmitButton}">
    <span class="keymaps-caption"><spring:message code="label.keymaps.post"/></span>
    <br>
  </c:if>
  <form:errors path="${bodyParameterName}" cssClass="help-inline focusToError"/>
</div>

<c:if test="${showSubmitButton}">
  <input id="post" type="submit" class="btn btn-primary" accesskey="s" name="post" tabindex="300"
         value="<spring:message code="${labelForAction}"/>"/>
</c:if>

<input id="preview" type="button" class="btn btn-success space-left-medium-nf" name="preview" tabindex="400"
       value="<spring:message code="label.answer.preview"/>"
       onclick="togglePreviewMode(new Array('posts', 'topics'));return null;"/>
<script type="text/javascript">
  initEditor("postBody", "editorBBCODEdiv", "htmlContent");
</script>
