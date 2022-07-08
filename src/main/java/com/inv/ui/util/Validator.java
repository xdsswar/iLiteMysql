package com.inv.ui.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XDSSWAR
 * Used for TextFields Validation
 */
public class Validator {
    public static final Pattern VALID_EMAIL =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Validate Email Address
     * @param email Email Address
     * @return true when is an Email
     */
    public static boolean validateEmail(String email){
        Matcher matcher=VALID_EMAIL.matcher(email);
        return matcher.find();
    }
}
