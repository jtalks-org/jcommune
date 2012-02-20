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
$(document).ready(function () {
	var c = 0;
	$('.messages tr.mess:even').css('background', '#d4d9df');
	$('.messages tr.mess:odd').css('background', '#cdcdcd');
	$('.counter').text('0 выбрано');

	$('.checker').on("click", function () {
		if ($(this).is(':checked')) {
			if (++c === $('.checker').length) {
				$('.check_all').attr('checked', true);
			};
			$(this).closest('.mess').addClass('check');
		} else {
			--c;
			$(this).closest('.mess').removeClass('check');
			$('.check_all').attr('checked', false);
		};
		$('.counter').text(c + ' выбрано');
	});

	$('.check_all').on("click", function () {
		if ($(this).is(':checked')) {
			$('.checker').attr('checked', true);
			c = $('.checker').length;
			$('.counter').text(c + ' выбрано');
			$('.mess').addClass('check');
		} else {
			$('.checker').attr('checked', false);
			c = 0;
			$('.mess').removeClass('check');
		}
	});
});