package uk.gov.dvsa.motr.web.validator;

import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class PhoneNumberValidator {

    private static final String EMPTY_PHONE_NUMBER_MESSAGE = "Enter your mobile number";
    private static final String INVALID_PHONE_NUMBER_MESSAGE = "Enter a valid UK mobile number";
    private static final String TOO_MANY_SUBSCRIPTIONS = "You can’t subscribe right now. You have already subscribed to two" +
            " MOT reminders at this phone number";

    private String message;

    private final SubscriptionService subscriptionService;

    @Inject
    public PhoneNumberValidator(SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    public boolean isValid(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            message = EMPTY_PHONE_NUMBER_MESSAGE;

            return false;
        }

        if (!subscriptionService.hasMaxTwoSubscriptionsForPhoneNumber(phoneNumber)) {
            message = TOO_MANY_SUBSCRIPTIONS;

            return false;
        }

        return validate(phoneNumber);
    }

    public String getMessage() {

        return message;
    }

    private boolean validate(String phoneNumber) {

        Pattern validationRegex = Pattern.compile("07\\d{9}");

        Matcher matcher = validationRegex.matcher(phoneNumber);

        if (!matcher.matches()) {
            message = INVALID_PHONE_NUMBER_MESSAGE;

            return false;
        }

        return true;
    }
}
