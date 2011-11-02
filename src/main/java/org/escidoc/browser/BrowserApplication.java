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
package org.escidoc.browser;

import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.EscidocServiceLocationImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.RepositoriesImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.helper.EscidocParameterHandler;
import org.escidoc.browser.ui.helper.internal.EscidocParameterHandlerImpl;
import org.escidoc.browser.ui.landingview.LandingViewImpl;
import org.escidoc.browser.ui.listeners.StartButtonListener;
import org.escidoc.browser.ui.listeners.WindowResizeListener;
import org.escidoc.browser.ui.listeners.WindowResizeObserver;
import org.escidoc.browser.ui.listeners.WindowResizeObserverImpl;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BrowserApplication extends Application implements HttpServletRequestListener {

    private final Window mainWindow = new Window(ViewConstants.MAIN_WINDOW_TITLE);

    private EscidocServiceLocation serviceLocation = new EscidocServiceLocationImpl();

    private EscidocParameterHandler paramaterHandler;

    private WindowResizeListener windowResizeListener;

    private WindowResizeObserver observer;

    private Map<String, String[]> parameters;

    private HttpServletResponse response;

    private HttpServletRequest request;

    @Override
    public void init() {
        setApplicationTheme();
        setMainWindow();
        addParameterHandler();
        addWindowDimensionDetection();
    }

    private void setApplicationTheme() {
        setTheme(ViewConstants.THEME_NAME);
    }

    private void setMainWindow() {
        setMainWindow(mainWindow);
    }

    private void addParameterHandler() {
        paramaterHandler = new EscidocParameterHandlerImpl(this, serviceLocation);
        mainWindow.addParameterHandler(paramaterHandler);
    }

    private void addWindowDimensionDetection() {
        observer = new WindowResizeObserverImpl();
        windowResizeListener = new WindowResizeListener(observer);
        mainWindow.addListener(windowResizeListener);
    }

    public void buildMainView() {
        if (observer.getDimension().getHeight() > 0) {
            buildMainWindow(serviceLocation);
        }
        else {
            showLandingView();
        }
    }

    private void showLandingView() {
        mainWindow.removeAllComponents();
        mainWindow.addComponent(new LandingViewImpl(serviceLocation, new StartButtonListener(observer, mainWindow,
            serviceLocation, this)));
    }

    public void buildMainWindow(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        mainWindow.setImmediate(true);
        mainWindow.setScrollable(true);
        setMainWindowContent(serviceLocation);
        setMainWindowHeight();
    }

    private void setMainWindowContent(final EscidocServiceLocation serviceLocation) {
        try {
            Router router = createRouter(serviceLocation, mainWindow);
            mainWindow.setContent(router.getLayout());
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
        catch (final MalformedURLException e) {
            mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private Router createRouter(final EscidocServiceLocation serviceLocation, final Window mainWindow)
        throws EscidocClientException, MalformedURLException {

        final Repositories repositories = new RepositoriesImpl(serviceLocation, mainWindow).createAllRepositories();
        repositories.loginWith(getCurrentUser().getToken());
        final Router router = new Router(mainWindow, serviceLocation, this, getCurrentUser(), repositories);

        return router;
    }

    private void setMainWindowHeight() {
        mainWindow.getContent().setHeight(getApplicationHeight() + "px");
    }

    public int getApplicationHeight() {
        Preconditions.checkArgument(observer.getDimension().getHeight() > 0, "Can not get window size");
        return Math.round(observer.getDimension().getHeight());
    }

    public int getApplicationWidth() {
        Preconditions.checkArgument(observer.getDimension().getWidth() > 0, "Can not get window size");
        return Math.round(observer.getDimension().getWidth());
    }

    public void setServiceLocation(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
    }

    public void setParameters(final Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public EscidocServiceLocation getServiceLocation() {
        return serviceLocation;
    }

    public CurrentUser getCurrentUser() {
        return (CurrentUser) getUser();
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        this.response = response;
        this.request = request;
        System.out.println("HEADER: " + request.getHeader("Referer"));
    }

    @Override
    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        // Just ignore this! I am not going to write anywhere else but the browser!
    }

    public void setCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // Use a fixed path
        cookie.setPath(AppConstants.COOKIE_PATH);
        cookie.setMaxAge(AppConstants.COOKIE_MAX_AGE); // One hour
        response.addCookie(cookie);
    }

    public String getCookieValue(String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AppConstants.COOKIE_NAME)) {
                    LOG.debug("Cookie was found " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void removeCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath(AppConstants.COOKIE_PATH);
        cookie.setMaxAge(0); // Delete
        response.addCookie(cookie);
    }

}