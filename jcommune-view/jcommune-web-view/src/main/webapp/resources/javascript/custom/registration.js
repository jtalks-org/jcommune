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
 
//handling click on menu link Sign Up 
$(function() {
    var firstView;
    $("#signup").on('click', function(e) {         
      firstView = true;
      SignupPopup();
      //if JS off, then open standart page
      e.preventDefault();
    });
    function SignupPopup(){ 
        var query;
        //data for query
        if(!firstView){query = "username="+ $('#username').val()+"&"+"password="+       
                      $('#password').val() + "&" + "passwordConfirm="+ 
                      $('#passwordConfirm').val()+"&" +
                      "email="+ $('#email').val()}else{
                      query = "username=&password=&passwordConfirm=&email=&firstView=false"};
        //POST-query
        $.ajax({
                 type:"POST",
                 url:"/jcommune/user/new",
                 data: query,
                 dataType: "html",
                 //handling query answer, create registration form
                 success: function(data) {
                            var form_elements = [];
						    $.each($(data).find("div.forum_row"),function(index,value){
	      			      	         if(firstView)$(value).find("span.error").remove();
						             form_elements[index] = $(value).html();
						     });
						     var content = '<ul><div>' + $(data).find("span.forum_header_answer").html() + 
							 '</div><br/><span class="empty_cell"></span>' + form_elements[0] + 
							 form_elements[1] + form_elements[2] + form_elements[3] + '</ul>';
						     //Check the query answer and displays prompt 
						     if($(data).find("span.forum_header_answer").html()!=null){
							 firstView = false;
		  		     		 $.prompt(content,
									  {buttons:{OK:true},  focus:0,
									  submit: SignupPopup});}else{
    				         $.prompt('Registration success');}
        }});         
    };
});