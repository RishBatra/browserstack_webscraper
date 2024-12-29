package com.browserstack.pages;

import com.browserstack.model.Article;
import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;
public class ArticlePage extends BasePage {
    private final By titleLocator = By.cssSelector("article header h1.a_t, article header h2.a_t");
    private final By contentLocator = By.cssSelector("article div figure figcaption span, article div[data-dtm-region='articulo_cuerpo'] p");
    private final By imageLocator = By.xpath("//article//header//img");

    public ArticlePage(WebDriver driver) {
        super(driver);
    }

    public Article extractArticleData() {
        waitForPageLoad();

        try {
            String title = waitForElementPresent(titleLocator).getText();
            String content = "";

            int retryCount = 0;
            while (retryCount < 3) {
                try {
                    waitForPageLoad();
                    List<WebElement> paragraphs = waitForElementsPresent(contentLocator);
                    content = paragraphs.stream()
                            .map(WebElement::getText)
                            .collect(Collectors.joining("\n"));
                    break;
                } catch (StaleElementReferenceException e) {
                    retryCount++;
                    if (retryCount == 3) {
                        throw e;
                    }
                    driver.navigate().refresh();
                    waitForPageLoad();
                }
            }

            String imageUrl = null;
            try {
                imageUrl = waitForElementPresent(imageLocator).getAttribute("src");
            } catch (Exception e) {
                // No image available
            }

            return new Article(title, content, imageUrl);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract article data", e);
        }
    }
}
