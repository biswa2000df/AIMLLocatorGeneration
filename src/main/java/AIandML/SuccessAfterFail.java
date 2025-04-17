package AIandML;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SuccessAfterFail {

	final static Logger logger = LogManager.getLogger(DynamicXPathGenerator.class.getName());

	static WebDriver driver;
	static String newXPath;

	public static void main(String[] args) throws InterruptedException {

		configureLog4j();

		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\biswa\\eclipse-workspace\\BSA_MOBILE_FRAMEWORK\\BrowserDriver\\chromedriver.exe");
//         ChromeOptions option = new ChromeOptions();
//         option.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver();
//
		try {

			driver.get("https://ishine.apmosys.com/?_gl=1*m82zdv*_ga*ODM0MzcwNzkwLjE3NDMzNDA2OTc.*_ga_HBXM8LSCLL*MTc0MzM0MDY5Ni4xLjEuMTc0MzM0MTA2Mi4wLjAuMA..#/login");
			String gmailXPath = "//button[contains(text(),'SIGN INnn')]"; // Updated class name
//			Thread.sleep(5000);
			String newXpath = clickWithDynamicXPath(gmailXPath);

			if (newXpath != null || !newXpath.isEmpty()) {
				driver.findElement(By.xpath(newXpath)).click();
			}

			/*
			 * driver.navigate().back();
			 *
			 * String aboutXpath = "//a[text()='Abouts']"; newXpath =
			 * clickWithDynamicXPath(aboutXpath);
			 *
			 * if(newXpath != null || !newXpath.isEmpty()) {
			 * driver.findElement(By.xpath(newXpath)).click(); }
			 *
			 * String productXpath = "//a[text()='Productss']"; newXpath =
			 * clickWithDynamicXPath(productXpath);
			 *
			 * if(newXpath != null || !newXpath.isEmpty()) {
			 * driver.findElement(By.xpath(newXpath)).click(); }
			 */

		} finally {
//			driver.quit();
		}

	}

	public static String clickWithDynamicXPath(String originalXPath) throws InterruptedException {
		try {
			driver.findElement(By.xpath(originalXPath)).click();

		} catch (Exception e) {

			System.out.println("Original XPath failed. Generating dynamic fallback...");
			e.printStackTrace();

			// Get current page HTML
			String pageHtml = driver.getPageSource();
			Document doc = Jsoup.parse(pageHtml);
//             System.out.println(pageHtml);
//             logger.info(pageHtml + "\n\n");

			// Find the target element's approximate position (e.g., by partial attributes)
			String targetElement = findClosestElement(doc, originalXPath);

			Set<String> generatedDynamicLocator = UniqueDynamicXpathGenerator.findLocators(targetElement, "all");

//             for(String generateLocator: generatedDynamicLocator) {
//            	 System.out.println(generateLocator);
//             }

			LocatorEvaluator locatorEvaluator = new LocatorEvaluator();
			String anUniqueLocator = locatorEvaluator.selectBestLocator(generatedDynamicLocator);

			System.out.println("The Best Unique Locator = " + anUniqueLocator);

			return anUniqueLocator;

//             logger.info(targetElement);

			// Generate new XPath dynamically
//             newXPath = generateXPathFromElement(targetElement);
//
//             logger.info(newXPath);

			// Retry with the new XPath
//             driver.findElement(By.xpath(anUniqueLocator)).click();

		}
		return null;
	}

	public static String findClosestElement(Document doc, String failedXPath) {

//        	List<String> htmlElementMatches = new ArrayList<>();
		String htmlElementMatches = null;

		// Parse the failed XPath to get clues (e.g., tag name, partial attributes)
		String tagName = failedXPath.split("//")[1].split("\\[")[0];

		logger.info("Tagname = " + tagName + "\n\n\n");

		// Find all elements with the same tag (e.g., all buttons)
		List<Element> candidates = doc.select(tagName);

//            logger.info(candidates + "\n\n\n\n\n");

		// Heuristic: Pick the first element with text (e.g., "Login")
		for (Element el : candidates) {
//            	logger.info("element = " + el.attr("class") + "\n\n\n\n\n");

			String LocatorFormat = matchLocatorFormat(failedXPath);
//            	logger.info("Final locator format = " + LocatorFormat + "\n\n");

			if (LocatorFormat.equalsIgnoreCase("id") || LocatorFormat.equalsIgnoreCase("class")
					|| LocatorFormat.equalsIgnoreCase("name")) {
				if (el.hasAttr(LocatorFormat)) {
//            	logger.info("element = " + el + "\n");

					String failedXpathClassName = extractFailedXpathAttributeValue(failedXPath, "@"+LocatorFormat+"='");

//            		logger.info("attr class = " + className);
//            		logger.info("Failed xpath class name = " + failedXpathClassName);

					if (failedXpathClassName.contains(el.attr(LocatorFormat)) || el.attr(LocatorFormat).contains(failedXpathClassName)) {
						htmlElementMatches = htmlElementMatches + el.toString();
						logger.info("class matched html Element = " + el + "\n");
						return htmlElementMatches;
					}

				}
			}

			if (LocatorFormat.equalsIgnoreCase("text") || LocatorFormat.equalsIgnoreCase("containsText")) {
				// for containsText "text\\(\\),\\'"
				
				String textSplit = "";
				if(LocatorFormat.equalsIgnoreCase("text")) {
					textSplit = "text\\(\\)=\'";
				}
				if(LocatorFormat.equalsIgnoreCase("containsText")) {
					textSplit = "text\\(\\),\\'";
				}
				

				String failedXpathText = extractFailedXpathAttributeValue(failedXPath, textSplit);
//            		logger.info("Failed xpath Text = " + failedXpathText + "\n\n\n\n");

//            		logger.info("Html ELement = " + el + "\n\n");
				logger.info("Html element text = [" + el.text() + "]" + "\n\n");
				logger.info("failedXpathText element text = [" + failedXpathText + "]" + "\n\n");

				if (!el.text().trim().isEmpty()) {
					if (failedXpathText.contains(el.text().trim())) {
						htmlElementMatches = htmlElementMatches + el.toString();
						logger.info("Text matched html Element = " + el + "\n");
						return htmlElementMatches;
					}
				}

			}

			if (LocatorFormat.equalsIgnoreCase("combiningAttribute")) {

//            		logger.info(" combine attribute "+"\n\n\n\n");

				// for containsIDAnd String splitText = "='";

				Map<String, String> failedXpathText = extractFailedXpathAttributeValueForCombineAttribute(failedXPath);

				List<String> attributeKey = new ArrayList<>();
				List<String> attributeValue = new ArrayList<>();

				for (Map.Entry<String, String> entry : failedXpathText.entrySet()) {

					String FailedXpathAttributeKey = entry.getKey();
					String FailedXpathAttributeValue = entry.getValue();
					attributeKey.add(FailedXpathAttributeKey);
					attributeValue.add(FailedXpathAttributeValue);

				}

				logger.info(" combine attribute  Attribute1 and Attribute2 are {" + attributeKey.get(0) + ", "
						+ attributeKey.get(1) + "}");
				logger.info(" combine attribute  value1 and value2 are {" + attributeValue.get(0) + ", "
						+ attributeValue.get(1) + "}");
				logger.info(" combine attribute  element attribute {" + el.attr(attributeKey.get(0)) + ", "
						+ el.attr(attributeKey.get(1)) + "}");

				logger.info("Html Element = " + el + "\n\n");
				logger.info("Html element text = [" + el.hasAttr("title") + "]" + "\n\n");
			
//				logger.info(attributeKey + "  " + attributeValue );

//            		logger.info("Html element text = [" + el.text() + "]" + "\n\n");

				logger.info("Html Element = " + el + "\n\n");
				
					if (el.hasAttr(attributeKey.get(1)) && attributeValue.get(1).contains(el.attr(attributeKey.get(1))) && el.hasAttr(attributeKey.get(0)) && attributeValue.get(0).contains(el.attr(attributeKey.get(0)))){
						htmlElementMatches = htmlElementMatches + el.toString();
						logger.info("class matched html Element = " + el + "\n");
						return htmlElementMatches;
					}
				

			}

			if (LocatorFormat.equalsIgnoreCase("containsClass") || LocatorFormat.equalsIgnoreCase("containsId")
					|| LocatorFormat.equalsIgnoreCase("containsName")) {
				// for containsID split = "@class, '"
				// for containsText "text\\(\\),\\ '"
				
				String locatorAttribute = "";
				
				if(LocatorFormat.equalsIgnoreCase("containsClass") ) {
					locatorAttribute = "class";
				}
				if(LocatorFormat.equalsIgnoreCase("containsId") ) {
					locatorAttribute = "id";
				}
				if(LocatorFormat.equalsIgnoreCase("containsName") ) {
					locatorAttribute = "name";
				}
				
				logger.info("locatorAttribute = " + locatorAttribute + "\n");
				
				
				if (el.hasAttr(locatorAttribute)) {
					logger.info("locatorAttribute is present in the element = " + el.hasAttr(locatorAttribute) + "\n");

					String failedXpathValue = extractFailedXpathAttributeValueForContainsFormat(failedXPath, "@"+locatorAttribute+",'");
            		logger.info("Failed xpath Text = " + failedXpathValue + "\n");

            		logger.info("Html ELement = " + el + "\n\n");
//					logger.info("Html element text = [" + el.text() + "]" + "\n\n");

					
					if (failedXpathValue.contains(el.attr(locatorAttribute))) {
						
							htmlElementMatches = htmlElementMatches + el.toString();
							logger.info("Text matched html Element = " + el + "\n");
							return htmlElementMatches;
						
					}
				}

			}

			

//
//            	String str1 = extractClassName(failedXPath);
//
//            	if (sequenceSimilarityPercentage(str1, el.attr("class")) >= 65) {
//            		logger.info("elment matched = " + el);
//            		 return el;
//        		}
//               /* if (el.text().toLowerCase().contains("gmail")) {
////                	logger.info("elment matched = " + el);
//                    return el;
//                }*/
		}
		return htmlElementMatches; // Fallback to first match
	}

	public static String matchLocatorFormat(String locatorFormat) {

		if (locatorFormat.contains("[@class=")) {
			return "class";
		}

		if (locatorFormat.contains("[text()=")) {
			return "text";
		}

		if (locatorFormat.contains("[@id=")) {
			return "id";
		}

		if (locatorFormat.contains("[@name=")) {
			return "name";
		}

		if (locatorFormat.contains(" and ")) {
			return "combiningAttribute";
		}

		if (locatorFormat.contains("[contains(@class")) {
			return "containsClass";
		}

		if (locatorFormat.contains("[contains(@id")) {
			return "containsId";
		}

		if (locatorFormat.contains("[contains(@name")) {
			return "containsName";
		}

		if (locatorFormat.contains("[contains(text()")) {
			return "containsText";
		}

		return null;
	}

	public static String extractFailedXpathAttributeValue(String xpath, String splitText) {
		if (xpath == null || xpath.isEmpty()) {
			return null;
		}

		String[] parts = xpath.split(splitText);
		if (parts.length > 1) {
			String classNamePart = parts[1];
			String[] classNameParts = classNamePart.split("'");
			if (classNameParts.length > 0) {
				return classNameParts[0];
			}
		}
		return null;
	}

	public static String extractFailedXpathAttributeValueForContainsFormat(String xpath, String splitText) {
		if (xpath == null || xpath.isEmpty()) {
			return null;
		}

		String[] parts = xpath.split(splitText);
		if (parts.length > 1) {
			String classNamePart = parts[1];
			String[] classNameParts = classNamePart.split("'");
			if (classNameParts.length > 0) {
				return classNameParts[0];
			}
		}
		return null;
	}

	public static Map<String, String> extractFailedXpathAttributeValueForCombineAttribute(String xpath) {

		Map<String, String> result = new HashMap<>();

		Pattern attributePattern = Pattern.compile("@([^=]+)='([^']+)'");
		Matcher attributeMatcher = attributePattern.matcher(xpath);

		while (attributeMatcher.find()) {
			String attributeName = attributeMatcher.group(1);
			String attributeValue = attributeMatcher.group(2);
			result.put(attributeName, attributeValue);
		}

		if (result.isEmpty()) {
			return null;
		} else {
			return result;
		}

	}

	public static double sequenceSimilarityPercentage(String str1, String str2) {
		if (str1 == null || str2 == null) {
			System.out.println("1st if");
			return 0.0;
		}

		if (str1.isEmpty() && str2.isEmpty()) {
			System.out.println("2nd if");
			return 100.0;
		}

		if (str1.isEmpty() || str2.isEmpty()) {
			System.out.println("3rd if");
			return 0.0;
		}

		int matches = 0;
		int index1 = 0;
		int index2 = 0;

		while (index1 < str1.length() && index2 < str2.length()) {
			if (str1.charAt(index1) == str2.charAt(index2)) {
				matches++;
				index1++;
				index2++;
			} else {
				index1++; // Skip char in str1 if no match.

			}
		}

		// System.out.println( "Matches = " + matches);

		int extraletter = str1.length() - matches;

		// System.out.println("extra letter = " + extraletter);

		// System.out.println(((double) matches / str1.length()) * 100.0);

		return ((double) matches / str1.length()) * 100.0;
	}

	public static void configureLog4j() {
		// Get the current LoggerContext
		LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);

		// Get the current Log4j configuration
		Configuration config = loggerContext.getConfiguration();

		// Set the root logger level to DEBUG
		final LoggerConfig rootLoggerConfig = config.getRootLogger();
		rootLoggerConfig.setLevel(org.apache.logging.log4j.Level.INFO); // HERE ALSO TO CHANGE THE DEBUG OR INFO OR ALL
																		// OR ERROR OR WARN ETC MODE,

		// Remove any existing appenders (including console appender)
		rootLoggerConfig.getAppenders().forEach((name, appender) -> {
			rootLoggerConfig.removeAppender(name);
			appender.stop();
		});

		// Create a FileAppender with a dynamically generated file name
		String logFileName = "LOG.log";

		File f = new File(logFileName);

		if (f.exists()) {
			f.delete();
		}

		FileAppender appender = FileAppender.newBuilder().setName("File").withFileName(logFileName)
//                .withAppend(false)//here  write the append false me u can not append the new run logs only overwrite if u want then make false to true
				.withAppend(true).setLayout(PatternLayout.newBuilder()
						.withPattern("%d{yyyy-MM-dd HH:mm:ss} %-5level %C{1} - %msg%n").build())
				.build();

		// Add the appender to the configuration
		appender.start();
		config.addAppender(appender);

		// Update the LoggerConfig to use the new appender
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.addAppender(appender, null, null);

		// Update the configuration
		config.getRootLogger().addAppender(appender, null, null);

		// Update the Log4j context
		loggerContext.updateLoggers();
//		System.out.println("FIle and log ");

		// Log a message indicating the new log file
		// logger.info("Logging to dynamically created file: {}", logFileName);
	}

}
