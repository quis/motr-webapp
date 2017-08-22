package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;

@GotoUrl("/vrm")
public class VrmPage extends Page {

    @FindBy(id = "regNumber")
    private WebElement vrmField;
    @FindBy(id = "continue")
    private WebElement continueButton;

    @Override
    protected String getContentHeader() {

        return "What is the vehicle's registration number?";
    }

    @Override
    protected String getPageTitle() {

        return "What is the vehicle's registration number? – MOT reminders";
    }

    public EmailPage enterVrm(String vrm){

        vrmField.sendKeys(vrm);
        continueButton.click();
        return new EmailPage();
    }

    public ChannelSelectionPage enterVrmSmsToggleOn(String vrm){

        vrmField.sendKeys(vrm);
        continueButton.click();
        return new ChannelSelectionPage();
    }

    public ReviewPage enterVrmExpectingReturnToReview(String vrm) {

        vrmField.clear();
        vrmField.sendKeys(vrm);
        continueButton.click();
        return new ReviewPage();
    }
}
