
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

/** Namespace for this script */
var Search = {};

/** Selectors for elements used in scripts */
Search.searchInputSelector = '#searchText';
Search.searchClearSelector = '#searchClear';

/**
	Checks content of search input and toggle visibility
	of clear button.
*/
Search.adjustClearButton = function() {
	var text = $(Search.searchInputSelector).val();
	$(Search.searchClearSelector).toggle(text != '');
}
 
$().ready(function() {
	
	Search.adjustClearButton();
		
	$(Search.searchInputSelector).bind('keyup', function() {
		Search.adjustClearButton();
	});
	
	
	$(Search.searchClearSelector).click(function() {
		$(Search.searchInputSelector).val('');
		$(Search.searchClearSelector).hide();
		$(Search.searchInputSelector).focusout();
	});
});