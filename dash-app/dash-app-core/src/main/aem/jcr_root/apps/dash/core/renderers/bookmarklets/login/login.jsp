<%@include file="/apps/dash/core/renderers/layout/before.jsp" %><%
%><c:set var="loginModel" value="<%= com.cognifide.aem.dash.core.bookmarklets.LoginModel.fromRequest(slingRequest) %>" />
<div id="dash-bookmarklet" class="container-fluid">
	<div class="page-header">
		<h2>Automatic log in</h2>
	</div>

	<div class="panel panel-info">
		<div class="panel-heading">
			<i class="glyphicon glyphicon-info-sign"></i> About
		</div>
		<div class="panel-body">
			<p>
				Please copy following source code and use it as URL address while adding new bookmark to your bar.<br>
				<strong>How to use it?</strong> While being on some page which requires authentication, just click prepared bookmarklet.
			</p>
		</div>

	</div>

	<div class="panel panel-primary">
		<div class="panel-heading">
			<i class="glyphicon glyphicon-text-width"></i> Source
		</div>
		<div class="panel-body">
			<form action="javascript:">
				<div class="form-group">
					<textarea class="bookmarklet-source form-control">javascript:(function(e,a,g,h,f,c,b,d){if(!(f=e.jQuery)||g>f.fn.jquery||h(f)){c=a.createElement("script");c.type="text/javascript";c.src="http://ajax.googleapis.com/ajax/libs/jquery/"+g+"/jquery.min.js";c.onload=c.onreadystatechange=function(){if(!b&&(!(d=this.readyState)||d=="loaded"||d=="complete")){h((f=e.jQuery).noConflict(1),b=1);f(c).remove()}};a.querySelector("head").appendChild(c)}})(window,document,"2.0.0",function($,L){var data=${loginModel.data};var currentUrl=window.location.host;data.forEach(function(e){return-1!=e.url.indexOf(currentUrl)||e.externalUrl&&-1!=e.externalUrl.indexOf(currentUrl)?(console.log("url detected"),void $.ajax({type:"POST",url:"/libs/granite/core/content/login.html/j_security_check",data:{j_username:e.login,j_password:e.password,j_validate:!0},success:function(){console.log("success"),location.reload(!1)},error:function(){console.log("fail",arguments),location.reload(!1)}})):void 0});});</textarea>
				</div>
			</form>
		</div>
	</div>
</div>
<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>