package uk.gov.dvsa.motr.journey;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import uk.gov.dvsa.motr.base.BaseTest;
import uk.gov.dvsa.motr.helper.RandomGenerator;
import uk.gov.dvsa.motr.ui.page.EmailConfirmationPendingPage;
import uk.gov.dvsa.motr.ui.page.EmailPage;
import uk.gov.dvsa.motr.ui.page.HomePage;
import uk.gov.dvsa.motr.ui.page.ReviewPage;
import uk.gov.dvsa.motr.ui.page.SubscriptionConfirmationPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribeConfirmationPage;
import uk.gov.dvsa.motr.ui.page.UnsubscribeErrorPage;
import uk.gov.dvsa.motr.ui.page.VrmPage;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class MotReminderTestsPreSms extends BaseTest {

    @Test(dataProvider = "dataProviderCreateEmailMotReminderForMyVehicle",
            description = "Owner of a vehicle with a mot is able to set up a MOT reminder with their VRM and email and unsubscribe from it",
            groups = {"PreSms"})
    public void createMotReminderForMyVehicleThenUnsubscribe(String vrm, String email) throws IOException, InterruptedException {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        //And I confirm my email address
        motReminder.subscribeToReminderAndConfirmEmail(vrm, email);

        //When I select to unsubscribe from an email reminder
        //And confirm that I would like to unsubscribe
        UnsubscribeConfirmationPage unsubscribeConfirmed = motReminder.unsubscribeFromReminder(vrm, email);

        //Then my MOT reminder subscription has been cancelled
        assertEquals(unsubscribeConfirmed.getBannerTitle(), "You've unsubscribed");

        //And I am shown a link to complete the unsubscribe survey
        assertTrue(unsubscribeConfirmed.isSurveyLinkDisplayed());
    }

    @Test(dataProvider = "dataProviderCreateEmailMotReminderForMyVehicle",
            description = "After confirming the reminder the user can click the link to go back to the start page",
            groups = {"PreSms"})
    public void afterConfirmationOfReminderUserCanGoToStartPageToSignUpAgain(String vrm, String email) throws Exception {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        //And I confirm my email address
        SubscriptionConfirmationPage confirmationPage = motReminder.subscribeToReminderAndConfirmEmail(vrm, email);

        //When I click sign up for another reminder
        //Then I am sent to the start page
        HomePage homePage = confirmationPage.clickSignUpForAnotherReminder();
    }

    @Test(dataProvider = "dataProviderCreateEmailMotReminderForMyVehicle",
            description = "Reminder subscriber with an active subscription creates another subscription with the same VRM and email" +
                    " does not need to confirm their email again",
            groups = {"PreSms"})
    public void createDuplicateMOTReminderDoesNotNeedToConfirmEmailAddressAgain(String vrm, String email)
            throws IOException, InterruptedException {

        // Given I am a user of the MOT reminders service with an active subscription
        motReminder.subscribeToReminderAndConfirmEmail(vrm, email);

        // When I create another MOT reminder subscription with the same VRM and email
        // Then I do not need to confirm my email address and am taken directly to the subscription confirmed page
        motReminder.enterAndConfirmPendingReminderDetailsSecondTime(vrm, email);
    }

    @Test(dataProvider = "dataProviderCreateEmailMotReminderForMyVehicle",
            description = "Reminder subscriber with multiple pending subscriptions is directed to the confirm email error page when " +
                    "selecting an old confirm email link",
            groups = {"PreSms"})
    public void userWithDuplicatePendingMotSubscriptionsIsDirectedToConfirmEmailErrorPageWhenSelectingOldConfirmEmailLink(
            String vrm, String email
    ) throws IOException, InterruptedException {

        // Given I am a user of the MOT reminders service with a pending subscription
        motReminder.enterAndConfirmPendingReminderDetails(vrm, email);
        String oldConfirmationId = motReminder.subscriptionDb.findConfirmationIdByVrmAndEmail(vrm, email);

        // When I create another MOT reminder subscription with the same VRM and email
        // And I select an old confirm email link
        motReminder.enterAndConfirmPendingReminderDetails(vrm, email);
        String newConfirmationId = motReminder.subscriptionDb.findConfirmationIdByVrmAndEmail(vrm, email);

        // Then I am directed to the MOT Reminder not found error page
        assertNotEquals(oldConfirmationId, newConfirmationId);
        motReminder.navigateToEmailConfirmationExpectingErrorPage(oldConfirmationId);
        // And I can still confirm my email address using newest email
        motReminder.navigateToEmailConfirmationPage(newConfirmationId);
    }

    @Test(dataProvider = "dataProviderCreateEmailMotReminderForMyVehicle",
            description = "A user who has previously unsubscribed from reminders will be displayed the unsubscribe error page",
            groups = {"PreSms"})
    public void reminderThatHasBeenUnsubscribedDisplaysErrorPage(String vrm, String email) {

        //Given I am a user of the MOT reminders service with an active subscription
        //When I unsubscribe from the email reminder via the unsubscribe link
        motReminder.subscribeToReminderAndConfirmEmail(vrm, email);
        String subscriptionId = motReminder.subscriptionDb.findUnsubscribeIdByVrmAndEmail(vrm, email);
        motReminder.unsubscribeFromReminder(vrm, email);

        //And select the unsubscribe link again
        UnsubscribeErrorPage errorPage = motReminder.navigateToUnsubscribeExpectingErrorPage(subscriptionId);

        //Then I receive a error message informing me that I have already unsubscribed
        assertEquals(errorPage.getErrorMessageText(), "You've already unsubscribed or the link hasn't worked.");
    }

    @Test(description = "Owner of a vehicle with a mot can change their email when creating MOT reminder",
            groups = {"PreSms"})
    public void canChangeEmailFromReviewWhenCreatingReminder() {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = motReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());

        //And I update my email address
        EmailPage emailPageFromReview = reviewPage.clickChangeEmail();
        ReviewPage reviewPageSubmit = emailPageFromReview.enterEmailAddress(RandomGenerator.generateEmail());

        //Then my mot reminder is set up successfully with the updated email address
        EmailConfirmationPendingPage confirmPage = reviewPageSubmit.confirmSubscriptionDetailsOnEmailChannel();
        assertEquals(confirmPage.getTitle(), "One more step");
    }

    @Test(description = "Owner of a vehicle with a mot can change their vrm when creating MOT reminder",
            groups = {"PreSms"})
    public void canChangeVrmFromReviewWhenCreatingReminder() {

        //Given I am a vehicle owner on the MOTR start page
        //When I enter the vehicle vrm and my email address
        ReviewPage reviewPage = motReminder.enterReminderDetails(RandomGenerator.generateVrm(), RandomGenerator.generateEmail());

        //And I update my vehicle vrm
        VrmPage vrmPageFromReview = reviewPage.clickChangeVrm();
        ReviewPage reviewPageSubmit = vrmPageFromReview.enterVrmExpectingReturnToReview(RandomGenerator.generateVrm());

        //Then my mot reminder is set up successfully with the updated vehicle vrm
        EmailConfirmationPendingPage confirmPage = reviewPageSubmit.confirmSubscriptionDetailsOnEmailChannel();
        assertEquals(confirmPage.getTitle(), "One more step");
    }

    @DataProvider(name = "dataProviderCreateEmailMotReminderForMyVehicle")
    public Object[][] dataProviderCreateMotReminderForMyVehicle() throws IOException {

        return new Object[][]{{RandomGenerator.generateVrm(), RandomGenerator.generateEmail()}};
    }
}
