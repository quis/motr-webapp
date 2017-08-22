package uk.gov.dvsa.motr.web.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {

    private static final String EMPTY_PHONE_NUMBER_MESSAGE = "Enter your mobile number";
    private static final String INVALID_PHONE_NUMBER_MESSAGE = "Enter a valid UK mobile number";

    private String message;

    public boolean isValid(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            message = EMPTY_PHONE_NUMBER_MESSAGE;

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
