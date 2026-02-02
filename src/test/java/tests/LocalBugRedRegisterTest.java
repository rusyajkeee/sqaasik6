package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ExcelReader;

import java.time.Duration;


public class LocalBugRedRegisterTest {

    WebDriver driver;

    @DataProvider(name = "registerData")
    public Object[][] registerData() throws Exception {
        return ExcelReader.getData("src/test/resources/test_data2.xlsx");
    }

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test(dataProvider = "registerData")
    public void registerTest(String id, String name, String email, String password, String expected) {

        driver.get("http://users.bugred.ru/user/register/index.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement form = driver.findElement(
                By.xpath("//form[contains(@action,'/user/register')]")
        );

        form.findElement(By.name("name")).sendKeys(name);
        form.findElement(By.name("email")).sendKeys(email);
        form.findElement(By.name("password")).sendKeys(password);

        form.findElement(By.name("act_register_now")).click();

        boolean busyEmail = isTextPresent("register_busy (email)");
        boolean badEmail = isTextPresent("register_not_correct_field (email)");
        boolean busyName = isTextPresent("register_busy (name)");



        boolean passed =
                (expected.equals("OK") && !busyEmail && !badEmail && !busyName) ||
                        (expected.equals("BUSY_EMAIL") && busyEmail) ||
                        (expected.equals("BAD_EMAIL") && badEmail) ||
                        (expected.equals("BUSY_NAME") && busyName);

        Assert.assertTrue(passed,
                "Expected: " + expected +
                        " | busyEmail=" + busyEmail +
                        " | badEmail=" + badEmail +
                        " | busyName=" + busyName);
    }

    private boolean isTextPresent(String text) {
        return driver.getPageSource().contains(text);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
