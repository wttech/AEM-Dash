package com.cognifide.aem.dash.core.playgrounds;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import com.cognifide.aem.dash.core.utils.Dash;
import com.cognifide.aem.dash.core.utils.TextUtils;
import com.google.common.collect.Maps;

@Component(metatype = true, immediate = true, enabled = true, label = Dash.PREFIX
		+ " Playground Origin Filter", description = "Improves cross domain access on external playgrounds")
@Service
@Properties({
		@Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR),
		@Property(name = Constants.SERVICE_RANKING, intValue = Integer.MAX_VALUE),
		@Property(name = "filter.scope", value = { "REQUEST", "INCLUDE", "FORWARD", "ERROR" }, propertyPrivate = true) })
public class PlaygroundOriginFilter implements Filter {

	private static final boolean ENABLED_DEFAULT = true;

	private boolean enabled;

	@Property(boolValue = ENABLED_DEFAULT, label = "Enabled")
	public static final String ENABLED_PROP = "enabled";

	private static final String ORIGIN_DEFAULT = "*";

	private String origin;

	@Property(value = ORIGIN_DEFAULT, label = "Origin", description = "Which host can display contents from that instance in iFrame")
	public static final String ORIGIN_PROP = "origin";

	private static final String CSP_DEFAULT = "default-src 'self' 'unsafe-inline' 'unsafe-eval' ${origin}";

	private String csp;

	@Property(value = CSP_DEFAULT, label = "CSP", description = "Header format for 'Content-Security-Policy'. Available vars: ${origin}")
	public static final String CSP_PROP = "csp";

	@Activate
	private void activate(ComponentContext ctx) {
		this.enabled = PropertiesUtil.toBoolean(ctx.getProperties().get(ENABLED_PROP), ENABLED_DEFAULT);
		this.origin = PropertiesUtil.toString(ctx.getProperties().get(ORIGIN_PROP), ORIGIN_DEFAULT);
		this.csp = PropertiesUtil.toString(ctx.getProperties().get(CSP_PROP), CSP_DEFAULT);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// nothing to do
	}

	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		if (enabled) {
			final Map<String, String> vars = Maps.newHashMap();
			vars.put("origin", origin);

			final String policy = TextUtils.injectVars(csp, vars);

			response.addHeader("Content-Security-Policy", policy);
			response.addHeader("X-Content-Security-Policy", policy);
			response.addHeader("X-Webkit-CSP", policy);

			response.addHeader("Access-Control-Allow-Origin", origin);
			// response.addHeader("X-Frame-Options", "ALLOW-FROM " + origin);
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// nothing to do
	}
}
