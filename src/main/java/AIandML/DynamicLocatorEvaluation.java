package AIandML;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicLocatorEvaluation {


	public static boolean isIDBased(String locator) {

		return locator.startsWith("#") ||  locator.contains("[@resource-id");
	}

	public static boolean isMultiAttributeXpathBased(String locator) {
		return (locator.startsWith("//") && locator.contains("and"))
				|| (locator.startsWith("(//") && locator.contains("])[") || locator.contains(":nth-of-type"));
	}
	
	public static boolean isTextBasedXpath(String locator) {
	    return locator.contains("[contains(") || locator.contains("text()") || 
	           locator.contains("contains(text()") || (locator.startsWith("//") && locator.contains("[.//"));
	}

	
	private static boolean isStableXpath(String locator) {
	    return locator.contains("@id") || locator.contains("@name") || locator.contains("@title") || locator.contains("[@href") || locator.contains("@name=") || locator.contains("@placeholder=") || locator.contains("@class=") || locator.contains("."); 
	}

//	public boolean isXPathMatch(String locator) {
//		return  locator.contains("[@title") || locator.contains("[@href")  ;
//	}
	
	
	private static int getLocatorScore(String locator) {
	    int score = 0;

	    if (isIDBased(locator)) score += 100;
	    if (isMultiAttributeXpathBased(locator)) score += 80;
	    if (isStableXpath(locator)) score += 60;
	    if (isTextBasedXpath(locator)) score += 40;
	   // if (isStableCssSelector(locator)) score += 40;
	    if (locator.length() <= 100) score += 20; // Readability bonus

	    return score;
	}
	
	public static List<String> selectTopLocators(Set<String> locators, int topN) {
	    List<String> sortedLocators = locators.stream()
	        .map(locator -> new AbstractMap.SimpleEntry<>(locator, getLocatorScore(locator.trim())))
	        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) // Descending
	        .map(Map.Entry::getKey)
	        .limit(topN)
	        .collect(Collectors.toList());

	    System.out.println("Top " + topN + " selected locators:");
	    sortedLocators.forEach(System.out::println);

	    return sortedLocators;
	}



	
}
