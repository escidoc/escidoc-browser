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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.GuestUser;
import org.escidoc.browser.repository.internal.UserRepositoryImpl;
import org.escidoc.browser.ui.helper.EscidocParameterHandler;
import org.escidoc.browser.ui.helper.ParamaterDecoder;
import org.escidoc.browser.ui.helper.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.source_code.base64Coder.Base64Coder;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class EscidocParameterHandlerImpl implements EscidocParameterHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EscidocParameterHandlerImpl.class);

    private static final int TEN_SECONDS = 10000;

    private final EscidocServiceLocation serviceLocation;

    private final BrowserApplication app;

    public EscidocParameterHandlerImpl(final BrowserApplication app, final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.app = app;
        this.serviceLocation = serviceLocation;
    }

    @Override
    public void handleParameters(final Map<String, String[]> parameters) {
        processParameters(parameters);
        app.setParameters(parameters);
        app.buildMainView();
    }

    private void processParameters(final Map<String, String[]> parameters) {
        if (Util.isEscidocUrlExists(parameters) && Util.doesTokenExist(parameters)) {
            setEscidocUri(parameters);
            doLogin(parameters);
        }
        if (Util.doesTokenExist(parameters) && serviceLocation.getEscidocUri() == null) {
            doLogin(parameters);
        }
        else if (Util.isEscidocUrlExists(parameters) && hasNotEscidocHandler(parameters)) {
            if (app.getCookieValue(AppConstants.COOKIE_NAME) != null) {
                setEscidocUri(parameters);
                app.setServiceLocation(serviceLocation);
                app.setLogoutURL(serviceLocation.getLogoutUri());
                loginThroughCookie(app.getCookieValue(AppConstants.COOKIE_NAME));

            }
            else if (isServerOnline(tryToParseEscidocUriFromParameter(parameters))) {
                setEscidocUri(parameters);
                app.setServiceLocation(serviceLocation);
                app.setLogoutURL(serviceLocation.getLogoutUri());
                app.setUser(new UserRepositoryImpl(serviceLocation).findCurrentUser());
            }
        }
        else if (!Util.isEscidocUrlExists(parameters) && hasNotEscidocHandler(parameters)) {
            LOG.debug("nothing");
            app.setUser(new GuestUser());
        }
    }

    private void doLogin(final Map<String, String[]> parameters) {
        app.setLogoutURL(serviceLocation.getLogoutUri());
        login(parameters);
    }

    private void setEscidocUri(final Map<String, String[]> parameters) {
        serviceLocation.setEscidocUri(tryToParseEscidocUriFromParameter(parameters));
        serviceLocation.setApplicationUri(toUri(app.getURL()));
    }

    private URI toUri(final URL url) {
        try {
            return url.toURI();
        }
        catch (final URISyntaxException e) {
            LOG.warn("Malformed URL: " + e);
            app.getMainWindow().showNotification(new Notification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
        }
        return null;
    }

    private void login(final Map<String, String[]> parameters) {
        final String escidocToken = ParamaterDecoder.parseAndDecodeToken(parameters);
        final UserRepositoryImpl userRepository = new UserRepositoryImpl(serviceLocation);
        userRepository.withToken(escidocToken);
        final CurrentUser currentUser = userRepository.findCurrentUser();
        app.setUser(currentUser);
        app.setCookie(AppConstants.COOKIE_NAME, findEscidocToken(parameters));
        try {
            app.getResponse().sendRedirect(app.getURL() + "?escidocurl=" + serviceLocation.getEscidocUri());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loginThroughCookie(String escidocToken) {
        final UserRepositoryImpl userRepository = new UserRepositoryImpl(serviceLocation);
        userRepository.withToken(Base64Coder.decodeString(escidocToken));
        final CurrentUser currentUser = userRepository.findCurrentUser();
        app.setUser(currentUser);
    }

    // TODO refactor this method, does not belong in this class.
    private boolean isServerOnline(final URI escidocUri) {
        URLConnection connection;
        try {
            connection = new URL(escidocUri.toString()).openConnection();
            connection.setConnectTimeout(TEN_SECONDS);
            connection.connect();
            final int responseCode = ((HttpURLConnection) connection).getResponseCode();
            return responseCode == 200;
        }
        catch (final IllegalArgumentException e) {
            LOG.warn("Malformed URL: " + e);
            app.getMainWindow().showNotification(new Notification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
            return false;
        }
        catch (final MalformedURLException e) {
            LOG.warn("Malformed URL: " + e);
            app.getMainWindow().showNotification(new Notification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
            return false;
        }
        catch (final IOException e) {
            LOG.warn("IOException: " + e);
            app.getMainWindow().showNotification(
                new Notification("Can not connect to: " + escidocUri, Notification.TYPE_ERROR_MESSAGE));
            return false;
        }
    }

    private boolean hasNotEscidocHandler(final Map<String, String[]> parameters) {
        return !Util.doesTokenExist(parameters);
    }

    private URI tryToParseEscidocUriFromParameter(final Map<String, String[]> parameters) {
        try {
            return Util.parseEscidocUriFrom(parameters);
        }
        catch (final URISyntaxException e) {
            LOG.error("Wrong URI syntax", e);
        }
        // FIXME: do not return null.
        return null;
    }

    @Override
    public EscidocServiceLocation getServiceLocation() {
        return serviceLocation;
    }

    private static String findEscidocToken(final Map<String, String[]> parameters) {
        final String[] escidocHandeList = parameters.get(AppConstants.ESCIDOC_USER_HANDLE);
        if (escidocHandeList.length > 1) {
            LOG.warn("Found more than one eSciDoc token. The first will be used.");

        }
        return escidocHandeList[0];
    }
}
