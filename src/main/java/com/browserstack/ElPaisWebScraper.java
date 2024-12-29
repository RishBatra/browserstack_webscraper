package com.browserstack;

import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import com.browserstack.model.Article;
import com.browserstack.pages.*;
import com.browserstack.utils.*;
import com.browserstack.config.BrowserStackConfig;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class ElPaisWebScraper {
    private WebDriver driver;
    private List<Article> articles;

    @Parameters({"browser", "os", "device"})
    @BeforeTest
    public void setupDriver(String browser, String os, String device) throws MalformedURLException {
        driver = BrowserStackConfig.createDriver(browser, os, device);
        if (device == null || device.isEmpty()) {
            driver.manage().window().maximize();
        }
    }

    @Test
    public void scrapeArticles() {
        try {
            driver.get("https://elpais.com");
            HomePage homePage = new HomePage(driver);
            OpinionPage opinionPage = homePage.goToOpinionSection();

            List<String> articleUrls = opinionPage.getFirstFiveArticleUrls();
            articles = new ArrayList<>();

            for (String url : articleUrls) {
                driver.get(url);
                ArticlePage articlePage = new ArticlePage(driver);
                Article article = articlePage.extractArticleData();

                if (article.getImageUrl() != null) {
                    ImageDownloader.downloadImage(
                            article.getImageUrl(),
                            article.getTitle().replaceAll("[^a-zA-Z0-9]", "_")
                    );
                }

                articles.add(article);
            }
            processArticles();
            markTestStatus("passed", "Test completed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            markTestStatus("failed", e.getMessage());
            throw e;
        }
    }

    private void processArticles() {
        Map<String, Integer> wordFrequency = new HashMap<>();
        System.out.println("LIST OF FIRST 5 ARTICLES IN SPANISH \n\n");
        int articleCount = 1;
        for (Article article : articles) {
            System.out.println(articleCount++ + ". TITLE: " + article.getTitle());
            System.out.println("CONTENT: " + article.getContent() + "\n");

            try {
                String translatedTitle = TranslationUtil.translateToEnglish(article.getTitle());
                article.setTranslatedTitle(translatedTitle);
                System.out.println("TRANSLATED TITLE: " + translatedTitle + "\n");

                String[] words = translatedTitle.toLowerCase().split("\\s+");
                for (String word : words) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            } catch (IOException e) {
                System.err.println("Error translating title: " + article.getTitle());
                e.printStackTrace();
                continue;
            }
        }

        if(!wordFrequency.isEmpty()) {
            System.out.println("LIST OF REPEATED WORDS IN ALL HEADERS COMBINED");
            wordFrequency.entrySet().stream()
                    .filter(e -> e.getValue() > 2)
                    .forEach(e -> System.out.println("WORD " + e.getKey() + ": " + e.getValue()));
        } else {
            System.out.println("NOTICE: THERE ARE NO WORDS IN THE HEADERS WHICH ARE REPEATED MORE THAN 2 TIMES");
        }
    }

    private void markTestStatus(String status, String reason) {
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        Map<String, Object> args = new HashMap<>();
        args.put("status", status);
        args.put("reason", reason);

        try {
            jse.executeScript(
                    "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": " +
                            new JSONObject(args).toString() + "}"
            );
        } catch (Exception e) {
            System.err.println("Error marking test status: " + e.getMessage());
        }
    }


    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
