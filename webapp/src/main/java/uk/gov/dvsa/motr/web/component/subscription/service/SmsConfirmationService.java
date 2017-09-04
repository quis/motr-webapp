package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SmsConfirmationRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.InvalidSmsConfirmationIdUsedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SmsConfirmationCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SmsConfrimationCreationFailedEvent;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.ConfirmationCodeGenerator.generateCode;

public class SmsConfirmationService {

    private SmsConfirmationRepository smsConfirmationRepository;
    private NotifyService notifyService;
    private UrlHelper urlHelper;

    private static final int ATTEMPTS = 0;
    private static final int RESEND_ATTEMPTS = 0;

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

    public String handleSmsConfirmationCreation(String vrm, String phoneNumber, String confirmationId) {

        createSmsConfirmation(vrm, phoneNumber, generateCode(), confirmationId);

        return urlHelper.phoneConfirmationLink();
    }

    //TODO: Take out vrm if not needed
    public String verifySmsConfirmation(String vrm, String phoneNumber, String confirmationId, String confirmationCode)
            throws InvalidConfirmationIdException {

        SmsConfirmation smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId)
                .orElseThrow(() -> {
                    EventLogger.logEvent(new InvalidSmsConfirmationIdUsedEvent().setUsedId(confirmationId).setPhoneNumber(phoneNumber));
                    return new InvalidConfirmationIdException();
                });

        if (smsConfirmation.getCode().equals(confirmationCode)
                && smsConfirmation.getPhoneNumber().equals(phoneNumber)) {

            return urlHelper.confirmSubscriptionLink(confirmationId);
        } else {

            //TODO: What to return if code is invalid? urLHelper
            return "";
        }
    }

    public String resendSms(String phoneNumber, String confirmationId)
            throws InvalidConfirmationIdException {

        SmsConfirmation smsConfirmation = smsConfirmationRepository.findByConfirmationId(confirmationId)
                .orElseThrow(() -> {
                    EventLogger.logEvent(new InvalidSmsConfirmationIdUsedEvent().setUsedId(confirmationId).setPhoneNumber(phoneNumber));
                    return new InvalidConfirmationIdException();
                });

        notifyService.sendPhoneNumberConfirmationSms(phoneNumber, smsConfirmation.getCode());

        return urlHelper.phoneConfirmationLink();
    }

    //TODO: Add params
    /**
     * Creates pending SMS subscription in the system to be verified by the user right away
     * @param phoneNumber           subscription phone number
     * @param confirmationCode      subscription confirmation code
     */
    private void createSmsConfirmation(String vrm, String phoneNumber, String confirmationCode, String confirmationId) {

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setPhoneNumber(phoneNumber)
                .setCode(confirmationCode)
                .setVrm(vrm)
                .setConfirmationId(confirmationId)
                .setAttempts(ATTEMPTS)
                .setResendAttempts(RESEND_ATTEMPTS);

        try {

            smsConfirmationRepository.save(smsConfirmation);
            notifyService.sendPhoneNumberConfirmationSms(phoneNumber, confirmationCode);
            EventLogger.logEvent(
                    new SmsConfirmationCreatedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode));
        } catch (Exception e) {
            EventLogger.logErrorEvent(
                    new SmsConfrimationCreationFailedEvent().setPhoneNumber(phoneNumber).setConfirmationCode(confirmationCode), e);
            throw e;
        }
    }
}
