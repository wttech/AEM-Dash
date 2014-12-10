package com.cognifide.aem.dash.core.finder.providers;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.dash.core.finder.Phrase;
import com.cognifide.aem.dash.core.finder.Provider;
import com.cognifide.aem.dash.core.finder.SearchResult;
import com.cognifide.aem.dash.core.finder.SearchUtils;
import com.cognifide.aem.dash.core.utils.Dash;
import com.google.common.collect.Lists;

@Component(label = Dash.PREFIX + " " + StaticProvider.LABEL, description = "Provides links to commonly used tools in CQ platform", immediate = true, metatype = true)
@Service(Provider.class)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class StaticProvider implements Provider {

	private static final Logger LOG = LoggerFactory.getLogger(StaticProvider.class);

	public static final String LABEL = "Finder Static Provider";

	private static final String ENTRY_DELIMITER = "|";

	private static final boolean ENABLED_DEFAULT = true;

	private boolean enabled;

	@Property(boolValue = ENABLED_DEFAULT, label = "Enabled")
	public static final String ENABLED_PROP = "enabled";

	private List<SearchResult> entries;

	@Property(value = { "Sling Filters | /system/console/status-slingfilter",
			"Recent Requests | /system/console/requests", "OSGi Console | /system/console",
			"OSGi Bundles | /system/console/bundles", "OSGi Configuration | /system/console/configMgr",
			"OSGi Status Dump | /libs/crx/core/content/welcome/osgi/statusdump.zip",
			"OSGi Services | /system/console/services", "OSGi Components | /system/console/components",
			"Sling Resource Resolver | /system/console/jcrresolver",
			"Sling Servlet Resolver | /system/console/servletresolver",
			"Sling Log Support | /system/console/slinglog", "Sling Adapters | /system/console/adapters",
			"Sling Jobs | /system/console/slingevent", "JMX | /system/console/jmx",
			"Memory Usage | /system/console/memoryusage", "System Information | /system/console/vmstat",
			"MIME Types | /system/console/mimetypes", "CRX DE Logs | /bin/crxde/logs?tail=5000",
			"CRX DE Lite | /crx/de", "CRX Explorer | /crx/explorer/browser/index.jsp",
			"CRX Package Manager | /crx/packmgr", "CRX Package Share | /crx/packageshare/",
			"Site Admin | /siteadmin", "User Admin | /useradmin", "DAM Admin | /damadmin",
			"Tools | /miscadmin", "Campaigns | /mcmadmin", "Communities | /socoadmin", "Inbox | /inbox",
			"Tagging | /tagging", "CQ Dashboard (EXT JS) | /welcome.html",
			"AEM Dashboard (Coral UI) | /projects.html",
			"Workflows | /libs/aem/workflow/content/console.html", "Replication | /etc/replication.html",
			"Replication - Activate Tree | /etc/replication/treeactivation.html",
			"Replication - Agents on author | /etc/replication/agents.author.html",
			"Replication - Agents on publish | /etc/replication/agents.publish.html",
			"Dump Client Libraries | /libs/granite/ui/content/dumplibs.html",
			"Query Builder Debugger | /libs/cq/search/content/querydebug.html",
			"Groovy Console | /etc/groovyconsole.html", "CQ Security Management | /etc/cqsm/import.html",
			"Clustering | /libs/cq/core/content/welcome/features/cluster.html",
			"Backup | /libs/cq/core/content/welcome/features/backup.html",
			"Creative Exchange Import | /etc/creativeExchange/import.html",
			"Creative Exchange Export | /etc/creativeExchange/export.html" }, label = "Entries", description = "Search result entries possible to found", unbounded = PropertyUnbounded.ARRAY)
	public static final String ENTRIES_PROP = "entries";

	private static final int RESULT_RANK_DEFAULT = 100;

	private int resultRank;

	@Property(intValue = RESULT_RANK_DEFAULT, label = "Result rank", description = "Ranking of provided results (for sorting, lower better)")
	public static final String RESULT_RANK_PROP = "resultRank";

	@Activate
	private void activate(ComponentContext ctx) {
		this.enabled = PropertiesUtil.toBoolean(ctx.getProperties().get(ENABLED_PROP), ENABLED_DEFAULT);
		this.resultRank = PropertiesUtil.toInteger(ctx.getProperties().get(RESULT_RANK_PROP),
				RESULT_RANK_DEFAULT);
		this.entries = parseEntries(PropertiesUtil.toStringArray(ctx.getProperties().get(ENTRIES_PROP),
				new String[] {}));
	}

	@Override
	public List<SearchResult> byPhrase(Phrase phrase) {
		final List<SearchResult> results = Lists.newArrayList();

		if (!enabled || StringUtils.isBlank(phrase.getValue())) {
			return results;
		}

		for (SearchResult result : entries) {
			if (SearchUtils.containsPhrase(phrase.getValue(),
					Arrays.asList(result.getLabel(), result.getPath()))) {
				results.add(result);
			}
		}

		return results;
	}

	@Override
	public void reset() {
		// nothing to do
	}

	private List<SearchResult> parseEntries(String[] entries) {
		final List<SearchResult> results = Lists.newArrayList();
		final String provider = SearchUtils.parseProviderFromLabel(LABEL);

		for (String entry : entries) {
			final String[] parts = StringUtils.trimToEmpty(entry).split(Pattern.quote(ENTRY_DELIMITER));
			if (parts.length != 2) {
				LOG.warn("Invalid entry parameter count ({})", parts.length);
			}

			final String label = StringUtils.trimToEmpty(parts[0]);
			final String url = StringUtils.trimToEmpty(parts[1]);

			final SearchResult sr = new SearchResult(provider, resultRank, label, url);

			final String lastPathPart = StringUtils.substringAfterLast(url, "/");
			if (StringUtils.isNotBlank(lastPathPart)) {
				sr.getPhrases().add(lastPathPart);
			}

			results.add(sr);
		}

		return results;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	public List<SearchResult> getEntries() {
		return entries;
	}
}
