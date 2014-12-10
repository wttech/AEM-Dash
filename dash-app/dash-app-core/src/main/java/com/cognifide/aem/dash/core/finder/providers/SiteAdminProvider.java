package com.cognifide.aem.dash.core.finder.providers;

import static com.cognifide.aem.dash.core.finder.SearchUtils.composeDescription;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
import com.cognifide.aem.dash.core.utils.QueryUtil;
import com.cognifide.aem.dash.core.utils.TextUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component(label = Dash.PREFIX + " " + SiteAdminProvider.LABEL, description = "Provides links which targets to site admin", policy = ConfigurationPolicy.OPTIONAL, immediate = true, metatype = true)
@Service(Provider.class)
@Properties({ @Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class SiteAdminProvider implements Provider {

	private static final Logger LOG = LoggerFactory.getLogger(SiteAdminProvider.class);

	public static final String LABEL = "Finder Site Admin Provider";

	private static final String ADMIN_PATH = "/siteadmin#%s";

	private static final String PREVIEW_PATH = "%s.html";

	private static final boolean ENABLED_DEFAULT = true;

	private boolean enabled;

	@Property(boolValue = ENABLED_DEFAULT, label = "Enabled")
	public static final String ENABLED_PROP = "enabled";

	private static final int RESULT_LIMIT_DEFAULT = 30;

	private int resultLimit;

	@Property(intValue = RESULT_LIMIT_DEFAULT, label = "Result limit", description = "Maximum number of queried results")
	public static final String RESULT_LIMIT_PROP = "resultLimit";

	private static final int RESULT_RANK_DEFAULT = 200;

	private int resultRank;

	@Property(intValue = RESULT_RANK_DEFAULT, label = "Result rank", description = "Ranking of provided results (for sorting, lower better)")
	public static final String RESULT_RANK_PROP = "resultRank";

	private static final String CONTENT_ROOT_DEFAULT = "/content";

	private String[] contentRoots;

	@Property(value = { CONTENT_ROOT_DEFAULT }, label = "Content roots", description = "Search paths for pages", unbounded = PropertyUnbounded.ARRAY)
	public static final String CONTENT_ROOT_PROP = "contentRoots";

	private String phraseQuery;

	private static final String PHRASE_QUERY_DEFAULT = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE([${contentRoot}]) and CONTAINS(s.*\\, '${phrase}')";

	@Property(value = PHRASE_QUERY_DEFAULT, label = "Phrase query", description = "Query in SQL2 for searching by phrase. Available variables: ${contentRoot}, ${phrase}.")
	public static final String PHRASE_QUERY_PROP = "phraseQuery";

	@Activate
	private void activate(ComponentContext ctx) {
		this.enabled = PropertiesUtil.toBoolean(ctx.getProperties().get(ENABLED_PROP), ENABLED_DEFAULT);
		this.resultLimit = PropertiesUtil.toInteger(ctx.getProperties().get(RESULT_LIMIT_PROP),
				RESULT_LIMIT_DEFAULT);
		this.resultRank = PropertiesUtil.toInteger(ctx.getProperties().get(RESULT_RANK_PROP),
				RESULT_RANK_DEFAULT);
		this.contentRoots = PropertiesUtil.toStringArray(ctx.getProperties().get(CONTENT_ROOT_PROP),
				new String[] { CONTENT_ROOT_DEFAULT });
		this.phraseQuery = PropertiesUtil.toString(ctx.getProperties().get(PHRASE_QUERY_PROP),
				PHRASE_QUERY_DEFAULT);
	}

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public List<SearchResult> byPhrase(Phrase phrase) {
		final List<SearchResult> results = Lists.newArrayList();
		if (!enabled || StringUtils.isBlank(phrase.getValue())) {
			return results;
		}

		ResourceResolver resolver = null;

		try {
			resolver = resolverFactory.getAdministrativeResourceResolver(null);

			final String text = phrase.getValue().toLowerCase();
			final QueryManager queryManager = resolver.adaptTo(Session.class).getWorkspace()
					.getQueryManager();

			for (String contentRoot : contentRoots) {
				final Map<String, String> vars = Maps.newHashMap();

				vars.put("contentRoot", contentRoot);
				vars.put("phrase", QueryUtil.escapeLuceneSpecialCharacters(text, false));

				final Query query = queryManager.createQuery(TextUtils.injectVars(phraseQuery, vars),
						Query.JCR_SQL2);

				query.setLimit(resultLimit);

				final QueryResult result = query.execute();
				final String[] selectors = result.getSelectorNames();
				final RowIterator it = result.getRows();

				while (it.hasNext()) {
					final Node node = it.nextRow().getNode(selectors[0]);
					final Resource resource = resolver.getResource(node.getPath());
					if (resource != null) {
						results.add(buildResult(resource.adaptTo(Page.class)));
					}
				}
			}
		} catch (LoginException e) {
			LOG.error("Cannot access repository", e);
		} catch (RepositoryException e) {
			LOG.error("Error while accessing repository", e);
		} finally {
			if (resolver != null) {
				resolver.close();
			}
		}

		return results;
	}

	private SearchResult buildResult(Page page) {
		final String provider = SearchUtils.parseProviderFromLabel(LABEL);
		final String path = String.format(ADMIN_PATH, page.getPath());
		final String label = StringUtils.isNotBlank(page.getTitle()) ? page.getTitle() : page.getName();

		final Map<String, Object> params = new LinkedHashMap<String, Object>();

		final Calendar modified = page.getLastModified();
		if (modified != null) {
			params.put("Last modified", modified.getTime());
		}

		final Template template = page.getTemplate();
		if (template != null) {
			params.put("Template", template.getPath());
		}

		final String description = composeDescription(params);
		final Map<String, Object> context = new LinkedHashMap<String, Object>();

		context.put("preview", String.format(PREVIEW_PATH, page.getPath()));

		final SearchResult sr = new SearchResult(provider, resultRank, label, path, description, context);
		sr.getPhrases().add(page.getName());

		return sr;
	}

	@Override
	public void reset() {
		// nothing to do
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

}
