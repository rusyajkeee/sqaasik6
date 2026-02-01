package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ExcelReader;

import java.time.Duration;

public class LocalDeviantArtSearchTest {

    WebDriver driver;

    @DataProvider(name = "searchData")
    public Object[][] searchData() throws Exception {
        return ExcelReader.getData("src/test/resources/test_data.xlsx");
    }

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test(dataProvider = "searchData")
    public void searchTest(String id, String query, String expected) {

        driver.get("https://www.deviantart.com/search?q=" + query);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

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
                (noResults && expected.equals("NO_RESULTS")) ||
                        (!noResults && expected.equals("HAS_RESULTS"));

        Assert.assertTrue(passed,
                "Query: " + query +
                        " | Expected: " + expected +
                        " | Actual: " + (noResults ? "NO_RESULTS" : "HAS_RESULTS"));

        System.out.println("Test " + id + " [" + query + "] -> " +
                (passed ? "PASSED" : "FAILED"));
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
