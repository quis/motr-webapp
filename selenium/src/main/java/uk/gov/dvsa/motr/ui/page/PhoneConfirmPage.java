package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/confirm-phone")
public class PhoneConfirmPage extends Page {

    @FindBy(id = "confirmationCode")
    private WebElement confirmationCodeField;
    @FindBy(id = "continue")
    private WebElement continueButton;

    @Override
    protected String getContentHeader() {

        return "One more step";
    }

    @Override
    protected String getPageTitle() {

        return "One more step – MOT reminders";
    }

    public SubscriptionConfirmationPage enterConfirmationCode(String confirmationCode) {

        confirmationCodeField.clear();
        confirmationCodeField.sendKeys(confirmationCode);
        continueButton.click();

        return new SubscriptionConfirmationPage();
    }
}
