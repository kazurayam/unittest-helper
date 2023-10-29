package io.github.bonigarcia.webdriver.jupiter.ch02.helloworld;

import com.kazurayam.unittest.TestHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldChromeJupiterTest {

    Logger log = LoggerFactory.getLogger(HelloWorldChromeJupiterTest.class);

    WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void test_unfavorable_location() throws Exception {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        assertThat(driver.getTitle()).contains("Selenium WebDriver");

        // Now I want to save the source of the Web page into a file
        String pageSource = driver.getPageSource();

        log.info("[test_unfavorable_location] user.dir = " + System.getProperty("user.dir"));
        Path out = Paths.get("pageSource1.html");
        log.info("[test_unfavorable_location] out.toAbsolutePath() = " + out.toAbsolutePath());

        Files.write(out, pageSource.getBytes(StandardCharsets.UTF_8));
        // the file will be saved into a file at
        //     "unittest-helper/pageSource.html"
        // .... this location is not welcomed. I would rather like
        //     "unittest-helper/app/test-output/pageSource.html"
        // but how?
    }

    /**
     * Here it is assumed that this test is built and executed in Gradle
     */
    @Test
    void test_write_into_default_dir() throws Exception {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        assertThat(driver.getTitle()).contains("Selenium WebDriver");

        // Now I want to save the source of the Web page into a file
        Path out = new TestHelper(this.getClass())
                .resolveOutput("pageSource2.html");

        String pageSource = driver.getPageSource();
        Files.write(out, pageSource.getBytes(StandardCharsets.UTF_8));
        // the file will be saved into a file at
        //     "unittest-helper/app/test-output/pageSource.html"
    }

    /**
     * Here it is assumed that this test is built and executed in Gradle
     */
    @Test
    void test_gradle_build_dir() throws Exception {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        assertThat(driver.getTitle()).contains("Selenium WebDriver");

        // Now I want to save the source of the Web page into a file
        TestHelper th = new TestHelper(this.getClass())
                .setOutputDirPath(Paths.get("build/tmp/testOutput"));
        Path out = th.resolveOutput("pageSource3.html");

        String pageSource = driver.getPageSource();
        Files.write(out, pageSource.getBytes(StandardCharsets.UTF_8));
        // the file will be saved into a file at
        //     "unittest-helper/app/build/tmp/testOutput/pageSource.html"
        assertThat(out.getParent().getFileName().toString()).isEqualTo("testOutput");
        assertThat(out.getParent()
                .getParent().getFileName().toString()).isEqualTo("tmp");
        assertThat(out.getParent()
                .getParent()
                .getParent().getFileName().toString()).isEqualTo("build");
        assertThat(out.getParent()
                .getParent()
                .getParent()
                .getParent().getFileName().toString()).isEqualTo("app");

    }

}
