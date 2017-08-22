package uk.gov.dvsa.motr.web.validator;

public class ChannelSelectionValidator {

    private static final String EMPTY_CHANNEL_SELECTION_MESSAGE = "Choose what type of reminder you want to receive";
    private static final String INVALID_CHANNEL_SELECTION_MESSAGE = "Choose what type of reminder you want to receive";
    private static final String EMAIL_CHANNEL = "email";
    private static final String TEXT_CHANNEL = "text";

    private String message;

    public boolean isValid(String selection) {

        if (selection == null || selection.isEmpty()) {
            message = EMPTY_CHANNEL_SELECTION_MESSAGE;

            return false;
        }

        if (!selection.equals(EMAIL_CHANNEL) && !selection.equals(TEXT_CHANNEL)) {
            message = INVALID_CHANNEL_SELECTION_MESSAGE;

            return false;
        }

        return true;
    }

    public String getMessage() {

        return message;
    }
}
