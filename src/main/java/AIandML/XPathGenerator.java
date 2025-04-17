package AIandML;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class XPathGenerator {

	public static List<String> findExtensiveXPaths(String htmlSnippet) {
		List<String> xpaths = new ArrayList<>();
		Document doc = Jsoup.parse(htmlSnippet);
		Element targetElement = doc.select("body > *").first(); // doc.select(":root").first();

//	        System.out.println(targetElement);

		if (targetElement != null) {
			String tagName = targetElement.tagName();
//	            System.out.println("TagName = " + tagName);
			org.jsoup.nodes.Attributes attributes = targetElement.attributes();
//	            System.out.println("Attribute = " + attributes);

			// 1. By ID
			if (attributes.hasKey("id")) {
				xpaths.add("//" + tagName + "[@id='" + attributes.get("id") + "']");
			}

			// 2. By Name
			if (attributes.hasKey("name")) {
				xpaths.add("//" + tagName + "[@name='" + attributes.get("name") + "']");
			}

			// 3. By Class (exact match and contains)
			if (attributes.hasKey("class")) {
				xpaths.add("//" + tagName + "[@class='" + attributes.get("class") + "']");
				String[] classes = attributes.get("class").split("\\s+");
				for (String cls : classes) {
					xpaths.add("//" + tagName + "[contains(@class, '" + cls + "')]");
				}
			}

			// 4. By other attributes (exact match, starts-with, ends-with)
			for (org.jsoup.nodes.Attribute attr : attributes) {
				String key = attr.getKey();
				String value = attr.getValue();
				if (!key.equals("id") && !key.equals("name") && !key.equals("class")) {
					xpaths.add("//" + tagName + "[@" + key + "='" + value + "']");
					if (value.length() > 3) { // Avoid short prefixes/suffixes
						xpaths.add("//" + tagName + "[starts-with(@" + key + ", '"
								+ value.substring(0, Math.min(5, value.length())) + "')]");
						xpaths.add("//" + tagName + "[ends-with(@" + key + ", '"
								+ value.substring(Math.max(0, value.length() - 5)) + "')]");
					}
				}
			}

			// 5. Combining two attributes
			List<String> attributeKeys = new ArrayList<>();
			for (org.jsoup.nodes.Attribute attr : attributes) {
				attributeKeys.add(attr.getKey());
			}
			for (int i = 0; i < attributeKeys.size(); i++) {
				for (int j = i + 1; j < attributeKeys.size(); j++) {
					String key1 = attributeKeys.get(i);
					String value1 = attributes.get(key1);
					String key2 = attributeKeys.get(j);
					String value2 = attributes.get(key2);
					xpaths.add("//" + tagName + "[@" + key1 + "='" + value1 + "' and @" + key2 + "='" + value2 + "']");
				}
			}

			// 6. Combining three attributes (if available)
			if (attributeKeys.size() >= 3) {
				for (int i = 0; i < attributeKeys.size(); i++) {
					for (int j = i + 1; j < attributeKeys.size(); j++) {
						for (int k = j + 1; k < attributeKeys.size(); k++) {
							String key1 = attributeKeys.get(i);
							String value1 = attributes.get(key1);
							String key2 = attributeKeys.get(j);
							String value2 = attributes.get(key2);
							String key3 = attributeKeys.get(k);
							String value3 = attributes.get(key3);
							xpaths.add("//" + tagName + "[@" + key1 + "='" + value1 + "' and @" + key2 + "='" + value2
									+ "' and @" + key3 + "='" + value3 + "']");
						}
					}
				}
			}

			// 7. Link Text based (for <a>)
			if (tagName.equals("a")) {
				String text = targetElement.text().trim();
				if (!text.isEmpty()) {
					xpaths.add("//a[text()='" + text + "']");
					xpaths.add("//a[contains(text(), '" + text + "')]");
				}
			}

			// 8. Basic tag name XPath
			xpaths.add("/" + tagName);

		} else {
			xpaths.add("No element found in the HTML.");
		}

		return new ArrayList<>(xpaths);
	}

	public static void main(String[] args) {
		List<String> xpathList = new ArrayList<>();
//	        String htmlSnippet = "<a class=\"gb_X main-link important\" aria-label=\"Gmail \" data-pid=\"23\" href=\"https://mail.google.com/mail/&amp;ogbl\" target=\"_top\" id=\"gmail-link\" name=\"gmail-button\">Gmail</a>";
		String htmlSnippet = "<button data-component=\"atoms-element-button-1\" data-hook=\"validate-password\" name=\"next\" type=\"button\"><span>Sign in</span></button>";

		xpathList = findExtensiveXPaths(htmlSnippet);

		System.out.println("Extensive XPaths for HTML:\n" + findExtensiveXPaths(htmlSnippet));

		String perfectLocator = selectPerfectLocator(xpathList);
		System.out.println("The selected perfect locator is: " + perfectLocator);
	}

	public static String selectPerfectLocator(List<String> xpaths) {
		String bestLocator = null;
		int bestScore = -1;

		for (String xpath : xpaths) {
			int currentScore = 0;

			// Prioritize combinations of attributes (higher score for more combined
			// attributes)
			int andCount = xpath.split(" and ").length - 1;
			if (andCount > 0) {
				currentScore += 5 + andCount * 2; // Base score for combination + bonus for each additional attribute
			}

			// Prioritize specific data- attributes (data-hook and data-component)
			if (xpath.contains("@data-hook='validate-password'")) {
				currentScore += 4;
			}
			if (xpath.contains("@data-component='atoms-element-button-1'")) {
				currentScore += 4;
			}

			// Favor exact attribute matches over starts-with or ends-with
			if (xpath.contains("[@") && !xpath.contains("starts-with") && !xpath.contains("ends-with")) {
				currentScore += 3;
			}

			// Penalize generic and less specific attributes (e.g., type='button')
			if (xpath.contains("@type='button'")) {
				currentScore -= 2;
			}
			if (xpath.contains("@name='next'")) { // While potentially useful, data- attributes might be more stable
				currentScore += 1;
			}

			// Penalize very basic tag name XPath
			if (xpath.equals("/button") || xpath.equals("//button")) {
				currentScore -= 5;
			}

			// Update the best locator if the current one has a higher score
			if (currentScore > bestScore) {
				bestScore = currentScore;
				bestLocator = xpath;
			}
		}

		return bestLocator;
	}

}
