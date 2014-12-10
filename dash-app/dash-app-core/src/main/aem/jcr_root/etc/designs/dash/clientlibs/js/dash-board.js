var dash = dash || {};

dash.board = {

	/**
	 * Callback when DOM is ready
	 */
	init: function () {
		var context = $(this);

		$('#dash-board', context).each(function () {
			var board = $(this),
				playgrounds = $('.playground', board);

			$('#dash-board-playground', board).each(function () {
				var input = $(this);

				input.on('keyup', function () {
					var phrase = input.val().toLowerCase();

					$.jStorage.set('playgroundFilter', input.val());

					playgrounds.each(function () {
						var playground = $(this),
							name = $('.panel-heading', playground).text().toLowerCase();

						if (!phrase.length || (name.indexOf(phrase) != -1)) {
							playground.fadeIn('fast');
						} else {
							playground.fadeOut('fast');
						}
					});
				});

				input.val($.jStorage.get('playgroundFilter')).trigger('keyup');
			});
		});
	}
}

$(document).ready(dash.board.init);