package com.browserstack.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.MutableCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

public class BrowserStackConfig {
//    private static final String USERNAME = "rishabhbatra_GUQgue";
//    private static final String ACCESS_KEY = "NnyWA7vbnVA8gnFTirEJ";
    private static final String USERNAME = "rishabhbatra_Ac0zof";
    private static final String ACCESS_KEY = "sDrs6KC9Ey9UxXkxBd5D";
    private static final String HUB_URL = "https://hub-cloud.browserstack.com/wd/hub";

    public static WebDriver createBrowserStackDriver(String browser, String os, String device) throws MalformedURLException {
        MutableCapabilities capabilities = new MutableCapabilities();
        HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();

        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.cookies", 2);
        prefs.put("profile.cookie_controls_mode", 2);
        prefs.put("profile.block_third_party_cookies", true);

        // Chrome options for cookie handling
        HashMap<String, Object> chromeOptions = new HashMap<>();
        chromeOptions.put("prefs", prefs);

        browserstackOptions.put("userName", USERNAME);
        browserstackOptions.put("accessKey", ACCESS_KEY);
        browserstackOptions.put("projectName", "El Pais Scraper");
        browserstackOptions.put("buildName", "Build 1.0");
        browserstackOptions.put("sessionName", "El Pais Article Scraping");
        browserstackOptions.put("debug", true);
        browserstackOptions.put("consoleLogs", "verbose");

        //for fixing test not running on firefox
        if (browser.equalsIgnoreCase("Firefox")) {
            capabilities.setCapability("browserVersion", "104.0");
        }

        // Desktop configuration
        if (device == null) {
            browserstackOptions.put("os", os);
            capabilities.setCapability("browserName", browser);
            capabilities.setCapability("browserVersion", "latest");
            capabilities.setCapability("goog:chromeOptions", chromeOptions);
        }
        // Mobile configuration
        else {
            browserstackOptions.put("deviceName", device);
            browserstackOptions.put("realMobile", "true");
            capabilities.setCapability("goog:chromeOptions", chromeOptions);
        }

        capabilities.setCapability("bstack:options", browserstackOptions);
        return new RemoteWebDriver(new URL(HUB_URL), capabilities);
    }

    public static List<Object[]> getBrowserConfigs() {
        return Arrays.asList(
                new Object[]{"Chrome", "Windows", null},
                new Object[]{"Firefox", "OS X", null},
                new Object[]{null, null, "iPhone 14"},
                new Object[]{null, null, "Samsung Galaxy S23"},
                new Object[]{"Edge", "Windows", null}
        );
    }

    public static WebDriver createDriver(String browser, String os, String device, boolean isLocal)throws MalformedURLException{
        if(isLocal){
            return createLocalDriver(browser);
        }
        return createBrowserStackDriver(browser, os, device);
    }

    public static WebDriver createLocalDriver(String browser){
        switch (browser.toLowerCase()){
            case "chrome":
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            case "edge":
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver();

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }
}
