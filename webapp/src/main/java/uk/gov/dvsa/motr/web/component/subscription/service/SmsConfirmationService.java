package uk.gov.dvsa.motr.web.component.subscription.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SmsConfirmationRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.InvalidSmsConfirmationIdUsedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SmsConfirmationCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SmsConfrimationCreationFailedEvent;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.ConfirmationCodeGenerator.generateCode;

import static java.time.temporal.ChronoUnit.SECONDS;

public class SmsConfirmationService {

    public enum Confirmation {
        CODE_NOT_VALID_RESEND_ALLOWED,
        CODE_NOT_VALID_RESEND_NOT_ALLOWED_TIME_LIMITED,
        CODE_NOT_VALID_MAX_ATTEMPTS_REACHED_RESEND_ALLOWED,
        CODE_NOT_VALID_MAX_ATTEMPTS_REACHED_RESEND_NOT_ALLOWED_TIME_LIMITED,
        CODE_VALID
    }

    private SmsConfirmationRepository smsConfirmationRepository;
    private NotifyService notifyService;
    private UrlHelper urlHelper;

    private static Logger LOGGER = LoggerFactory.getLogger(SmsConfirmationService.class);
    private static final int ATTEMPTS = 0;
    private static final int RESEND_ATTEMPTS = 0;
    private static final int MAX_ATTEMPTS = 3;
    private static final int MAX_RESEND_ATTEMPTS_BEFORE_RATE_LIMIT = 2;
    private static final int MIN_TIME_SECONDS_BETWEEN_RESENDS = 300;

    @Inject
    public SmsConfirmationService(
            SmsConfirmationRepository smsConfirmationRepository,
            NotifyService notifyService,
            UrlHelper urlHelper
    ) {

        this.smsConfirmationRepository = smsConfirmationRepository;
        this.notifyService = notifyService;
        this.urlHelper = urlHelper;
    }

    public String handleSmsConfirmationCreation(String vrm, String phoneNumber, String confirmationId)
            throws InvalidConfirmationIdException {

        String confirmationCode = generateCode();
        try {
            Optional<SmsConfirmation> smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId);
            if (!smsConfirmation.isPresent()) {
                createSmsConfirmation(vrm, phoneNumber, confirmationCode, confirmationId);


                SmsConfirmation confirmation = new SmsConfirmation()
                        .setPhoneNumber(phoneNumber)
                        .setCode(confirmationCode)
                        .setVrm(vrm)
                        .setConfirmationId(confirmationId)
                        .setAttempts(ATTEMPTS)
                        .setResendAttempts(RESEND_ATTEMPTS);

                smsConfirmationRepository.save(confirmation, true);
                notifyService.sendPhoneNumberConfirmationSms(phoneNumber, confirmationCode);
                EventLogger.logEvent(
                        new SmsConfirmationCreatedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode));

            } else if (smsSendingNotRestrictedByRateLimiting(vrm, phoneNumber, confirmationId)) {
                notifyService.sendPhoneNumberConfirmationSms(phoneNumber, confirmationCode);
                EventLogger.logEvent(
                        new SmsConfirmationCreatedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode));
            }
        } catch (Exception e) {
            EventLogger.logErrorEvent(
                    new SmsConfrimationCreationFailedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode), e);
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
                && smsConfirmation.getVrm().equals(vrm)) {
            return Confirmation.CODE_VALID;
        }

        incrementAttempts(smsConfirmation);

        boolean maxResendsBeforeLimitingReached = smsConfirmation.getResendAttempts() >= MAX_RESEND_ATTEMPTS_BEFORE_RATE_LIMIT;
        boolean insideTimeRestriction =
                SECONDS.between(smsConfirmation.getLatestResendAttempt(), ZonedDateTime.now()) < MIN_TIME_SECONDS_BETWEEN_RESENDS;

        if (smsConfirmation.getAttempts() < MAX_ATTEMPTS - 1) {
            if (maxResendsBeforeLimitingReached && insideTimeRestriction) {
                return Confirmation.CODE_NOT_VALID_RESEND_NOT_ALLOWED_TIME_LIMITED;
            }
            return Confirmation.CODE_NOT_VALID_RESEND_ALLOWED;
        }

        if (maxResendsBeforeLimitingReached && insideTimeRestriction) {
            return Confirmation.CODE_NOT_VALID_MAX_ATTEMPTS_REACHED_RESEND_NOT_ALLOWED_TIME_LIMITED;
        }

        return Confirmation.CODE_NOT_VALID_MAX_ATTEMPTS_REACHED_RESEND_ALLOWED;
    }

    public boolean smsSendingNotRestrictedByRateLimiting(String vrm, String phoneNumber, String confirmationId)
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

        incrementResentAttempts(smsConfirmation);

        notifyService.sendPhoneNumberConfirmationSms(phoneNumber, smsConfirmation.getCode());

        return urlHelper.phoneConfirmationLink();
    }

    /**
     * Creates pending SMS subscription in the system to be verified by the user right away
     * @param vrm                   Vehicle's Registration
     * @param phoneNumber           Subscription phone number
     * @param confirmationCode      Subscription confirmation code
     * @param confirmationId      Subscription confirmation ID - used to link with a pending subscription
     */
    private void createSmsConfirmation(String vrm, String phoneNumber, String confirmationCode, String confirmationId) {


    }

    private void incrementAttempts(SmsConfirmation smsConfirmation) {

        SmsConfirmation newSmsConfirmation = copySmSConfirmation(smsConfirmation)
                .setAttempts(smsConfirmation.getAttempts() + 1);
        smsConfirmationRepository.save(newSmsConfirmation, false);
    }

    private void incrementResentAttempts(SmsConfirmation smsConfirmation) {

        SmsConfirmation newSmsConfirmation = copySmSConfirmation(smsConfirmation)
                .setResendAttempts(smsConfirmation.getResendAttempts() + 1)
                .setAttempts(ATTEMPTS);
        smsConfirmationRepository.save(newSmsConfirmation, true);
    }

    private SmsConfirmation copySmSConfirmation(SmsConfirmation smsConfirmation) {

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
