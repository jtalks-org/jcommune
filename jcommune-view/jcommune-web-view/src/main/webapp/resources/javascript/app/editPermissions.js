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
                content += "<div class='group-container'><input type='checkbox' id='" + groupsInfo[i].id + "' /> \
                        <label for='" + groupsInfo[i].id + "'>" + groupsInfo[i].name + "</label> </div>";
            }
            content += "</div>";
            return content;
        }

        var newlyAdded = [];
        var removed = [];

        function showDialog(selectedGroups, availableGroups, permissionName, allowed) {
            var footerContent = ' \
            <button id="cancelEditPermission" class="btn" href="#">' + $labelCancel + '</button> \
            <button id="savePermission" class="btn btn-primary" href="#">' + $labelOk + '</button>';

            var content = "<div class='two-list-selector'> <div class='pull-left list-container'>"
                + getGroupListHtml("Available", availableGroups, $permissionsGroupAvailable);

            content += "</div>\
                    <div class='two-list-selector-controls'> \
                        <div class='two-list-selector-control'>\
                            <a id='addSelected' href='#' class='btn'><i class='icon-chevron-right'></i></a>\
                        </div>\
                        <div class='two-list-selector-control'>\
                            <a id='addAll' href='#' class='btn'><i class='icon-forward'></i></a>\
                        </div>\
                        <div class='two-list-selector-control'>\
                            <a id='removeSelected' href='#' class='btn'><i class='icon-chevron-left'></i></a>\
                        </div> \
                        <div class='two-list-selector-control'>\
                            <a id='removeAll' href='#' class='btn'><i class='icon-backward'></i></a>\
                        </div> \
                    </div>";
            content += "<div class='pull-right list-container'>"
                + getGroupListHtml("AlreadyAdded", selectedGroups, $permissionsGroupAlreadyAdded);

            content += "</div></div>";

            var submitFunc = function(e) {
                e.preventDefault();
                if ((newlyAdded.length == 0) && (removed.length == 0)) {
                    jDialog.closeDialog();
                    return;
                }
                var permissionInfo = {
                    branchId: branchId,
                    permissionMask: permissionMask,
                    newlyAddedGroupIds: newlyAdded,
                    removedGroupIds: removed
                };
                $.ajax ({
                    url: baseUrl + "/branch/permissions/edit",
                    type: "POST",
                    contentType: "application/json",
                    async: false,
                    data: JSON.stringify(permissionInfo),
                    success : function(resp) {
                        jDialog.closeDialog();
                    },
                    error : function(resp) {
                        console.log("error occur");
                    }
                });
            };

            jDialog.createDialog({
                dialogId: 'permissionsEditor',
                footerContent: footerContent,
                title: (allowed == true ? $allowPermission : $restrictPermission) + ' ' + permissionName,
                bodyContent: content,
                maxWidth: 800,
                maxHeight: 600,
                handlers: {
                    '#cancelEditPermission' : {'static':'close'},
                    '#savePermission' : {'click':submitFunc}
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

            //TODO: refactoring
            $("#addSelected").on('click', function(e) {
                console.log("addSelected");
                selectedGroups = getSelectedAvailableGroups().concat(selectedGroups);
                console.log("new selected " + selectedGroups.length);
                jDialog.closeDialog();
                showDialog(selectedGroups, availableGroups, permissionName, allowed);
            });

            $("#addAll").on('click', function(e) {
                console.log("addAll");
                for (var i = 0; i < availableGroups.length; i ++) {
                    if (removed.indexOf(availableGroups[i].id) == -1) {
                        newlyAdded.push(availableGroups[i].id);
                    } else {
                        removed.splice(removed.indexOf(availableGroups[i].id), 1);
                    }
                }
                console.log("newly added size - " + newlyAdded.length);
                console.log("removed size - " + removed.length);
                selectedGroups = availableGroups.concat(selectedGroups);
                availableGroups.length = 0;
                jDialog.closeDialog();
                showDialog(selectedGroups, availableGroups, permissionName, allowed);
            });

            $("#removeSelected").on('click', function(e) {
                console.log("removeSelected");
                availableGroups = getSelectedAlreadyAddedGroups().concat(availableGroups);
                jDialog.closeDialog();
                showDialog(selectedGroups, availableGroups, permissionName, allowed);
            });

            $("#removeAll").on('click', function(e) {
                console.log("removeAll");
                for (var i = 0; i < selectedGroups.length; i ++) {
                    if (newlyAdded.indexOf(selectedGroups[i].id) == -1) {
                        removed.push(selectedGroups[i].id);
                    } else {
                        newlyAdded.splice(newlyAdded.indexOf(selectedGroups[i].id), 1);
                    }
                }
                console.log("newly added size - " + newlyAdded.length);
                console.log("removed size - " + removed.length);
                availableGroups = selectedGroups.concat(availableGroups);
                selectedGroups.length = 0;
                jDialog.closeDialog();
                showDialog(selectedGroups, availableGroups, permissionName, allowed);
            });

            function getSelectedAvailableGroups() {
                var selected = [];
                $("#groupListAvailable input[type='checkbox']:checked").each(function() {
                    var group = getGroupInfoById(availableGroups, parseInt($(this).prop('id'), 10));
                    if (removed.indexOf(group.id) == -1) {
                        newlyAdded.push(group.id);
                    } else {
                        removed.splice(removed.indexOf(group.id), 1);
                    }
                    console.log("newly added size - " + newlyAdded.length);
                    console.log("removed size - " + removed.length);
                    selected.push(group);
                });
                return selected;
            }

            function getSelectedAlreadyAddedGroups() {
                var selected = [];
                $("#groupListAlreadyAdded input[type='checkbox']:checked").each(function() {
                    var group = getGroupInfoById(selectedGroups, parseInt($(this).prop('id'), 10));
                    if (newlyAdded.indexOf(group.id) == -1) {
                        removed.push(group.id);
                    } else {
                        newlyAdded.splice(newlyAdded.indexOf(group.id), 1);
                    }
                    console.log("newly added size - " + newlyAdded.length);
                    console.log("removed size - " + removed.length);
                    selected.push(group);
                });
                return selected;
            }

            function getGroupInfoById(groupsInfo, id) {
                for (var i = 0; i < groupsInfo.length; i ++) {
                    if (groupsInfo[i].id == id) {
                        tmp = groupsInfo[i];
                        groupsInfo.splice(i, 1);
                        return tmp;
                    }
                }
            }
        }
    }
});
