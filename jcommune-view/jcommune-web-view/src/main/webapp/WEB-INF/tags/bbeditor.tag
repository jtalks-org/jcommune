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
                <input id="format_b" type="button" class="button" accesskey="b" name="format_b"
                       value=" B " onclick="doClick('bold');"/>
                <input id="format_i" type="button" class="button" accesskey="i" name="format_i"
                       value=" i " onclick="doClick('italic');"/>
                <input id="format_u" type="button" class="button" accesskey="u" name="format_u"
                       value=" u " onclick="doClick('underline');"/>
                <input id="format_s" type="button" class="button" accesskey="s" name="format_s"
                       value=" s " onclick="doClick('line-through');"/>
                <input id="format_highlight" type="button" class="button" accesskey="highlight"
                       name="format_highlight"
                       value=" Highlight " onclick="doClick('highlight');"/>
                <input id="format_left" type="button" class="button" accesskey="left" name="format_left"
                       value=" Left " onclick="doClick('left');"/>
                <input id="format_center" type="button" class="button" accesskey="center"
                       name="format_center" value=" Center " onclick="doClick('center');"/>
                <input id="format_right" type="button" class="button" accesskey="right" name="format_right"
                       value=" Right " onclick="doClick('right');"/>
                <input id="select_color" type="button" class="button" name="select_color"
                       value="Color" onclick="showColorGrid2('none')"/>
                <span id="colorpicker201" class="colorpicker201"></span>
                <input id="format_quote" type="button" class="button" accesskey="q" name="format_quote"
                       value="Quote" onclick="doQuote();"/>
                <input id="format_code" type="button" class="button" accesskey="c" name="format_code"
                       value="Code" onclick="doClick('code');"/>
                <input id="format_list" type="button" class="button" accesskey="l" name="format_list"
                       value="List" onclick="doClick('InsertUnorderedList');"/>
                <input id="format_listeq" type="button" class="button" accesskey="o" name="format_listeq"
                       value="List=" onclick="doClick('listElement');"/>
                <input id="format_img" type="button" class="button" accesskey="p" name="format_img"
                       value="Img" onclick="doImage();"/>
                <input id="format_url" type="button" class="button" accesskey="w" name="format_url"
                       value="URL" onclick="doLink();"/>
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
				</span>
            <a href="" onclick="closeTags();return false;"><spring:message code="label.answer.close_tags"/></a>
            <br/><br/>
            <%-- <div id="helpline"><spring:message code="label.answer.tooltip"/></div>--%>
            <textarea id="tbMsg" name="${bodyParameterName}" class="editorBBCODE"
                      style="width: 90%; height: 400px;" tabindex="3"
                      onclick="resetSizeSelector();resetIndentSelector();">${postText}</textarea>
            <br>
            <form:errors path="${bodyParameterName}" cssClass="error"/>

        </div>
    </li>
    <li class="forum_row">
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
    </li>
</ul>
<a href="${back}"><input id="back" type="button" class="button" tabindex="5" name="back"
                         value="<spring:message code="label.back"/>"/></a>
<input id="preview" type="button" class="button" tabindex="5" name="preview"
       value="<spring:message code="label.answer.preview"/>" onclick="SwitchEditor();return null;"/>
<script type="text/javascript">
    initEditor("tbMsg");
</script>
<input id="post" type="submit" class="button" accesskey="s" tabindex="6" name="post"
       value="<spring:message code="${labelForAction}"/>"/>
