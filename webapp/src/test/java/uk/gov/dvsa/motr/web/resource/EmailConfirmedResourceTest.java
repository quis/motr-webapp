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
import uk.gov.dvsa.motr.web.cookie.EmailConfirmationParams;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConfirmedResourceTest {

    private static final String CONFIRMATION_ID = "asdadasd";
    private static final String UNSUBSCRIBE_ID = "unsubscribe_id";
    private static final String EMAIL = "email";
    private static final String VRM = "vrm";
    private static final String TEST_NUMBER = "12345";
    private static final String DVLA_ID = "54321";

    public static final LocalDate DATE = LocalDate.now();
    private TemplateEngineStub engine = new TemplateEngineStub();
    private EmailConfirmedResource resource;
    private SubscriptionConfirmationService pendingSubscriptionActivatorService = mock(SubscriptionConfirmationService.class);
    private UrlHelper urlHelper = mock(UrlHelper.class);
    private MotrSession motrSession = mock(MotrSession.class);

    @Before
    public void setup() throws SubscriptionAlreadyExistsException, InvalidConfirmationIdException {

        resource = new EmailConfirmedResource(
                engine,
                pendingSubscriptionActivatorService,
                motrSession,
                urlHelper
        );

        when(urlHelper.emailConfirmedFirstTimeLink()).thenReturn("confirm-email/confirmed");
    }

    @Test
    public void subscriptionIsCreatedWhenUserConfirmsEmail() throws Exception {

        mockSubscription(TEST_NUMBER, null);
        ArgumentCaptor<EmailConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(EmailConfirmationParams.class);

        Response response = resource.confirmEmailGet(CONFIRMATION_ID);

        verify(pendingSubscriptionActivatorService, times(1)).confirmSubscription(CONFIRMATION_ID);
        verify(motrSession, times(1)).setEmailConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("confirm-email/confirmed", response.getLocation().toString());
        assertEquals(VRM, paramsArgumentCaptor.getValue().getRegistration());
    }

    @Test
    public void sessionIsClearedPendingSubscriptionConfirmed() throws Exception {

        mockSubscription(TEST_NUMBER, null);
        resource.confirmEmailGet(CONFIRMATION_ID);

        verify(motrSession).clear();
    }

    @Test
    public void dataLayerIsBeingStoredOnFirstVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerStoresDvlaIdWhenMotTestNumberNotPresent() {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailFirstTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsBeingStoredOnNthVisit() throws Exception {

        motrSessionWillReturnValidPageParams();

        resource.confirmEmailNthTimeGet();
        verifyDataLayer(getDataLayer());
    }

    @Test
    public void dataLayerIsPopulatedWithDvlaIdWhenMotTestNumberNotPresent() throws Exception {

        EmailConfirmationParams confirmationParams = new EmailConfirmationParams();
        confirmationParams.setRegistration(VRM);
        confirmationParams.setDvlaId(DVLA_ID);
        when(motrSession.getEmailConfirmationParams()).thenReturn(confirmationParams);

        resource.confirmEmailFirstTimeGet();

        assertEquals("{\"vrm\":\"vrm\",\"dvla-id\":\"54321\"}", getDataLayer());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionDoesntExist() throws Exception {

        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenThrow(InvalidConfirmationIdException.class);

        resource.confirmEmailGet(CONFIRMATION_ID);

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnFirstVisit() throws Exception {

        resource.confirmEmailFirstTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    @Test
    public void errorPageIsShownWhenSubscriptionInNotPresentInTheSessionOnNthVisit() throws Exception {

        resource.confirmEmailNthTimeGet();

        assertEquals("subscription-error", engine.getTemplate());
    }

    private void motrSessionWillReturnValidPageParams() {

        EmailConfirmationParams confirmationParams = new EmailConfirmationParams();
        confirmationParams.setRegistration(VRM);
        confirmationParams.setMotTestNumber(TEST_NUMBER);
        when(motrSession.getEmailConfirmationParams()).thenReturn(confirmationParams);
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
                .setMotIdentification(motIdentification);
        when(pendingSubscriptionActivatorService.confirmSubscription(CONFIRMATION_ID)).thenReturn(subscription);
    }
}
