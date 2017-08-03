package uk.gov.dvsa.motr.web.viewmodel;

import uk.gov.dvsa.motr.web.formatting.DateFormatter;

import java.time.LocalDate;

public class UnsubscribeViewModel {

    private String registration;
    private String email;
    private LocalDate expiryDate;
    private boolean isDvlaVehicle;
    private String makeModel;

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

        return DateFormatter.asDisplayDate(expiryDate);
    }

    public UnsubscribeViewModel setExpiryDate(LocalDate expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }

    public String getMakeModel() {

        return this.makeModel;
    }

    public UnsubscribeViewModel setMakeModel(String makeModel) {

        this.makeModel = makeModel;
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
