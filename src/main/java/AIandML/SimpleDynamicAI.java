package AIandML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleDynamicAI {
	private static final String KNOWLEDGE_FILE = "knowledge_base.txt";
	private static Map<String, String> knowledgeBase = new HashMap<>();

	// Load the knowledge base from the file
	private static void loadKnowledgeBase() {
		try (BufferedReader reader = new BufferedReader(new FileReader(KNOWLEDGE_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=", 2); // Ensure only the first '=' is split
				if (parts.length == 2) {
					String normalizedKey = normalizeQuestion(parts[0].trim()); // Normalize keys
					knowledgeBase.put(normalizedKey, parts[1].trim());
				}
			}
		} catch (IOException e) {
			System.out.println("Knowledge base file not found. Starting with an empty base.");
		}
	}

	// Save the knowledge base to the file
	private static void saveKnowledgeBase() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(KNOWLEDGE_FILE))) {
			for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
				writer.write(normalizeQuestion(entry.getKey()) + "=" + entry.getValue()); // Ensure normalized key
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error saving the knowledge base.");
		}
	}

	// Normalize the question to handle minor variations (remove punctuation, lower
	// case, etc.)
	private static String normalizeQuestion(String question) {
		// Remove punctuation like question marks, exclamation marks, etc.
		question = question.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
		return question;
	}

	// Map common typos or abbreviations to correct terms
	private static String correctCommonTypos(String question) {
		Map<String, String> typoCorrections = new HashMap<>();
		typoCorrections.put("hat", "what");
		typoCorrections.put("jav", "java");
		typoCorrections.put("isnt", "is not");
		typoCorrections.put("im", "i am");
		typoCorrections.put("thank u", "thank you");
		typoCorrections.put("thanku", "thank you");

		// Correct common typos or abbreviations
		for (Map.Entry<String, String> entry : typoCorrections.entrySet()) {
//            question = question.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
			question = question.replaceAll("(?i)\\b" + entry.getKey() + "\\b", entry.getValue());

		}

		return question;
	}

	// Calculate Levenshtein Distance to compare similarity between strings
	private static int calculateLevenshteinDistance(String a, String b) {
		int[] costs = new int[b.length() + 1];
		for (int i = 0; i <= a.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= b.length(); j++) {
				if (i == 0) {
					costs[j] = j;
				} else {
					int newValue = (j == 0) ? i
							: Math.min(Math.min(costs[j] + 1, lastValue + 1),
									(a.charAt(i - 1) == b.charAt(j - 1)) ? lastValue : lastValue + 1);
					costs[j] = newValue;
				}
				lastValue = costs[j];
			}
		}
		return costs[b.length()];
	}

	// Find the most similar question from the knowledge base
	private static String getBestMatch(String question) {
		int minDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		for (String storedQuestion : knowledgeBase.keySet()) {
			int distance = calculateLevenshteinDistance(normalizeQuestion(question), storedQuestion);
			if (distance < minDistance) {
				minDistance = distance;
				bestMatch = storedQuestion;
			}
		}
		return bestMatch;
	}

	// Process the user's question and return an answer
	public static String getAnswer(String question) {
		String correctedQuestion = correctCommonTypos(normalizeQuestion(question)); // Normalize input
		return knowledgeBase.getOrDefault(correctedQuestion, null); // Direct lookup
	}

	// Teach the AI a new answer
	public static void learnNewAnswer(String question, String answer) {
		String normalizedQuestion = normalizeQuestion(question);
		knowledgeBase.put(normalizedQuestion, answer);
		saveKnowledgeBase(); // Save the new knowledge to the file
	}

	// Check if the AI should respond based on certain keywords (e.g., "hello",
	// "help")
	public static String handleKeywordResponse(String question) {
		Map<String, String> keywordResponses = new HashMap<>();
		keywordResponses.put("hello", "Hi there! How can I help you?");
		keywordResponses.put("help", "I am here to help! What do you need assistance with?");
		keywordResponses.put("thank you", "You're welcome! Feel free to ask anything.");

		// Normalize and correct typos before checking for a match
		String normalizedQuestion = normalizeQuestion(correctCommonTypos(question));

		return keywordResponses.getOrDefault(normalizedQuestion, null);
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		loadKnowledgeBase(); // Load the knowledge base from the file

		System.out.println("Ask me anything! (type 'exit' to quit)");

		while (true) {
			System.out.print("You: ");
			String question = scanner.nextLine().trim();

			if (question.equalsIgnoreCase("exit")) {
				break;
			}

			// First check if the AI knows the answer from the knowledge base
			String answer = getAnswer(question);

			if (answer != null) {
				System.out.println("AI: " + answer);
			} else {
				// Check if the question matches a known keyword (e.g., hello, help)
				answer = handleKeywordResponse(question);

				if (answer != null) {
					System.out.println("AI: " + answer);
				} else {
					// If the AI doesn't know, ask the user to teach it
					System.out.println("AI: I don't know the answer to that question.");
					System.out.print("Please tell me the answer: ");
					String userAnswer = scanner.nextLine().trim();
					learnNewAnswer(question, userAnswer);
					System.out.println("AI has learned the new answer!");
				}
			}
		}

		scanner.close();
	}
}
