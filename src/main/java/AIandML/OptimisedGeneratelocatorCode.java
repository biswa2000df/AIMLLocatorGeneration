package AIandML;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

public class OptimisedGeneratelocatorCode {
	


    public static Set<String> findLocators(String htmlSnippet, String locatorType) {
        Set<String> locators = new HashSet<>();
        Document doc = Jsoup.parse(htmlSnippet, "", Parser.xmlParser());
        List<Element> targetElements = doc.select("*");

        if (targetElements.isEmpty()) {
            locators.add("No element found in the HTML.");
            return locators;
        }

        Map<String, List<Element>> elementGroups = targetElements.stream()
                .collect(Collectors.groupingBy(el -> (
                        el.tagName() + "|" + el.attributes().asList().stream()
                                .filter(attr -> !attr.getKey().equals("class"))
                                .sorted(Comparator.comparing(Attribute::getKey))
                                .map(attr -> attr.getKey() + "=" + attr.getValue())
                                .collect(Collectors.joining(","))
                )));

        for (Element targetElement : targetElements) {
            String tagName = targetElement.tagName();
            if (tagName.startsWith("#")) continue;

            Attributes attributes = targetElement.attributes();
            String id = attributes.get("id");
            String name = attributes.get("name");
            String className = attributes.get("class");
            String text = targetElement.ownText().trim();

            // Include all available attributes in locators
            for (Attribute attr : attributes) {
                String attrName = attr.getKey();
                String attrValue = attr.getValue();
                
                if (!isReliableAttribute(attrName)) continue;

                // Generate locators based on the attribute
                if (shouldInclude(locatorType, "xpath")) {
                    locators.add("//" + tagName + "[@" + attrName + "='" + attrValue + "']");
                }

                if (shouldInclude(locatorType, "css")) {
                    locators.add(tagName + "[" + attrName + "='" + attrValue + "']");
                }
            }

            // Other locator generation logic as before

            // ID-based locators
            if (shouldInclude(locatorType, "id") && !id.isEmpty()) {
                locators.add(id);
                locators.add("//" + tagName + "[@id='" + id + "']");
                locators.add("#" + id);
            }

            // Name-based locators
            if (shouldInclude(locatorType, "name") && !name.isEmpty()) {
                locators.add(name);
                locators.add("//" + tagName + "[@name='" + name + "']");
                locators.add(tagName + "[name='" + name + "']");
            }

            // Class-based locators
            if (shouldInclude(locatorType, "class") && !className.isEmpty()) {
                String[] classes = className.split("\\s+");
                for (String cls : classes) {
                    if (cls.isEmpty()) continue;
                    locators.add("." + cls);
                    locators.add("//" + tagName + "[contains(@class, '" + cls + "')]");
                }
            }
            
            

            // XPath based on text
            if (shouldInclude(locatorType, "linktext") && !text.isEmpty()) {
                locators.add("//" + tagName + "[text()='" + text + "']");
                locators.add("//" + tagName + "[contains(text(), '" + text + "')]");
            }

            // Combining two attributes
            if (shouldInclude(locatorType, "CombiningTwoAttribute")) {
            	List<String> keys = attributes.asList().stream()
            		    .map(Attribute::getKey)
            		    .filter(attr -> isUsefulAttr(attr) && isReliableAttribute(attr))
            		    .collect(Collectors.toList());
                for (int i = 0; i < keys.size(); i++) {
                    for (int j = i + 1; j < keys.size(); j++) {
                        locators.add("//" + tagName + "[@" + keys.get(i) + "='" + attributes.get(keys.get(i))
                                + "' and @" + keys.get(j) + "='" + attributes.get(keys.get(j)) + "']");
                    }
                }
            }

            // Indexing-based locators
            if (shouldInclude(locatorType, "indexing")) {
                String key = tagName + "|" + attributes.asList().stream()
                        .filter(attr -> !attr.getKey().equals("class"))
                        .sorted(Comparator.comparing(Attribute::getKey))
                        .map(attr -> attr.getKey() + "=" + attr.getValue()).collect(Collectors.joining(","));
                List<Element> group = elementGroups.getOrDefault(key, Collections.singletonList(targetElement));
                if (group.size() > 1) {
                    int index = group.indexOf(targetElement) + 1;
                    String xpath = "//" + tagName + attributes.asList().stream()
                            .filter(attr -> !attr.getKey().equals("class"))
                            .map(attr -> "[@" + attr.getKey() + "='" + attr.getValue() + "']")
                            .collect(Collectors.joining("")) + "[" + index + "]";
                    locators.add(xpath);
                }
            }

            // Child-based locators
            if (shouldInclude(locatorType, "text")) {
                Elements children = targetElement.getAllElements();
                for (Element child : children) {
                    if (child == targetElement) continue;
                    String childText = child.ownText().trim();
                    if (!childText.isEmpty()) {
                        locators.add("//" + tagName + "[.//" + child.tagName() + "[contains(text(), '" + childText + "')]]");
                    }
                }
            }
        }

        return locators;
    }

