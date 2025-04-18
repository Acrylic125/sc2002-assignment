package com.group6.utils;

import java.util.regex.Pattern;

/**
* utility class to validate NRIC for {@link com.group6.views.LoginView}
*
* See {@link com.group6.users.UserManager}.
*/
public class ValidateUtils {

    /**
     * Method to define the parameters for nric.
     * <p>
     * The NRIC format should start with either 'S' or 'T', followed by 7 digits and end with a capital letter.
     * </p>
     */
    private static final String nric = "^[ST]\\d{7}[A-Z]$";

    /**
     * Method to compile regex pattern for NRIC validation.
     */
    private static final Pattern NRIC_PATTERN = Pattern.compile(nric);

    /**
    * Validates a given NRIC number.
    * <p>
    * This method checks if the given NRIC conforms to the standard format.
    * </p>
    *
     * @param nric the NRIC number to validate.
    * @return {@code true} if NRIC is valid.
    */
    public static boolean isValidNRIC(String nric){
        return NRIC_PATTERN.matcher(nric).matches();
    }
}
