package uk.gov.dvsa.motr.web.viewmodel;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReviewViewModelTest {

    private static String UNKNOWN_STRING = "UNKNOWN";

    @Test
    public void whenColourIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour(null, null);
        assertTrue(viewModel.getColour().equals(UNKNOWN_STRING));
    }

    @Test
    public void whenColourIsEmptyItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("", null);
        assertTrue(viewModel.getColour().equals(UNKNOWN_STRING));
    }

    @Test
    public void colourIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("black", "");
        assertTrue(viewModel.getColour().equals("BLACK"));
    }

    @Test
    public void colourAndSecondaryColourIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("black", "blue");
        assertTrue(viewModel.getColour().equals("BLACK, BLUE"));
    }

    @Test
    public void whenYearOfManufactureIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setYearOfManufacture(null);
        assertTrue(viewModel.getYearOfManufacture().equals(UNKNOWN_STRING));
    }

    @Test
    public void whenYearOfManufactureIsEmptyItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setYearOfManufacture("");
        assertTrue(viewModel.getYearOfManufacture().equals(UNKNOWN_STRING));
    }

    @Test
    public void whenExpiryDateIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setExpiryDate(null);
        assertTrue(viewModel.getExpiryDate().equals(UNKNOWN_STRING));
    }

    @Test
    public void whenExpiryDateIsSetCorrectFormatIsReturned() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setExpiryDate(LocalDate.of(2017, 3, 10));
        assertTrue(viewModel.getExpiryDate().equals("10 March 2017"));
    }

    @Test
    public void whenEmailIsSetItCanBeRetrieved() {

        String expected = "test@test.com";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setContact(expected);
        assertTrue(expected.equals(viewModel.getContact()));
    }

    @Test
    public void whenPhoneNumberIsSetItCanBeRetrieved() {

        String expected = "07806754189";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setContact(expected);
        assertEquals(expected, viewModel.getContact());
    }

    @Test
    public void whenRegistrationIsSetItCanBeRetrieved() {

        String expected = "test-reg";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setRegistration(expected);
        assertTrue(expected.equals(viewModel.getRegistration()));
    }

    @Test
    public void whenContactTypeIsSetItToEmailCanBeRetrieved() {

        String expected = "email";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setContactType(expected);
        assertEquals(expected, viewModel.getContactType());
    }

    @Test
    public void whenContactTypeIsSetItToTextCanBeRetrieved() {

        String expected = "text";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setContactType(expected);
        assertEquals(expected, viewModel.getContactType());
    }

    @Test
    public void makeIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake("Honda");
        assertEquals("HONDA", viewModel.getMake());
    }

    @Test
    public void modelIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setModel("Civic");
        assertEquals("CIVIC", viewModel.getModel());
    }

    @Test
    public void modelIsUnknownWhenNotSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        assertEquals(UNKNOWN_STRING, viewModel.getModel());
    }

    @Test
    public void whenMakeIsNullAndMakeInFullIsSet_MakeIsReturnedAsMakeInFull() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake(null);
        viewModel.setMakeInFull("Ford Focus 1.8 Tdi");
        assertEquals("FORD FOCUS 1.8 TDI", viewModel.getMakeInfull());
    }

    @Test
    public void whenMakeIsNullAndMakeInFullIsSet_ModelIsReturnedAsEmpty() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake(null);
        viewModel.setMakeInFull("Ford Focus 1.8 TDI");
        viewModel.setModel("Focus");
        assertEquals("", viewModel.getModel());
    }

    @Test
    public void whenMakeAndMakeInFullIsNull_MakeIsReturnedAsUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake(null);
        viewModel.setMakeInFull(null);
        assertEquals(UNKNOWN_STRING, viewModel.getMake());
    }

    @Test
    public void whenMakeIsSetAndMakeInFullIsSet_MakeIsReturned() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake("Ford");
        viewModel.setMakeInFull("Ford Focus");
        assertEquals("FORD", viewModel.getMake());
    }

}
