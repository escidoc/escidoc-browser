/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.model.internal;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.EscidocServiceLocation;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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
    public String getLoginUri(String appUrl) {
        return escidocUri + AppConstants.LOGIN_TARGET + appUrl + "?escidocurl=" + escidocUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.model.EscidocServiceLocation#getLogoutUri()
     */
    @Override
    public String getLogoutUri() {
        return escidocUri + AppConstants.LOGOUT_TARGET + appUri + "?escidocurl=" + escidocUri;
    }

    @Override
    public void setEscidocUri(final URI escidocUri) {
        this.escidocUri = escidocUri.toString();
    }

    @Override
    public void setEscidocUri(final String escidocUri) {
        this.escidocUri = escidocUri.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EscidocServiceLocationImpl [");
        if (getEscidocUri() != null) {
            builder.append("getUri()=").append(getEscidocUri()).append(", ");
        }
        // if (getLoginUri() != null) {
        // builder.append("getLoginUri()=").append(getLoginUri()).append(", ");
        // }
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

    @Override
    public URL getEscidocUrl() throws MalformedURLException {
        return new URL(escidocUri);
    }

}