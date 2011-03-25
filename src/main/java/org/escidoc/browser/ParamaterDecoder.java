package org.escidoc.browser;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.source_code.base64Coder.Base64Coder;

import com.google.common.base.Preconditions;

public class ParamaterDecoder {

    private static final Logger LOG = LoggerFactory
        .getLogger(ParamaterDecoder.class);

    public ParamaterDecoder(final BrowserApplication app) {
        Preconditions.checkNotNull(app, "app is null: %s", app);
        this.app = app;
    }

    private final BrowserApplication app;

    public String parseAndDecodeToken(final Map<String, String[]> parameters) {
        final String parameter =
            parameters.get(AppConstants.ESCIDOC_USER_HANDLE)[0];
        return tryToDecode(parameter);
    }

    private String tryToDecode(final String parameter) {
        try {
            return Base64Coder.decodeString(parameter);
        }
        catch (final IllegalArgumentException e) {
            Preconditions.checkNotNull(app.getMainWindow(),
                "MainWindow is null: %s", app.getMainWindow());
       }
        return AppConstants.EMPTY_STRING;
    }
}
