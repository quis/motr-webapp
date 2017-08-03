package uk.gov.dvsa.motr.web.component.subscription.model;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;

import java.time.LocalDate;

public class Subscription {

    private String unsubscribeId;

    private String vrm;

    private String email;

    private LocalDate motDueDate;

    private MotIdentification motIdentification;

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public Subscription setUnsubscribeId(String id) {
        this.unsubscribeId = id;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public Subscription setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Subscription setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public Subscription setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public MotIdentification getMotIdentification() {
        return motIdentification;
    }

    public Subscription setMotIdentification(MotIdentification motIdentification) {
        this.motIdentification = motIdentification;
        return this;
    }
}
