package com.cognifide.aem.dash.core.finder;

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

@SlingServlet(paths = { "/bin/dash/finder/search" }, label = Dash.PREFIX + " Finder Search Servlet")
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Finder servlet for servicing 'search everywhere' feature"),
		@Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class SearchServlet extends SlingAllMethodsServlet {

	@Reference
	private Search search;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String action = StringUtils.trimToEmpty(request.getParameter("action"));

		if (action.equals("byPhrase")) {
			JSONUtils.writeJson(response, search.byPhrase(request.getParameter("phrase")));
		} else if (action.equals("reset")) {
			search.reset();

			JSONUtils.writeMessage(response, "success", "Cache cleared successfully");
		} else {
			JSONUtils
					.writeMessage(response, "error", String.format("Invalid action specified: '%s'", action));
		}
	}
}
