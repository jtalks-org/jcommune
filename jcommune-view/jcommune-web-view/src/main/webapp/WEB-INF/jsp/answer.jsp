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

<html>
<head>
    <title><spring:message code="label.answer_to"/>: <c:out value="${topic.title}"/></title>
    <link href="${pageContext.request.contextPath}/resources/css/main.css" type=text/css rel=stylesheet>
</head>
<body>

<h1>JTalks</h1>

<div class="wrap answer_page">
    <jsp:include page="../template/topLine.jsp"/>
    <!-- Начало всех форумов -->
    <div class="all_forums">
        <jtalks:form action="${pageContext.request.contextPath}/posts/new?topicId=${topicId}"
                     method="POST" modelAttribute="postDto">
            <h2><a class="heading" href="#"><c:out value="${topic.title}"/></a></h2>

            <div class="forum_misc_info">
                Ответ!
            </div>

            <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>
            <form:hidden path="topicId" />

            <!-- Начало группы форумов -->
            <div class="forum_header_table"> <!-- Шапка топика -->
                <div class="forum_header">
                    <span class="forum_header_answer"><spring:message code="label.answer"/></span>
                    <span class="empty_cell"></span> <!-- Необходима для корректного отображения псевдотаблицы -->
                </div>
            </div>
            <ul class="forum_table"> <!-- Форма ответа -->
                <li class="forum_row">
                    <div class="forum_answer_left">
                        <spring:message code="label.answer.topic"/>
                    </div>
                    <div class="forum_answer_right">
                        <input id="subject" type="text" name="subject" size="45" maxlength="60" tabindex="1"
                               class="post"
                               value="RE: <c:out value="${topic.title}"/>"/>
                    </div>
                </li>
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
                                   value=" B "/>
                            <input id="format_i" type="button" class="button" accesskey="i" name="format_i"
                                   value=" i "/>
                            <input id="format_u" type="button" class="button" accesskey="u" name="format_u"
                                   value=" u "/>
                            <input id="format_quote" type="button" class="button" accesskey="q" name="format_quote"
                                   value="Quote"/>
                            <input id="format_code" type="button" class="button" accesskey="c" name="format_code"
                                   value="Code"/>
                            <input id="format_list" type="button" class="button" accesskey="l" name="format_list"
                                   value="List"/>
                            <input id="format_listeq" type="button" class="button" accesskey="o" name="format_listeq"
                                   value="List="/>
                            <input id="format_img" type="button" class="button" accesskey="p" name="format_img"
                                   value="Img"/>
                            <input id="format_url" type="button" class="button" accesskey="w" name="format_url"
                                   value="URL"/>
                        </div>
						<span class="genmed">
							Цвет шрифта:
							<select id="select_color" name="select_color">
                                <option>Чорный</option>
                                <option>Белiй</option>
                            </select>
							Размер шрифта:
							<select id="select_size" name="select_size">
                                <option>Mali</option>
                                <option>Bolsho</option>
                                <option>Oche Bolsho</option>
                            </select>
						</span>
                        <a href="#" onmouseover="helpline('a')">Закрыть теги</a>

                        <div id="helpline">Здесь должна быть подсказка</div>
                        <form:textarea path="bodyText" rows="15" cols="35" tabindex="3" cssClass="post" />
                        <form:errors path="bodyText" />

                    </div>
                </li>
                <li class="forum_row">
                    <div class="forum_answer_left">
                        <spring:message code="label.answer.options"/>
                    </div>
                    <div class="forum_answer_right options">
                        <input id="notify" type="checkbox" name="notify" checked="checked"/> Сообщать мне о получении
                        ответа
                        <br/>
                        <input id="nosmiles" type="checkbox" name="nosmiles" checked="checked"/> Не отображать смайлы
                    </div>
                </li>
            </ul>
            <input id="preview" type="submit" class="button" tabindex="5" name="preview" value="Предв. просмотр"/>
            <input id="post" type="submit" class="button" accesskey="s" tabindex="6" name="post"
                   value="<spring:message code="label.answer"/>"/>
        </jtalks:form>
    </div>
    <!-- Конец всех форумов -->
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>


</body>
</html>