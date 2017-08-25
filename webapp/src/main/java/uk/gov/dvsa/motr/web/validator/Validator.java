package uk.gov.dvsa.motr.web.validator;

public interface Validator {

    boolean isValid(String stringToValidate);

    String getMessage();

    void setMessage(String message);

    String getMessageAtField();

    void setMessageAtField(String messageAtField);
}
