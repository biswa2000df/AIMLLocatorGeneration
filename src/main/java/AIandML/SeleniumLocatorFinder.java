package AIandML;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SeleniumLocatorFinder {

	public static List<String> findSeleniumLocators(String htmlSnippet) {
		List<String> locators = new ArrayList<>();
		Document doc = Jsoup.parse(htmlSnippet);
		Element targetElement = doc.select("body > *").first();

		if (targetElement != null) {
			String tagName = targetElement.tagName();

			// 1. ID Locator
			if (targetElement.hasAttr("id")) {
				String id = targetElement.attr("id");
				locators.add("ID: " + id);
				locators.add("XPath (by ID): //" + tagName + "[@id='" + id + "']");
				locators.add("CSS Selector (by ID): #" + id);
			}

			// 2. Name Locator
			if (targetElement.hasAttr("name")) {
				String name = targetElement.attr("name");
				locators.add("Name: " + name);
				locators.add("XPath (by Name): //" + tagName + "[@name='" + name + "']");
				locators.add("CSS Selector (by Name): " + tagName + "[name='" + name + "']");
			}

			// 3. Class Name Locator
			if (targetElement.hasAttr("class")) {
				String className = targetElement.attr("class");
				locators.add("Class Name: " + className);
				locators.add("XPath (by Class): //" + tagName + "[@class='" + className + "']");
				String[] classes = className.split("\\s+");
				for (String cls : classes) {
					locators.add("XPath (contains Class): //" + tagName + "[contains(@class, '" + cls + "')]");
					locators.add("CSS Selector (by Class): ." + cls);
				}
			}

			// 4. XPath Locator (Basic Attribute-Based)
			org.jsoup.nodes.Attributes attributes = targetElement.attributes();
			for (org.jsoup.nodes.Attribute attr : attributes) {
				String key = attr.getKey();
				String value = attr.getValue();
				// Avoid redundancy with ID, Name, and Class which are already handled
				if (!key.equals("id") && !key.equals("name") && !key.equals("class") && !key.equals("target")) {
					locators.add("XPath (by Attribute '" + key + "'): //" + tagName + "[@" + key + "='" + value + "']");
					locators.add(
							"CSS Selector (by Attribute '" + key + "'): " + tagName + "[" + key + "='" + value + "']");
				}
			}

			// 5. Link Text and Partial Link Text (for <a> tags)
			if (tagName.equals("a")) {
				String text = targetElement.text().trim();
				if (!text.isEmpty()) {
					locators.add("Link Text: " + text);
					locators.add("XPath (by Link Text): //a[text()='" + text + "']");
					locators.add("XPath (contains Link Text): //a[contains(text(), '" + text + "')]");
				}
			}
		} else {
			locators.add("No element found in the HTML.");
		}

		return locators;
	}

	public static void main(String[] args) {
		String htmlSnippet1 = "<a class=\"gb_X\" aria-label=\"Gmail \" data-pid=\"23\" href=\"https://mail.google.com/mail/?tab=rm&amp;authuser=0&amp;ogbl\" target=\"_top\">Gmail</a>";

//	        String htmlSnippet1 = "<a class=\"_1krdK5 _3jeYYh\" href=\"/viewcart?exploreMode=true&amp;preference=FLIPKART\" title=\"Cart\">Cart</a>";

		// String htmlSnippet1 = "<button class=\"test-id__inline-edit-trigger
		// slds-shrink-none inline-edit-trigger slds-button slds-button_icon\"
		// lwc-618vdk5svck=\"\" title=\"Edit Documents Send Back\"><span
		// aria-hidden=\"true\" lwc-618vdk5svck=\"\" class=\"inline-edit-trigger-icon
		// slds-button__icon slds-button__icon_hint\"></span><span
		// class=\"slds-assistive-text\" lwc-618vdk5svck=\"\">Edit Documents Send
		// Back</span></button>";

//	        System.out.println("Locators for HTML 1:\n" + findSeleniumLocators(htmlSnippet1));

		List<String> generatedXPaths = findSeleniumLocators(htmlSnippet1);
		for (String xpath : generatedXPaths) {
			System.out.println(xpath);
		}

//	        String htmlSnippet2 = "<div id=\"container\" class=\"main-content\"><h1>Welcome</h1><p class=\"info\">This is some information.</p><button data-action=\"submit\" name=\"submit-button\">Submit</button></div>";
//	        System.out.println("\nLocators for HTML 2:\n" + findSeleniumLocators(htmlSnippet2));
	}

}
