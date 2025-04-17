package AIandML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class SpecificLocatorFinder {
	/*
	 *
	 * public static List<String> findLocators(String htmlSnippet, String
	 * locatorType) { List<String> locators = new ArrayList<>(); Document doc =
	 * Jsoup.parse(htmlSnippet); List<Element> targetElements =
	 * doc.select("body > *");
	 *
	 * if (!targetElements.isEmpty()) { for (Element targetElement : targetElements)
	 * { String tagName = targetElement.tagName();
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("id")) { if (targetElement.hasAttr("id")) {
	 * String id = targetElement.attr("id"); locators.add("ID: " + id);
	 * locators.add("XPath (by ID): //" + tagName + "[@id='" + id + "']");
	 * locators.add("CSS Selector (by ID): #" + id); } else if (locatorType != null
	 * && locatorType.equalsIgnoreCase("id")) { locators.add("ID not found."); } }
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("name")) { if (targetElement.hasAttr("name")) {
	 * String name = targetElement.attr("name"); locators.add("Name: " + name);
	 * locators.add("XPath (by Name): //" + tagName + "[@name='" + name + "']");
	 * locators.add("CSS Selector (by Name): " + tagName + "[name='" + name + "']");
	 * } else if (locatorType != null && locatorType.equalsIgnoreCase("name")) {
	 * locators.add("Name not found."); } }
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("class")) { if (targetElement.hasAttr("class"))
	 * { String className = targetElement.attr("class"); String[] classes =
	 * className.split("\\s+"); for (String cls : classes) { List<Element>
	 * elementsWithClass = doc.select("." + cls); if (elementsWithClass.size() == 1)
	 * { locators.add("XPath (by Class): //" + tagName + "[@class='" + className +
	 * "']"); locators.add("CSS Selector (by Class): ." + cls); }
	 * locators.add("XPath (contains Class): //" + tagName + "[contains(@class, '" +
	 * cls + "')]");
	 *
	 * if (elementsWithClass.size() > 1) { int index =
	 * elementsWithClass.indexOf(targetElement) + 1;
	 * locators.add("CSS Selector (by Class, Indexed): ." + cls + ":nth-child(" +
	 * index + ")");
	 *
	 * List<Element> allElementsWithSameTag = doc.select(tagName); List<Element>
	 * elementsWithClassAndTag = allElementsWithSameTag.stream() .filter(el ->
	 * el.className().contains(cls)) .collect(Collectors.toList()); if
	 * (elementsWithClassAndTag.size() > 1) { int indexTagAndClass =
	 * elementsWithClassAndTag.indexOf(targetElement) + 1;
	 * locators.add("XPath (by Class, Indexed): //" + tagName +
	 * "[contains(@class, '" + cls + "')][" + indexTagAndClass + "]"); } } } } else
	 * if (locatorType != null && locatorType.equalsIgnoreCase("class")) {
	 * locators.add("Class not found."); } }
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("xpath")) { org.jsoup.nodes.Attributes
	 * attributes = targetElement.attributes(); for (org.jsoup.nodes.Attribute attr
	 * : attributes) { String key = attr.getKey(); String value = attr.getValue();
	 * if (!key.equals("id") && !key.equals("name") && !key.equals("class") &&
	 * !key.equals("target")) { locators.add("XPath (by Attribute '" + key +
	 * "'): //" + tagName + "[@" + key + "='" + value + "']"); } } if (locatorType
	 * != null && locatorType.equalsIgnoreCase("xpath") &&
	 * locators.stream().noneMatch(s -> s.startsWith("XPath"))) {
	 * locators.add("XPath locators not found based on other attributes."); } }
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("css")) { org.jsoup.nodes.Attributes attributes
	 * = targetElement.attributes(); for (org.jsoup.nodes.Attribute attr :
	 * attributes) { String key = attr.getKey(); String value = attr.getValue(); if
	 * (!key.equals("id") && !key.equals("name") && !key.equals("class") &&
	 * !key.equals("target")) { locators.add("CSS Selector (by Attribute '" + key +
	 * "'): " + tagName + "[" + key + "='" + value + "']"); } } if (locatorType !=
	 * null && locatorType.equalsIgnoreCase("css") && locators.stream().noneMatch(s
	 * -> s.startsWith("CSS Selector"))) {
	 * locators.add("CSS Selectors not found based on other attributes."); } }
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("linktext")) { if (tagName.equals("a")) { String
	 * text = targetElement.text().trim(); if (!text.isEmpty()) {
	 * locators.add("Link Text: " + text);
	 * locators.add("XPath (by Link Text): //a[text()='" + text + "']");
	 * locators.add("XPath (contains Link Text): //a[contains(text(), '" + text +
	 * "')]"); } else if (locatorType != null &&
	 * locatorType.equalsIgnoreCase("linktext")) {
	 * locators.add("Link text not found."); } } else if (locatorType != null &&
	 * locatorType.equalsIgnoreCase("linktext")) {
	 * locators.add("Link text is only applicable for 'a' tags."); } }
	 *
	 * if (locatorType == null || locatorType.equalsIgnoreCase("all") ||
	 * locatorType.equalsIgnoreCase("combiningtwoattribute")) { List<String>
	 * attributeKeys = new ArrayList<>(); org.jsoup.nodes.Attributes attributes =
	 * targetElement.attributes();
	 *
	 * for (org.jsoup.nodes.Attribute attr : attributes) {
	 * attributeKeys.add(attr.getKey()); }
	 *
	 * for (int i = 0; i < attributeKeys.size(); i++) { for (int j = i + 1; j <
	 * attributeKeys.size(); j++) { String key1 = attributeKeys.get(i); String
	 * value1 = attributes.get(key1); String key2 = attributeKeys.get(j); String
	 * value2 = attributes.get(key2); if (!key1.equals("id") && !key1.equals("name")
	 * && !key1.equals("class") && !key1.equals("target") && !key2.equals("id") &&
	 * !key2.equals("name") && !key2.equals("class") && !key2.equals("target")) {
	 * locators.add("XPath (by combiningTwoAttribute '" + key1 + "' and '" + key2 +
	 * "'): //" + tagName + "[@" + key1 + "='" + value1 + "' and @" + key2 + "='" +
	 * value2 + "']"); } } } } } } else {
	 * locators.add("No element found in the HTML."); }
	 *
	 * return locators; }
	 *
	 */

	public static Set<String> findLocators(String htmlSnippet, String locatorType) {
		Set<String> locators = new HashSet<>();
//		Document doc = Jsoup.parse(htmlSnippet);
		Document doc = Jsoup.parse(htmlSnippet, "", Parser.xmlParser());
		List<Element> targetElements = doc.select("*");

		if (!targetElements.isEmpty()) {

			// Group elements by their unique characteristics (tag and attributes)
			Map<String, List<Element>> elementGroups = new HashMap<>();
			for (Element el : targetElements) {
				String key = el.tagName() + "|"
						+ el.attributes().asList().stream().filter(attr -> !attr.getKey().equals("class")) // Exclude
																											// class for
																											// this
																											// general
																											// grouping
								.sorted((a1, a2) -> a1.getKey().compareTo(a2.getKey()))
								.map(attr -> attr.getKey() + "=" + attr.getValue()).collect(Collectors.joining(","));
				elementGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(el);
			}

			for (Element targetElement : targetElements) {
				String tagName = targetElement.tagName();
				 if (tagName.startsWith("#")) {
				        continue;
				    }

				if (locatorType == null || locatorType.equalsIgnoreCase("all") || locatorType.equalsIgnoreCase("id")) {
					if (targetElement.hasAttr("id")) {
						String id = targetElement.attr("id");
						locators.add("ID: " + id);
						locators.add("XPath (by ID): //" + tagName + "[@id='" + id + "']");
						locators.add("CSS Selector (by ID): #" + id);
					} else if (locatorType != null && locatorType.equalsIgnoreCase("id")) {
						locators.add("ID not found.");
					}
				}

				if (locatorType == null || locatorType.equalsIgnoreCase("all")
						|| locatorType.equalsIgnoreCase("name")) {
					if (targetElement.hasAttr("name")) {
						String name = targetElement.attr("name");
						locators.add("Name: " + name);
						locators.add("XPath (by Name): //" + tagName + "[@name='" + name + "']");
						locators.add("CSS Selector (by Name): " + tagName + "[name='" + name + "']");
					} else if (locatorType != null && locatorType.equalsIgnoreCase("name")) {
						locators.add("Name not found.");
					}
				}

				if (locatorType == null || locatorType.equalsIgnoreCase("all")
						|| locatorType.equalsIgnoreCase("class")) {
					if (targetElement.hasAttr("class")) {
						String className = targetElement.attr("class");
						locators.add("By Class: " + className);
						locators.add("XPath (by Class): //" + tagName + "[@class='" + className + "']");

						String[] classes = className.split("\\s+");
						for (String cls : classes) {
							List<Element> elementsWithClass = doc.select("." + cls);

//                            if (elementsWithClass.size() == 1) {
//                                locators.add("XPath (by Class): //" + tagName + "[@class='" + className + "']");
//                                locators.add("CSS Selector (by Class): ." + cls);
//                            }
							locators.add("XPath (contains Class): //" + tagName + "[contains(@class, '" + cls + "')]");
							locators.add("CSS Selector (by Class): ." + cls);

							if (elementsWithClass.size() > 1) {
								int index = elementsWithClass.indexOf(targetElement) + 1;
								locators.add("CSS Selector (by Class, Indexed): ." + cls + ":nth-child(" + index + ")");

								List<Element> allElementsWithSameTag = doc.select(tagName);
								List<Element> elementsWithClassAndTag = allElementsWithSameTag.stream()
										.filter(el -> el.className().contains(cls)).collect(Collectors.toList());
								if (elementsWithClassAndTag.size() > 1) {
									int indexTagAndClass = elementsWithClassAndTag.indexOf(targetElement) + 1;
									locators.add("XPath (by Class, Indexed): //" + tagName + "[contains(@class, '" + cls
											+ "')][" + indexTagAndClass + "]");
								}
							}
						}
					} else if (locatorType != null && locatorType.equalsIgnoreCase("class")) {
						locators.add("Class not found.");
					}
				}

				// General Indexing Logic
				if (locatorType == null || locatorType.equalsIgnoreCase("all")
						|| locatorType.equalsIgnoreCase("indexing")) {

					String generalKey = tagName + "|" + targetElement.attributes().asList().stream()
							.filter(attr -> !attr.getKey().equals("class"))
							.sorted((a1, a2) -> a1.getKey().compareTo(a2.getKey()))
							.map(attr -> attr.getKey() + "=" + attr.getValue()).collect(Collectors.joining(","));

					if (elementGroups.containsKey(generalKey) && elementGroups.get(generalKey).size() > 1) {
						List<Element> group = elementGroups.get(generalKey);
						int index = group.indexOf(targetElement) + 1;

						// XPath with general indexing
						String xpathBase = "//" + tagName;
						List<String> attributeConditions = targetElement.attributes().asList().stream()
								.filter(attr -> !attr.getKey().equals("class"))
								.sorted((a1, a2) -> a1.getKey().compareTo(a2.getKey()))
								.map(attr -> "@" + attr.getKey() + "='" + attr.getValue() + "'")
								.collect(Collectors.toList());

						if (!attributeConditions.isEmpty()) {
							xpathBase += "[" + String.join(" and ", attributeConditions) + "]";
						}
						locators.add("XPath (General Indexed): (" + xpathBase + ")[" + index + "]");

						// CSS Selector with general indexing (less straightforward without specific
						// attributes)
						// We can try to build a CSS selector based on attributes, but true general
						// indexing
						// in CSS without specific selectors is harder. A simplified approach:
						StringBuilder cssSelector = new StringBuilder(tagName);
						for (Attribute attr : targetElement.attributes()) {
							if (!attr.getKey().equals("id") && !attr.getKey().equals("class")) {
								cssSelector.append("[").append(attr.getKey()).append("='").append(attr.getValue())
										.append("']");
							} else if (attr.getKey().equals("id")) {
								cssSelector = new StringBuilder("#" + attr.getValue());
								break; // ID is unique, no need for further indexing in most cases
							}
						}
						if (cssSelector.toString().equals(tagName)) {
							// If no specific attributes for CSS, we can't reliably index generally
							// Consider using XPath in such cases.
						} else {
							// Note: This CSS selector might still match other elements if the attribute
							// combination isn't unique enough outside the grouped elements.
							locators.add("CSS Selector (General, needs review): " + cssSelector + ":nth-of-type("
									+ index + ")");
						}
					} else if (locatorType != null && locatorType.equalsIgnoreCase("indexing")) {
						locators.add("No indexing or duplicate element found");
					}

				}

				if (locatorType == null || locatorType.equalsIgnoreCase("all")
						|| locatorType.equalsIgnoreCase("xpath")) {
					org.jsoup.nodes.Attributes attributes = targetElement.attributes();
					for (org.jsoup.nodes.Attribute attr : attributes) {
						String key = attr.getKey();
						String value = attr.getValue();
						if (!key.equals("id") && !key.equals("name") && !key.equals("class") && !key.equals("target")
								&& !key.equals("displayed") && !key.equals("checked") && !key.equals("enabled")
								&& !key.equals("scrollable") && !key.equals("bounds") && !key.equals("focused")
								&& !key.equals("clickable") && !key.equals("selected") && !key.equals("long-clickable")
								&& !key.equals("index") && !key.equals("package") && !key.equals("password")
								&& !key.equals("checkable") && !key.equals("focusable")) {

							locators.add("XPath (by Attribute '" + key + "'): //" + tagName + "[@" + key + "='" + value
									+ "']");
						}
					}
					if (locatorType != null && locatorType.equalsIgnoreCase("xpath")
							&& locators.stream().noneMatch(s -> s.startsWith("XPath"))) {
						locators.add("XPath locators not found based on other attributes.");
					}
				}

				if (locatorType == null || locatorType.equalsIgnoreCase("all") || locatorType.equalsIgnoreCase("css")) {
					org.jsoup.nodes.Attributes attributes = targetElement.attributes();
					for (org.jsoup.nodes.Attribute attr : attributes) {
						String key = attr.getKey();
						String value = attr.getValue();
						if (!key.equals("id") && !key.equals("name") && !key.equals("class") && !key.equals("target")
								&& !key.equals("displayed") && !key.equals("checked") && !key.equals("enabled")
								&& !key.equals("scrollable") && !key.equals("bounds") && !key.equals("focused")
								&& !key.equals("clickable") && !key.equals("selected") && !key.equals("long-clickable")
								&& !key.equals("index") && !key.equals("package") && !key.equals("password")
								&& !key.equals("checkable") && !key.equals("focusable")) {

							locators.add("CSS Selector (by Attribute '" + key + "'): " + tagName + "[" + key + "='"
									+ value + "']");
						}
					}
					if (locatorType != null && locatorType.equalsIgnoreCase("css")
							&& locators.stream().noneMatch(s -> s.startsWith("CSS Selector"))) {
						locators.add("CSS Selectors not found based on other attributes.");
					}
				}

				if (locatorType == null || locatorType.equalsIgnoreCase("all")
				        || locatorType.equalsIgnoreCase("linktext")) {

				    String text = targetElement.ownText().trim();
				    if (!text.isEmpty()) {
				        locators.add("XPath (by Text): //" + tagName + "[text()='" + text + "']");
				        locators.add("XPath (contains Text): //" + tagName + "[contains(text(), '" + text + "')]");
				    } else if (locatorType != null && locatorType.equalsIgnoreCase("linktext")) {
//				        locators.add("Text not found.");
				    }
				}


				if (locatorType == null || locatorType.equalsIgnoreCase("all")
						|| locatorType.equalsIgnoreCase("CombiningTwoAttribute")) {
					
					
					
					List<String> attributeKeys = new ArrayList<>();
					org.jsoup.nodes.Attributes attributes = targetElement.attributes();

					for (org.jsoup.nodes.Attribute attr : attributes) {
						attributeKeys.add(attr.getKey());
					}

					for (int i = 0; i < attributeKeys.size(); i++) {
						for (int j = i + 1; j < attributeKeys.size(); j++) {
							String key1 = attributeKeys.get(i);
							String value1 = attributes.get(key1);
							String key2 = attributeKeys.get(j);
							String value2 = attributes.get(key2);
							if (!key1.equals("id") && !key1.equals("name") && !key1.equals("class")
									&& !key1.equals("target") && !key2.equals("id") && !key2.equals("name")
									&& !key2.equals("class") && !key2.equals("target")) {
								locators.add("XPath (by combiningTwoAttribute '" + key1 + "' and '" + key2 + "'): //"
										+ tagName + "[@" + key1 + "='" + value1 + "' and @" + key2 + "='" + value2
										+ "']");
							}
						}
					}
				}
				
				if (locatorType == null || locatorType.equalsIgnoreCase("all") || locatorType.equalsIgnoreCase("text")) {
					 Elements allDescendants = targetElement.getAllElements();

			            for (Element descendant : allDescendants) {
			                if (descendant == targetElement) continue; // skip self
			                String tag = descendant.tagName();
			                String text = descendant.ownText().trim();

			                if (!text.isEmpty()) {
			                    // Exact match
			                	locators.add("XPath: //" + tagName + "/" + tag + "[text()='" + text + "']");
			                    // Contains
			                	locators.add("XPath: //" + tagName + "/" + tag + "[contains(text(), '" + text + "')]");
			                    // Deep descendant exact
			                	locators.add("XPath: //" + tagName + "[.//" + tag + "[text()='" + text + "']]");
			                    // Deep descendant contains
			                	locators.add("XPath: //" + tagName + "[.//" + tag + "[contains(text(), '" + text + "')]]");
			                }
			            }
			        }
				

				
					if (locatorType == null || locatorType.equalsIgnoreCase("all")
							|| locatorType.equalsIgnoreCase("text")) {
						if (targetElement != null) {
							String tag = targetElement.tagName();
							Attributes attributes = targetElement.attributes();

							for (Attribute attr : attributes) {
								String attrName = attr.getKey();
								String attrValue = attr.getValue();

								if (!attrValue.isEmpty()) {
									// Basic attribute-based locator
									String base = "//" + tag + "[@" + attrName + "='" + attrValue + "']";
									locators.add("XPath: " + base);

									// Loop through all children of targetElement
									Elements children = targetElement.getAllElements();
									for (Element child : children) {
										if (child == targetElement)
											continue;

										String childTag = child.tagName();
										Attributes childAttrs = child.attributes();
										String childText = child.ownText().trim();

										if (!childText.isEmpty()) {
											locators.add("XPath: " + base + "//" + childTag + "[text()='" + childText
													+ "']");
											locators.add("XPath: " + base + "//" + childTag + "[contains(text(), '"
													+ childText + "')]");
										}

										for (Attribute childAttr : childAttrs) {
											String cAttrName = childAttr.getKey();
											String cAttrValue = childAttr.getValue();

											if (!cAttrValue.isEmpty()) {
												locators.add("XPath: " + base + "//" + childTag + "[@" + cAttrName
														+ "='" + cAttrValue + "']");
												locators.add("XPath: " + base + "//" + childTag + "[contains(@"
														+ cAttrName + ", '" + cAttrValue + "')]");
											}
										}
									}
								}
							}
						}
					}
			

				
				
			}
		} else {
			locators.add("No element found in the HTML.");
		}

		return locators;
	}

	public static void main(String[] args) {
//        String htmlSnippet = "<a class=\"gb_X\" aria-label=\"Gmail \" data-pid=\"23\" href=\"https://mail.google.com/mail/?tab=rm&amp;authuser=0&amp;ogbl\" target=\"_top\">Gmail</a>";

//        String htmlSnippet = "<button data-component=\"atoms-element-button-1\" data-hook=\"validate-email\" name=\"next\" type=\"button\"><span>Next</span></button>";

		String htmlSnippet = "<div id=\"mainContainer\" class=\"container\">\r\n"
				+ "    <h1 class=\"header\">Sample HTML Elements</h1>\r\n"
				+ "    <p id=\"description\" class=\"text\">This is a sample paragraph with various HTML elements.</p>\r\n"
				+ "    \r\n"
				+ "    <input type=\"text\" id=\"username\" name=\"username\" class=\"input-field\" placeholder=\"Enter your username\" />\r\n"
				+ "    <input type=\"password\" id=\"password\" name=\"password\" class=\"input-field\" placeholder=\"Enter your password\" />\r\n"
				+ "    \r\n"
				+ "    <button id=\"submitBtn\" class=\"btn\">Submit</button>\r\n"
				+ "    \r\n"
				+ "    <div class=\"child\" id=\"child1\">\r\n"
				+ "        <h2 class=\"child-header\">Child Element 1</h2>\r\n"
				+ "        <p class=\"child-text\">This is the first child element.</p>\r\n"
				+ "    </div>\r\n"
				+ "    \r\n"
				+ "</div>";
		
		
		
//		String htmlSnippet = "<button data-hook='validate-email'><span class='label'>Next</span></button>\r\n"
//				+ "";

//        String htmlSnippet = "<button data-component=\"atoms-element-button-1\" data-hook=\"validate-password\" name=\"next\" type=\"button\"><span>Sign in</span></button>";

//        System.out.println("All Locators:\n" + findLocators(htmlSnippet, "all"));
//        System.out.println("\nOnly ID:\n" + findLocators(htmlSnippet, "id"));
//
//        System.out.println("\nOnly Class:\n" + findLocators(htmlSnippet, "class"));
//
//        System.out.println("\nOnly XPath:\n" + findLocators(htmlSnippet, "xpath"));
//
//        System.out.println("\nOnly CSS:\n" + findLocators(htmlSnippet, "css"));
//
//        System.out.println("\nOnly LinkText:\n" + findLocators(htmlSnippet, "linktext"));
//
//        System.out.println("\nOnly Name:\n" + findLocators(htmlSnippet, "name"));

        System.out.println("All Locators:\n");
        xpathPrintSequence(htmlSnippet, "all");

		System.out.println("\n\nOnly ID:\n");
		xpathPrintSequence(htmlSnippet, "id");

		System.out.println("\n\nOnly Class:\n");
		xpathPrintSequence(htmlSnippet, "class");

		System.out.println("Indexing Locators:\n");
		xpathPrintSequence(htmlSnippet, "indexing");

		System.out.println("\n\nOnly Name:\n");
		xpathPrintSequence(htmlSnippet, "name");

		System.out.println("\n\nOnly XPath:\n");
		xpathPrintSequence(htmlSnippet, "xpath");

		System.out.println("\n\nOnly CSS:\n");
		xpathPrintSequence(htmlSnippet, "css");

		System.out.println("\n\nOnly LinkText:\n");
		xpathPrintSequence(htmlSnippet, "linktext");

		System.out.println("\n\nOnly CombiningTwoAttribute:\n");
		xpathPrintSequence(htmlSnippet, "CombiningTwoAttribute");

	}

	public static void xpathPrintSequence(String htmlSnippet, String locatorType) {
		for (String xpath : findLocators(htmlSnippet, locatorType)) {
			System.out.println(xpath);
		}
	}

}
