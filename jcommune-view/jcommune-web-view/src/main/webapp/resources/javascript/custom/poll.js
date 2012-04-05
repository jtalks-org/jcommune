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

$(document).ready(function() {   

	$("#pollAjaxLoader").hide(); //hide the ajax loader
	$("#pollMessage").hide(); //hide the ajax loader
	$("#pollSubmit").click(function() {
		var pollAnswerVal = $('input:radio[name=pollAnswer]:checked').val();//Getting the value of a selected radio element.
		if ($('input:radio[name=pollAnswer]:checked').length) {
			$("#pollAjaxLoader").show(); //show the ajax loader
			return false; 
		} else {
			$("#pollMessage").html("please select an answer.").fadeTo("slow", 1, function(){
				setTimeout(function() {
					$("#pollMessage").fadeOut("slow");
				}, 3000);																		 
			});
			return false;
		}
	
	});

});