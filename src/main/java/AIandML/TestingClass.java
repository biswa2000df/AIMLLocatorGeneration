package AIandML;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestingClass {


	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
//		System.setProperty("webdriver.chrome.driver",
//				"C:\\Users\\biswa\\eclipse-workspace\\BSA_MOBILE_FRAMEWORK\\BrowserDriver\\chromedriver.exe");
//         ChromeOptions option = new ChromeOptions();
//         option.addArguments("--remote-allow-origins=*");
	WebDriver driver = new ChromeDriver();
	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	
	driver.get("https://mail.apmosys.com/webmail/");
	String gmailXPath = "//button[@name=\"next\"]"; // Updated class name
	
	WebElement targetElement = driver.findElement(By.xpath(gmailXPath));
	
	
	
	
	
	 // Get the full HTML of the target element
    String targetHtml = targetElement.getAttribute("outerHTML");
    System.out.println("Target Element HTML: " + targetHtml);
   
    
    
    System.out.println(targetElement.getLocation());

    // **Get the Parent Element**
    WebElement parentElement = targetElement.findElement(By.xpath("..")); // ".." navigates to the parent
    String parentHtml = parentElement.getAttribute("outerHTML");
    System.out.println("Parent Element HTML: " + parentHtml);

    // **Get Child Elements**
    java.util.List<WebElement> childElements = targetElement.findElements(By.xpath(".//*"));
    System.out.println("Number of Child Elements: " + childElements.size());
    for (int i = 0; i < childElements.size(); i++) {
        WebElement child = childElements.get(i);
        String childHtml = child.getAttribute("outerHTML");
        System.out.println("Child Element " + (i + 1) + " HTML: " + childHtml);
        // You can further inspect the attributes or text of each child element
    }
    
    
    

    // **Finding Siblings using XPath directly (Alternative)**

    // **Next Sibling:**
/*    try {
        WebElement nextSibling = driver.findElement(By.xpath("//button[@name=\"next\"]/following-sibling::*"));
        System.out.println("\nNext Sibling (if any):");
        System.out.println("  " + nextSibling.getTagName() + " - " + nextSibling.getAttribute("outerHTML") + "...");
    } catch (org.openqa.selenium.NoSuchElementException e) {
        System.out.println("\nNo next sibling found.");
    }

    // **Previous Sibling:**
    try {
        WebElement previousSibling = driver.findElement(By.xpath("//button[@name=\"next\"]/preceding-sibling::*[1]"));
        System.out.println("\nPrevious Sibling (if any):");
        System.out.println("  " + previousSibling.getTagName() + " - " + previousSibling.getAttribute("outerHTML")+ "...");
    } catch (org.openqa.selenium.NoSuchElementException e) {
        System.out.println("\nNo previous sibling found.");
    }
    */
   
	
	
	//chatgpt
	
	/*

    JavascriptExecutor js = (JavascriptExecutor) driver;

    // Get outerHTML of clicked element
    String clickedHtml = (String) js.executeScript("return arguments[0].outerHTML;", element);
    System.out.println("Clicked Element HTML:\n" + clickedHtml);

    // Get outerHTML of parent element
    String parentHtml = (String) js.executeScript(
        "return arguments[0].parentElement ? arguments[0].parentElement.outerHTML : null;", element);
    System.out.println("Parent Element HTML:\n" + parentHtml);

    // Get outerHTML of each child
    StringBuilder childHtmlBuilder = new StringBuilder();
    Long childCount = (Long) js.executeScript("return arguments[0].children.length;", element);
    for (int i = 0; i < childCount; i++) {
        String childHtml = (String) js.executeScript(
            "return arguments[0].children[" + i + "].outerHTML;", element);
        childHtmlBuilder.append("Child ").append(i + 1).append(":\n").append(childHtml).append("\n");
    }
    System.out.println("Child Elements HTML:\n" + childHtmlBuilder.toString());
	
	*/
	
	

    // Print the full HTML to the console
//    System.out.println("Full HTML of the element: " + fullHtml);
	
    
    
    Map<String, Object> elementData = new HashMap<>();

    elementData.put("outerHTML", targetElement.getAttribute("outerHTML"));
    elementData.put("xpath", gmailXPath);
    elementData.put("tagName", targetElement.getTagName());
    elementData.put("textContent", targetElement.getText());
    elementData.put("location", targetElement.getLocation());
    elementData.put("size", targetElement.getSize());
    elementData.put("isDisplayed", targetElement.isDisplayed());
    elementData.put("isEnabled", targetElement.isEnabled());

    // Capture all attributes
    Map<String, String> attributes = new HashMap<>();
    JavascriptExecutor js = (JavascriptExecutor) driver;
    String getAttrsScript = 
        "var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) " +
        "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;";
    attributes = (Map<String, String>) js.executeScript(getAttrsScript, targetElement);

    elementData.put("attributes", attributes);

    // Convert map to JSON (optional for DB)
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(elementData);
    System.out.println(json);
    
    
    for(Map.Entry m : elementData.entrySet()){    
        System.out.println(m.getKey()+" "+m.getValue());    
       }  



    
    
    String pageSource = driver.getPageSource();
    
   

    // Parse with Jsoup (HTML parser) to find similar elements
    Document doc = Jsoup.parse(pageSource);
    Elements buttons = doc.getElementsByTag("button");
    
    System.out.println(buttons);
    
    
    System.out.println(targetElement.getDomAttribute("name"));
    
    WebElement element = driver.findElement(By.xpath("//button[@data-hook='validate-email' and @type='button']"));
    
//    System.out.println(element.getShadowRoot());
    System.out.println(element.getSize());
    System.out.println(element.getLocation());
    
    
    int storedX = 380;
    int storedY = 296;

    String script =
        "var x = " + storedX + ", y = " + storedY + ";" +
        "var minDist = Number.MAX_VALUE;" +
        "var closest = null;" +
        "var all = document.querySelectorAll('*');" +
        "for (var i = 0; i < all.length; i++) {" +
        "  var rect = all[i].getBoundingClientRect();" +
        "  var cx = rect.left + rect.width / 2;" +
        "  var cy = rect.top + rect.height / 2;" +
        "  var dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));" +
        "  if (dist < minDist) { minDist = dist; closest = all[i]; }" +
        "}" +
        "return closest;";

    WebElement healedElement = (WebElement) ((JavascriptExecutor) driver).executeScript(script);
    String outerHTML = healedElement.getAttribute("outerHTML");
    System.out.println("Healed Element by Position:\n" + outerHTML);
    
	driver.quit();

		
	}
	
}