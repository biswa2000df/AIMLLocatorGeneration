package AIandML;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;



public class CaptureWebElementWhileAnAUtomation {

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\biswa\\eclipse-workspace\\BSA_MOBILE_FRAMEWORK\\BrowserDriver\\chromedriver.exe");

		// Initialize WebDriver
		WebDriver driver = new ChromeDriver();

	
			// Open any URL
//			driver.get("https://mail.apmosys.com/webmail/");
			driver.get("https://mmfsl--mfuat.sandbox.my.salesforce.com/");
			
			
//			driver.get("https://mmfsl--mfuat.sandbox.my.salesforce.com/");
			// JavaScript to capture and log the button's outer HTML when clicked
		/*	 String script = """
			            document.addEventListener('click', function(event) {
			                let elem = event.target;
			                console.log('Clicked element:', elem.outerHTML);
			                alert('Clicked element:\\n' + elem.outerHTML);
			            });
			        """;

			        ((JavascriptExecutor) driver).executeScript(script);   */
			
		/*	
			
			// 1. Inject JavaScript to capture click and store clicked element HTML
	        String jsListener = """
	            window.clickedElement = null;
	            document.addEventListener('click', function(event) {
	                window.clickedElement = event.target.outerHTML;
	            });
	        """;
	        ((JavascriptExecutor) driver).executeScript(jsListener);

	        System.out.println("Click any element on the page. Waiting...");

	        // 2. Poll every second to see if something was clicked
	        String lastElement = null;
	        for (int i = 0; i < 30; i++) {  // Wait up to 30 seconds
	            Object result = ((JavascriptExecutor) driver).executeScript("return window.clickedElement;");
	            if (result != null && !result.equals(lastElement)) {
	                lastElement = (String) result;
	                System.out.println("Clicked Element:\n" + lastElement);
//	                break;
	            }
	            Thread.sleep(1000); // Wait 1 sec
	        }
*/
			
			
			

			
			
	/*		
			 // Inject JS to track clicks in capturing phase
	        String script = """
	            window.clickedElement = null;
	            document.addEventListener('click', function(event) {
	                try {
	                    window.clickedElement = event.target.outerHTML;
	                } catch (e) {
	                    window.clickedElement = '[Unable to capture element]';
	                }
	            }, true);
	        """;
	        ((JavascriptExecutor) driver).executeScript(script);

	        System.out.println("Click anywhere. Buttons, inputs, etc...");

	        String last = "";

	        while (true) {
	            Object result = ((JavascriptExecutor) driver).executeScript("return window.clickedElement;");
	            if (result != null) {
	                String html = result.toString();
	                if (!html.equals(last)) {
	                    last = html;
	                    System.out.println("\nNew Clicked Element:\n" + html);

	                    // Reset
	                    ((JavascriptExecutor) driver).executeScript("window.clickedElement = null;");
	                }
	            }
	            Thread.sleep(500);
	        }  */
			
			
			
	/*		
			injectClickLogger(driver); // Inject once at start

	        int lastCount = 0;

	        while (true) {
	            // Get the clicked elements list
	            Object result = ((JavascriptExecutor) driver).executeScript("return window.clickedElements || [];");
	            if (result instanceof java.util.List<?> clicks) {
	                if (clicks.size() > lastCount) {
	                    for (int i = lastCount; i < clicks.size(); i++) {
	                        System.out.println("\nClicked Element:\n" + clicks.get(i));
	                    }
	                    lastCount = clicks.size();
	                }
	            }

	            Thread.sleep(300); // Polling interval
	        }
	    }

	    private static void injectClickLogger(WebDriver driver) {
	        String js = """
	            (function setupClickCapture() {
	                if (window._clickCaptureInstalled) return;
	                window._clickCaptureInstalled = true;

	                window.clickedElements = [];

	                document.addEventListener('click', function(event) {
	                    if (!event || !event.target) return;

	                    const el = event.target;
	                    const html = el.outerHTML;

	                    window.clickedElements.push(html);
	                    console.log('Captured:', html);
	                }, true);
	            })();
	        """;
	        ((JavascriptExecutor) driver).executeScript(js);
	    
*/
			
			int previousCount = 0;

