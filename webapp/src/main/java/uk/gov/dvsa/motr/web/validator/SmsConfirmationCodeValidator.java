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

    public static final String CODE_INCORRECT_3_TIMES_CAN_GET_NEW_CODE = "Code was entered incorrectly 3 times. <br/>" +
            "<br/>You can request a new code. ";
    public static final String CODE_INCORRECT_3_TIMES_CAN_GET_NEW_CODE_AT_FIELD = "???";

    public static final String CODE_INCORRECT_3_TIMES_CANNOT_GET_NEW_CODE = "You can’t subscribe now.<br/>" +
            "<br/>Code was entered incorrectly 3 times.<br/>" +
            "<br/>Come back later and try to subscribe again.";
    public static final String CODE_INCORRECT_3_TIMES_CANNOT_GET_NEW_CODE_AT_FIELD = "???";

    public static final String RESEND_LIMIT_REACHED = "Activation code was already resent.<br/>" +
            "<br/>It can take several minutes to arrive.<br/>" +
            "<br/>If you didn’t receive the code, come back later and try to subscribe again ";
    public static final String RESEND_LIMIT_REACHED_AT_FIELD = "???";

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
