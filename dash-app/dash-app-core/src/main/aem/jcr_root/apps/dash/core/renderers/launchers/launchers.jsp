<%@include file="/apps/dash/core/renderers/layout/before.jsp" %><%
%><c:set var="launcherModel" value="<%= com.cognifide.aem.dash.core.launchers.LauncherModel.fromRequest(slingRequest) %>" />
<div class="container-fluid">
	<div class="page-header">
		<h2>Launchers
	</div>

	<div class="panel panel-info">
		<div class="panel-heading">
			<i class="glyphicon glyphicon-info-sign"></i> About
		</div>
		<div class="panel-body">
			<p>
				This feature extends Finder. It is adding a support for predefined actions that could be executed immediately.
			</p>
		</div>
	</div>

	<div class="panel panel-default">
		<div class="panel-heading">
			<i class="glyphicon glyphicon-cog"></i> Configuration
		</div>

		<div class="panel-body">
		
			<table class="table table-hover">
				<thead>
					<tr>
						<th>Service</th>
						<th>Description</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${launcherModel.manager.label}</td>
						<td>&mdash;</td>
						<td>
							<a class="btn btn-default" href="/system/console/configMgr/${launcherModel.manager.class.name}"
								x-cq-linkchecker="skip">
								<i class="glyphicon glyphicon-wrench"></i> Configure
							</a>
						</td>
					</tr>
					<c:forEach var="launcher" items="${launcherModel.launchers}">
						<tr>
							<td>${launcher.label}</td>
							<td>${launcher.description}</td>
							<td>
								<a class="btn btn-default" href="/system/console/configMgr/${launcher.class.name}"
									x-cq-linkchecker="skip">
									<i class="glyphicon glyphicon-wrench"></i> Configure
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>

<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>

