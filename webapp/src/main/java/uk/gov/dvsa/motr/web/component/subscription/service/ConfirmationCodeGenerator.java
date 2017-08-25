package uk.gov.dvsa.motr.web.component.subscription.service;

import java.util.Random;

class ConfirmationCodeGenerator {

    static String generateCode() {

        Integer randomSixDigitCode = new Random().nextInt(900000) + 100000;
        return randomSixDigitCode.toString();
    }
}