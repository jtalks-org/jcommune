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
var Antimultipost = {};

/**
 * Checks if given element being already submitted
 * @param element element to check
 */
Antimultipost.beingSubmitted = function(element) {
	if (element.attr('submitted')) {
		return true;
	};
	return false;
}

/**
 * Mark element as being submitted
 * @param element element to mark
 */
Antimultipost.disableSubmit = function(element) {
	element.attr('submitted', 'true');
}

/**
 * Mark element as not being submitted
 * @param element element to mark
 */
Antimultipost.enableSubmit = function(element) {
	element.removeAttr('submitted');
}