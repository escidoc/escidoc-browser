package org.escidoc.browser.model;

import java.net.URI;

import org.escidoc.browser.AppConstants;

public class EscidocServiceLocationImpl implements EscidocServiceLocation {

    private String escidocUri;

    public EscidocServiceLocationImpl() {
        // empty
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getUri()
     */
    @Override
    public String getUri() {
        return escidocUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLoginUri()
     */
    @Override
    public String getLoginUri() {
        return escidocUri + AppConstants.LOGIN_TARGET;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLogoutUri()
     */
    @Override
    public String getLogoutUri() {
        return escidocUri + AppConstants.LOGOUT_TARGET;
    }

    @Override
    public void setUri(final URI escidocUri) {
        this.escidocUri = escidocUri.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EscidocServiceLocationImpl [");
        if (getUri() != null) {
            builder.append("getUri()=").append(getUri()).append(", ");
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

}