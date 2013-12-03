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
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag body-content="empty" %>
<%@ attribute name="text" required="true" type="java.lang.String" %>
<%@ attribute name="signature" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="word-wrap post-content-body"><jtalks:bb2html bbCode="${text}"/></div>
<c:if test="${!empty signature}">
  <div class="signature-conatiner">
    <hr/>
        <span class='signature'>
            <jtalks:bb2html bbCode="${signature}"/>
        </span>
  </div>
</c:if>