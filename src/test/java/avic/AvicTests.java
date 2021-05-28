package avic;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {
    WebDriver driver;

    @BeforeTest
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test
    public void checkThatAllPricesAreLessThan40000WhenWrite40000InMaxFieldOfPriceFilter() {
        driver.findElement(xpath("//div[@class='category-items--left']//a[text()='iPhone 12 Pro Max']")).click();
        WebElement maxFieldOfPriceFilter = driver.findElement(xpath("//input[contains(@class, 'form-control-max')]"));
        maxFieldOfPriceFilter.clear();
        maxFieldOfPriceFilter.sendKeys("40000", Keys.ENTER);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath("//div[contains(@class, 'open-filter-tooltip')]//span[@class='filter-tooltip-inner']")));
        driver.findElement(xpath("//div[contains(@class, 'open-filter-tooltip')]//span[@class='filter-tooltip-inner']")).click();
        List<WebElement> pricesList = driver.findElements(xpath("//div[@class='prod-cart__prise-new']"));
        ArrayList<Integer> prices = new ArrayList<>();
        for (WebElement price : pricesList) {
            prices.add(Integer.parseInt(price.getText().replace(" грн", "")));
        }
        for (Integer price: prices) {
            assertTrue(price < 40000);
        }
    }

    @Test
    public void checkThatFilterResultsContainsFilterWord() {
        WebElement hoverElement = driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'elektronika')]"));
        Actions builder = new Actions(driver);
        builder.moveToElement(hoverElement).perform();
        driver.findElement(xpath("//a[@class='sidebar-item' and contains(@href, 'gotovyie-pk')]")).click();
        driver.findElement(xpath("//label[@for='fltr-proizvoditel-dell']")).click();
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__descr']"));
        for (WebElement webElement : elementList) {
            assertTrue(webElement.getText().contains("Dell"));
        }
    }

    @Test
    public void checkThatErrorMessageAppearsAfterLoginWithNotRegisteredAccount() {
        driver.findElement(xpath("//div[contains(@class, 'header-bottom__right')]//a[contains(@href, 'sign-in')]")).click();
        WebElement loginField = driver.findElement(xpath("//div[contains(@class, 'sign-holder')]//input[@name='login']"));
        loginField.clear();
        loginField.sendKeys("test@gmail.com");
        WebElement passwordField = driver.findElement(xpath("//div[contains(@class, 'sign-holder')]//input[@name='password']"));
        passwordField.clear();
        passwordField.sendKeys("test1234");
        driver.findElement(xpath("//div[contains(@class, 'sign-holder')]//button[contains(@class, 'submit')]")).click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAlert")));
        WebElement errorMessage = driver.findElement(xpath("//div[contains(@class, 'js_message')]"));
        assertEquals(errorMessage.getText(), "Неверные данные авторизации.");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
