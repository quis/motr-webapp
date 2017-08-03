package uk.gov.dvsa.motr.remote.vehicledetails;


import java.util.Optional;

public class MotIdentification {

    private String motTestNumber;

    private String dvlaId;

    public MotIdentification(String motTestNumber, String dvlaId) {

        this.motTestNumber = motTestNumber;
        this.dvlaId = dvlaId;
    }

    public Optional<String> getMotTestNumber() {

        return Optional.ofNullable(motTestNumber);
    }

    public Optional<String> getDvlaId() {

        return Optional.ofNullable(dvlaId);
    }

    @Override
    public String toString() {
        return "MotIdentification{" +
                "motTestNumber='" + motTestNumber + '\'' +
                ", dvlaId='" + dvlaId + '\'' +
                '}';
    }
}
