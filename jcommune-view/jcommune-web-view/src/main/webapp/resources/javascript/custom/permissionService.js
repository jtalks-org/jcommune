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

/** Namespace for this file */
var PermissionService = {};

/**
 * Checks if current user granted with permission
 * @param targetId the identifier for the object instance
 * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
 * @param permission a representation of the permission object as supplied by the expression system. Not null.
 * @param callback function to be called if user granted with permission
 */
PermissionService.hasPermission = function(targetId, targetType, permission, callback) {
	$.get(baseUrl + '/security/haspermission',
		{targetId:targetId, targetType:targetType, permission:permission})
		.success(function(data) {
			if (data.status == 'SUCCESS') {
				callback();
			}
		})
		.error(function() {
            jDialog.createDialog({
                type: jDialog.alertType,
                bodyMessage: $labelUnexpectedError
            });
		});
}

/**
 * Checks if current user granted with permission.
 * @param targetId the identifier for the object instance
 * @param targetType a String representing the target's type (e.g. 'BRANCH' or 'USER'). Not null.
 * @param permission a representation of the permission object as supplied by the expression system. Not null.
 * @return true if granted, false otherwise
 */
PermissionService.getHasPermission = function(targetId, targetType, permission) {
	var result = false;
	$.ajax({
		url: baseUrl + '/security/haspermission',
		async: false,
		data: {targetId:targetId, targetType:targetType, permission:permission},
		success: function(data) {
			result = (data.status == 'SUCCESS');
		},
		error : function() {
            jDialog.createDialog({
                type: jDialog.alertType,
                bodyMessage: $labelUnexpectedError
            });
		}
	});
	
	return result;
}