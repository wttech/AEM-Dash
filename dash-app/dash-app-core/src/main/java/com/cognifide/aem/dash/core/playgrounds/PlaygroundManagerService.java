package com.cognifide.aem.dash.core.playgrounds;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Service(PlaygroundManager.class)
@Component(label = Dash.PREFIX + " " + PlaygroundManagerService.LABEL, description = "Playground management centre", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class PlaygroundManagerService implements PlaygroundManager {

	private static final Logger LOG = LoggerFactory.getLogger(PlaygroundManagerService.class);

	public static final String LABEL = "Playground Manager";

	private static final String ENTRY_DELIMITER = "|";

	private List<Playground> playgrounds;

	@Property(value = { "Dev Author | localhost:4502 | admin | admin",
			"Dev Publish | localhost:4503 | admin | admin" }, label = "Playgrounds", description = "CQ instances configuration. As first please define local instance. Format: '[name] | [host:port] | [username] | [password]'", unbounded = PropertyUnbounded.ARRAY)
	public static final String PLAYGROUNDS_PROP = "playgrounds";

	@Activate
	private void activate(ComponentContext ctx) {
		this.playgrounds = parsePlaygrounds(PropertiesUtil.toStringArray(
				ctx.getProperties().get(PLAYGROUNDS_PROP), new String[] {}));
	}

	private List<Playground> parsePlaygrounds(String[] values) {
		final List<Playground> playgrounds = Lists.newArrayList();

		for (String value : values) {
			final String[] parts = StringUtils.trimToEmpty(value).split(Pattern.quote(ENTRY_DELIMITER));

			if (parts.length == 2) {
				final String name = StringUtils.trimToEmpty(parts[0]);
				final String url = StringUtils.trimToEmpty(parts[1]);

				playgrounds.add(new Playground(name, url));
			} else if (parts.length == 4) {
				final String name = StringUtils.trimToEmpty(parts[0]);
				final String url = StringUtils.trimToEmpty(parts[1]);
				final String username = StringUtils.trimToEmpty(parts[2]);
				final String password = StringUtils.trimToEmpty(parts[3]);

				playgrounds.add(new Playground(name, url, username, password));
			} else {
				LOG.warn("Invalid playground parameter count ({})", parts.length);
			}
		}

		return playgrounds;
	}

	@Override
	public List<Playground> getPlaygrounds() {
		return playgrounds;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}
}
