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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Форум JTalks</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" media="screen, projection"
          href="${pageContext.request.contextPath}/resources/css/screen.css"/>
    <link rel="shortcut icon" type="image/ico" href="favicon.ico"/>
</head>
<body>
<h1>JTalks</h1>

<div class="wrap main_page">
    <!-- Начало всех форумов -->
    <div class="all_forums">
        <a class="forum_top_right_link" href="${pageContext.request.contextPath}/topics/recent"><spring:message
                code="label.recent"/></a> <br/>
        <a class="forum_top_right_link" href="#">Сообщения без ответа</a>

        <h2><a class="heading" href="#">Java форум JTalks</a></h2>
        <br/>

        <div class="forum_misc_info">
            форум программистов
        </div>

        <jtalks:breadcrumb breadcrumbList="${breadcrumbList}"/>

        <c:forEach var="section" items="${sectionList}">
            <!-- Начало группы форумов -->
            <div class="forum_header_table"> <!-- Шапка группы форумов -->
                <div class="forum_header">
                    <h3><a class="forum_header_link"
                           href="${pageContext.request.contextPath}/sections/${section.id}">
                        <c:out value="${section.name}"/></a></h3>
                    <span class="forum_header_themes"><spring:message code="label.section.header.topics"/></span>
                    <span class="forum_header_messages"><spring:message code="label.section.header.messages"/></span>
                    <span class="forum_header_last_message"><spring:message
                            code="label.section.header.lastMessage"/></span>
                </div>
            </div>

            <ul class="forum_table"> <!-- Группа форумов -->
                <c:forEach var="branch" items="${section.branches}" varStatus="i">
                    <li class="forum_row"> <!-- Отдельный форум -->
                        <div class="forum_icon"> <!-- Иконка с кофе -->
                            <img class="icon" src="${pageContext.request.contextPath}/resources/images/closed_cup.png"
                                 alt=""
                                 title="Форум закрыт"/>
                        </div>
                        <div class="forum_info"> <!-- Информация о форуме -->
                            <h4><a class="forum_link"
                                   href="${pageContext.request.contextPath}/branches/${branch.id}">
                                <c:out value="${branch.name}"/></a></h4> <!-- Ссылка на форум -->
                            <p>
                                <c:out value="${branch.description}"/>
                                <a href="#">ЧаВО</a>
                                <br/>
                                Модераторы: <a class="moderator" href="#">Vurn</a>
                            </p>
                        </div>
                        <div class="forum_themes">
                            <c:out value="${branch.topicCount}"/>
                        </div>
                        <div class="forum_messages">
                            6574
                        </div>
                        <div class="forum_last_message">
                            <span>Июл 04, 2011 23:54</span>
                            <br/>
                            <a href="#">Pahan</a>
                            <a href="#"><img
                                    src="${pageContext.request.contextPath}/resources/images/icon_latest_reply.gif"
                                    alt="Последнее сообщение"/></a>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:forEach>
        <!-- Конец группы форумов -->
    </div>
    <!-- Конец всех форумов -->
    <div class="users_information">    <!-- Информация о посетителях -->
        <div class="forum_header_table"> <!-- Шапка группы -->
            <div class="forum_header">
                <h3><a class="users_information_link" href="#">Кто сейчас на форуме?</a></h3>
                <span class="empty_cell"></span> <!-- Необходим для правильного отображения псевдотаблицы -->
            </div>
        </div>
        <div class="forum_table"> <!-- Таблица -->
            <div class="forum_row"> <!-- Отдельный ряд -->
                <div class="forum_info"> <!-- Содержимое ряда -->
                    Наши пользователи оставили сообщений: 120693
                    <br/>
                    Всего зарегистрированных пользователей: 10478
                </div>
                <div class="empty_cell"></div>
                <!-- Необходим для правильного отображения псевдотаблицы -->
            </div>
            <div class="forum_row"> <!-- Отдельный ряд -->
                <div class="forum_info"> <!-- Содержимое ряда -->
                    Сейчас посетителей на форуме: 35,
                    из них зарегистрированных: 5,
                    скрытых: 0
                    и гостей: 30
                    <span class="admin"> [Администратор] </span> <span class="moderator"> [Модератор]</span>
                    <br/>
                    Зарегистрированные пользователи:
                    <ul class="users_list">
                        <li><a href="#" class="moderator">andreyko</a>,</li>
                        <li><a href="#" class="admin">Староверъ</a>,</li>
                        <li><a href="#" class="user">Вася</a>.</li>
                    </ul>
                </div>
                <div class="empty_cell"></div>
                <!-- Необходим для правильного отображения псевдотаблицы -->
            </div>
        </div>
    </div>
    <div class="footer_buffer"></div>
    <!-- Несемантичный буфер для прибития подвала -->
</div>
</body>
</html>
