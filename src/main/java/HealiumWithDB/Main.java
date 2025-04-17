package HealiumWithDB;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Main {
    public static void main(String[] args) {
        // Database connection details
        String dbUrl = "jdbc:postgresql://localhost:5432/healenium_db";
        String dbUser = "postgres";
        String dbPassword = "postgres";
        
        try {
            // First test run - stores snapshots
            SelfHealingDriver driver1 = new SelfHealingDriver("LoginTest", dbUrl, dbUser, dbPassword);
            driver1.get("https://mail.apmosys.com/webmail/");
            driver1.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            
            // This will be stored in the database
            WebElement username1 = driver1.findElement(By.xpath("//button[@name='next']"));
            
            username1.click();
            driver1.quit();
            
            // Second test run - uses healing if needed
            SelfHealingDriver driver2 = new SelfHealingDriver("LoginTest", dbUrl, dbUser, dbPassword);
            driver2.get("https://mail.apmosys.com/webmail/");
            driver2.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            
            // If element changed, will heal automatically
            WebElement username2 = driver2.findElement(By.xpath("//button[@name='nextttt']"));
            username2.click();
            
            driver2.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
