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

public class DeviantArtSearchTest {

    WebDriver driver;

    private static final String USERNAME = "oauth-texnoz110518-2dee6";
    private static final String ACCESS_KEY = "3407d1c7-9db8-4026-b8cd-388655f3b7a6";

    @DataProvider(name = "browsers", parallel = true)
    public Object[][] browsers() {
        return new Object[][]{
                {"chrome"},
                {"firefox"}
        };
    }

    @DataProvider(name = "searchData")
    public Object[][] searchData() throws Exception {
        return ExcelReader.getData("src/test/resources/test_data.xlsx");
    }

    @BeforeMethod
    @Parameters("browser")
    public void setup(String browser) throws Exception {

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
        sauceOptions.put("username", USERNAME);
        sauceOptions.put("accessKey", ACCESS_KEY);
        sauceOptions.put("build", "DeviantArt-DDT-MultiBrowser");
        sauceOptions.put("name", "DeviantArt Search - " + browser);

        options.setCapability("sauce:options", sauceOptions);

        URL url = new URL("https://ondemand.eu-central-1.saucelabs.com/wd/hub");
        driver = new RemoteWebDriver(url, options);
    }


    @Test(dataProvider = "searchData")
    public void searchTest(String id, String query, String expected) {

        driver.get("https://www.deviantart.com/search?q=" + query);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        boolean noResults;
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(text(),\"We couldn't find results\")]")
            ));
            noResults = true;
        } catch (TimeoutException e) {
            noResults = false;
        }

        boolean passed =
                (noResults && expected.equalsIgnoreCase("NO_RESULTS")) ||
                        (!noResults && expected.equalsIgnoreCase("HAS_RESULTS"));

        ((JavascriptExecutor) driver)
                .executeScript("sauce:job-result=" + (passed ? "passed" : "failed"));

        Assert.assertTrue(passed,
                "Query: " + query +
                        " | Expected: " + expected +
                        " | Actual: " + (noResults ? "NO_RESULTS" : "HAS_RESULTS"));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
