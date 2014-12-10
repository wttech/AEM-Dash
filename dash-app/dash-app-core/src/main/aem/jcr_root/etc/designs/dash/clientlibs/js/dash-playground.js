var dash = dash || {};

dash.playground = {

	/**
	 * Callback when DOM is ready
	 */
	init: function () {
		var context = $(this);

		$('.dash-menu-reload', context).each(function () {
			$(this).on('click', function () {
				parent.location.reload();
				return false;
			});
		});
	}
}

$(document).ready(dash.playground.init);