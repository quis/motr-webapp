package uk.gov.dvsa.motr.web.viewmodel;

import com.amazonaws.util.StringUtils;

import uk.gov.dvsa.motr.web.formatting.DateFormatter;

import java.time.LocalDate;

public class ReviewViewModel {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ReviewViewModel.class);

    private static String UNKNOWN_STRING = "UNKNOWN";

    private String registration;
    private String contact;
    private String contactType;
    private String make;
    private String model;
    private String makeInfull;
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

    public ReviewViewModel setContactType(String contactType) {

        this.contactType = contactType;
        return this;
    }

    public String getContactType() {

        return contactType;
    }

    public String getContact() {

        return contact;
    }

    public ReviewViewModel setContact(String contact) {

        this.contact = contact;
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

    public String getMake() {

        if (useMakeInFull()) {
            return makeInfull.toUpperCase();
        }
        if (!StringUtils.isNullOrEmpty(make)) {
            return make.toUpperCase();
        }

        return UNKNOWN_STRING;
    }

    public ReviewViewModel setMake(String make) {

        this.make = make;
        return this;
    }

    public String getModel() {

        if (useMakeInFull()) {
            return "";
        }

        if (StringUtils.isNullOrEmpty(model)) {
            return UNKNOWN_STRING;
        }
        return model.toUpperCase();
    }

    public ReviewViewModel setModel(String model) {

        this.model = model;
        return this;
    }

    public String getMakeInfull() {

        return makeInfull.toUpperCase();
    }

    public ReviewViewModel setMakeInFull(String makeInFull) {

        this.makeInfull = makeInFull;
        return this;
    }

    private boolean useMakeInFull() {

        if (StringUtils.isNullOrEmpty(make) && !StringUtils.isNullOrEmpty(makeInfull)) {
            return true;
        }
        return false;
    }

}
