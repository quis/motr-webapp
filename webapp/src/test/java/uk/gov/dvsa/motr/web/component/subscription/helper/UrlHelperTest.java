package uk.gov.dvsa.motr.web.component.subscription.helper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlHelperTest {

    private static final String baseUri = "http://test-url/";
    private UrlHelper urlHelper;

    @Before
    public void setup() {
        this.urlHelper = new UrlHelper(baseUri);
    }

    @Test
    public void testConfirmEmailUri() throws Exception {

        assertEquals(baseUri + "confirm-subscription/AAAAb", urlHelper.confirmSubscriptionLink("AAAAb"));
    }

    @Test
    public void testEmailConfirmationPending() throws Exception {

        assertEquals(baseUri + "email-confirmation-pending", urlHelper.emailConfirmationPendingLink());
    }

    @Test
    public void testEmailConfirmedFirstTimeLink() throws Exception {

        assertEquals(baseUri + "confirm-subscription/confirmed", urlHelper.subscriptionConfirmedFirstTimeLink());
    }

    @Test
    public void testPhoneConfirmedNthTimeLink() throws Exception {

        assertEquals(baseUri + "confirm-subscription/confirmed", urlHelper.phoneConfirmedNthTimeLink());
    }

    @Test
    public void testEmailAlreadyConfirmedLink() throws Exception {

        assertEquals(baseUri + "confirm-subscription/already-confirmed", urlHelper.emailConfirmedNthTimeLink());
    }

    @Test
    public void testPhoneConfirmationLink() throws Exception {

        assertEquals(baseUri + "confirm-phone", urlHelper.phoneConfirmationLink());
    }

    @Test
    public void testUnsubscribeUri() throws Exception {

        assertEquals(baseUri + "unsubscribe/AAAA", urlHelper.unsubscribeLink("AAAA"));
    }
}
