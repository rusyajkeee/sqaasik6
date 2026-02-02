package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ExcelReader;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BugRedRegisterTest {

    WebDriver driver;

    private static final String SAUCE_URL =
            "https://ondemand.eu-central-1.saucelabs.com:443/wd/hub";

    @DataProvider(name = "registerData")
    public Object[][] registerData() throws Exception {
        return ExcelReader.getData("src/test/resources/test_data2.xlsx");
    }

    @Parameters("browser")
    @BeforeMethod
    public void setup(@Optional("chrome") String browser) throws Exception {

        MutableCapabilities options;

        if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions ff = new FirefoxOptions();
            ff.setPlatformName("Windows 11");
            ff.setBrowserVersion("latest");
            options = ff;
        } else {
            ChromeOptions ch = new ChromeOptions();
            ch.setPlatformName("Windows 11");
            ch.setBrowserVersion("latest");
            options = ch;
        }

        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", "oauth-texnoz110518-2dee6");
        sauceOptions.put("accessKey", "3407d1c7-9db8-4026-b8cd-388655f3b7a6");
        sauceOptions.put("build", "BugRed-DDT-Register");
        sauceOptions.put("name", "BugRed Register - " + browser);

        options.setCapability("sauce:options", sauceOptions);

        driver = new RemoteWebDriver(new URL(SAUCE_URL), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test(dataProvider = "registerData")
    public void registerTest(String id, String name, String email, String password, String expected) {

        driver.get("http://users.bugred.ru/user/register/index.html");

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

