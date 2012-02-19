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
 * Application base path with trailing slash. Must be defined somewhere within the global scope.
 */
var baseUrl = $root;

/**
 * Id of topic which will move to another branch.
 */
var topicId = $topicId;

$(document).ready(function () {
    $("#move_topic").click(function () {
        $.getJSON(baseUrl + "/sections/json", function (sections) {
            var sectionsSize = sections.length;
            var str = '<b>Move topic</b><br/><select name="section_name" id="section_name" size="' + sectionsSize + '">';
            $.each(sections, function (i, section) {
                str += '<option value="' + section.id + '">' + section.name + '</option>';
            });
            str += '</select>';
            str += '<select name="branch_name" id="branch_name" size="' + sectionsSize + '">';
            str += '<option value="' + 0 + '">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</option>';
            str += '</select>';
            var branchId;
            $.prompt(str, {
                buttons:{ Move:true, Cancel:false},
                loaded:function () {
                    $("#section_name").change(function () {
                        var sectionId = $(this).val();
                        $.ajax({
                            url:baseUrl + '/branches/json/' + sectionId,
                            success:function (branches) {
                                $("#branch_name").children().remove();
                                $("#branch_name").append(getBranchItemHtml(branches));
                            }
                        });
                    });
                    $("#branch_name").change(function () {
                        branchId = $(this).val();
                    });
                },
                callback:function (value) {
                    if (value != undefined && value) {
                        $.ajax({
                            url:baseUrl + '/topics/json/' + topicId,
                            type:"POST",
                            data:{"branchId":branchId},
                            success:function () {
                                document.location = baseUrl + '/topics/' + topicId;
                            }
                        });
                    }
                }
            });
        });
    });
});


/**
 * Returns HTML code for options in branches select element.
 *
 * @param branches list of all branches in section
 * @return html template
 */
function getBranchItemHtml(branches) {
    var template = '';
    $.each(branches, function (i, branch) {
        template += '<option value="' + branch.id + '">' + branch.name + '</option>';
    });
    return template;
}
