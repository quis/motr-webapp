package uk.gov.dvsa.motr.web.viewmodel;

import uk.gov.dvsa.motr.web.formatting.DateFormatter;

import java.time.LocalDate;

public class ReviewViewModel {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ReviewViewModel.class);

    private static String UNKNOWN_STRING = "Unknown";

    private String registration;
    private String email;
    private String makeModel;
    private String colour;
    private String yearOfManufacture;
    private LocalDate expiryDate;
    private boolean isDvlaVehicle;

    public String getRegistration() {
        return registration;
    }

    public ReviewViewModel setRegistration(String registration) {

        this.registration = registration;
        return this;
    }

    public String getEmail() {

        return email;
    }

    public ReviewViewModel setEmail(String email) {

        this.email = email;
        return this;
    }

    public String getMakeModel() {

        return makeModel;
    }

    public ReviewViewModel setMakeModel(String make, String model) {

        if (model == null || "".equals(model)) {
            this.makeModel = make.toUpperCase();
        } else {
            String makeModel = make + " " + model;
            this.makeModel = makeModel.toUpperCase();
        }
        return this;
    }

    public String getColour() {

        return colour;
    }

    public ReviewViewModel setColour(String colour, String colourSecondary) {

        if (colour == null || "".equals(colour)) {
            this.colour = UNKNOWN_STRING;
            return this;
        }

        if (colourSecondary == null || "".equals(colourSecondary)) {
            this.colour = colour.toUpperCase();
        } else {
            String colours = colour + ", " + colourSecondary;
            this.colour = colours.toUpperCase();
        }

        return this;
    }

    public String getYearOfManufacture() {

        return yearOfManufacture;
    }

    public ReviewViewModel setYearOfManufacture(String yearOfManufacture) {

        if (yearOfManufacture == null || "".equals(yearOfManufacture)) {
            this.yearOfManufacture = UNKNOWN_STRING;
            return this;
        }
        this.yearOfManufacture = yearOfManufacture;
        return this;
    }

    public String getExpiryDate() {

        if (expiryDate == null) {
            return UNKNOWN_STRING;
        }

        return DateFormatter.asDisplayDate(expiryDate);
    }

    public ReviewViewModel setExpiryDate(LocalDate expiryDate) {

        this.expiryDate = expiryDate;
        return this;
    }

    public boolean isDvlaVehicle() {

        return isDvlaVehicle;
    }

    public ReviewViewModel setDvlaVehicle(boolean dvlaVehicle) {

        logger.info("ReviewViewModel setDvlaVehicle to: " + dvlaVehicle);
        isDvlaVehicle = dvlaVehicle;
        return this;
    }
}
