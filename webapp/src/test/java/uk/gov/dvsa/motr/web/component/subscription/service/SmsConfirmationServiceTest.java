package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SmsConfirmationRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class SmsConfirmationServiceTest {

    private final SmsConfirmationRepository smsConfirmationRepository = mock(SmsConfirmationRepository.class);
    private final NotifyService notifyService = mock(NotifyService.class);
    private final UrlHelper urlHelper = mock(UrlHelper.class);

    private static final String TEST_VRM = "TEST-REG";
    private static final String INCORRECT_TEST_VRM = "TEST-REG-123";
    private static final String MOBILE = "07912345678";
    private static final String INCORRECT_MOBILE = "07777777777";
    private static final String CONFIRMATION_ID = "Asd";
    private static final String CONFIRMATION_CODE = "123456";
    private static final String INCORRECT_CONFIRMATION_CODE = "654321";
    private static final int INITIAL_ATTEMPTS = 0;
    private static final int INITIAL_RESEND_ATTEMPTS = 0;
    private static final String PHONE_CONFIRMATION_LINK = "PHONE_CONFIRMATION_LINK";

    private SmsConfirmationService smsConfirmationService;

    @Before
    public void setUp() {

        this.smsConfirmationService = new SmsConfirmationService(
                smsConfirmationRepository,
                notifyService,
                urlHelper
        );

        when(urlHelper.phoneConfirmationLink()).thenReturn(PHONE_CONFIRMATION_LINK);
    }

    @Test(expected = RuntimeException.class)
    public void whenDbSaveFailsConfirmationSmsIsNotSent() throws Exception {

        doThrow(new RuntimeException()).when(smsConfirmationRepository).save(any(SmsConfirmation.class));

        this.smsConfirmationService.createSmsConfirmation(TEST_VRM, MOBILE, CONFIRMATION_CODE, CONFIRMATION_ID);
        verify(smsConfirmationRepository, times(1)).save(any(SmsConfirmation.class));
        verifyZeroInteractions(notifyService);
    }

    @Test
    public void createSmsConfirmationCallsDbToSaveDetailsAndSendsNotification() throws Exception {

        doNothing().when(notifyService).sendPhoneNumberConfirmationSms(MOBILE, CONFIRMATION_CODE);

        this.smsConfirmationService.createSmsConfirmation(TEST_VRM, MOBILE, CONFIRMATION_CODE, CONFIRMATION_ID);

        verify(smsConfirmationRepository, times(1)).save(any(SmsConfirmation.class));
        verify(notifyService, times(1)).sendPhoneNumberConfirmationSms(MOBILE,CONFIRMATION_CODE);
    }

    @Test
    public void handleSmsConfirmationCreationWillCreateSmsConfirmation() {

        ArgumentCaptor<SmsConfirmation> smsConfirmationArgumentCaptor = ArgumentCaptor.forClass(SmsConfirmation.class);

        String redirectUri = this.smsConfirmationService.handleSmsConfirmationCreation(TEST_VRM, MOBILE, CONFIRMATION_ID);

        verify(smsConfirmationRepository, times(1)).save(smsConfirmationArgumentCaptor.capture());
        verify(notifyService, times(1)).sendPhoneNumberConfirmationSms(any(), any());
        assertEquals(smsConfirmationArgumentCaptor.getValue().getAttempts(), INITIAL_ATTEMPTS);
        assertEquals(smsConfirmationArgumentCaptor.getValue().getPhoneNumber(), MOBILE);
        assertEquals(smsConfirmationArgumentCaptor.getValue().getVrm(), TEST_VRM);
        assertEquals(smsConfirmationArgumentCaptor.getValue().getConfirmationId(), CONFIRMATION_ID);
        assertEquals(smsConfirmationArgumentCaptor.getValue().getResendAttempts(), INITIAL_RESEND_ATTEMPTS);
        assertEquals(PHONE_CONFIRMATION_LINK, redirectUri);
    }

    @Test
    public void verifySmsConfirmationCodeWillReturnTrueWhenCodeIsValidForThatRecord() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE)
                .setConfirmationId(CONFIRMATION_ID)
                .setPhoneNumber(MOBILE)
                .setVrm(TEST_VRM);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        boolean smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                TEST_VRM, MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertTrue(smsConfirmationCodeVerified);
    }

    @Test(expected = InvalidConfirmationIdException.class)
    public void verifySmsConfirmationCodeWillThrowInvalidConfirmationIdExceptionWhenNoRecordIsFound() throws Exception {

        withExpectedSmsConfirmation(Optional.empty());

        this.smsConfirmationService.verifySmsConfirmationCode(TEST_VRM, MOBILE, CONFIRMATION_ID, INCORRECT_CONFIRMATION_CODE);
    }

    @Test
    public void verifySmsConfirmationCodeWillReturnFalseWhenCodeIsNotValidForThatRecord() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE)
                .setConfirmationId(CONFIRMATION_ID)
                .setPhoneNumber(MOBILE)
                .setVrm(TEST_VRM);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        boolean smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                TEST_VRM, MOBILE, CONFIRMATION_ID, INCORRECT_CONFIRMATION_CODE);

        assertFalse(smsConfirmationCodeVerified);
    }

    @Test
    public void verifySmsConfirmationCodeWillReturnFalseWhenVrmDoesNotMatchForThatRecord() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE)
                .setConfirmationId(CONFIRMATION_ID)
                .setPhoneNumber(MOBILE)
                .setVrm(TEST_VRM);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        boolean smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                INCORRECT_TEST_VRM, MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertFalse(smsConfirmationCodeVerified);
    }

    @Test
    public void verifySmsConfirmationCodeWillReturnFalseWhenPhoneNumberDoesNotMatchForThatRecord() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE)
                .setConfirmationId(CONFIRMATION_ID)
                .setPhoneNumber(MOBILE)
                .setVrm(TEST_VRM);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        boolean smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                TEST_VRM, INCORRECT_MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertFalse(smsConfirmationCodeVerified);
    }

    @Test
    public void resendSmsWillCorrectlyResendSmsWithTheSameConfirmationCode() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        String redirectUri = this.smsConfirmationService.resendSms(MOBILE, CONFIRMATION_ID);

        verify(notifyService, times(1)).sendPhoneNumberConfirmationSms(MOBILE, CONFIRMATION_CODE);
        assertEquals(PHONE_CONFIRMATION_LINK, redirectUri);
    }

    @Test(expected = InvalidConfirmationIdException.class)
    public void resendSmsWillThrowInvalidConfirmationIdExceptionWhenNoRecordIsFound() throws Exception {

        withExpectedSmsConfirmation(Optional.empty());

        this.smsConfirmationService.resendSms(MOBILE, CONFIRMATION_ID);
    }

    private void withExpectedSmsConfirmation(Optional<SmsConfirmation> smsConfirmation) {

        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(smsConfirmation);
    }
}