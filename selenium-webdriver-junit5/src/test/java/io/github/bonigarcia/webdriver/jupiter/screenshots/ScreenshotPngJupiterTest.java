package io.github.bonigarcia.webdriver.jupiter.screenshots;

/* Quoted from Boni Gracia, "Selenium WebDriver with Java", O'Reilly
 * https://github.com/bonigarcia/selenium-webdriver-java/blob/1.3.0/selenium-webdriver-junit5/src/test/java/io/github/bonigarcia/webdriver/jupiter/ch04/screenshots/ScreenshotPngJupiterTest.java
 */

/*
 * (C) Copyright 2021 Boni Garcia (https://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.invoke.MethodHandles.lookup;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

class ScreenshotPngJupiterTest {

    static final Logger log = getLogger(lookup().lookupClass());
    WebDriver driver;

    @BeforeEach
    void setup() {
        driver = WebDriverManager.chromedriver().create();
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    public void test_show_CWD() {
        Path p = Paths.get(".");
        log.info("[test_show_CWD] " + p.toAbsolutePath().normalize());
    }

    @Test
    void testScreenshotPng() throws IOException {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        TakesScreenshot ts = (TakesScreenshot) driver;
        File screenshot = ts.getScreenshotAs(OutputType.FILE);
        log.debug("Screenshot created on {}", screenshot);

        Path destination = Paths.get("screenshot.png");

        Files.move(screenshot.toPath(), destination, REPLACE_EXISTING);
        log.debug("Screenshot moved to {}", destination);
        assertThat(destination).exists();
    }

}