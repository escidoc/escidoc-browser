package org.escidoc.browser.ui.helper;

import java.util.Map;

import org.escidoc.browser.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.source_code.base64Coder.Base64Coder;

public class ParamaterDecoder {

    private static final Logger LOG = LoggerFactory
        .getLogger(ParamaterDecoder.class);

    public static String parseAndDecodeToken(
        final Map<String, String[]> parameters) {
        return tryToDecode(findEscidocToken(parameters));
    }

    private static String findEscidocToken(
        final Map<String, String[]> parameters) {
        final String[] escidocHandeList =
            parameters.get(AppConstants.ESCIDOC_USER_HANDLE);
        if (escidocHandeList.length > 1) {
            LOG
                .warn("Found more than one eSciDoc token. The first will be used.");

        }
        return escidocHandeList[0];
    }

    private static String tryToDecode(final String parameter) {
        return Base64Coder.decodeString(parameter);
    }
}
