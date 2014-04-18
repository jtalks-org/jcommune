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

$(function () {
    var baseUrl = $root;

    $(".editAllowedPermission").on('click', function (e) {
        e.preventDefault();
        showEditGroupsDialog($(this).data("branch"), $(this).data("permission"), $(this).data("permission-name"), true);
    });

    $(".editRestrictedPermission").on('click', function (e) {
        e.preventDefault();
        showEditGroupsDialog($(this).data("branch"), $(this).data("permission"), $(this).data("permission-name"), false);
    });

    /**
     * Shows dialog for set up permission groups details: User can select which groups will have the selected
     * permission from the list of the all available groups
     * @param branchId id of the branch for which permission is edited
     * @param permissionMask mask of the permission to be changed
     * @param permissionName name of the permission to be changed
     * @param allowed allow or restrict selected permission for the selected groups
     */
    function showEditGroupsDialog(branchId, permissionMask, permissionName, allowed) {
        var permissionInfo = {
            branchId: branchId,
            permissionMask: permissionMask,
            allowed: allowed
        };

        $.ajax({
            url: baseUrl + "/branch/permissions/json",
            type: "POST",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(permissionInfo),
            success: function (resp) {
                showDialog(resp.result.selectedGroups, resp.result.availableGroups, permissionName, allowed);
            },
            error: function (resp) {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelFailedToLoadGroups
                });
            }
        });

        function getGroupListHtml(idSuffix, groupsInfo, selectAllCaption) {
            var content = "<div class='group-list-caption-container'>\
                        <input type='checkbox' id='selectAll" + idSuffix + "'/>\
                        <label for='selectAll" + idSuffix + "' class='group-caption'>" + selectAllCaption + ":</label>\
                        </div><div class='group-list' id='groupList" + idSuffix + "'>";
            for(var i = 0, size = groupsInfo.length; i < size ; i++){
                content += "<div class='group-container'><input type='checkbox' id='group" + groupsInfo[i].id + "' /> \
                        <label for='group" + groupsInfo[i].id + "'>" + groupsInfo[i].name + "</label> </div>";
            }
            content += "</div>";
            return content;
        }

        function showDialog(selectedGroups, availableGroups, permissionName, allowed) {
            var footerContent = ' \
            <button id="cancelEditPermission" class="btn" href="#">' + $labelCancel + '</button> \
            <button id="savePermission" class="btn btn-primary" href="#">' + $labelOk + '</button>';

            var content = "<div class='two-list-selector'> <div class='pull-left list-container'>"
                + getGroupListHtml("Available", availableGroups, $permissionsGroupAvailable);

            content += "</div>\
                    <div class='two-list-selector-controls'> \
                        <div class='two-list-selector-control'>\
                            <a href='#' class='btn'><i class='icon-chevron-right'></i></a>\
                        </div>\
                        <div class='two-list-selector-control'>\
                            <a href='#' class='btn'><i class='icon-forward'></i></a>\
                        </div>\
                        <div class='two-list-selector-control'>\
                            <a href='#' class='btn'><i class='icon-chevron-left'></i></a>\
                        </div> \
                        <div class='two-list-selector-control'>\
                            <a href='#' class='btn'><i class='icon-backward'></i></a>\
                        </div> \
                    </div>";
            content += "<div class='pull-right list-container'>"
                + getGroupListHtml("AlreadyAdded", selectedGroups, $permissionsGroupAlreadyAdded)

            content += "</div></div>";

            jDialog.createDialog({
                dialogId: 'permissionsEditor',
                footerContent: footerContent,
                title: permissionName + ": " + (allowed == true ? $permissionsAllowed : $permissionsRestricted),
                bodyContent: content,
                maxWidth: 800,
                maxHeight: 600,
                handlers: {
                    '#cancelEditPermission' : {'static':'close'},
                    '#savePermission' : {'static':'close'}
                }
            });

            $("#selectAllAvailable").on('click', function (e) {
                $("#groupListAvailable input[type='checkbox']").each(function() {
                    $(this).prop('checked', $("#selectAllAvailable").prop('checked'));
                });
            });

            $("#selectAllAlreadyAdded").on('click', function (e) {
                $("#groupListAlreadyAdded input[type='checkbox']").each(function() {
                    $(this).prop('checked', $("#selectAllAlreadyAdded").prop('checked'));
                });
            });
        }
    }
});
