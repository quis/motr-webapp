package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;
import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSmsConfirmationRepository;
import uk.gov.dvsa.motr.web.cookie.MotrSession;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService.Confirmation.CODE_NOT_VALID;
import static uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService.Confirmation.CODE_NOT_VALID_MAX_ATTEMPTS_REACHED;
import static uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService.Confirmation.CODE_VALID;

public class SmsConfirmationServiceTest {

    private DynamoDbSmsConfirmationRepository smsConfirmationRepository;
    private final NotifyService notifyService = mock(NotifyService.class);
    private final UrlHelper urlHelper = mock(UrlHelper.class);
    private final MotrSession motrSession = mock(MotrSession.class);

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

        smsConfirmationRepository = mock(DynamoDbSmsConfirmationRepository.class);

        this.smsConfirmationService = new SmsConfirmationService(
                smsConfirmationRepository,
                notifyService,
                urlHelper,
                motrSession
        );

        when(urlHelper.phoneConfirmationLink()).thenReturn(PHONE_CONFIRMATION_LINK);
    }

    @Test
    public void handleSmsConfirmationCreationWillCreateSmsConfirmation() throws InvalidConfirmationIdException {

        ArgumentCaptor<SmsConfirmation> smsConfirmationArgumentCaptor = ArgumentCaptor.forClass(SmsConfirmation.class);
        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(Optional.empty());

        String redirectUri = this.smsConfirmationService.handleSmsConfirmationCreation(TEST_VRM, MOBILE, CONFIRMATION_ID);

        verify(smsConfirmationRepository, times(1)).saveWithResendTimestampUpdate(smsConfirmationArgumentCaptor.capture());
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

        SmsConfirmationService.Confirmation smsConfirmationCodeVerified =
                this.smsConfirmationService.verifySmsConfirmationCode(TEST_VRM, MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertEquals(CODE_VALID.name(), smsConfirmationCodeVerified.name());
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

        SmsConfirmationService.Confirmation smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                TEST_VRM, MOBILE, CONFIRMATION_ID, INCORRECT_CONFIRMATION_CODE);

        assertEquals(CODE_NOT_VALID, smsConfirmationCodeVerified);
    }

    @Test
    public void verifySmsConfirmationCodeWillReturnFalseWhenVrmDoesNotMatchForThatRecord() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE)
                .setConfirmationId(CONFIRMATION_ID)
                .setPhoneNumber(MOBILE)
                .setVrm(TEST_VRM);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        SmsConfirmationService.Confirmation smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                INCORRECT_TEST_VRM, MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertEquals(CODE_NOT_VALID, smsConfirmationCodeVerified);
    }

    @Test
    public void verifySmsConfirmationCodeWillReturnFalseWhenPhoneNumberDoesNotMatchForThatRecord() throws Exception {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setCode(CONFIRMATION_CODE)
                .setConfirmationId(CONFIRMATION_ID)
                .setPhoneNumber(MOBILE)
                .setVrm(TEST_VRM);

        withExpectedSmsConfirmation(Optional.of(smsConfirmation));

        SmsConfirmationService.Confirmation smsConfirmationCodeVerified = this.smsConfirmationService.verifySmsConfirmationCode(
                TEST_VRM, INCORRECT_MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertEquals(CODE_NOT_VALID, smsConfirmationCodeVerified);
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

    @Test
    public void whenThereIsAnExistingConfirmation_AndResendNotRestricted_NewConfirmationCreated() throws InvalidConfirmationIdException {

        ArgumentCaptor<SmsConfirmation> smsConfirmationArgumentCaptor = ArgumentCaptor.forClass(SmsConfirmation.class);

        SmsConfirmation existingConfirmation = new SmsConfirmation();
        existingConfirmation.setResendAttempts(0);
        existingConfirmation.setLatestResendAttempt(LocalDateTime.now());
        existingConfirmation.setCode(CONFIRMATION_CODE);
        existingConfirmation.setConfirmationId(CONFIRMATION_ID);
        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(Optional.of(existingConfirmation));

        String redirectUri = this.smsConfirmationService.handleSmsConfirmationCreation(TEST_VRM, MOBILE, CONFIRMATION_ID);

        verify(smsConfirmationRepository, times(1)).saveWithResendTimestampUpdate(smsConfirmationArgumentCaptor.capture());
        verify(notifyService, times(1)).sendPhoneNumberConfirmationSms(any(), any());
        assertEquals(smsConfirmationArgumentCaptor.getValue().getAttempts(), INITIAL_ATTEMPTS);
        assertEquals(smsConfirmationArgumentCaptor.getValue().getConfirmationId(), CONFIRMATION_ID);
        assertEquals(smsConfirmationArgumentCaptor.getValue().getResendAttempts(), INITIAL_RESEND_ATTEMPTS + 1);
        assertEquals(PHONE_CONFIRMATION_LINK, redirectUri);
    }

    @Test
    public void whenThereIsAnExistingConfirmation_AndResendIsRestricted_NewConfirmationIsNotCreated()
            throws InvalidConfirmationIdException {

        SmsConfirmation existingConfirmation = new SmsConfirmation();
        existingConfirmation.setResendAttempts(4);
        existingConfirmation.setLatestResendAttempt(LocalDateTime.now());
        existingConfirmation.setCode(CONFIRMATION_CODE);
        existingConfirmation.setConfirmationId(CONFIRMATION_ID);
        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(Optional.of(existingConfirmation));

        this.smsConfirmationService.handleSmsConfirmationCreation(TEST_VRM, MOBILE, CONFIRMATION_ID);

        verify(notifyService, times(0)).sendPhoneNumberConfirmationSms(any(), any());
        verify(motrSession, times(1)).setSmsConfirmResendLimited(true);
    }

    @Test
    public void whenCodeNotValidAndMaxAttemptsReached_thenCorrectResponseIsReturned() throws InvalidConfirmationIdException {

        SmsConfirmation existingConfirmation = new SmsConfirmation();
        existingConfirmation.setAttempts(2);
        existingConfirmation.setLatestResendAttempt(LocalDateTime.now());
        existingConfirmation.setCode(CONFIRMATION_CODE);
        existingConfirmation.setConfirmationId(CONFIRMATION_ID);
        existingConfirmation.setPhoneNumber(MOBILE);
        existingConfirmation.setVrm(TEST_VRM);
        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(Optional.of(existingConfirmation));

        SmsConfirmationService.Confirmation confirmation =
                this.smsConfirmationService.verifySmsConfirmationCode(TEST_VRM, MOBILE, CONFIRMATION_ID, "XXXX");

        assertEquals(CODE_NOT_VALID_MAX_ATTEMPTS_REACHED.name(), confirmation.name());
    }

    @Test
    public void whenCodeIsValidButMaxAttemptsPreviouslyReached_thenCorrectResponseIsReturned() throws InvalidConfirmationIdException {

        SmsConfirmation existingConfirmation = new SmsConfirmation();
        existingConfirmation.setAttempts(3);
        existingConfirmation.setLatestResendAttempt(LocalDateTime.now());
        existingConfirmation.setCode(CONFIRMATION_CODE);
        existingConfirmation.setConfirmationId(CONFIRMATION_ID);
        existingConfirmation.setPhoneNumber(MOBILE);
        existingConfirmation.setVrm(TEST_VRM);
        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(Optional.of(existingConfirmation));

        SmsConfirmationService.Confirmation confirmation =
                this.smsConfirmationService.verifySmsConfirmationCode(TEST_VRM, MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertEquals(CODE_NOT_VALID_MAX_ATTEMPTS_REACHED, confirmation);
    }

    @Test
    public void whenCodeIsValidAndMaxAttemptsNotReached_thenCorrectResponseIsReturned() throws InvalidConfirmationIdException {

        SmsConfirmation existingConfirmation = new SmsConfirmation();
        existingConfirmation.setAttempts(2);
        existingConfirmation.setLatestResendAttempt(LocalDateTime.now());
        existingConfirmation.setCode(CONFIRMATION_CODE);
        existingConfirmation.setConfirmationId(CONFIRMATION_ID);
        existingConfirmation.setPhoneNumber(MOBILE);
        existingConfirmation.setVrm(TEST_VRM);
        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(Optional.of(existingConfirmation));

        SmsConfirmationService.Confirmation confirmation =
                this.smsConfirmationService.verifySmsConfirmationCode(TEST_VRM, MOBILE, CONFIRMATION_ID, CONFIRMATION_CODE);

        assertEquals(CODE_VALID, confirmation);
    }

    private void withExpectedSmsConfirmation(Optional<SmsConfirmation> smsConfirmation) {

        when(smsConfirmationRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(smsConfirmation);
    }
}
