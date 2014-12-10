var dash = dash || {};

dash.bookmarklet = {

	/**
	 * Callback when DOM is ready
	 */
	init: function () {
		var context = $(this);

		$('#dash-bookmarklet', context).each(function () {
			var container = $(this);

			$('.bookmarklet-source', container).each(function () {
				$(this).focus(function() {
					var source = $(this);

					source.select();

					// Work around Chrome's little problem
					source.mouseup(function() {
					   // Prevent further mouseup intervention
					   source.unbind("mouseup");
					   return false;
					});
				});
			});
		});
	}
}

$(document).ready(dash.bookmarklet.init);