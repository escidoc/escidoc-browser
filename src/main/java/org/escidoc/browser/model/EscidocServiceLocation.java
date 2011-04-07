package org.escidoc.browser.model;

import java.net.URI;

public interface EscidocServiceLocation {

    String getEscidocUri();

    String getLoginUri();

    String getLogoutUri();

    void setEscidocUri(URI escidocUri);

    void setApplicationUri(URI appUri);

}