package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.ui.base.Page;

public class ChannelSelectionPage extends Page {

    @FindBy(id = "radio-1")
    private WebElement emailChannelRadio;
    @FindBy(id = "radio-2")
    private WebElement textChannelRadio;
    @FindBy(id = "continue")
    private WebElement continueButton;

    @Override
    protected String getPageTitle() {

        return "What type of reminder do you want to get? – MOT reminders";
    }

    @Override
    protected String getContentHeader() {

        return "What type of reminder do you want to get?";
    }

    public EmailPage selectEmailChannel() {

        emailChannelRadio.click();
        continueButton.click();

        return new EmailPage();
    }

    public PhoneNumberEntryPage selectPhoneChannel() {

        textChannelRadio.click();
        continueButton.click();

        return new PhoneNumberEntryPage();
    }
}
