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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="btn-toolbar">
    <div class="btn-group">
        <a id="format_b" class="btn" accesskey="b" name="format_b" onclick="doClick('bold');" title="Bold">
            <i class="icon-bold"></i>
        </a>
        <a id="format_i" class="btn" accesskey="i" name="format_i" onclick="doClick('italic');" title="Italic">
            <i class="icon-italic"></i>
        </a>
        <a id="format_u" class="btn" accesskey="u" name="format_u" onclick="doClick('underline');" title="Underline">
            <i class="icon-underline"></i>
        </a>
        <a id="format_s" class="btn" accesskey="s" name="format_s" onclick="doClick('line-through');" title="Strike">
            <i class="icon-strike"></i>
        </a>
        <a id="format_highlight" class="btn" accesskey="h" name="format_highlight"
           onclick="doClick('highlight');" title='Highlight'>
            &nbsp;Highlight&nbsp;
        </a>
    </div>
    <div class='btn-group'>
        <a id="format_left" class="btn" accesskey="left" name="format_left" onclick="doClick('left');"
           title="Align left">
            <i class="icon-align-left"></i>
        </a>
        <a id="format_center" class="btn" accesskey="c" name="format_center" onclick="doClick('center');"
           title="Align center">
            <i class="icon-align-center"></i>
        </a>
        <a id="format_right" class="btn" accesskey="right" name="format_right"
           value=" Right " onclick="doClick('right');" title="Align right">
            <i class="icon-align-right"></i>
        </a>
    </div>
    <div class='btn-group'>
        <a id="format_list" class="btn" accesskey="l" name="format_list"
           onclick="doClick('InsertUnorderedList');" title="List">
            <i class="icon-list"></i>
        </a>
        <a id="format_listeq" class="btn" accesskey="o" name="format_listeq"
           onclick="doClick('listElement');" title="Add list item">
            <i class="icon-add-row"></i>
        </a>
    </div>
    <div class='btn-group'>
        <a id="select_color" class="btn" name="select_color" onclick="showColorGrid2('none')" title="Text color">
            <i class="icon-palette"></i>
        </a>
        <span id="colorpicker201" class="colorpicker201"></span>

        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" title="Text size">
            <i class="icon-text-height"></i>
            <span class="caret"></span>
        </a>
        <ul class="dropdown-menu" id='select_size'>
            <li><a href="#" value='10'><spring:message code="label.answer.font_size.small"/></a></li>
            <li><a href="#" value='15'><spring:message code="label.answer.font_size.large"/></a></li>
            <li><a href="#" value='20'><spring:message code="label.answer.font_size.king_size"/></a></li>
        </ul>
    </div>
    <div class='btn-group'>
        <a id="format_img" class="btn" accesskey="p" name="format_img" onclick="doImage();" title="Insert picture">
            <i class="icon-picture"></i>
        </a>
        <a id="format_url" class="btn" accesskey="w" name="format_url" onclick="doLink();" title="Insert link">
            <i class="icon-link"></i>
        </a>

        <label for="select_code"> <spring:message code="label.answer.font_code"/>
        </label>
        <select id="select_code" name="select_code" onchange="doCode();">
            <option value="0" selected="selected">
                <spring:message code="label.answer.none"/></option>
            <option value="cpp">C/C++</option>
            <option value="csharp">C#</option>
            <option value="java">Java</option>
            <option value="php">PHP</option>
            <option value="python">Python</option>
            <option value="pascal">Pascal</option>
            <option value="bash">Bash</option>
            <option value="js">JavaScript</option>
            <option value="html">HTML</option>
            <option value="css">CSS</option>
            <option value="sql">SQL</option>
            <option value="xml">XML</option>
        </select>

        <a id="format_quote" class="btn" accesskey="q" name="format_quote" onclick="doQuote();" title="Quote">
            <i class="icon-quote"></i>
        </a>
    </div>
    <div class='btn-group'>
        <label for="select_indent"><spring:message code="label.answer.indent"/></label>
        <select id="select_indent" name="select_indent" onchange="doIndent();">
            <option value="0" selected="selected">
                <spring:message code="label.answer.none"/></option>
            <option value="15">15</option>
            <option value="20">20</option>
            <option value="25">25</option>
        </select>
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
                <textarea id="tbMsg" name="${bodyParameterName}" style="width:100%;height: 350px"
                          placeholder="${placeholderEditorContent}"><c:out
                        value="${postText}"/></textarea>
            </span>
    <br>
    <form:errors path="${bodyParameterName}" cssClass="help-inline"/>
</div>


<div class="control-group">
    <spring:message code="label.answer.options"/>
    <br/>
    <input id="notify" type="checkbox" name="notify" checked="checked" style="margin-right: 10px;"/><spring:message
        code="label.answer.notify_message"/>
    <br/>
    <input id="nosmiles" type="checkbox" name="nosmiles" checked="checked" style="margin-right: 10px;"/><spring:message
        code="label.answer.no_smiles"/>
</div>


<input id="post" type="submit" class="btn btn-primary" accesskey="s" tabindex="6" name="post"
       value="<spring:message code="${labelForAction}"/>"/>

<input id="preview" type="button" class="btn btn-success" tabindex="5" name="preview"
       value="<spring:message code="label.answer.preview"/>" onclick="SwitchEditor();return null;"/>
<script type="text/javascript">
    initEditor("tbMsg", "editorBBCODEdiv", "htmlContent");
</script>
