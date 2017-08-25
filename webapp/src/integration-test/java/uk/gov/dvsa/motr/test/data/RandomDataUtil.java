package uk.gov.dvsa.motr.test.data;

import org.apache.commons.lang3.RandomStringUtils;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.random;

public class RandomDataUtil {

    private static final Random random = new Random();

    public static Subscription.ContactType emailOrMobileContactType() {

        return random.nextBoolean() ? Subscription.ContactType.EMAIL : Subscription.ContactType.MOBILE;
    }

    public static String emailOrPhoneNumber() {

        return random.nextBoolean() ? email() : phoneNumber();
    }

    private static String email() {

        return UUID.randomUUID().toString().replaceAll("[-]", "") +
                "@" + UUID.randomUUID().toString().replaceAll("[-]", "") + ".com";
    }

    public static String phoneNumber() {

        return "07" + random(9,false,true);
    }

    public static String vrm() {

        return random(3, true, false) +
                random(4, false, true);
    }

    public static int singleDigitBetweenZeroAndThree() {

        return random.nextInt(3);
    }

    public static LocalDate dueDate() {

        return LocalDate.parse("201" + RandomStringUtils.randomNumeric(1) +
                "-" + "10" +
                "-" + "1" + RandomStringUtils.randomNumeric(1));
    }

    public static LocalDateTime latestResendAttempt() {

        return LocalDateTime.parse("2" + RandomStringUtils.randomNumeric(3) +
                "-" + "10" +
                "-" + "1" + RandomStringUtils.randomNumeric(1) +
                "T" + "13:5" + RandomStringUtils.randomNumeric(1) +
                ":5"  + RandomStringUtils.randomNumeric(1));
    }

    public static String motTestNumber() {

        return random(6, false, true);
    }

    public static String confirmationCode() {

        return random(6, false, true);
    }
}
