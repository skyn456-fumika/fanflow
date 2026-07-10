package com.fanflow.global.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {

	private final Safelist safelist;

	public HtmlSanitizer() {
		this.safelist = Safelist.relaxed().addTags("figure", "figcaption", "s", "u").addAttributes("figure", "class")
				.addAttributes("figcaption", "class").addAttributes("img", "src", "alt", "width", "height", "class")
				.addAttributes("a", "href", "target", "rel").addProtocols("a", "href", "http", "https", "mailto")
				.addProtocols("img", "src", "http", "https", "#").preserveRelativeLinks(true);
	}

	public String sanitize(String html) {
		if (html == null || html.isBlank()) {
			return "";
		}

		Document.OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.prettyPrint(false);

		return Jsoup.clean(html, "https://fanflow.hongtfolio.kr", safelist, outputSettings);
	}
}