package com.cognifide.aem.dash.core.playgrounds;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

import com.cognifide.aem.dash.core.utils.Dash;
import com.cognifide.aem.dash.core.utils.JSONUtils;

@SlingServlet(paths = { "/bin/dash/playground/manager" }, label = Dash.PREFIX + " Playground Manager Servlet")
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Playground servlet for managing defined CQ instances"),
		@Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class PlaygroundManagerServlet extends SlingAllMethodsServlet {

	@Reference
	private PlaygroundManager manager;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String action = StringUtils.trimToEmpty(request.getParameter("action"));

		if (action.equals("list")) {
			JSONUtils.writeJson(response, manager.getPlaygrounds());
		} else {
			JSONUtils
					.writeMessage(response, "error", String.format("Invalid action specified: '%s'", action));
		}

	}
}
