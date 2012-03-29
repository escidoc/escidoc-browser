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
package org.escidoc.browser.ui.helper.internal;

import com.google.common.base.Preconditions;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CookieHandler.class);

    private HttpServletResponse response;

    private HttpServletRequest request;

    public CookieHandler(BrowserApplication app) {
        Preconditions.checkNotNull(app, "app is null: %s", app);
        this.response = app.getResponse();
        this.request = app.getRequest();
    }

    public void setCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // Use a fixed path
        cookie.setPath(AppConstants.COOKIE_PATH);
        cookie.setMaxAge(AppConstants.TWO_HOURS);
        response.addCookie(cookie);
    }

    public String getCookieValue() {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(AppConstants.COOKIE_NAME)) {
                LOG.debug("cookie value" + cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    public void eraseCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath(AppConstants.COOKIE_PATH);
        cookie.setMaxAge(0); // Delete
        response.addCookie(cookie);
    }
}
