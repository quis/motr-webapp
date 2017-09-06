package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/review")
public class ReviewPage extends Page {

    @FindBy(id = "change-registration-link") private WebElement changeRegistrationLink;
    @FindBy(id = "change-email-link") private WebElement changeEmailLink;
    @FindBy(id = "continue-button") private WebElement continueButton;

    @Override
    protected String getContentHeader() {

        return "Check your details";
    }

    @Override
    protected String getPageTitle() {

        return "Check your details – MOT reminders";
    }

    public EmailConfirmationPendingPage confirmSubscriptionDetailsOnEmailChannel() {

        continueButton.click();
        return new EmailConfirmationPendingPage();
    }

    public PhoneConfirmPage confirmSubscriptionDetailsOnMobileChannel() {

        continueButton.click();
        return new PhoneConfirmPage();
    }

    public SubscriptionConfirmationPage confirmSubscriptionDetailsNthTime() {

        continueButton.click();
        return new SubscriptionConfirmationPage();
    }

    public EmailPage clickChangeEmail() {

        changeEmailLink.click();
        return new EmailPage();
    }

    public VrmPage clickChangeVrm() {

        changeRegistrationLink.click();
        return new VrmPage();
    }
}
