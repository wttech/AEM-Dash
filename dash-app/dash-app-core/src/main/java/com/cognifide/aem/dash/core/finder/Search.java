package com.cognifide.aem.dash.core.finder;

import java.util.List;

public interface Search extends Component {

	List<Provider> getProviders();

	<T> T getProvider(Class<T> providerClass);

	SearchProgress byPhrase(String phrase);

	String getStartupPath();

	String getTheme();
}
