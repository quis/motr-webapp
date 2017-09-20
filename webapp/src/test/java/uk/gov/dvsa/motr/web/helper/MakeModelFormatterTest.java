package uk.gov.dvsa.motr.web.helper;

import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;

import static org.junit.Assert.assertEquals;

public class MakeModelFormatterTest {

    @Test
    public void whenNoModelButMake_MakeIsReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("TEST-MAKE");
        assertEquals("TEST-MAKE", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));
    }

    @Test
    public void whenNoMakeButModel_ModelIsReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setModel("TEST-MODEL");
        assertEquals("TEST-MODEL", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));
    }

    @Test
    public void whenMakeAndModel_MakeAndModelAreReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setModel("TEST-MODEL");
        vehicleDetails.setMake("TEST-MAKE");
        assertEquals("TEST-MAKE TEST-MODEL", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));

    }

    @Test
    public void whenNoMakeAndNoModel_EmptyStringIsReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        assertEquals("", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));
    }

    @Test
    public void whenNoMakeButHasMakeInFull_MakeInFullIsReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMakeInFull("Ford Focus 1.8 TDCI");
        assertEquals("FORD FOCUS 1.8 TDCI", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));
    }

    @Test
    public void whenMakeAndModelUnknown_EmptyStringIsReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("UNKNOWN");
        vehicleDetails.setModel("UNKNOWN");
        assertEquals("", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));
    }

    @Test
    public void whenMakeAndModelUnknownButHasMakeInFull_MakeInFullIsReturned() throws Exception {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("UNKNOWN");
        vehicleDetails.setModel("UNKNOWN");
        vehicleDetails.setMakeInFull("Ford Focus");
        assertEquals("FORD FOCUS", MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,null));
    }
}
