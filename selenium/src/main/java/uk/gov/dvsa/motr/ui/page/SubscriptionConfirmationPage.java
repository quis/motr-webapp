package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/confirm-subscription/{0}")
public class SubscriptionConfirmationPage extends Page {

    @FindBy(className = "bold-large")
    private WebElement headerTitle;

    @FindBy(id = "email")
    private WebElement userEmail;

    @FindBy(id = "vrm")
    private WebElement registration;

    @FindBy(id = "expiry-date")
    private WebElement expiryDate;

    @FindBy(id = "sign-up-for-another-reminder")
    private WebElement signupForAnotherReminderLink;

    @Override
    protected void selfVerify() {

        if (!getHeaderTitle().contains(getContentHeader()) || !this.driver.getTitle().equals(getPageTitle())) {

            throw new PageIdentityVerificationException("Page identity verification failed: "
                    + String.format("\n Expected: %s page, \n Found: %s page", getContentHeader(), getHeaderTitle())
            );
        }
    }

    @Override
    protected String getContentHeader() {

        return "You've signed up for an MOT reminder";
    }

    @Override
    protected String getPageTitle() {

        return "You've signed up for an MOT reminder";
    }

    public String getHeaderTitle() {

        return headerTitle.getText();
    }

    public HomePage clickSignUpForAnotherReminder() {

        signupForAnotherReminderLink.click();
        return new HomePage();
    }
}
