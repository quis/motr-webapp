package uk.gov.dvsa.motr.notifications.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent;
import uk.gov.dvsa.motr.web.formatting.DateFormatter;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent.Type.EMAIL_CONFIRMATION;
import static uk.gov.dvsa.motr.web.eventlog.subscription.NotifyClientFailedEvent.Type.SUBSCRIPTION_CONFIRMATION;

public class NotifyService {

    private NotificationClient notificationClient;
    private String subscriptionConfirmationTemplateId;
    private String emailConfirmationTemplateId;

    public NotifyService(
            NotificationClient notificationClient,
            String subscriptionConfirmationTemplateId,
            String emailConfirmationTemplateId
    ) {

        this.notificationClient = notificationClient;
        this.subscriptionConfirmationTemplateId = subscriptionConfirmationTemplateId;
        this.emailConfirmationTemplateId = emailConfirmationTemplateId;
    }

    public void sendSubscriptionConfirmationEmail(
            String emailAddress,
            String vehicleDetails,
            LocalDate motExpiryDate,
            String unsubscribeLink,
            MotIdentification motIdentification
    ) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("vehicle_details", vehicleDetails);
        personalisation.put("mot_expiry_date", DateFormatter.asDisplayDate(motExpiryDate));
        personalisation.put("unsubscribe_link", unsubscribeLink);

        if (motIdentification.getDvlaId().isPresent()) {
            personalisation.put("is_due_or_expires", "is due");
        } else {
            personalisation.put("is_due_or_expires", "expires");
        }

        try {

            notificationClient.sendEmail(subscriptionConfirmationTemplateId, emailAddress, personalisation, "");

        } catch (NotificationClientException e) {

            EventLogger.logErrorEvent(new NotifyClientFailedEvent().setEmail(emailAddress).setType(SUBSCRIPTION_CONFIRMATION), e);
            // wrapping because nothing can be done about it
            throw new RuntimeException(e);
        }
    }

    public void sendEmailAddressConfirmationEmail(String emailAddress, String confirmationLink, String vehicleDetails) {

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("confirmation_link", confirmationLink);
        personalisation.put("vehicle_details", vehicleDetails);

        try {

            notificationClient.sendEmail(emailConfirmationTemplateId, emailAddress, personalisation, "");

        } catch (NotificationClientException e) {

            EventLogger.logErrorEvent(new NotifyClientFailedEvent().setEmail(emailAddress).setType(EMAIL_CONFIRMATION), e);
            // wrapping because nothing can be done about it
            throw new RuntimeException(e);
        }
    }
}
