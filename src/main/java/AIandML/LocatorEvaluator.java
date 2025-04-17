package AIandML;

import java.util.List;
import java.util.Set;

public class LocatorEvaluator {

	public String selectBestLocator(Set<String> locators) {
		String bestLocator = null;

		// Step 1: Prioritize ID-based locators
		for (String locator : locators) {
			String trimmedLocator = locator.trim(); // Trim the locator
			if (isIDBased(trimmedLocator)) {
				bestLocator = trimmedLocator;
				System.out.println("3");
				break;
			}
		}

		// Step 2: Check for Multi attribute Xpath locators
		if (bestLocator == null) {
			for (String locator : locators) {
				String trimmedLocator = locator.trim();
				if (isMultiAttributeXpathBased(trimmedLocator)) {
					bestLocator = trimmedLocator;
					System.out.println("4");
					break;
				}
			}
		}

		// Step 3: Check for simple and stable locators
		if (bestLocator == null) {
			for (String locator : locators) {
				String trimmedLocator = locator.trim();
				if (isSimpleAndStable(trimmedLocator)) {
					bestLocator = trimmedLocator;
					System.out.println("5");
					break;
				}
			}
		}

		// Step 4: Fallback to XPath with contains or text matching.
		if (bestLocator == null) {
			for (String locator : locators) {
				String trimmedLocator = locator.trim();
				if (isXPathMatch(trimmedLocator)) {
					bestLocator = trimmedLocator;
					break;
				}
			}
		}

		// Step 5: If no good locator found, return the best effort locator
		return bestLocator != null ? bestLocator : "No good locator found";
	}

	public boolean isIDBased(String locator) {
		return locator.startsWith("#") || locator.contains("@id=") || locator.contains("[@resource-id");
	}

	public boolean isMultiAttributeXpathBased(String locator) {
		return (locator.startsWith("//") && locator.contains("and"))
				|| (locator.startsWith("(//") && locator.contains("])[") || locator.contains(":nth-of-type")
						|| locator.contains("[@text"));
	}

	public boolean isSimpleAndStable(String locator) {
		return locator.startsWith("//") && locator.contains("contains") || locator.contains("text()");
	}

	public boolean isXPathMatch(String locator) {
		return  locator.contains("[@title") || locator.contains("[@name")
				|| locator.contains("[@href")  || locator.contains(".");
	}

//    public boolean isXPathMatch(String locator) {
//        // Check if it's an XPath with exact match for an attribute or class
//        return locator.contains("contains") && locator.contains("[class]");
//    }

	// Example of using the selectBestLocator method
	public static void main(String[] args) {
		LocatorEvaluator evaluator = new LocatorEvaluator();

		// Example list of locators (You can test with your actual locators)
//        List<String> locators = List.of(
//                "#submitButton",  // ID-based CSS Selector (unique and stable)
//                ".button-primary", // Class-based CSS Locator (can be common, not unique)
//                "//button[@title='Submit']", // Exact XPath with title attribute
//                "//button[contains(@class, 'submit')]",
//                "//input[@id='username']" // XPath with exact ID match
//        );

        Set<String> locators = Set.of("[next, //button[@data-component='atoms-element-button-1' and @data-hook='validate-email'], //button[@data-hook='validate-email'], button[data-hook='validate-email'], //button[@data-component='atoms-element-button-1' and @type='button'], button[name='next'], //button[@name='next'], button[data-component='atoms-element-button-1'], //button[@data-hook='validate-email' and @type='button'], //button[@data-component='atoms-element-button-1'], button[type='button'], //button[@type='button']]"
        		+ "");

		// Select the best locator from the list
        String bestLocator = evaluator.selectBestLocator(locators);
        System.out.println("Best Locator: " + bestLocator);
	}
}
