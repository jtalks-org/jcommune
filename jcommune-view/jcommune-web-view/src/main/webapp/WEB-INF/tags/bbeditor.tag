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
        <a id="format_b" class="btn" accesskey="b" name="format_b" onclick="doClick('bold');"
           title="<spring:message code='label.answer.bold'/>">
            <i class="icon-bold"></i>
        </a>
        <a id="format_i" class="btn" accesskey="i" name="format_i" onclick="doClick('italic');"
           title="<spring:message code='label.answer.italic'/>">
            <i class="icon-italic"></i>
        </a>
        <a id="format_u" class="btn" accesskey="u" name="format_u" onclick="doClick('underline');"
           title="<spring:message code='label.answer.underline'/>">
            <i class="icon-underline"></i>
        </a>
        <a id="format_s" class="btn" accesskey="s" name="format_s" onclick="doClick('line-through');"
           title="<spring:message code='label.answer.striked'/>">
            <i class="icon-strike"></i>
        </a>
        <a id="format_highlight" class="btn" accesskey="h" name="format_highlight"
           onclick="doClick('highlight');"
           title='<spring:message code='label.answer.highlight'/>'>
            &nbsp;Highlight&nbsp;
        </a>
    </div>
    <div class='btn-group'>
        <a id="format_left" class="btn" accesskey="left" name="format_left" onclick="doClick('left');"
           title="<spring:message code='label.answer.align_left'/>">
            <i class="icon-align-left"></i>
        </a>
        <a id="format_center" class="btn" accesskey="c" name="format_center" onclick="doClick('center');"
           title="<spring:message code='label.answer.align_center'/>">
            <i class="icon-align-center"></i>
        </a>
        <a id="format_right" class="btn" accesskey="right" name="format_right"
           onclick="doClick('right');"
           title="<spring:message code='label.answer.align_right'/>">
            <i class="icon-align-right"></i>
        </a>
    </div>
    <div class='btn-group'>
        <a id="format_list" class="btn" accesskey="l" name="format_list"
           onclick="doClick('InsertUnorderedList');"
           title="<spring:message code='label.answer.list'/>">
            <i class="icon-list"></i>
        </a>
        <a id="format_listeq" class="btn" accesskey="o" name="format_listeq"
           onclick="doClick('listElement');"
           title="<spring:message code='label.answer.list_item'/>">
            <i class="icon-add-row"></i>
        </a>
    </div>
    <div class='btn-group'>
        <a id="select_color" class="btn" name="select_color" onclick="showColorGrid2('none')"
           title="<spring:message code='label.answer.font_color'/>">
            <i class="icon-palette"></i>
        </a>
        <span id="colorpicker201" class="colorpicker201"></span>

        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"
           title="<spring:message code="label.answer.font_size"/>">
            <i class="icon-text-height"></i>
            <span class="caret"></span>
        </a>
        <ul class="dropdown-menu" id='select_size'>
            <li><a href="#" value='7'><spring:message code="label.answer.font_size.tiny"/></a></li>
            <li><a href="#" value='9'><spring:message code="label.answer.font_size.small"/></a></li>
            <li><a href="#" value='12'><spring:message code="label.answer.font_size.normal"/></a></li>
            <li><a href="#" value='18'><spring:message code="label.answer.font_size.large"/></a></li>
            <li><a href="#" value='24'><spring:message code="label.answer.font_size.huge"/></a></li>
        </ul>
    </div>
    <div class='btn-group'>
        <a id="format_img" class="btn" accesskey="p" name="format_img" onclick="doImage();"
           title="<spring:message code='label.answer.insert_picture'/>">
            <i class="icon-picture"></i>
        </a>
        <a id="format_url" class="btn" accesskey="w" name="format_url" onclick="doLink();"
           title="<spring:message code='label.answer.insert_link'/>">
            <i class="icon-link"></i>
        </a>

        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
            <spring:message code="label.answer.font_code"/>
            <span class="caret"></span>
        </a>
        <ul class="dropdown-menu" id='select_code'>
            <li><a href="#" value="cpp">C/C++</a></li>
            <li><a href="#" value="csharp">C#</a></li>
            <li><a href="#" value="java">Java</a></li>
            <li><a href="#" value="php">PHP</a></li>
            <li><a href="#" value="python">Python</a></li>
            <li><a href="#" value="pascal">Pascal</a></li>
            <li><a href="#" value="bash">Bash</a></li>
            <li><a href="#" value="js">JavaScript</a></li>
            <li><a href="#" value="html">HTML</a></li>
            <li><a href="#" value="css">CSS</a></li>
            <li><a href="#" value="sql">SQL</a></li>
            <li><a href="#" value="xml">XML</a></li>
        </ul>

        <a id="format_quote" class="btn" accesskey="q" name="format_quote" onclick="doQuote();"
           title="<spring:message code='label.answer.quote'/>">
            <i class="icon-quote"></i>
        </a>
    </div>
    <div class='btn-group'>
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"
           title="<spring:message code="label.answer.indent"/>">
            <spring:message code="label.answer.indent"/>
            <span class="caret"></span>
        </a>
        <ul class="dropdown-menu" id='select_indent'>
            <li><a href="#" value="15">15</a></li>
            <li><a href="#" value="20">20</a></li>
            <li><a href="#" value="25">25</a></li>
        </ul>
    </div>
    <div class='btn-group'>
        <a href="" class='btn' onclick="closeTags();return false;"
           title='<spring:message code="label.answer.close_tags"/>'>[/..]</a>
    </div>
</div>

<div class='control-group'>
    <%-- <div id="helpline"><spring:message code="label.answer.tooltip"/></div>--%>
            <span id="editorBBCODEdiv">
                <pre id="htmlContent"></pre>
                        <spring:message code="placeholder.editor.content" var="placeholderEditorContent"/>
                <textarea id="tbMsg" name="${bodyParameterName}" tabindex="200" style="width:100%;height: 350px"
                          placeholder="${placeholderEditorContent}" class="script-confirm-unsaved"><c:out
                        value="${postText}"/></textarea>
            </span>
    <br>
    <c:if test="${showSubmitButton}">
        <span class="keymaps-caption"><spring:message code="label.keymaps.post"/></span>
        <br>
    </c:if>
    <form:errors path="${bodyParameterName}" cssClass="help-inline"/>
</div>

<c:if test="${showSubmitButton}">
    <input id="post" type="submit" class="btn btn-primary" accesskey="s" name="post" tabindex="300"
           value="<spring:message code="${labelForAction}"/>"/>
</c:if>

<input id="preview" type="button" class="btn btn-success" name="preview" tabindex="400"
       value="<spring:message code="label.answer.preview"/>" onclick="SwitchEditor();return null;"/>
<script type="text/javascript">
    initEditor("tbMsg", "editorBBCODEdiv", "htmlContent");
    if(initContextMenu){
        initContextMenu("tbMsg");
    }
</script>
