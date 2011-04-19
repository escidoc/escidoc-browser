package org.escidoc.browser.model.internal;

import java.net.URI;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.EscidocServiceLocation;

public class EscidocServiceLocationImpl implements EscidocServiceLocation {

    private String escidocUri;

    private URI appUri;

    public EscidocServiceLocationImpl() {
        // empty
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getUri()
     */
    @Override
    public String getEscidocUri() {
        return escidocUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLoginUri()
     */
    @Override
    public String getLoginUri() {
        return escidocUri + AppConstants.LOGIN_TARGET + appUri + "?escidocurl="
            + escidocUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLogoutUri()
     */
    @Override
    public String getLogoutUri() {
        return escidocUri + AppConstants.LOGOUT_TARGET + appUri
            + "?escidocurl=" + escidocUri;
    }

    @Override
    public void setEscidocUri(final URI escidocUri) {
        this.escidocUri = escidocUri.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EscidocServiceLocationImpl [");
        if (getEscidocUri() != null) {
            builder.append("getUri()=").append(getEscidocUri()).append(", ");
        }
        if (getLoginUri() != null) {
            builder.append("getLoginUri()=").append(getLoginUri()).append(", ");
        }
        if (getLogoutUri() != null) {
            builder.append("getLogoutUri()=").append(getLogoutUri());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setApplicationUri(final URI appUri) {
        this.appUri = appUri;
    }

}