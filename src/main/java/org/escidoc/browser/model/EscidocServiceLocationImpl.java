package org.escidoc.browser.model;

import org.escidoc.browser.AppConstants;

import com.google.common.base.Preconditions;

public class EscidocServiceLocationImpl implements EscidocServiceLocation {

    private final String eSciDocUri;

    public EscidocServiceLocationImpl(final String eSciDocUri) {
        Preconditions.checkNotNull(eSciDocUri, "eSciDocUri is null");
        this.eSciDocUri = eSciDocUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getUri()
     */
    @Override
    public String getUri() {
        return eSciDocUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLoginUri()
     */
    @Override
    public String getLoginUri() {
        return eSciDocUri + AppConstants.LOGIN_TARGET;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLogoutUri()
     */
    @Override
    public String getLogoutUri() {
        return eSciDocUri + AppConstants.LOGOUT_TARGET;
    }
}