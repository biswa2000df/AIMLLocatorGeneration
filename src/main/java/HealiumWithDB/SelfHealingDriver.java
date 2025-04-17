package HealiumWithDB;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.sql.*;
import java.util.*;
import java.util.NoSuchElementException;
import java.security.MessageDigest;

public class SelfHealingDriver extends ChromeDriver {
    private final ElementSnapshotDB db;
    private final String testName;
    
    public SelfHealingDriver(String testName, String dbUrl, String dbUser, String dbPassword) throws SQLException {
        this.db = new ElementSnapshotDB(dbUrl, dbUser, dbPassword);
        this.testName = testName;
    }
    
    @Override
    public WebElement findElement(By by) throws NoSuchElementException {
        try {
            // First try original locator
            WebElement element = super.findElement(by);
            
            // If found, store snapshot
            try {
				storeElementSnapshot(element, by);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return element;
        } catch (NoSuchElementException e) {
            // If not found, try healing
            return healAndFindElement(by);
        }
    }
    
    private void storeElementSnapshot(WebElement element, By locator) throws SQLException {
        String domState = getPageSource();
        String attributesJson = getElementAttributesJson(element);
        String elementId = generateElementId(locator, domState);
        
        db.saveSnapshot(
            elementId,
            testName,
            locator.getClass().getSimpleName(),
            getLocatorValue(locator),
            domState,
            attributesJson
        );
    }
    
    private WebElement healAndFindElement(By brokenLocator) {
        try {
            String currentDom = getPageSource();
            ResultSet snapshots = db.findSnapshotsByTest(testName);
            
            while (snapshots.next()) {
                String originalLocatorType = snapshots.getString("original_locator_type");
                String originalLocatorValue = snapshots.getString("original_locator_value");
                
                if (matchesOriginalLocator(brokenLocator, originalLocatorType, originalLocatorValue)) {
                    // Try healed locator first if exists
                    String healedLocatorType = snapshots.getString("healed_locator_type");
                    String healedLocatorValue = snapshots.getString("healed_locator_value");
                    
                    if (healedLocatorType != null && healedLocatorValue != null) {
                        By healedLocator = createLocator(healedLocatorType, healedLocatorValue);
                        try {
                            return super.findElement(healedLocator);
                        } catch (NoSuchElementException e) {
                            // Healed locator didn't work, continue to healing
                        }
                    }
                    
                    // Perform healing
                    String oldDom = snapshots.getString("dom_state");
                    String attributesJson = snapshots.getString("element_attributes");
                    
                    WebElement healedElement = findSimilarElement(oldDom, currentDom, attributesJson);
                    if (healedElement != null) {
                        // Store the healed locator
                        By healedLocator = generateHealedLocator(healedElement);
                        db.updateHealedLocator(
                            snapshots.getString("element_id"),
                            healedLocator.getClass().getSimpleName(),
                            getLocatorValue(healedLocator)
                        );
                        return healedElement;
                    }
                }
            }
            throw new NoSuchElementException("Failed to heal locator: " + brokenLocator);
        } catch (SQLException e) {
            throw new RuntimeException("Database error during healing", e);
        }
    }
    
    private WebElement findSimilarElement(String oldDom, String currentDom, String attributesJson) {
        Document oldDomDoc = Jsoup.parse(oldDom);
        Document currentDomDoc = Jsoup.parse(currentDom);
        
        Map<String, String> attributes = parseAttributesJson(attributesJson);
        String tagName = attributes.get("tag");
        
        Elements candidates = currentDomDoc.select(tagName);
        for (Element candidate : candidates) {
            if (matchesAttributes(candidate, attributes)) {
                By healedLocator = generateHealedLocator(candidate);
                try {
                    return super.findElement(healedLocator);
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
        }
        return null;
    }
    
    private boolean matchesOriginalLocator(By locator, String locatorType, String locatorValue) {
        return locator.getClass().getSimpleName().equals(locatorType) &&
               getLocatorValue(locator).equals(locatorValue);
    }
    
    private boolean matchesAttributes(Element element, Map<String, String> targetAttributes) {
        for (Map.Entry<String, String> entry : targetAttributes.entrySet()) {
            if (!entry.getKey().equals("tag") && 
                !element.attr(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    private By generateHealedLocator(WebElement element) {
        String id = element.getAttribute("id");
        if (id != null && !id.isEmpty()) {
            return By.id(id);
        }
        
        String name = element.getAttribute("name");
        if (name != null && !name.isEmpty()) {
            return By.name(name);
        }
        
        return By.xpath(generateXPath(element));
    }
    
    private By generateHealedLocator(Element element) {
        if (!element.attr("id").isEmpty()) {
            return By.id(element.attr("id"));
        } else if (!element.attr("name").isEmpty()) {
            return By.name(element.attr("name"));
        } else {
            return By.xpath(generateXPath(element));
        }
    }
    
    private String generateXPath(WebElement element) {
        return (String) ((JavascriptExecutor) this).executeScript(
            "function getElementXPath(element) {" +
            "    if (element.id !== '') return '//*[@id=\"' + element.id + '\"]';" +
            "    if (element === document.body) return '/html/body';" +
            "    var ix = 0;" +
            "    var siblings = element.parentNode.childNodes;" +
            "    for (var i = 0; i < siblings.length; i++) {" +
            "        var sibling = siblings[i];" +
            "        if (sibling === element) return getElementXPath(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + (ix + 1) + ']';" +
            "        if (sibling.nodeType === 1 && sibling.tagName === element.tagName) ix++;" +
            "    }" +
            "}" +
            "return getElementXPath(arguments[0]);", element);
    }
    
    private String generateXPath(Element element) {
        List<String> path = new ArrayList<>();
        Element current = element;
        while (current.parent() != null) {
            int index = current.elementSiblingIndex() + 1;
            path.add(current.tagName() + "[" + index + "]");
            current = current.parent();
        }
        Collections.reverse(path);
        return "//" + String.join("/", path);
    }
    
    private String getElementAttributesJson(WebElement element) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("tag", element.getTagName());
        attributes.put("id", element.getAttribute("id"));
        attributes.put("name", element.getAttribute("name"));
        attributes.put("class", element.getAttribute("class"));
        attributes.put("text", element.getText());
        return new com.google.gson.Gson().toJson(attributes);
    }
    
    private Map<String, String> parseAttributesJson(String json) {
        return new com.google.gson.Gson().fromJson(json, 
            new com.google.gson.reflect.TypeToken<Map<String, String>>(){}.getType());
    }
    
    private String generateElementId(By locator, String domState) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = locator.toString() + testName + domState;
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate element ID", e);
        }
    }
    
    private String getLocatorValue(By locator) {
        String locatorStr = locator.toString();
        return locatorStr.substring(locatorStr.indexOf(":") + 2);
    }
    
    private By createLocator(String locatorType, String locatorValue) {
        switch (locatorType) {
            case "ById": return By.id(locatorValue);
            case "ByName": return By.name(locatorValue);
            case "ByClassName": return By.className(locatorValue);
            case "ByXPath": return By.xpath(locatorValue);
            case "ByCssSelector": return By.cssSelector(locatorValue);
            case "ByLinkText": return By.linkText(locatorValue);
            case "ByPartialLinkText": return By.partialLinkText(locatorValue);
            case "ByTagName": return By.tagName(locatorValue);
            default: throw new IllegalArgumentException("Unknown locator type: " + locatorType);
        }
    }
    
    @Override
    public void quit() {
        try {
            db.close();
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
        super.quit();
    }
}
