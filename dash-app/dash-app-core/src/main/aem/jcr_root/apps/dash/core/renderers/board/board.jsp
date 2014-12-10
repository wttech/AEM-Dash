<%@include file="/apps/dash/core/renderers/layout/before.jsp" %><%
%><c:set var="boardModel" value="<%= com.cognifide.aem.dash.core.board.BoardModel.fromRequest(slingRequest) %>" />
<div id="dash-board" class="container-fluid">
	<div class="panel panel-blank">
		<form class="form-inline" role="form">
			<div class="form-group">
				<label for="dash-board-playground" class="sr-only">Filter playgrounds</label>
				<input id="dash-board-playground" type="text" class="form-control" placeholder="Filter playgrounds">
			</div>

			<div class="btn-group">
				<a href="/system/console/configMgr/com.cognifide.aem.dash.core.finder.providers.StaticProvider"
					x-cq-linkchecker="skip" class="btn btn-default">
					<i class="glyphicon glyphicon-link"></i> Modify links
				</a>
				<a href="/system/console/configMgr/com.cognifide.aem.dash.core.playgrounds.PlaygroundManagerService"
					x-cq-linkchecker="skip" class="btn btn-default">
					<i class="glyphicon glyphicon-cloud"></i> Manage playgrounds
				</a>

				<a href="/etc/dash/bookmarklets/login.html" x-cq-linkchecker="skip" class="btn btn-default">
					<i class="glyphicon glyphicon-log-in"></i> Automatic log in
				</a>
			</div>
		</form>
    </div>

	<div class="row">
		<c:forEach var="playground" items="${boardModel.playgrounds}" varStatus="status">
			<div class="playground col-md-4">
				<div class="panel panel-primary">
					<div class="panel-heading">
						<span class="badge">${status.count}</span> &nbsp; ${playground.name}
						<div class="pull-right">
							<a href="http://${playground.loginUrl}/system/console/configMgr" target="dash-frame"
								rel="tooltip" title="Login and open system console on that playground"
								x-cq-linkchecker="skip" class="login-console btn btn-xs btn-default"><i class="glyphicon glyphicon-log-in"></i></a>
						</div>
					</div>

					<table class="table-credentials table table-bordered table-condensed small">
						<tr>
							<th>URL:</th>
							<td>
								<a href="http://${playground.url}" target="dash-frame"
									rel="tooltip" title="Go to home page on that playground" x-cq-linkchecker="skip">${playground.url}</a>
							</td>
						</tr>
						<tr>
							<th>Username:</th>
							<td>${playground.username}</td>
						</tr>
						<tr>
							<th>Password:</th>
							<td>${playground.password}</td>
						</tr>
					</table>

					<table class="table-entries table table-condensed table-striped">
						<c:forEach var="letter" items="${boardModel.entries}">
							<tr>
								<th><strong>${letter.key}</strong></th>
								<td>
									<c:forEach var="entry" items="${letter.value}">
										<a class="btn btn-link btn-xs" href="http://${playground.url}${entry.path}" target="dash-frame"
											x-cq-linkchecker="skip">${entry.label}</a>
									</c:forEach>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</c:forEach>

		<div class="clearfix visible-xs-block"></div>
	</div>
</div>

<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>

