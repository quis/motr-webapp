package uk.gov.dvsa.motr.remote.vehicledetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.dvsa.motr.web.serialisation.LocalDateDeserialiser;

import java.io.Serializable;
import java.time.LocalDate;

public class VehicleDetails implements Serializable {

    @JsonProperty("make")
    private String make;

    @JsonProperty("model")
    private String model;

    @JsonProperty("primaryColour")
    private String primaryColour;

    @JsonProperty("secondaryColour")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String secondaryColour;

    @JsonProperty("registration")
    private String regNumber;

    @JsonProperty("manufactureYear")
    private Integer yearOfManufacture;

    @JsonDeserialize(using = LocalDateDeserialiser.class)
    @JsonProperty("motTestExpiryDate")
    private LocalDate motExpiryDate;

    @JsonProperty("motTestNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String motTestNumber;

    @JsonProperty("dvlaId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dvlaId;

    public String getMake() {

        return make;
    }

    public void setMake(String make) {

        this.make = make;
    }

    public String getModel() {

        return model;
    }

    public void setModel(String model) {

        this.model = model;
    }

    public String getPrimaryColour() {

        return primaryColour;
    }

    public void setPrimaryColour(String primaryColour) {

        this.primaryColour = primaryColour;
    }

    public String getSecondaryColour() {

        return secondaryColour;
    }

    public void setSecondaryColour(String secondaryColour) {

        this.secondaryColour = secondaryColour;
    }

    public String getRegNumber() {

        return regNumber;
    }

    public void setRegNumber(String regNumber) {

        this.regNumber = regNumber;
    }

    public Integer getYearOfManufacture() {

        return yearOfManufacture;
    }

    public void setYearOfManufacture(Integer yearOfManufacture) {

        this.yearOfManufacture = yearOfManufacture;
    }

    public LocalDate getMotExpiryDate() {

        return motExpiryDate;
    }

    public void setMotExpiryDate(LocalDate motExpiryDate) {

        this.motExpiryDate = motExpiryDate;
    }

    public String getMotTestNumber() {

        return motTestNumber;
    }

    public void setMotTestNumber(String motTestNumber) {

        this.motTestNumber = motTestNumber;
    }

    public String getDvlaId() {

        return dvlaId;
    }

    public void setDvlaId(String dvlaId) {

        this.dvlaId = dvlaId;
    }

    public MotIdentification getMotIdentification() {
        return new MotIdentification(this.motTestNumber, this.dvlaId);
    }

    @Override
    public String toString() {
        return "VehicleDetails{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", primaryColour='" + primaryColour + '\'' +
                ", secondaryColour='" + secondaryColour + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", yearOfManufacture=" + yearOfManufacture +
                ", motExpiryDate=" + motExpiryDate +
                ", motTestNumber='" + motTestNumber + '\'' +
                ", dvlaId='" + dvlaId + '\'' +
                '}';
    }
}