    // Check if the locator type should be included
    private static boolean shouldInclude(String type, String target) {
        return type == null || type.equalsIgnoreCase("all") || type.equalsIgnoreCase(target);
    }
    
    public static boolean isReliableAttribute(String attrName) {
        return !attrName.matches("(?i)(target|onclick|onchange|onmouseover|onmouseout|style|onload|onsubmit|onblur|onfocus|displayed|checked|enabled|scrollable|bounds|focused|clickable|selected|long-clickable|index|package|password|checkable|focusable)");
    }

    // Check if the attribute is useful for locating
    private static boolean isUsefulAttr(String key) {
        return !Arrays.asList("id", "name", "class", "target", "displayed", "checked", "enabled", "scrollable",
                "bounds", "focused", "clickable", "selected", "long-clickable", "index", "package", "password",
                "checkable", "focusable").contains(key);
    }


	
	public static void main(String[] args) throws Exception {
	
//      String htmlSnippet = "<a class=\"gb_X\" aria-label=\"Gmail \" data-pid=\"23\" href=\"https://mail.google.com/mail/?tab=rm&amp;authuser=0&amp;ogbl\" target=\"_top\">Gmail</a>";
      	
//      String   htmlSnippet = " <android.widget.Button index=\"0\" package=\"com.google.android.permissioncontroller\" class=\"android.widget.Button\" text=\"While using the app\" resource-id=\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[67,1711][1013,1812]\" displayed=\"true\" />\r\n"
//      		+ "                <android.widget.Button index=\"1\" package=\"com.google.android.permissioncontroller\" class=\"android.widget.Button\" text=\"Only this time\" resource-id=\"com.android.permissioncontroller:id/permission_allow_one_time_button\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[67,1857][1013,1958]\" displayed=\"true\" />\r\n"
//      		+ "                <android.widget.Button index=\"2\" package=\"com.google.android.permissioncontroller\" class=\"android.widget.Button\" text=\"Don't allow\" resource-id=\"com.android.permissioncontroller:id/permission_deny_button\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[67,2003][1013,2104]\" displayed=\"true\" />\r\n"
//      		+ "              </android.widget.LinearLayout>";
    
//		String htmlSnippet = "<button data-component=\"atoms-element-button-1\" data-hook=\"validate-email\" name=\"next\" type=\"button\"><span>Next</span></button>";
		
//		String htmlSnippet = "<input data-component=\"atoms-element-input-4\" class=\" o-form__element\" type=\"email\" name=\"email-address\" value=\"biswajit.sahoo@apmosys.com\" data-hook=\"email\">";
	
		    String htmlSnippet = """
		        <div>
		          <input id="email" name="userEmail" type="text" class="input-field" placeholder="Enter your email" data-test="emailInput">
		          <input name="userEmail" type="text" placeholder="Repeat email" class="input-field" data-test="emailInput">

		          <button class="btn primary" name="submitBtn" type="submit" data-action="submit-form">Submit</button>

		          <a href="#" class="link-class" id="linkId" name="linkName">Click Here</a>

		          <div class="container" data-role="main" data-view="home">
		            <span class="inner-text">Nested Text</span>
		          </div>

		          <label for="email" class="label-text" data-label-type="emailLabel">Email Address</label>

		          <p class="info" data-info="extra">Some paragraph text</p>
		          <p class="info" data-info="extra">Some paragraph text</p>

		          <section class="section-wrapper" data-section="intro">
		            <div class="content-block">
		              <strong>Bold Statement</strong>
		            </div>
		          </section>
		        </div>
		        """;

		System.out.println("All Locators:\n");
	    xpathPrintSequence(htmlSnippet, "all");

//	    System.out.println("\n\nOnly ID:\n");
//	    xpathPrintSequence(htmlSnippet, "id");
//
//	    System.out.println("\n\nOnly Class:\n");
//	    xpathPrintSequence(htmlSnippet, "class");
//
//	    System.out.println("\n\nOnly XPath:\n");
//	    xpathPrintSequence(htmlSnippet, "xpath");
//
//	    System.out.println("\n\nOnly CSS:\n");
//	    xpathPrintSequence(htmlSnippet, "css");
//
//	    System.out.println("\n\nOnly LinkText:\n");
//	    xpathPrintSequence(htmlSnippet, "linktext");
//
//	    System.out.println("\n\nOnly CombiningTwoAttribute:\n");
//	    xpathPrintSequence(htmlSnippet, "CombiningTwoAttribute");
//
//	    System.out.println("\n\nIndexing Locators:\n");
//	    xpathPrintSequence(htmlSnippet, "indexing");
//
//	    System.out.println("\n\nOnly Text-based Locators:\n");
//	    xpathPrintSequence(htmlSnippet, "text");
		}

