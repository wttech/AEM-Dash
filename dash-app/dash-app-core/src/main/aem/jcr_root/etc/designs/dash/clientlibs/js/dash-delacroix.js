var dash = dash || {};

dash.delacroix = {

	/**
	 * Callback when DOM is ready
	 */
	init: function () {
		var context = $(this);

		$('#dash-delacroix', context).each(function () {
			var delacroix = $(this);

			$('.show-xml', delacroix).each(function () {
				$(this).on('click', function () {
					bootbox.alert('Not yet implemented!');
				});
			});

			$('.export-package', delacroix).each(function () {
				$(this).on('click', function () {
					bootbox.alert('Not yet implemented!');
				});
			});

			$('.import-package', delacroix).each(function () {
				$(this).on('click', function () {
					bootbox.alert('Not yet implemented!');
				});
			});
			
			$('.export-xml', delacroix).each(function () {
				$(this).on('click', function () {
					bootbox.alert('Not yet implemented!');
				});
			});

			$('.import-xml', delacroix).each(function () {
				$(this).on('click', function () {
					bootbox.alert('Not yet implemented!');
				});
			});
		});
	}
}

$(document).ready(dash.delacroix.init);