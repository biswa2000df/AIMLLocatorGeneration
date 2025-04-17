package AIandML;

import java.io.File;
import java.util.List;

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

public class DynamicXPathGenerator {

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

		driver.get("https://www.google.com");
		String gmailXPath = "//a[@class='gb_Xx']"; // Updated class name
		clickWithDynamicXPath(gmailXPath);

//              driver.navigate().back();

//              driver.quit();

	}

	public static void clickWithDynamicXPath(String originalXPath) throws InterruptedException {
		try {

			driver.findElement(By.xpath(originalXPath)).click();

		} catch (Exception e) {

//        	   Thread.sleep(3000);

			e.printStackTrace();

			System.out.println("Original XPath failed. Generating dynamic fallback...");
			// Get current page HTML
			String pageHtml = driver.getPageSource();
			Document doc = Jsoup.parse(pageHtml);
//             System.out.println(pageHtml);
//             logger.info(pageHtml + "\n\n");

			// Find the target element's approximate position (e.g., by partial attributes)
			Element targetElement = findClosestElement(doc, originalXPath);

//             logger.info(targetElement);

			// Generate new XPath dynamically
			newXPath = generateXPathFromElement(targetElement);
//
			logger.info(newXPath);

			// Retry with the new XPath
			driver.findElement(By.xpath(newXPath)).click();

		}
	}

	public static String generateXPathFromElement(Element element) {
		StringBuilder xpath = new StringBuilder();
		xpath.append("//").append(element.tagName());

		// Prioritize unique attributes
		if (element.hasAttr("id")) {
			xpath.append("[@id='").append(element.attr("id")).append("']");
		} else if (element.hasAttr("class")) {
			xpath.append("[contains(@class, '").append(element.attr("class").split(" ")[0]).append("')]");
		} else if (element.hasAttr("data-testid")) {
			xpath.append("[@data-testid='").append(element.attr("data-testid")).append("']");
		} else if (!element.text().isEmpty()) {
			xpath.append("[contains(text(), '").append(element.text().trim()).append("')]");
		} else {
			xpath.append("[@type='").append(element.attr("type")).append("']"); // Fallback
		}

		return xpath.toString();
	}

	public static Element findClosestElement(Document doc, String failedXPath) {
		// Parse the failed XPath to get clues (e.g., tag name, partial attributes)
		String tagName = failedXPath.split("//")[1].split("\\[")[0];

//            logger.info("Tagname = " + tagName);

		// Find all elements with the same tag (e.g., all buttons)
		List<Element> candidates = doc.select(tagName);

//            logger.info(candidates + "\n\n\n\n\n");

		// Heuristic: Pick the first element with text (e.g., "Login")
		for (Element el : candidates) {
//            	logger.info("element = " + el.attr("class") + "\n\n\n\n\n");
			logger.info("element = " + el + "\n");

			String str1 = extractClassName(failedXPath);

			if (sequenceSimilarityPercentage(str1, el.attr("class")) >= 65) {
				logger.info("elment matched = " + el);
				return el;
			}
			/*
			 * if (el.text().toLowerCase().contains("gmail")) { //
			 * logger.info("elment matched = " + el); return el; }
			 */
		}
		return candidates.get(0); // Fallback to first match
	}

	public static String extractClassName(String xpath) {
		if (xpath == null || xpath.isEmpty()) {
			return null;
		}

		String[] parts = xpath.split("@class='");
		if (parts.length > 1) {
			String classNamePart = parts[1];
			String[] classNameParts = classNamePart.split("']");
			if (classNameParts.length > 0) {
				return classNameParts[0];
			}
		}
		return null;
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
		String logFileName = "Demo.log";

		File f = new File(logFileName);

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
