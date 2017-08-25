package uk.gov.dvsa.motr.web.component.subscription.model;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;

import java.time.LocalDate;

public class PendingSubscription {

    private String confirmationId;

    private String vrm;

    private String contact;

    private LocalDate motDueDate;

    private MotIdentification motIdentification;

    private String dvlaId;

    private String motTestNumber;

    private Subscription.ContactType contactType;

    public String getConfirmationId() {
        return confirmationId;
    }

    public PendingSubscription setConfirmationId(String id) {
        this.confirmationId = id;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public PendingSubscription setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public PendingSubscription setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public PendingSubscription setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public MotIdentification getMotIdentification() {
        return motIdentification;
    }

    public PendingSubscription setMotIdentification(MotIdentification motIdentification) {
        this.motIdentification = motIdentification;
        return this;
    }

    public void setDvlaId(String dvlaId) {
        this.dvlaId = dvlaId;
        this.motIdentification = new MotIdentification(this.motTestNumber, dvlaId);
    }

    public void setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        this.motIdentification = new MotIdentification(motTestNumber, this.dvlaId);
    }

    public PendingSubscription setContactType(Subscription.ContactType contactType) {
        this.contactType = contactType;
        return this;
    }

    public Subscription.ContactType getContactType() {
        return this.contactType;
    }
}
