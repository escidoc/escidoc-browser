package org.escidoc.browser.model;

import java.net.URI;

public interface EscidocServiceLocation {

    String getUri();

    String getLoginUri();

    String getLogoutUri();

    void setUri(URI tryToParseEscidocUriFromParameter);

}