package uk.gov.dvsa.motr.ui.page;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.ui.base.Page;

public class PhoneNumberEntryPage extends Page {

    @FindBy(id = "phoneNumber")
    private WebElement phoneNumberField;
    @FindBy(id = "continue")
    private WebElement continueButton;

    @Override
    protected String getPageTitle() {

        return "What is your mobile number? – MOT reminders";
    }

    @Override
    protected String getContentHeader() {

        return "What is your mobile number?";
    }

    public ReviewPage enterPhoneNumber(String number) {

        phoneNumberField.clear();
        phoneNumberField.sendKeys(number);
        continueButton.click();

        return new ReviewPage();
    }
}
