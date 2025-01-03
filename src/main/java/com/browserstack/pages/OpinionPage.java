package com.browserstack.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class OpinionPage extends BasePage {
    private By articleLinks = By.xpath("//article//header//h2//a");

    public OpinionPage(WebDriver driver){
        super(driver);
    }

    public List<String> getFirstFiveArticleUrls(){
        List<String> urls = new ArrayList<>();
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                List<WebElement> articles = waitForElementsVisible(articleLinks);
                for(int i = 0; i< Math.min(5,articles.size());i++){
                    urls.add(articles.get(i).getAttribute("href"));
                }
                break;
            }catch (StaleElementReferenceException e){
                retryCount++;
                if (retryCount == 3) {
                    throw e;
                }
                driver.navigate().refresh();
                waitForPageLoad();
            }
        }
        return urls;
    }
}
