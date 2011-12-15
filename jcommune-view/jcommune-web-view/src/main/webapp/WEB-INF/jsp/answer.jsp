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
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><spring:message code="label.answer_to"/>: <c:out value="${topic.title}"/></title>
    <script src="${pageContext.request.contextPath}/resources/javascript/licensed/wysiwyg-bbcode/editor.js"
            type="text/javascript"></script>
</head>
<body>

<h1>JTalks</h1>

<div class="wrap answer_page">
    <jsp:include page="../template/topLine.jsp"/>
    <!-- Начало всех форумов -->
    <div class="all_forums">

        <jtalks:form action="${pageContext.request.contextPath}/posts/new?topicId=${topicId}"
                     method="POST" modelAttribute="postDto" onsubmit="doCheck();return true;">
            <h2><a class="heading" href="#"><c:out value="${topic.title}"/></a></h2>

            <div class="forum_misc_info">
                <spring:message code="label.answer.title_label"/>
            </div>

            <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
            <form:hidden path="topicId"/>

            <!-- Начало группы форумов -->
            <div class="forum_header_table"> <!-- Шапка топика -->
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="label.answer"/></span>
                    <span class="empty_cell"></span> <!-- Необходима для корректного отображения псевдотаблицы -->
                </div>
            </div>
            <ul class="forum_table"> <!-- Форма ответа -->
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
                            <br>
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

							<spring:message code="label.answer.font_size"/>
							<select id="select_size" name="select_size" onchange="doSize();">
                                <option value="0" selected="selected">
                                    <spring:message code="label.answer.none"/></option>
                                <option value="10"><spring:message code="label.answer.font_size.small"/></option>
                                <option value="15"><spring:message code="label.answer.font_size.large"/></option>
                                <option value="20"><spring:message code="label.answer.font_size.king_size"/></option>
                            </select>
                            <spring:message code="label.answer.indent"/>
							<select id="select_indent" name="select_indent" onchange="doIndent();">
                                <option value="0" selected="selected">
                                    <spring:message code="label.answer.none"/></option>
                                <option value="15">15</option>
                                <option value="20">20</option>
                                <option value="25">25</option>
                            </select>
						</span>
                        <a href="" onclick="closeTags();"><spring:message code="label.answer.close_tags"/></a>

                        <div id="helpline"><spring:message code="label.answer.tooltip"/></div>
                        <form:textarea id="tbMsg" path="bodyText" cssStyle="width:90%; height:400px;" tabindex="3"
                                       cssClass="post"
                                       onclick="resetSizeSelector();resetIndentSelector();"/>
                        <br>
                        <form:errors path="bodyText" cssClass="error"/>

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
            <input id="preview" type="button" class="button" tabindex="5" name="preview"
                   value="<spring:message code="label.answer.preview"/>" onclick="javascript:SwitchEditor()"/>
            <script type="text/javascript">
                initEditor("tbMsg", true);
                SwitchEditor();
            </script>
            <input id="post" type="submit" class="button" accesskey="s" tabindex="6" name="post"
                   value="<spring:message code="label.answer"/>" onclick="doCheck();return true;"/>
        </jtalks:form>
    </div>
    <!-- Конец всех форумов -->
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>
</body>