	        while (true) {
	            try {
	                injectLogger(driver); // Inject click and input logger

	                Object result = ((JavascriptExecutor) driver).executeScript("return window.userActions || [];");
	                if (result instanceof List<?> entries) {
	                    if (entries.size() > previousCount) {
	                        for (int i = previousCount; i < entries.size(); i++) {
	                            Map<?, ?> entry = (Map<?, ?>) entries.get(i);
	                            String type = (String) entry.get("type");
	                            String html = (String) entry.get("html");
	                            String value = (String) entry.get("value");

	                            System.out.println("\n--- User Action: " + type.toUpperCase() + " ---");
	                            System.out.println(html);
	                            if ("input".equalsIgnoreCase(type) && value != null && !value.isEmpty()) {
	                                System.out.println("Typed Value: " + value);
	                            }
	                        }
	                        previousCount = entries.size();
	                    }
	                }
	            } catch (Exception e) {
	                System.out.println("[Error] " + e.getMessage());
	            }

	            Thread.sleep(300); // Adjust based on your need
	        }
	    }

	private static void injectLogger(WebDriver driver) {
	    String script = """
	        (function() {
	            try {
	                if (window._loggerInitialized) return;
	                window._loggerInitialized = true;

	                window.userActions = window.userActions || [];
	                window._lastClickedElement = null;

	                function attachListeners() {
	                    document.removeEventListener('mousedown', window._clickLogger, true);
	                    document.removeEventListener('input', window._inputLogger, true);

	                    // CLICK LOGGER (capture phase)
	                    window._clickLogger = function(e) {
	                        try {
	                            if (e && e.target && e.target.outerHTML) {
	                                const action = {
	                                    type: 'click',
	                                    html: e.target.outerHTML,
	                                    timestamp: Date.now()
	                                };
	                                window.userActions.push(action);
	                                window._lastClickedElement = action; // Save in case of navigation
	                            }
	                        } catch (err) {
	                            console.error('Click logger error:', err);
	                        }
	                    };
	                    document.addEventListener('mousedown', window._clickLogger, true);

	                    // INPUT LOGGER (final value only)
	                    window._inputLogger = function(e) {
	                        const tag = e.target.tagName.toLowerCase();
	                        if (tag === 'input' || tag === 'textarea') {
	                            clearTimeout(window._inputTimeout);
	                            window._inputTimeout = setTimeout(() => {
	                                window.userActions.push({
	                                    type: 'input',
	                                    html: e.target.outerHTML,
	                                    value: e.target.value,
	                                    timestamp: Date.now()
	                                });
	                            }, 700);
	                        }
	                    };
	                    document.addEventListener('input', window._inputLogger, true);
	                }

	                attachListeners();

	                // Ensure last click is saved before unload
	                window.addEventListener('beforeunload', function () {
	                    try {
	                        if (window._lastClickedElement) {
	                            window.userActions.push(window._lastClickedElement);
	                        }
	                    } catch (e) {
	                        console.error('[BeforeUnload Error]', e);
	                    }
	                });

	                // MutationObserver to re-attach
	                const observer = new MutationObserver(() => {
	                    attachListeners();
	                });
	                observer.observe(document, {
	                    childList: true,
	                    subtree: true
	                });

	                console.log('[Injected] Logger with MutationObserver and beforeunload hook.');
	            } catch (e) {
	                console.error('[Injection Error]', e);
	            }
	        })();
	    """;

	    ((JavascriptExecutor) driver).executeScript(script);
	}




	
	




	/*private static void injectClickLogger(WebDriver driver) {
	    String script = """
	        (function() {
	            try {
	                // Always rebind to avoid missing dynamic page rebuilds
	                document.removeEventListener('click', window._clickLogger, true);

	                window.clickedElements = window.clickedElements || [];
	                window._clickLogger = function(event) {
	                    try {
	                        if (event && event.target && event.target.outerHTML) {
	                            window.clickedElements.push(event.target.outerHTML);
	                        }
	                    } catch (err) {
	                        console.error('Click logger error:', err);
	                    }
	                };
	                document.addEventListener('click', window._clickLogger, true);
	                console.log('[Injected] Click logger attached.');
	            } catch (e) {
	                console.error('[Injection Failed]', e);
	            }
	        })();
	    """;

	    ((JavascriptExecutor) driver).executeScript(script);
	}*/
	
}
