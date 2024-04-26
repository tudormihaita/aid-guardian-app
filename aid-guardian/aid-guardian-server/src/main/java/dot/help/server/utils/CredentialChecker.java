package dot.help.server.utils;

import java.util.function.Predicate;

public class CredentialChecker {
    private static final Predicate<String> isEmail = credential -> credential.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Predicate<String> isUsername = credential -> credential.matches("[a-zA-Z0-9._%+-]+");

    public static boolean isEmail(String credential) {
        return isEmail.test(credential);
    }

    public static boolean isUsername(String credential) {
        return isUsername.test(credential);
    }
}
