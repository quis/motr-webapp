package uk.gov.dvsa.motr.web.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsConfirmationCodeValidator implements Validator {

    public static final String EMPTY_CONFIRMATION_CODE_MESSAGE = "Enter 6-digit code from text message<br/>" +
            "<br/>It can take a couple of minutes for the text to arrive.";
    public static final String EMPTY_CONFIRMATION_CODE_MESSAGE_AT_FIELD = "Enter 6 digits from text message";
    public static final String INVALID_CONFIRMATION_CODE_MESSAGE = "Entered code is invalid<br/>" +
            "<br/>Enter 6 digits you received in text message";
    public static final String INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD = "Enter 6 digits from text message";

    private String message;
    private String messageAtField;

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getMessageAtField() {

        return messageAtField;
    }

    public void setMessageAtField(String messageAtField) {

        this.messageAtField = messageAtField;
    }

    public boolean isValid(String confirmationCode) {

        if (confirmationCode == null || confirmationCode.isEmpty()) {
            message = EMPTY_CONFIRMATION_CODE_MESSAGE;
            messageAtField = EMPTY_CONFIRMATION_CODE_MESSAGE_AT_FIELD;

            return false;
        }

        return validate(confirmationCode);
    }

    private boolean validate(String confirmationCode) {

        Pattern validationRegex = Pattern.compile("\\d{6}");

        Matcher matcher = validationRegex.matcher(confirmationCode);

        if (!matcher.matches()) {
            message = INVALID_CONFIRMATION_CODE_MESSAGE;
            messageAtField = INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD;

            return false;
        }

        return true;
    }
}