		public static void xpathPrintSequence(String htmlSnippet, String locatorType) {
		    for (String locator : OptimisedGeneratelocatorCode.findLocators(htmlSnippet, locatorType)) {
		        System.out.println(locator);
		       identifyLocatorType(locator);
		    }
		
		    //get each locator one one element
		    //getTopLocators(OptimisedGeneratelocatorCode.findLocators(htmlSnippet, locatorType)).forEach(System.out::println);
		    
		    System.out.println("Locators Type 'ID' and LocatorsValue : " + locatorID);
		    System.out.println("Locators Type 'CSS' and LocatorsValue : " + locatorCss);
		    System.out.println("Locators Type 'XPATH' and LocatorsValue : " + locatorXpath);
		    System.out.println("Locators Type 'CombineXpath' and LocatorsValue : " + locatorCombinXpath);
////		    
//		    if(!locatorID.isEmpty() || locatorID != null) {
//		    	System.out.println("ID = " + (locatorID.size()>0? locatorID.get(0) : "No ID locator found"));
//		    }
//		    
//		    if(!locatorCss.isEmpty() || locatorCss != null) {
//		    	System.out.println("Css = " + (locatorCss.size()>=1? locatorCss.get(1) : locatorCss.get(0)));
//		    }
//		    
//		    if(!locatorXpath.isEmpty() || locatorXpath != null) {
//		    	System.out.println("Xpath = " + (locatorXpath.size()>=3? locatorXpath.get(3) : locatorXpath.get(2)));
//		    }
//		    
//		    if(!locatorCombinXpath.isEmpty() || locatorCombinXpath != null) {
//		    	System.out.println("CombinXpath = " + (locatorCombinXpath.size()>0? locatorCombinXpath.get(0) : "No CombinXpath locator found"));
//		    }
		}
		
		
		static List<String> locatorID = new ArrayList<String>();
		static List<String> locatorCss = new ArrayList<String>();
		static List<String> locatorXpath = new ArrayList<String>();
		static List<String> locatorCombinXpath = new ArrayList<String>();
		
		public static String identifyLocatorType(String locator) {

			// Count how many attributes (@) are present
		    long attrCount = locator.chars().filter(c -> c == '@').count();

		    if ((locator.contains("and") && attrCount >= 2) ) {
		        locatorCombinXpath.add(locator);
		        return "CombineXpath";

		    } else if (locator.startsWith("//") || locator.contains("[@") || locator.contains("text()") || locator.contains("contains(")) {
				locatorXpath.add(locator);
				return "XPath";
				
			} else if (locator.startsWith("#")) {
				locatorID.add(locator);
				return "ID";
				
			} else if (locator.contains("[")) {
				locatorCss.add(locator);
				return "CSS";
			} else {
				return "Invalid locator Type";
			}
		}

		
		
		public static List<String> getTopLocators(Set<String> locators) {
		    String idLocator = null;
		    String combineLocator = null;
		    String xpathLocator = null;
		    String cssLocator = null;
		    int i = 0;

		    for (String locator : locators) {
		        String type = identifyLocatorType(locator);

		        if (type.equals("ID") && idLocator == null) {
		            idLocator = locator;
		        } else if (type.equals("CombineXpath") && combineLocator == null) {
		            combineLocator = locator;
		        } else if (type.equals("XPath") && xpathLocator == null) {
		            xpathLocator = locator;
				} else if (type.equals("CSS") && cssLocator == null) {
					
					if (i == 2) {
						cssLocator = locator;
					} else {
						i = 2;
					}
				}

		        // Exit early if we already found all 4
		        if (idLocator != null && combineLocator != null && xpathLocator != null && cssLocator != null) {
		            break;
		        }
		    }

		    // Final list with priority
		    List<String> topLocators = new ArrayList<>();
		    if (idLocator != null) {
		        topLocators.add(idLocator);
		    }
		    if (combineLocator != null) {
		        topLocators.add(combineLocator);
		    }
		    if (xpathLocator != null) {
		        topLocators.add(xpathLocator);
		    }
		    if (cssLocator != null) {
		        topLocators.add(cssLocator);
		    }

		    return topLocators;
		}

		

	
	
}
