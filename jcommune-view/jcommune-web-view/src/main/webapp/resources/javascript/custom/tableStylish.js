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