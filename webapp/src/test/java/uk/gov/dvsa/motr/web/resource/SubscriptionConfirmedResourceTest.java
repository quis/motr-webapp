package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.SubscriptionConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionConfirmedResourceTest {

    private static final String CONFIRMATION_ID = "asdadasd";
    private static final String UNSUBSCRIBE_ID = "unsubscribe_id";
    private static final String EMAIL = "email";
    private static final String VRM = "vrm";
    private static final String CONTACT_TYPE = "EMAIL";
    private static final String TEST_NUMBER = "12345";
    private static final String DVLA_ID = "54321";

    public static final LocalDate DATE = LocalDate.now();
    private TemplateEngineStub engine = new TemplateEngineStub();
    private SubscriptionConfirmedResource resource;
    private SubscriptionConfirmationService pendingSubscriptionActivatorService = mock(SubscriptionConfirmationService.class);
    private UrlHelper urlHelper = mock(UrlHelper.class);
    private MotrSession motrSession = mock(MotrSession.class);

    @Before
    public void setup() throws SubscriptionAlreadyExistsException, InvalidConfirmationIdException {

        resource = new SubscriptionConfirmedResource(
                engine,
                pendingSubscriptionActivatorService,
                motrSession,
                urlHelper
        );

        when(urlHelper.emailConfirmedFirstTimeLink()).thenReturn("confirm-subscription/confirmed");
    }

    @Test
    public void subscriptionIsCreatedWhenUserConfirmsEmail() throws Exception {

        mockSubscription(TEST_NUMBER, null);
        ArgumentCaptor<SubscriptionConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(SubscriptionConfirmationParams.class);

        Response response = resource.confirmSubscriptionGet(CONFIRMATION_ID);

        verify(pendingSubscriptionActivatorService, times(1)).confirmSubscription(CONFIRMATION_ID);
        verify(motrSession, times(1)).setSubscriptionConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("confirm-subscription/confirmed", response.getLocation().toString());
        assertEquals(VRM, paramsArgumentCaptor.getValue().getRegistration());
    }

    @Test
    public void sessionIsClearedPendingSubscriptionConfirmed() throws Exception {

        mockSubscription(TEST_NUMBER, null);
        resource.confirmSubscriptionGet(CONFIRMATION_ID);

        verify(motrSession).clear();
    }

    @Test
    public void dataLayerIsBeingStoredOnFirstVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmSubscriptionFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerStoresDvlaIdWhenMotTestNumberNotPresent() {

        motrSessionWillReturnValidPageParams();

        resource.confirmSubscriptionFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsBeingStoredOnNthVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmSubscriptionNthTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsPopulatedWithDvlaIdWhenMotTestNumberNotPresent() throws Exception {

        SubscriptionConfirmationParams confirmationParams = new SubscriptionConfirmationParams();
        confirmationParams.setRegistration(VRM);
        confirmationParams.setDvlaId(DVLA_ID);
        confirmationParams.setContactType(CONTACT_TYPE);
        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);

        resource.confirmSubscriptionFirstTimeGet();

        assertEquals("{\"vrm\":\"vrm\",\"dvla-id\":\"54321\"}", getDataLayer());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionDoesntExist() throws Exception {

        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenThrow(InvalidConfirmationIdException.class);

        resource.confirmSubscriptionGet(CONFIRMATION_ID);

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnFirstVisit() throws Exception {

        resource.confirmSubscriptionFirstTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnNthVisit() throws Exception {

        resource.confirmSubscriptionNthTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    private void motrSessionWillReturnValidPageParams() {

        SubscriptionConfirmationParams confirmationParams = new SubscriptionConfirmationParams();
        confirmationParams.setRegistration(VRM);
        confirmationParams.setMotTestNumber(TEST_NUMBER);
        confirmationParams.setContactType(CONTACT_TYPE);
        when(motrSession.getSubscriptionConfirmationParams()).thenReturn(confirmationParams);
        when(motrSession.isAllowedOnChannelSelectionPage()).thenReturn(false);
        when(motrSession.isUsingSmsChannel()).thenReturn(false);
    }

    private String getDataLayer() {

        return engine.getContext(Map.class).get("dataLayer").toString();
    }

    private void verifyDataLayer(String dataLayer) {

        assertEquals("{\"vrm\":\"vrm\",\"mot-test-number\":\"12345\"}", dataLayer);
    }

    private void mockSubscription(String motTestNumber, String dvlaId) throws InvalidConfirmationIdException {

        MotIdentification motIdentification = new MotIdentification(motTestNumber, dvlaId);
        Subscription subscription = new Subscription()
                .setUnsubscribeId(UNSUBSCRIBE_ID)
                .setEmail(EMAIL)
                .setVrm(VRM)
                .setMotDueDate(DATE)
                .setContactType(Subscription.ContactType.EMAIL)
                .setMotIdentification(motIdentification);
        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenReturn(subscription);
    }
}
