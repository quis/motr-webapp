package uk.gov.dvsa.motr.journey;

import org.testng.annotations.Test;

import uk.gov.dvsa.motr.base.BaseTest;
import uk.gov.dvsa.motr.ui.page.CookiesPage;
import uk.gov.dvsa.motr.ui.page.PrivacyPage;
import uk.gov.dvsa.motr.ui.page.TermsAndConditionsPage;

import static org.testng.Assert.assertEquals;

public class MotReminderTests extends BaseTest {

    @Test(description = "As a user of the site with a vested interest in cookie policy, I can view them")
    public void canViewCookiesPageWhenClickingLinkInFooter() {

        //Given I am a user of the site
        //When I click the cookies link in footer of the page
        CookiesPage cookiesPage = motReminder.clickCookiesLink();

        //Then I am taken to the cookies page
        assertEquals(cookiesPage.getTitle(), "Cookies", "Cookies page is not returned");
    }

    @Test(description = "As a user of the site with a vested interest in terms and conditions of the service, I can view them")
    public void canViewTermsAndConditionsPageWhenClickingLinkInFooter() {

        //Given I am a user of the site
        //When I click the terms and conditions link in footer of the page
        TermsAndConditionsPage termsAndConditionsPage = motReminder.clickTermsAndConditionsLink();

        //Then I am taken to the terms and conditions page
        assertEquals(termsAndConditionsPage.getTitle(), "Terms and conditions", "Terms and conditions page is not returned");
    }

    @Test(description = "As a user of the site with a vested interest in the privacy policy of the service, I can view them")
    public void canViewPrivacyPageWhenClickingLinkInFooter() {

        //Given I am a user of the site
        //When I click the privacy policy link in footer of the page
        PrivacyPage privacyPage = motReminder.clickPrivacyPolicyLink();

        //Then I am taken to the privacy policy page
        assertEquals(privacyPage.getTitle(), "Privacy policy", "Privacy policy page is not returned");
    }
}
