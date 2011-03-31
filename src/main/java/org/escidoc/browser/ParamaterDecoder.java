package org.escidoc.browser;

import java.util.Map;

import biz.source_code.base64Coder.Base64Coder;

public class ParamaterDecoder {

    public static String parseAndDecodeToken(
        final Map<String, String[]> parameters) {
        final String parameter =
            parameters.get(AppConstants.ESCIDOC_USER_HANDLE)[0];
        return tryToDecode(parameter);
    }

    private static String tryToDecode(final String parameter) {
        return Base64Coder.decodeString(parameter);
    }
}
