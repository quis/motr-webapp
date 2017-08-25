package uk.gov.dvsa.motr.web.viewmodel;

public class SmsConfirmationCodeViewModel {

    private String phoneNumber;

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public SmsConfirmationCodeViewModel setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
        return this;
    }
}
