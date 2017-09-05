package uk.gov.dvsa.motr.ui.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import uk.gov.dvsa.motr.navigation.GotoUrl;
import uk.gov.dvsa.motr.ui.base.Page;
import uk.gov.dvsa.motr.ui.base.PageIdentityVerificationException;

@GotoUrl("/confirm-subscription/{0}")
public class SubscriptionConfirmationErrorPage extends Page {

    @FindBy(className = "heading-xlarge")
    private WebElement headerTitle;

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

        return "No MOT reminder found";
    }

    @Override
    protected String getPageTitle() {

        return "No MOT reminder found – MOT reminders";
    }

    private String getHeaderTitle() {

        return headerTitle.getText();
    }
}
