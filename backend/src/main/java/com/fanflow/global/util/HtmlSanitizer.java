package com.fanflow.global.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {

	private static final Safelist SAFE_LIST = Safelist.relaxed().addTags("figure", "figcaption", "s", "u").addAttributes("figure", "class")
			.addAttributes("figcaption", "class").addAttributes("img", "src", "alt", "title", "width", "height", "class")
			.addAttributes("a", "href", "title", "target", "rel").addProtocols("a", "href", "http", "https", "mailto")
			.addProtocols("img", "src", "http", "https").preserveRelativeLinks(true);

	public String sanitize(String html) {
		if (html == null || html.trim().isEmpty()) {
			return "";
		}

		return Jsoup.clean(html, SAFE_LIST);
	}
}