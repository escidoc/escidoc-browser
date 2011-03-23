package org.escidoc.browser;

import java.util.Map;

public class Util {

    public static boolean isEscidocUrlExists(
        final Map<String, String[]> parameters) {
        return parameters.containsKey(AppConstants.ESCIDOC_URL);
    }

    public static boolean isTokenExist(final Map<String, String[]> parameters) {
        return parameters.containsKey(AppConstants.ESCIDOC_USER_HANDLE);
    }

}
