package uk.gov.dvsa.motr.helper;

import uk.gov.dvsa.motr.navigation.PageNavigator;
import uk.gov.dvsa.motr.ui.page.ChannelSelectionPage;
import uk.gov.dvsa.motr.ui.page.CookiesPage;
import uk.gov.dvsa.motr.ui.page.EmailConfirmationPendingPage;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.HomePage;
import uk.gov.dvsa.motr.ui.page.PhoneConfirmPage;
import uk.gov.dvsa.motr.ui.page.PhoneNumberEntryPage;
import uk.gov.dvsa.motr.ui.page.PrivacyPage;
import uk.gov.dvsa.motr.ui.page.ReviewPage;
import uk.gov.dvsa.motr.ui.page.SubscriptionConfirmationErrorPage;
import uk.gov.dvsa.motr.ui.page.SubscriptionConfirmationPage;
import uk.gov.dvsa.motr.ui.page.TermsAndConditionsPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribeConfirmationPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribeErrorPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribePage;
import uk.gov.dvsa.motr.ui.page.VrmPage;

public class MotReminder {

    public DynamoDbSubscriptionHelper subscriptionDb = new DynamoDbSubscriptionHelper();
    public DynamoDbSmsConfirmationHelper smsConfirmationHelper = new DynamoDbSmsConfirmationHelper();

    public ReviewPage enterReminderDetails(String vrm, String email) {
        HomePage page = PageNavigator.goTo(HomePage.class);
        VrmPage vrmPage = page.clickStartNow();
        EmailPage emailPage = vrmPage.enterVrm(vrm);
        return emailPage.enterEmailAddress(email);
    }

    public ReviewPage enterReminderDetailsSmsToggleOn(String vrm, String email) {
        HomePage page = PageNavigator.goTo(HomePage.class);
        VrmPage vrmPage = page.clickStartNow();
        ChannelSelectionPage channelSelectionPage = vrmPage.enterVrmSmsToggleOn(vrm);
        EmailPage emailPage = channelSelectionPage.selectEmailChannel();
        return emailPage.enterEmailAddress(email);
    }

    public ReviewPage enterReminderDetailsUsingMobileChannel(String vrm, String mobileNumber) {
        HomePage page = PageNavigator.goTo(HomePage.class);
        VrmPage vrmPage = page.clickStartNow();
        ChannelSelectionPage channelSelectionPage = vrmPage.enterVrmSmsToggleOn(vrm);
        PhoneNumberEntryPage phoneNumberEntryPage = channelSelectionPage.selectPhoneChannel();
        return phoneNumberEntryPage.enterPhoneNumber(mobileNumber);
    }

    public CookiesPage clickCookiesLink() {
        HomePage page = PageNavigator.goTo(HomePage.class);
        return page.clickCookiesLink();
    }

    public TermsAndConditionsPage clickTermsAndConditionsLink() {
        HomePage page = PageNavigator.goTo(HomePage.class);
        return page.clickTermsAndConditionsLink();
    }

    public UnsubscribePage navigateToUnsubscribe(String vrm, String email) {

        String unsubscribeId = subscriptionDb.findUnsubscribeIdByVrmAndEmail(vrm, email);
        return PageNavigator.goTo(UnsubscribePage.class, unsubscribeId);
    }

    public UnsubscribeErrorPage navigateToUnsubscribeExpectingErrorPage(String unsubscribeId) {

        return PageNavigator.goTo(UnsubscribeErrorPage.class, unsubscribeId);
    }

    public SubscriptionConfirmationPage subscribeToReminderAndConfirmEmail(String vrm, String email) {

        enterAndConfirmPendingReminderDetails(vrm, email);

        return navigateToEmailConfirmationPage(vrm, email);
    }

    public SubscriptionConfirmationPage subscribeToReminderAndConfirmEmailPostSms(String vrm, String email) {

        enterAndConfirmPendingReminderDetailsPostSms(vrm, email);

        return navigateToEmailConfirmationPage(vrm, email);
    }

    public EmailConfirmationPendingPage enterAndConfirmPendingReminderDetails(String vrm, String email) {

        return enterReminderDetails(vrm, email).confirmSubscriptionDetailsOnEmailChannel();
    }

    public EmailConfirmationPendingPage enterAndConfirmPendingReminderDetailsPostSms(String vrm, String email) {

        return enterReminderDetailsSmsToggleOn(vrm, email).confirmSubscriptionDetailsOnEmailChannel();
    }


    public SubscriptionConfirmationPage subscribeToReminderAndConfirmMobileNumber(String vrm, String mobileNumber) {

        ReviewPage reviewPage = enterReminderDetailsUsingMobileChannel(vrm, mobileNumber);
        PhoneConfirmPage phoneConfirmPage = reviewPage.confirmSubscriptionDetailsOnMobileChannel();
        return phoneConfirmPage.enterConfirmationCode(smsConfirmationCode(vrm, mobileNumber));
    }

    public SubscriptionConfirmationPage enterAndConfirmReminderDetailsSecondTimeOnMobileChannel(String vrm, String mobileNumber) {

        ReviewPage reviewPage = enterReminderDetailsUsingMobileChannel(vrm, mobileNumber);
        return reviewPage.confirmSubscriptionDetailsNthTime();
    }

    public String smsConfirmationCode(String vrm, String mobileNumber) {

        return smsConfirmationHelper.findSmsConfirmCodeFromVrmAndMobileNumber(vrm, mobileNumber);
    }

    public SubscriptionConfirmationPage enterAndConfirmPendingReminderDetailsSecondTime(String vrm, String email) {

        return enterReminderDetails(vrm, email).confirmSubscriptionDetailsNthTime();
    }

    public SubscriptionConfirmationPage enterAndConfirmPendingReminderDetailsSecondTimePostSms(String vrm, String email) {

        return enterReminderDetailsSmsToggleOn(vrm, email).confirmSubscriptionDetailsNthTime();
    }

    public UnsubscribeConfirmationPage unsubscribeFromReminder(String vrm, String email) {

        return navigateToUnsubscribe(vrm, email).clickUnsubscribe();
    }

    private SubscriptionConfirmationPage navigateToEmailConfirmationPage(String vrm, String email) {

        String confirmationId = subscriptionDb.findConfirmationIdByVrmAndEmail(vrm, email);
        return navigateToEmailConfirmationPage(confirmationId);
    }

    public SubscriptionConfirmationPage navigateToEmailConfirmationPage(String confirmationId) {

        return PageNavigator.goTo(SubscriptionConfirmationPage.class, confirmationId);
    }

    public SubscriptionConfirmationErrorPage navigateToEmailConfirmationExpectingErrorPage(String confirmationId) {

        return PageNavigator.goTo(SubscriptionConfirmationErrorPage.class, confirmationId);
    }

    public PrivacyPage clickPrivacyPolicyLink() {
        HomePage page = PageNavigator.goTo(HomePage.class);
        return page.clickPrivacyPolicyLink();
    }
}
