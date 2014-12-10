var dash = dash || {};

dash.finder = {

	/**
	 * Callback when DOM is ready
	 */
	init: function () {
		var context = $(this);

		$('#dash-finder', context).each(function () {
			var finder = $(this);

			// Finder actions
			$('.dash-finder-reset', context).each(function () {
				$(this).on('click', function () {
					$.ajax({
						url: '/bin/dash/finder/search?action=reset',
						dataType: 'json',
						success: function (response) {
							bootbox.alert(response.message);
						}
					});

					return false;
				});
			});
		});
	}
}

$(document).ready(dash.finder.init);