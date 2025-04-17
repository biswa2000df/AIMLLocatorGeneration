package AIandML;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class GeneratorDynamicXPath {

	public static List<String> generateUniqueXPaths(String htmlSnippet) {
		// 1. Parse the HTML snippet into a DOM (Document Object Model) tree.
		// - Jsoup.parse() is a method from the Jsoup library that takes an HTML string
		// as input and converts it into a Document object.
		// - The Document object represents the entire HTML structure and allows you to
		// navigate and extract information from it.
		Document doc = Jsoup.parse(htmlSnippet);

		// 2. Find the target element. In this simplified system, we assume the snippet
		// contains only one top-level element that we want to generate XPaths for.
		// - doc.select(":root") selects the root element of the document (usually the
		// <html> tag).
		// - .first() gets the first element from the selection, which in our case is
		// the
		// top-level element of the snippet.
		Element targetElement = doc.select("body > *").first();

		// 3. If there's no element in the snippet, return an empty list of XPaths.
		if (targetElement == null) {
			return new ArrayList<>();
		}

		// 4. Get the tag name of the target element (e.g., "a", "div", "span").
		String tagName = targetElement.tagName();

		// 5. Get all the attributes of the target element as a map of key-value pairs.
		org.jsoup.nodes.Attributes attributes = targetElement.attributes();

		// 6. Create a Set to store the generated XPath expressions. Using a Set
		// automatically
		// prevents duplicate XPaths from being added.
		Set<String> xpaths = new HashSet<>();

		// 7. Generate XPath expressions based on different attributes.

		// 7.1. Using the 'id' attribute:
		// - If the element has an 'id' attribute, it's often unique within the HTML.
		// - We construct an XPath that selects the element with this specific 'id'.
		if (attributes.hasKey("id")) {
			String idValue = attributes.get("id");
			xpaths.add(String.format("//%s[@id='%s']", tagName, idValue));
		}

		// 7.2. Using the 'aria-label' attribute:
		// - 'aria-label' is used for accessibility and can often be unique or highly
		// specific.
		// - We construct an XPath that selects the element with this specific
		// 'aria-label'.
		if (attributes.hasKey("aria-label")) {
			String ariaLabelValue = attributes.get("aria-label");
			xpaths.add(String.format("//%s[@aria-label='%s']", tagName, ariaLabelValue));
		}

		// 7.3. Using the 'href' attribute (specifically for 'a' tags):
		// - For anchor tags (<a>), the 'href' attribute (the link URL) is usually
		// unique.
		// - We construct an XPath that selects the 'a' element with this specific
		// 'href'.
		if (tagName.equals("a") && attributes.hasKey("href")) {
			String hrefValue = attributes.get("href");
			xpaths.add(String.format("//a[@href='%s']", hrefValue));
		}

		// 7.4. Using 'data-*' attributes:
		// - Attributes starting with 'data-' are custom data attributes and can be
		// unique.
		// - We iterate through all attributes and check if the key starts with "data-".
		// - For each such attribute, we create an XPath.
		for (org.jsoup.nodes.Attribute attr : attributes) {
			if (attr.getKey().startsWith("data-")) {
				String attributeName = attr.getKey();
				String attributeValue = attr.getValue();
				xpaths.add(String.format("//%s[@%s='%s']", tagName, attributeName, attributeValue));
			}
		}

		// 7.5. Combining 'class' and 'aria-label' attributes:
		// - Combining attributes can increase the specificity and likelihood of
		// uniqueness.
		// - If both 'class' and 'aria-label' are present, we create a combined XPath.
		if (attributes.hasKey("class") && attributes.hasKey("aria-label")) {
			String classValue = attributes.get("class");
			String ariaLabelValue = attributes.get("aria-label");
			xpaths.add(String.format("//%s[@class='%s' and @aria-label='%s']", tagName, classValue, ariaLabelValue));
		}

		// 7.6. Combining 'class' and 'href' attributes (specifically for 'a' tags):
		// - Similar to the above, but combining 'class' and 'href' for anchor tags.
		if (tagName.equals("a") && attributes.hasKey("class") && attributes.hasKey("href")) {
			String classValue = attributes.get("class");
			String hrefValue = attributes.get("href");
			xpaths.add(String.format("//a[@class='%s' and @href='%s']", classValue, hrefValue));
		}

		// 7.7. Using the 'class' attribute alone (handle multiple classes):
		// - The 'class' attribute can have multiple space-separated values.
		// - We create an XPath that matches the entire class string.
		// - We also create XPaths that use the `contains()` function to match if the
		// element has a specific class (useful if there are multiple classes).
		if (attributes.hasKey("class")) {
			String classValue = attributes.get("class");
			xpaths.add(String.format("//%s[@class='%s']", tagName, classValue));
			String[] classNames = classValue.split("\\s+"); // Split by whitespace
			for (String className : classNames) {
				xpaths.add(String.format("//%s[contains(@class, '%s')]", tagName, className));
			}
		}

		// 7.8. Using the tag name as a basic XPath (least specific):
		// - As a fallback or a very general XPath, we include one that just uses the
		// tag name.
		xpaths.add(String.format("//%s", tagName));

		// 8. Convert the Set of XPaths to a List and return it.
		return new ArrayList<>(xpaths);
	}

	public static void main(String[] args) {
		String htmlSnippet = "<a class=\"gb_X\" aria-label=\"Gmail \" data-pid=\"23\" href=\"https://mail.google.com/mail/&amp;ogbl\" target=\"_top\">Gmail</a>";

//	      String  htmlSnippet = "<button class=\"test-id__inline-edit-trigger slds-shrink-none inline-edit-trigger slds-button slds-button_icon\" lwc-618vdk5svck=\"\" title=\"Edit Documents Send Back\"><span aria-hidden=\"true\" lwc-618vdk5svck=\"\" class=\"inline-edit-trigger-icon slds-button__icon slds-button__icon_hint\"></span><span class=\"slds-assistive-text\" lwc-618vdk5svck=\"\">Edit Documents Send Back</span></button>";

		List<String> generatedXPaths = generateUniqueXPaths(htmlSnippet);
		for (String xpath : generatedXPaths) {
			System.out.println(xpath);
		}

	}

}
