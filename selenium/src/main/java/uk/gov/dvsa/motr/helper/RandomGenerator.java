package uk.gov.dvsa.motr.helper;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class RandomGenerator {

    public static String generateEmail() {

        String emailDomain = "@" + UUID.randomUUID() + ".doesnotexist";
        return UUID.randomUUID() + emailDomain;
    }

    public static String generateVrm() {

        return RandomStringUtils.randomAlphabetic(10).toUpperCase();
    }

    public static String generateDvlaVrm() {

        return "DVLA-ID-" + RandomStringUtils.randomAlphabetic(5).toUpperCase();
    }

}
