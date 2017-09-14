package uk.gov.dvsa.motr.web.validator;

import java.util.regex.Pattern;

public class VrmValidator {

    public static final String REGISTRATION_EMPTY_MESSAGE = "Enter the vehicle's registration";
    public static final String REGISTRATION_TOO_LONG_MESSAGE = "Registration must be shorter than 14 characters";
    public static final String REGISTRATION_CAN_ONLY_CONTAIN_LETTERS_NUMBERS_AND_HYPHENS_MESSAGE = "Registration can only contain " +
            "letters, numbers and hyphens";
    private static final int REGISTRATION_MAX_LENGTH = 13;
    private static final String VALID_REGISTRATION_REGEX = "^[a-zA-Z0-9-]*$";

    private String message;

    public boolean isValid(String vehicleRegistration) {

        if (vehicleRegistration == null || "".equals(vehicleRegistration)) {
            this.message = REGISTRATION_EMPTY_MESSAGE;
            return false;
        }

        if (vehicleRegistration.length() > REGISTRATION_MAX_LENGTH) {
            this.message = REGISTRATION_TOO_LONG_MESSAGE;
            return false;
        }

        if (!Pattern.compile(VALID_REGISTRATION_REGEX).matcher(vehicleRegistration).matches()) {
            this.message = REGISTRATION_CAN_ONLY_CONTAIN_LETTERS_NUMBERS_AND_HYPHENS_MESSAGE;
            return false;
        }

        return true;
    }

    public String getMessage() {

        return this.message;
    }
}
