package uk.gov.dvsa.motr.web.formatting;

import com.amazonaws.util.StringUtils;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;

public class MakeModelFormatter {

    private static String UNKNOWN_VALUE = "UNKNOWN";

    public static String getMakeModelString(String make, String model, String makeInFull) {

        boolean makeValid = !StringUtils.isNullOrEmpty(make) && !make.equalsIgnoreCase(UNKNOWN_VALUE);
        boolean modelValid = !StringUtils.isNullOrEmpty(model) && !model.equalsIgnoreCase(UNKNOWN_VALUE);

        if (!makeValid && !modelValid) {
            if (StringUtils.isNullOrEmpty(makeInFull)) {
                return "";
            }
            return makeInFull.toUpperCase();
        }

        if (makeValid && !modelValid) {
            return make.toUpperCase();
        }
        if (!makeValid && modelValid) {
            return model.toUpperCase();
        }

        return (make + " " + model).toUpperCase();
    }

    public static String getMakeModelDisplayStringFromVehicleDetails(VehicleDetails vehicleDetails, String endDelimeter) {

        if (vehicleDetails == null) {
            return "";
        }

        String makeModel =
                MakeModelFormatter.getMakeModelString(vehicleDetails.getMake(), vehicleDetails.getModel(), vehicleDetails.getMakeInFull());

        if (endDelimeter == null) {
            return makeModel;
        }
        return makeModel.equals("") ? makeModel : makeModel + endDelimeter;
    }

}
