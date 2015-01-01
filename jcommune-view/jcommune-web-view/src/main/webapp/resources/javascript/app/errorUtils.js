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

var ErrorUtils = {};

/** Pattern for a row with error message */
ErrorUtils.patternForErrorRow = '<span class="help-inline">${message}</span>';

/** Add necessary classes to page elements to highlight errors for current design */
ErrorUtils.fixErrorHighlighting = function() {
	$('div.control-group:not(:has(.rememberme-lbl))').addClass('error');
}

/** Add required classes to highlight errors in specified input for current design 
 * @param inputOrSelector - either selector of input with errors or input itself 
 * 		(e.g. $('#inputId'))
 */
ErrorUtils.addErrorStyles = function(inputOrSelector) {
	var input = ErrorUtils.getInput(inputOrSelector);
	input.closest('div.control-group').addClass('error');
}

/** Remove error classes for specified input for current design 
 * @param inputOrSelector - either selector of input to disable error highlighting
 * 		or input itself (e.g. $('#inputId'))
 */
ErrorUtils.removeErrorStyles = function(inputOrSelector) {
	var input = ErrorUtils.getInput(inputOrSelector)
	input.closest('div.control-group').removeClass('error');
} 

/** Adds error message for specified input and highlights all control group
 * @param  inputOrSelector - either selector of input to disable error highlighting
 * 		or input itself (e.g. $('#inputId'))
 * @param message - error message to be displayed
 */
ErrorUtils.addErrorMessage = function(inputOrSelector, message) {
	var input = ErrorUtils.getInput(inputOrSelector)	
	var insertedRow = input.closest('div.controls').append(ErrorUtils.getErrorRow(message));
	ErrorUtils.addErrorStyles(insertedRow);
}

/** Removes error message for specified input and removes highlighting of all 
 * control group
 * @param  inputOrSelector - either selector of input to disable error highlighting
 * 		or input itself (e.g. $('#inputId'))
 */
ErrorUtils.removeErrorMessage = function(inputOrSelector) {
	var input = ErrorUtils.getInput(inputOrSelector)	
	input.closest('div.controls').find('span.help-inline').remove();
	ErrorUtils.removeErrorStyles(input);
}

/**
 *  Removes all errors message
 */
ErrorUtils.removeAllErrorMessages = function() {
    var input = $('span.help-inline').siblings('input');
    input.closest('div.controls').find('span.help-inline').remove();
    ErrorUtils.removeErrorStyles(input);
}

/** Returns actual error row with given message based on pattern for errors
 * @param - error message to be displayed
 */
ErrorUtils.getErrorRow = function(message) {
	var row = ErrorUtils.patternForErrorRow.replace("${message}", message); 
	return row;
}

/** Returns jquery input (like $('#inputId'))
 * @param  inputOrSelector - either selector (e.g. #inputId) or input itself 
 * 		(e.g. $('#inputId'))
 */
ErrorUtils.getInput = function(inputOrSelector) {
	var input = inputOrSelector;
	if (typeof(inputOrSelector) == "string") {
		input = $(inputOrSelector);
	}
	return input;
}

/**
 * Make our design compatible with form:errors tag.
 */
$(document).ready(function() {
	ErrorUtils.fixErrorHighlighting();
    var targets = $( '.focusToError').parent().find('input, textarea');
    Utils.focusFirstEl(targets[0] || "#subject");
});


