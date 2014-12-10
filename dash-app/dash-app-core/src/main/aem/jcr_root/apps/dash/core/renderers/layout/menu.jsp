<c:set var="searchModel" value="<%= com.cognifide.aem.dash.core.finder.SearchModel.fromRequest(slingRequest) %>" />
<div id="dash-menu" class="navbar navbar-default ${searchModel.navbarClass}" role="navigation">
	<div class="container-fluid">
		<div class="navbar-collapse collapse">
			<div class="nav navbar-nav navbar-form">
				<form id="dash-finder" method="POST" action="javascript:" class="form-inline">
					<div class="form-group">
						<input name="phrase" type="text" class="form-control" data-placeholder="AEM Dash"
							autofocus="autofocus" autocomplete="off" style="width: 282px;">
					</div>
					<div class="form-group">
						<div class="btn-group">
							<div id="dash-finder-playgrounds" class="btn-group">
								<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown"
									rel="tooltip" title="Switch playground">
									<i class="glyphicon glyphicon-transfer"></i>
								</button>
								<ul class="dropdown-menu">
									<li><a tabindex="-1" href="javascript:">Lack of playgrounds</a></li>
								</ul>
							</div>

							<div class="btn-group">
								<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown"
									rel="tooltip" title="Recent searches">
									<i class="glyphicon glyphicon-star"></i>
								</button>
								<ul id="dash-finder-recent-searches" class="dropdown-menu">
									<li><a tabindex="-1" href="javascript:" class="small">Lack of searches</a></li>
								</ul>
							</div>

							<a class="btn btn-default dash-finder-credentials" href="javascript:">
								<i class="glyphicon glyphicon-lock"></i>
							</a>
						</div>
					</div>
					<div id="dash-finder-location" class="form-group control-group">
						<input name="location" type="text" class="form-control" placeholder="Current playground"
							readonly="readonly" style="width: 480px;" rel="tooltip"
							title="Keep in mind that page location after clicking some link is up to date only while being on local playground (on remote playground could not be updated due to cross domain browser violations).">
					</div>
				</form>
			</div>
			<ul class="nav navbar-nav">
				<li class="dropdown">
					<a href="/etc/dash/board.html" class="dropdown-toggle" target="dash-frame">
						<i class="glyphicon glyphicon-cog"></i> Tools
					</a>
					<ul class="dropdown-menu" role="menu">
						<li>
							<a href="/etc/dash/board.html" target="dash-frame" rel="tooltip" title="Navigation panel with static links for each playground">
								<i class="glyphicon glyphicon-th"></i> Board
							</a>
						</li>
						<li>
							<a href="/etc/dash/delacroix.html" target="dash-frame" rel="tooltip" title="More deluxe version of CRX DE Lite">
								<i class="glyphicon glyphicon-floppy-disk"></i> Delacroix
							</a>
						</li>
					</ul>
				</li>
				<li class="dropdown">
					<a href="/etc/dash/finder.html" class="dropdown-toggle" target="dash-frame">
						<i class="glyphicon glyphicon-wrench"></i> Config
					</a>
					<ul class="dropdown-menu" role="menu">
						<li>
							<a href="/etc/dash/finder.html" target="dash-frame" rel="tooltip" title="Configure search everywhere engine">
								<i class="glyphicon glyphicon-zoom-in"></i> Finder
							</a>
						</li>
						<li>
							<a href="/etc/dash/playgrounds.html" target="dash-frame" rel="tooltip" title="Configure support for multiple AEM instances">
								<i class="glyphicon glyphicon-cloud"></i> Playgrounds
							</a>
						</li>
						<li>
							<a href="/etc/dash/launchers.html" target="dash-frame" rel="tooltip" title="Configure quick fixes that can be launched directly from search input">
								<i class="glyphicon glyphicon-flash"></i> Launchers
							</a>
						</li>
					</ul>
				</li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li>
					<a id="dash-closer" href="/welcome.html">
						<i class="glyphicon glyphicon-remove"></i> Close
					</a>
				</li>
			</ul>
		</div>
  	</div>

	<%@include file="/apps/dash/core/renderers/layout/templates.jsp" %>
</div>