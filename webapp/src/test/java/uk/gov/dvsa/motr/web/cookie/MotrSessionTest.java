package uk.gov.dvsa.motr.web.cookie;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MotrSessionTest {

    private static final String VRM = "VRZ";
    private static final String EMAIL = "test@test.com";
    private static final String PHONE_NUMBER = "07801987627";
    private static final String CHANNEL = "email";
    private static final String EMAIL_CHANNEL = "email";
    private static final String TEXT_CHANNEL = "text";

    private MotrSession motrSession;

    @Before
    public void setUp() {

        motrSession = new MotrSession(true);
    }

    @Test
    public void isAllowedOnPageReturnsTrueWhenVrmAndEmailSessionEntered() {

        motrSession.setEmail(EMAIL);
        motrSession.setVrm(VRM);
        boolean actual = motrSession.isAllowedOnReviewPage();
        assertTrue(actual);
    }

    @Test
    public void isAllowedOnPageReturnsFalseWhenNoEmailAndNoPhoneNumberSessionEntered() {

        motrSession.setVrm(VRM);
        boolean actual = motrSession.isAllowedOnReviewPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnPageReturnsTrueWhenNoEmailAndPhoneNumberIsSet() {

        motrSession.setVrm(VRM);
        motrSession.setPhoneNumber(PHONE_NUMBER);
        boolean actual = motrSession.isAllowedOnReviewPage();
        assertTrue(actual);
    }

    @Test
    public void isAllowedOnPageReturnsFalseWhenNoVrmSessionEntered() {

        motrSession.setEmail(EMAIL);
        boolean actual = motrSession.isAllowedOnReviewPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnPageReturnsFalseWhenNoSessionEntered() {

        boolean actual = motrSession.isAllowedOnReviewPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnEmailPageReturnsFalseWhenNoSessionEntered() {

        boolean actual = motrSession.isAllowedOnEmailPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnEmailPageReturnsTrueWhenVrmSessionEntered() {

        motrSession.setVrm(VRM);
        boolean actual = motrSession.isAllowedOnEmailPage();
        assertTrue(actual);
    }

    @Test
    public void getRegFromSessionReturnsRegWhenInSession() {

        motrSession.setVrm(VRM);
        String actual = motrSession.getVrmFromSession();
        assertEquals("VRZ", actual);
    }

    @Test
    public void getRegFromSessionReturnsEmptyStringWhenNoRegInSession() {

        String actual = motrSession.getVrmFromSession();
        assertEquals("", actual);
    }

    @Test
    public void getEmailFromSessionReturnsEmailWhenInSession() {

        motrSession.setEmail(EMAIL);
        String actual = motrSession.getEmailFromSession();
        assertEquals("test@test.com", actual);
    }

    @Test
    public void getPhoneNumberFromSessionReturnsPhoneNumberWhenInSession() {

        motrSession.setPhoneNumber(PHONE_NUMBER);
        String actual = motrSession.getPhoneNumberFromSession();
        assertEquals(PHONE_NUMBER, actual);
    }

    @Test
    public void getChannelFromSessionReturnsChannelWhenInSession() {

        motrSession.setChannel(CHANNEL);
        String actual = motrSession.getChannelFromSession();
        assertEquals(CHANNEL, actual);
    }

    @Test
    public void visitingFromReviewPageReturnsFalseWhenNotSet() {

        boolean actual = motrSession.visitingFromReviewPage();
        assertFalse(actual);
    }

    @Test
    public void visitingFromReviewPageReturnsTrueWhenSet() {

        motrSession.setVisitingFromReview(true);
        boolean actual = motrSession.visitingFromReviewPage();
        assertTrue(actual);
    }

    @Test
    public void visitingFromContactEntryPageReturnsFalseWhenNotSet() {

        boolean actual = motrSession.visitingFromContactEntryPage();
        assertFalse(actual);
    }

    @Test
    public void visitingFromContactEntryPageReturnsTrueWhenSet() {

        motrSession.setVisitingFromContactEntry(true);
        boolean actual = motrSession.visitingFromContactEntryPage();
        assertTrue(actual);
    }

    @Test
    public void getEmailFromSessionReturnsEmptyStringWhenNoEmailInSession() {

        String actual = motrSession.getEmailFromSession();
        assertEquals("", actual);
    }

    @Test
    public void getPhoneNumberFromSessionReturnsEmptyStringWhenNoEmailInSession() {

        String actual = motrSession.getPhoneNumberFromSession();
        assertEquals("", actual);
    }

    @Test
    public void getChannelFromSessionReturnsEmptyStringWhenNoEmailInSession() {

        String actual = motrSession.getChannelFromSession();
        assertEquals("", actual);
    }

    @Test
    public void isUsingEmailChannelReturnsTrueWhenUsingEmailChannel() {

        motrSession.setChannel(EMAIL_CHANNEL);
        assertTrue(motrSession.isUsingEmailChannel());
    }

    @Test
    public void isUsingSmsChannelReturnsFalseWhenUsingEmailChannel() {

        motrSession.setChannel(EMAIL_CHANNEL);
        assertFalse(motrSession.isUsingSmsChannel());
    }

    @Test
    public void isUsingSmsChannelReturnsTrueWhenUsingTextChannel() {

        motrSession.setChannel(TEXT_CHANNEL);
        assertTrue(motrSession.isUsingSmsChannel());
    }

    @Test
    public void isUsingEmailChannelReturnsFalseWhenUsingSmsChannel() {

        motrSession.setChannel(TEXT_CHANNEL);
        assertFalse(motrSession.isUsingEmailChannel());
    }

    @Test
    public void whenVehicleDetailsIsSetItCanBeRetrievedCorrectly() {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("TEST-MAKE");

        motrSession.setVehicleDetails(vehicleDetails);
        VehicleDetails actual = motrSession.getVehicleDetailsFromSession();
        assertEquals("TEST-MAKE", actual.getMake());
    }

    @Test
    public void isAllowedOnChannelSelectionPageReturnsFalseWithEmptyVrmAndFeatureToggleOn() {

        assertFalse(motrSession.isAllowedOnChannelSelectionPage());
    }

    @Test
    public void isAllowedOnChannelSelectionPageReturnsTrueWithToggleOnAndVrm() {

        motrSession.setVrm(VRM);

        assertTrue(motrSession.isAllowedOnChannelSelectionPage());
    }

    @Test
    public void isAllowedOnChannelSelectionPageReturnsFalseWithEmptyVrmAndFeatureToggleOff() {

        motrSession = new MotrSession(false);

        assertFalse(motrSession.isAllowedOnChannelSelectionPage());
    }

    @Test
    public void isAllowedOnChannelSelectionPageReturnsFalseWithVrmAndFeatureToggleOff() {

        motrSession = new MotrSession(false);
        motrSession.setVrm(VRM);

        assertFalse(motrSession.isAllowedOnChannelSelectionPage());
    }

    @Test
    public void getSmsFeatureToggleValueReturnsTrueWithToggleOn() {

        assertTrue(motrSession.isSmsFeatureToggleOn());
    }

    @Test
    public void getSmsFeatureToggleValueReturnsFalseWithToggleOff() {

        motrSession = new MotrSession(false);

        assertFalse(motrSession.isSmsFeatureToggleOn());
    }
}
