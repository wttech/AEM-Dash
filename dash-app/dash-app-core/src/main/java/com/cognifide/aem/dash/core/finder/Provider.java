package com.cognifide.aem.dash.core.finder;

import java.util.List;

public interface Provider extends Component {

	List<SearchResult> byPhrase(Phrase phrase);
}
