package uk.gov.dvsa.motr.web.viewmodel;

import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;

import java.time.LocalDate;

public class UnsubscribeViewModel {

    private String registration;
    private String email;
    private LocalDate expiryDate;
    private boolean isDvlaVehicle;

    public String getRegistration() {
        return registration;
    }

    public UnsubscribeViewModel setRegistration(String registration) {

        this.registration = registration;
        return this;
    }

    public String getEmail() {

        return email;
    }

    public UnsubscribeViewModel setEmail(String email) {

        this.email = email;
        return this;
    }

    public String getExpiryDate() {

        if (expiryDate == null) {
            return "Unknown";
        }

        return DateDisplayHelper.asDisplayDate(expiryDate);
    }

    public UnsubscribeViewModel setExpiryDate(LocalDate expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }

    public boolean isDvlaVehicle() {

        return isDvlaVehicle;
    }

    public UnsubscribeViewModel setDvlaVehicle(boolean dvlaVehicle) {

        isDvlaVehicle = dvlaVehicle;
        return this;
    }
}
