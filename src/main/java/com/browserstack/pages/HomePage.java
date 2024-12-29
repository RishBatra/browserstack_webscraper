package com.browserstack.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage{
    private By hamBurgerMenuButton = By.xpath("//button[@id='btn_open_hamburger']");
    private By opinionLink = By.xpath("//div[@data-dtm-region='portada_menu' or @class='header b_h _df _pr']//a[text()='Opinión']");
    private By opinionLinkOnMobbile = By.xpath("//div[@id=\"hamburger_container\"]//li//a[text()='Opinión']");
    private By cookiesNotice1 = By.xpath("//div[@data-testid=\"notice\"]");
    private By acceptCookiesButton2 = By.xpath("//a[contains(text(), 'Accept and continue')]");
    private By acceptCookiesButton1 = By.xpath("//div[@data-testid=\"notice\"]//button[@id=\"didomi-notice-agree-button\"]");
    private By cookiesNotice2 = By.xpath("//div[@class='pmConsentWall-content']");

    public HomePage(WebDriver driver){
        super(driver);
    }

    public OpinionPage goToOpinionSection(){
        handleCookiePopup();
        waitForPageLoad();
        System.out.println("I AM HERE");
        if (isMobileDevice()) {
            // Mobile-specific navigation
            waitForElementVisible(hamBurgerMenuButton).click();
            waitForElementPresent(opinionLinkOnMobbile).click();
        } else {
            // Desktop navigation
            System.out.println("I AM HERE");
            waitForElement(opinionLink).click();
        }

        return new OpinionPage(driver);
    }

    public void handleCookiePopup() {
        boolean popupDetected = false;
        try {
            WebElement popup = waitForElementPresent(cookiesNotice1);
            if (popup.isDisplayed()) {
                waitForElementClickable(acceptCookiesButton1).click();
            }
            popupDetected = true;
        } catch (TimeoutException e) {
            // Cookie popup not present, continue
        }
        if(!popupDetected){
            try {
                WebElement popup = waitForElementVisible(cookiesNotice2);
                if (popup.isDisplayed()) {
                    waitForElementClickable(acceptCookiesButton2).click();
                }
            } catch (TimeoutException e) {
                // Cookie popup not present, continue
            }
        }

    }

}
