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
<%--
    Represents a row in a table of the forum, that contains a poll.
    The left side of a row contains information about the author of the poll.
    The right side of a row contains the poll of topic.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<c:if test="${poll != null}">
    <div class="row well" style="padding: 5px; margin: 5px 0;">
        <div class="span4" style="float: none; margin: 0 auto;">
            <jtalks:poll isVoteButtonEnabled="true" pollItems="${pollOptions}" poll="${poll}"/>
        </div>
    </div>
</c:if>