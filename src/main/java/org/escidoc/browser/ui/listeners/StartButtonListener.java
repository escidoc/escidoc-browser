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
package org.escidoc.browser.ui.listeners;

import com.google.common.base.Preconditions;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("serial")
public final class StartButtonListener implements Button.ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(StartButtonListener.class);

    private static final int FIVE_SECONDS = 5000;

    private final WindowResizeObserver observer;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final BrowserApplication app;

    private TextField inputField;

    private String responseMessage;

    public StartButtonListener(final WindowResizeObserver observer, final Window mainWindow,
        final EscidocServiceLocation serviceLocation, final BrowserApplication app) {
        Preconditions.checkNotNull(observer, "observer is null: %s", observer);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(app, "app is null: %s", app);
        this.observer = observer;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.app = app;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        Preconditions.checkArgument(observer.getDimension().getHeight() > 0, "Can not get window size");
        if (validateUserInput()) {
            try {
                serviceLocation.setEscidocUri(new URI((String) inputField.getValue()));
                showMainView();
            }
            catch (final URISyntaxException e) {
                mainWindow.showNotification(new Notification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
            }
        }
    }

    private boolean validateUserInput() {
        Preconditions.checkNotNull(inputField, "escidocServiceUrl is null: %s", inputField);
        return validate();
    }

    private boolean validate() {
        try {
            validateInputField(inputField);
            return testConnection(inputField);
        }
        catch (final EmptyValueException e) {
            mainWindow.showNotification(new Notification("eSciDoc URI can not be empty. " + e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
        return false;
    }

    private boolean testConnection(final AbstractField escidocUriField) {
        if (validateConnection(escidocUriField)) {
            return true;
        }

        mainWindow.showNotification(new Window.Notification(buildMessage(escidocUriField),
            Notification.TYPE_ERROR_MESSAGE));
        return false;
    }

    private String buildMessage(final AbstractField escidocUriField) {
        StringBuilder builder = new StringBuilder();
        builder.append("Can not connect to: ").append(escidocUriField.getValue());

        if (responseMessage != null) {
            builder.append(", cause: ").append(responseMessage);
        }

        String message = builder.toString();
        return message;
    }

    private boolean validateConnection(final AbstractField escidocUriField) {
        final String strUrl = (String) escidocUriField.getValue();
        URLConnection connection;
        try {
            connection = new URL(strUrl).openConnection();
            connection.setConnectTimeout(FIVE_SECONDS);
            connection.connect();
            final int responseCode = ((HttpURLConnection) connection).getResponseCode();
            responseMessage = ((HttpURLConnection) connection).getResponseMessage();
            return responseCode == 200;
        }
        catch (final IllegalArgumentException e) {
            LOG.warn("Malformed URL: " + e);
            return false;
        }
        catch (final MalformedURLException e) {
            LOG.warn("Malformed URL: " + e);
            return false;
        }
        catch (final IOException e) {
            LOG.warn("IOException: " + e);
            return false;
        }
    }

    private void validateInputField(final AbstractField escidocUriField) {
        escidocUriField.validate();
    }

    private void showMainView() {
        mainWindow.removeAllComponents();
        app.buildMainWindow(serviceLocation);
    }

    public void setInputField(final TextField inputField) {
        this.inputField = inputField;
    }

}