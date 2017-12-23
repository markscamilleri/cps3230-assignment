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
}
