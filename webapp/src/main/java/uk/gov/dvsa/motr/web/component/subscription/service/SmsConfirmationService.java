package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SmsConfirmationRepository;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.eventlog.subscription.InvalidSmsConfirmationIdUsedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.MaxConfirmationCodeEntriesAttemptedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SmsConfirmationCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SmsConfrimationCreationFailedEvent;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.ConfirmationCodeGenerator.generateCode;

import static java.time.temporal.ChronoUnit.SECONDS;

public class SmsConfirmationService {

    public enum Confirmation {
        CODE_NOT_VALID,
        CODE_NOT_VALID_MAX_ATTEMPTS_REACHED,
        CODE_VALID
    }

    private static final int INITIAL_ATTEMPTS = 0;
    private static final int INITIAL_RESEND_ATTEMPTS = 0;
    private static final int MAX_ATTEMPTS = 3;
    private static final int MAX_RESEND_ATTEMPTS_BEFORE_RATE_LIMIT = 1;
    private static final int MIN_TIME_SECONDS_BETWEEN_RESENDS = 600;

    private SmsConfirmationRepository smsConfirmationRepository;
    private NotifyService notifyService;
    private UrlHelper urlHelper;
    private MotrSession motrSession;

    @Inject
    public SmsConfirmationService(
            SmsConfirmationRepository smsConfirmationRepository,
            NotifyService notifyService,
            UrlHelper urlHelper,
            MotrSession motrSession
    ) {

        this.smsConfirmationRepository = smsConfirmationRepository;
        this.notifyService = notifyService;
        this.urlHelper = urlHelper;
        this.motrSession = motrSession;
    }

    public String handleSmsConfirmationCreation(String vrm, String phoneNumber, String confirmationId)
            throws InvalidConfirmationIdException {

        try {
            Optional<SmsConfirmation> smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId);
            if (!smsConfirmation.isPresent()) {

                String confirmationCode = generateCode();
                motrSession.setSmsConfirmResendLimited(false);
                SmsConfirmation confirmation = new SmsConfirmation()
                        .setPhoneNumber(phoneNumber)
                        .setCode(confirmationCode)
                        .setVrm(vrm)
                        .setConfirmationId(confirmationId)
                        .setAttempts(INITIAL_ATTEMPTS)
                        .setResendAttempts(INITIAL_RESEND_ATTEMPTS);

                smsConfirmationRepository.saveWithResendTimestampUpdate(confirmation);
                notifyService.sendPhoneNumberConfirmationSms(phoneNumber, confirmationCode);

                EventLogger.logEvent(
                        new SmsConfirmationCreatedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode));

            } else if (smsSendingNotRestrictedByRateLimiting(phoneNumber, confirmationId)) {

                motrSession.setSmsConfirmResendLimited(false);
                String confirmationCode = smsConfirmation.get().getCode();
                notifyService.sendPhoneNumberConfirmationSms(phoneNumber, confirmationCode);
                incrementResendAttempts(smsConfirmation.get());

                EventLogger.logEvent(
                        new SmsConfirmationCreatedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode));
            } else {

                motrSession.setSmsConfirmResendLimited(true);
            }
        } catch (Exception e) {

            EventLogger.logErrorEvent(
                    new SmsConfrimationCreationFailedEvent().setPhoneNumber(phoneNumber), e);
            throw e;
        }

        return urlHelper.phoneConfirmationLink();
    }

    public Confirmation verifySmsConfirmationCode(String vrm, String phoneNumber, String confirmationId, String confirmationCode)
            throws InvalidConfirmationIdException {

        SmsConfirmation smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId)
                .orElseThrow(() -> {
                    EventLogger.logEvent(new InvalidSmsConfirmationIdUsedEvent().setUsedId(confirmationId).setPhoneNumber(phoneNumber));
                    return new InvalidConfirmationIdException();
                });

        if (smsConfirmation.getCode().equals(confirmationCode)
                && smsConfirmation.getPhoneNumber().equals(phoneNumber)
                && smsConfirmation.getVrm().equals(vrm)
                && smsConfirmation.getAttempts() < MAX_ATTEMPTS) {
            return Confirmation.CODE_VALID;
        }

        if (smsConfirmation.getAttempts() < MAX_ATTEMPTS) {
            incrementAttemptsForThisConfirmationInDatabase(smsConfirmation);
        }

        if (smsConfirmation.getAttempts() < MAX_ATTEMPTS - 1) {
            return Confirmation.CODE_NOT_VALID;
        }

        EventLogger.logEvent(new MaxConfirmationCodeEntriesAttemptedEvent().setPhoneNumber(phoneNumber).setConfirmationId(confirmationId));
        return Confirmation.CODE_NOT_VALID_MAX_ATTEMPTS_REACHED;
    }

    public boolean smsSendingNotRestrictedByRateLimiting(String phoneNumber, String confirmationId)
            throws InvalidConfirmationIdException {

        SmsConfirmation smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId)
                .orElseThrow(() -> {
                    EventLogger.logEvent(new InvalidSmsConfirmationIdUsedEvent().setUsedId(confirmationId).setPhoneNumber(phoneNumber));
                    return new InvalidConfirmationIdException();
                });

        boolean maxResendsBeforeLimitingReached = smsConfirmation.getResendAttempts() >= MAX_RESEND_ATTEMPTS_BEFORE_RATE_LIMIT;
        boolean insideTimeRestriction =
                SECONDS.between(smsConfirmation.getLatestResendAttempt(), ZonedDateTime.now()) < MIN_TIME_SECONDS_BETWEEN_RESENDS;

        return !(maxResendsBeforeLimitingReached && insideTimeRestriction);
    }

    public String resendSms(String phoneNumber, String confirmationId)
            throws InvalidConfirmationIdException {

        SmsConfirmation smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId)
                .orElseThrow(() -> {
                    EventLogger.logEvent(new InvalidSmsConfirmationIdUsedEvent().setUsedId(confirmationId).setPhoneNumber(phoneNumber));
                    return new InvalidConfirmationIdException();
                });

        incrementResendAttempts(smsConfirmation);

        notifyService.sendPhoneNumberConfirmationSms(phoneNumber, smsConfirmation.getCode());

        return urlHelper.phoneConfirmationLink();
    }

    private void incrementAttemptsForThisConfirmationInDatabase(SmsConfirmation smsConfirmation) {

        SmsConfirmation newSmsConfirmation = copySmsConfirmation(smsConfirmation)
                .setAttempts(smsConfirmation.getAttempts() + 1);
        smsConfirmationRepository.save(newSmsConfirmation);
    }

    private void incrementResendAttempts(SmsConfirmation smsConfirmation) {

        SmsConfirmation newSmsConfirmation = copySmsConfirmation(smsConfirmation)
                .setResendAttempts(smsConfirmation.getResendAttempts() + 1)
                .setAttempts(INITIAL_ATTEMPTS);
        smsConfirmationRepository.saveWithResendTimestampUpdate(newSmsConfirmation);
    }

    private SmsConfirmation copySmsConfirmation(SmsConfirmation smsConfirmation) {

        return new SmsConfirmation()
                .setPhoneNumber(smsConfirmation.getPhoneNumber())
                .setCode(smsConfirmation.getCode())
                .setVrm(smsConfirmation.getVrm())
                .setConfirmationId(smsConfirmation.getConfirmationId())
                .setAttempts(smsConfirmation.getAttempts())
                .setResendAttempts(smsConfirmation.getResendAttempts())
                .setLatestResendAttempt(smsConfirmation.getLatestResendAttempt());
    }

}
