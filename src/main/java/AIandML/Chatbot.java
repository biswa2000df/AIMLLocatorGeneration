package AIandML;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Chatbot {

	LocatorEvaluator evaluator = new LocatorEvaluator();
	

	public String getResponse(String userInput) {
		if (userInput.toLowerCase().contains("select")) {
			try {
				// Improved bracket extraction
				int startBracket = userInput.indexOf('[');
				int endBracket = userInput.lastIndexOf(']');

				if (startBracket == -1 || endBracket == -1) {
					return "Error: No locators found between brackets";
				}

				String locatorsString = userInput.substring(startBracket + 1, endBracket).trim();
         //       System.out.println("Raw locators string: '" + locatorsString + "'");

				Set<String> locators = parseLocators(locatorsString);
             //   System.out.println("Parsed locators: " + locators);

				if (locators.isEmpty()) {
					return "Error: No valid locators found";
				}

//				String bestLocator = evaluator.selectBestLocator(locators);
			List<String>	bestLocator = DynamicLocatorEvaluation.selectTopLocators(locators, 50);
			
				return "The best locator is: " + bestLocator + bestLocator.size();

			} catch (Exception e) {
				return "Error parsing locators: " + e.getMessage();
			}
		}
		return "Sorry, I don't understand that question.";
	}

	private Set<String> parseLocators(String locatorsString) {
		Set<String> locators = new HashSet<>();
		int bracketLevel = 0;
		boolean inQuotes = false;
		char quoteChar = '\0';
		StringBuilder currentLocator = new StringBuilder();

		for (int i = 0; i < locatorsString.length(); i++) {
			char c = locatorsString.charAt(i);

			// Handle quotes
			if ((c == '\'' || c == '"') && !inQuotes) {
				inQuotes = true;
				quoteChar = c;
			} else if (c == quoteChar && inQuotes) {
				inQuotes = false;
				quoteChar = '\0';
			}

			// Track bracket levels
			if (c == '[')
				bracketLevel++;
			if (c == ']')
				bracketLevel--;

			// Split only at top-level commas
			if (c == ',' && bracketLevel == 0 && !inQuotes) {
				String locator = currentLocator.toString().trim();
				if (!locator.isEmpty()) {
					locators.add(locator);
				}
				currentLocator.setLength(0);
			} else {
				currentLocator.append(c);
			}
		}

		// Add the last locator
		String lastLocator = currentLocator.toString().trim();
		if (!lastLocator.isEmpty()) {
			locators.add(lastLocator);
		}

		return locators;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Chatbot chatbot = new Chatbot();

		System.out.println("Hello! I'm your chatbot. How can I help you today?");
		while (true) {
			System.out.print("You: ");
			String userInput = scanner.nextLine();

			if (userInput.equalsIgnoreCase("Thank you")) {
				System.out.println("Welcome!");
				break;
			}

			if (userInput.equals("exit")) {
				System.out.println("Goodbye!");
				break;
			}

			String response = chatbot.getResponse(userInput);
			System.out.println("AI: " + response);
		}
		scanner.close();
	}

//    [//android.widget.button[@text='While using the app'], //android.widget.button[@resource-id='com.android.permissioncontroller:id/permission_allow_foreground_only_button'], //android.widget.button[contains(@class, 'android.widget.Button')], .android.widget.Button ]
}