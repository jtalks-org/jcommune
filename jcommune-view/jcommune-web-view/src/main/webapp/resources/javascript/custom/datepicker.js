/*
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * This script provides jQuery datepicker functionality
 * Depend on jquery-ui-i18n.min.js, jquery-ui.min.js, jquery-ui.css files
 * TODO need to code proper locale selecting
 */

$(function () {
    $('#datepicker').datepicker({
        dateFormat:'dd-mm-yy',
        showOn:'button',
        buttonImage:'../resources/images/calendar.gif',
        buttonImageOnly:true,
        numberOfMonths:2,
        minDate:'1d',
        firstDay:1,
        showButtonPanel:true
    });


});