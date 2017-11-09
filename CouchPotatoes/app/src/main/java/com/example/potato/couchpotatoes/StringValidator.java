package com.example.potato.couchpotatoes;

import android.text.TextUtils;

/**
 * Validates strings to be of the correct type.
 * Author: Alan Mai
 */
public class StringValidator {

    private static final int MIN_PASS_LENGTH = 5;

    // regex matching for alphanumeric string, starting with either letter or number
    private static final String ALPHA_NUMERIC_REGEX = "^[\\p{Alnum}]+$";

    // regex for password checking
    private static final String PASSWORD_REGEX = "^[\\p{Alnum}\\p{Punct}]+$";

    /**
     * Default Constructor
     */
    public StringValidator() {}

    /**
     * Verifies if the given string is a proper email address.
     * Taken from https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
     *
     * @param target the string to be checked as an email address.
     * @return <code>true</code> if the string is a proper email address.
     */
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Verifies whether the target string is made up of only letters.
     * @param target the string to be checked
     * @return <code>true</code> if target string is made up of only letters.
     */
    public static boolean isAlpha(String target) {
        if (TextUtils.isEmpty(target)) return false;

        // split and check each character
        char[] chars = target.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) return false;
        }

        return true;
    }

    /**
     * Verifies if the given string is made up of alphanumeric characters, starting with either
     * numbers or letters.
     * @param target the string to be checked as alphanumeric.
     * @return <code>true</code> if target string is made up of alphanumeric characters
     */
    public static boolean isAlphaNumeric(String target) {
        if (target == null) return false;
        if (target.isEmpty()) return false;

        return target.matches(ALPHA_NUMERIC_REGEX);
    }

    /**
     * Verifies that the given string has valid characters for a string
     * Valid passwords are: Alphanumerics, Punctuation symbols, no whitespace.
     * @param target password string to be checked
     * @return <code>true</code> if the string is a valid password
     */
    public static boolean isValidPassword(String target) {
        if (TextUtils.isEmpty(target) || target.length() < MIN_PASS_LENGTH) return false;
        return target.matches(PASSWORD_REGEX);
    }

    /**
     * Verifies if the given password strings are valid passwords and that they equal each other
     * @param target1 the password to be checked
     * @param target2 copy of the password that should match target1
     * @return <code>true</code> if the passwords are valid and they match
     */
    public static boolean checkPasswords(String target1, String target2) {
        if (!isValidPassword(target1) || !isValidPassword(target2)) return false;

        return TextUtils.equals(target1, target2);
    }

}
