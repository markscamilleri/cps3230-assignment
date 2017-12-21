package util;

import java.util.Random;

public class Utils {

    public static String getNRandomCharacters(int characterCount) {

        final StringBuilder randString = new StringBuilder();
        final Random rand = new Random();

        for (int i = 0; i < characterCount; i++) {
            randString.append((char) rand.nextInt());
        }
        return randString.toString();
    }

    public static String getNCharacters(int characterCount, String startWith) {

        final StringBuilder nonRandString = new StringBuilder();
        nonRandString.append(startWith);

        for (int i = 0; nonRandString.length() < characterCount; i++) {
            nonRandString.append((char) i);
        }
        return nonRandString.substring(0, characterCount);
    }

    public static String getNCharacters(int characterCount) {
        return getNCharacters(characterCount, "");
    }
}
