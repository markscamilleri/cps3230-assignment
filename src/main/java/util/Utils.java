package util;

import java.util.Locale;
import java.util.Random;

public class Utils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!%^&*()";
    private static final String ALLCHARS = UPPER + LOWER + DIGITS + SYMBOLS;

    public static String getNRandomCharacters(int characterCount) {

        final StringBuilder randString = new StringBuilder();
        final Random rand = new Random();

        for (int i = 0; i < characterCount; i++) {
            randString.append(ALLCHARS.charAt(rand.nextInt(ALLCHARS.length())));
        }
        return randString.toString();
    }

    public static String getNCharacters(int characterCount, String startWith) {

        final StringBuilder nonRandString = new StringBuilder();
        nonRandString.append(startWith);

        for (int i = 0; nonRandString.length() < characterCount; i++) {
            nonRandString.append(ALLCHARS.charAt(i % ALLCHARS.length()));
        }
        return nonRandString.substring(0, characterCount);
    }

    public static String getNCharacters(int characterCount) {
        return getNCharacters(characterCount, "");
    }
}
