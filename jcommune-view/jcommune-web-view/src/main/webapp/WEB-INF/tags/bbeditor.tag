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

<ul class="forum_table">
    <li class="forum_row">
        <div class="forum_answer_left align-top">
            <spring:message code="label.answer.message"/>
            <table class="smiles_table">
                <tbody>
                <tr>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="forum_answer_right">
            <div class="formatting_buttons">
                <a id="format_b" class="button" accesskey="b" name="format_b" onclick="doClick('bold');">
                    &nbsp;B&nbsp;
                </a>
                <a id="format_i" class="button" accesskey="i" name="format_i" onclick="doClick('italic');">
                    <span class="italic">&nbsp;i&nbsp;</span>
                </a>
                <a id="format_u" class="button" accesskey="u" name="format_u" onclick="doClick('underline');">
                    <span class="underline">&nbsp;U&nbsp;</span>
                </a>
                <a id="format_s" class="button" accesskey="s" name="format_s" onclick="doClick('line-through');">
                    <span class="striked">&nbsp;S&nbsp;</span>
                </a>
                <a id="format_highlight" class="button" accesskey="h" name="format_highlight"
                   onclick="doClick('highlight');">
                    &nbsp;Highlight&nbsp;
                </a>
                <a id="format_left" class="button" accesskey="left" name="format_left" onclick="doClick('left');">
                    &nbsp;Left&nbsp;
                </a>
                <a id="format_center" class="button" accesskey="c" name="format_center" onclick="doClick('center');">
                    &nbsp;Center&nbsp;
                </a>
                <a id="format_right" class="button" accesskey="right" name="format_right"
                   value=" Right " onclick="doClick('right');">
                    &nbsp;Right&nbsp;
                </a>
                <a id="select_color" class="button" name="select_color" onclick="showColorGrid2('none')">
                    Color
                </a>
                <span id="colorpicker201" class="colorpicker201"></span>
                <a id="format_quote" class="button" accesskey="q" name="format_quote" onclick="doQuote();">
                    Quote
                </a>
                <a id="format_list" class="button" accesskey="l" name="format_list"
                   onclick="doClick('InsertUnorderedList');">
                    List
                </a>
                <a id="format_listeq" class="button" accesskey="o" name="format_listeq"
                   onclick="doClick('listElement');">
                    List=
                </a>
                <a id="format_img" class="button" accesskey="p" name="format_img" onclick="doImage();">
                    Img
                </a>
                <a id="format_url" class="button" accesskey="w" name="format_url" onclick="doLink();">
                    <span class="underline">&nbsp;URL&nbsp;</span>
                </a>
            </div>
				<span class="genmed">
					<label for="select_size">
                        <spring:message code="label.answer.font_size"/>
                    </label>
                    <select id="select_size" name="select_size" onchange="doSize();">
                        <option value="0" selected="selected">
                            <spring:message code="label.answer.none"/></option>
                        <option value="10"><spring:message code="label.answer.font_size.small"/></option>
                        <option value="15"><spring:message code="label.answer.font_size.large"/></option>
                        <option value="20"><spring:message code="label.answer.font_size.king_size"/></option>
                    </select>

					<label for="select_indent"><spring:message code="label.answer.indent"/></label>
                    <select id="select_indent" name="select_indent" onchange="doIndent();">
                        <option value="0" selected="selected">
                            <spring:message code="label.answer.none"/></option>
                        <option value="15">15</option>
                        <option value="20">20</option>
                        <option value="25">25</option>
                    </select>

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
				</span>
            <a href="" onclick="closeTags();return false;"><spring:message code="label.answer.close_tags"/></a>
            <br/><br/>
            <%-- <div id="helpline"><spring:message code="label.answer.tooltip"/></div>--%>
            <div id="editorBBCODEdiv" class="editorBBCODE">
                <pre id="htmlContent"></pre>
                <textarea id="tbMsg" name="${bodyParameterName}"><c:out value="${postText}"/></textarea>
            </div>
            <br>
            <form:errors path="${bodyParameterName}" cssClass="error"/>

        </div>
    </li>
    <%--   <li class="forum_row">
      <div class="forum_answer_left">
          <spring:message code="label.answer.options"/>
      </div>
      <div class="forum_answer_right options">
          <input id="notify" type="checkbox" name="notify" checked="checked"/><spring:message
              code="label.answer.notify_message"/>
          <br/>
          <input id="nosmiles" type="checkbox" name="nosmiles" checked="checked"/><spring:message
              code="label.answer.no_smiles"/>
      </div>
  </li>  --%>
</ul>
<a href="${back}"><input id="back" type="button" class="button" tabindex="5" name="back"
                         value="<spring:message code="label.back"/>"/></a>
<input id="preview" type="button" class="button" tabindex="5" name="preview"
       value="<spring:message code="label.answer.preview"/>" onclick="SwitchEditor();return null;"/>
<script type="text/javascript">
    initEditor("tbMsg", "editorBBCODEdiv", "htmlContent");
</script>
<input id="post" type="submit" class="button" accesskey="s" tabindex="6" name="post"
       value="<spring:message code="${labelForAction}"/>"/>
