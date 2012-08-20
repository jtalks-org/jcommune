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
 * Stores a list of components that have disabled event 'click'.
 */
var disabledLinks = new Array();

/**
 * Disable click event for components with concrete
 * selector.
 * 
 * @param selector click event for all components with this selector
 *  			   will be disabled
 */
function disableClickEventForComponent(selector) {
	var reference = $(selector);
	var href = reference.attr('href');
	var link = new Link(reference, href);
	disabledLinks.push(link);
	reference.attr('href', '#');
	reference.offtmp('click');
}

/**
 * Enable click event for all components  for which it has been disabled.
 */
function enableClickEventForDisabledComponents() {
	for (i in disabledLinks){
		var link = disabledLinks[i];
		var linkReference = link.reference;
		linkReference.ontmp('click');
		linkReference.attr('href', link.href);
	}
	disabledLinks.length = 0;
}

/**
 * Create an object with data about component, for which
 * 'click' event was disabled.
 * 
 * @param reference reference to component
 * @param href href of component
 * @returns an object with data about component
 */
function Link(reference, href) {
	this.reference = reference;
	this.href = href;
}