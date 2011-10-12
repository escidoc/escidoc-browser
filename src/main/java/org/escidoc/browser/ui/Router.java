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
package org.escidoc.browser.ui;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.layout.SimpleLayout;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.escidoc.browser.ui.maincontent.SearchAdvancedView;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class Router extends VerticalLayout {

    private static final String ERROR_NO_LAYOUT =
        "Couldn't create a layout for you.";

    private final BrowserApplication app;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private LayoutDesign layout;

    private String layoutname = "SimpleLayout.java";

    private Properties browserProperties;

    /**
     * The mainWindow should be revised whether we need it or not the appHeight
     * is the Height of the Application and I need it for calculations in the
     * inner elements
     * 
     * @param mainWindow
     * @param repositories
     * @throws EscidocClientException
     */
    public Router(final Window mainWindow,
        final EscidocServiceLocation serviceLocation,
        final BrowserApplication app, final CurrentUser currentUser,
        final Repositories repositories) throws EscidocClientException {

        this.serviceLocation = serviceLocation;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
        this.repositories = repositories;

        init();
    }

    private void init() throws EscidocClientException {

        initiatePlugins();
        createLayout();

    }

    /**
     * Read the Plugins from a directory
     */
    private void initiatePlugins() {
        // Determin here the Layout and additional Controllers that have to be
        // used
        try {
            browserProperties = new Properties();
            browserProperties.load(this.getClass().getResourceAsStream(
                "browser.properties"));
            String pluginString = browserProperties.getProperty("plugin");
            String[] plugins = pluginString.split(",");

            for (int i = 0; i < plugins.length; i++) {
                browserProperties.load(this.getClass().getResourceAsStream(
                    plugins[i]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initiate Layout / Default or another one
     * 
     * @return
     */
    private void createLayout() {
        String layoutClassName = browserProperties.getProperty("design");
        try {
            // Class cls = Class.forName(layoutClassName);
            // layout = cls.newInstance();
            layout = new SimpleLayout(mainWindow, serviceLocation, app, currentUser,
                repositories, this);
        }
        catch (EscidocClientException e) {
            showError(ERROR_NO_LAYOUT + e.getLocalizedMessage());
        }
        // catch (ClassNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    /**
     * 
     * @return Layout to the MainWindow normally
     */
    public VerticalLayout getLayout() {
        return (VerticalLayout) layout;
    }

    /**
     * This method handles the open of a new tab on the right section of the
     * mainWindow This is the perfect place to inject Views that represent
     * objects
     * 
     * @param cmp
     * @param tabname
     */
    @Deprecated
    public void openTab(final Component cmp, String tabname) {
        ((SimpleLayout) layout).contentView(cmp, tabname);

    }

    public int getApplicationHeight() {
        return app.getApplicationHeight();
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    /**
     * This is the Permanent Link entry point Check if there is a tab variable
     * set in the GET Method of the page and open a tab containing the object
     * with that ID URL Example
     * http://escidev4:8084/browser/mainWindow?tab=escidoc:16037
     * &type=Item&escidocurl=http://escidev4.fiz-karlsruhe.de:8080
     */
    private void permanentURLelement() {
        final Map<String, String[]> parameters = app.getParameters();
        if (Util.hasTabArg(parameters) && Util.hasObjectType(parameters)) {
            final String escidocID = parameters.get(AppConstants.ARG_TAB)[0];

            final ResourceModelFactory resourceFactory =
                new ResourceModelFactory(repositories);

            if (parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTEXT")) {
                try {
                    final ContextProxyImpl context =
                        (ContextProxyImpl) resourceFactory.find(escidocID,
                            ResourceType.CONTEXT);
                    openTab(new ContextView(serviceLocation, this, context,
                        mainWindow, currentUser, repositories),
                        context.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Context");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0]
                .equals("CONTAINER")) {
                try {
                    final ContainerProxy container =
                        (ContainerProxy) resourceFactory.find(escidocID,
                            ResourceType.CONTAINER);
                    openTab(new ContainerView(serviceLocation, this, container,
                        mainWindow, currentUser, repositories),
                        container.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Container");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("ITEM")) {
                try {
                    final ItemProxy item =
                        (ItemProxy) resourceFactory.find(escidocID,
                            ResourceType.ITEM);
                    openTab(new ItemView(serviceLocation, repositories, this,
                        item, mainWindow, currentUser), item.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Item");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("sa")) {
                final SearchAdvancedView srch =
                    new SearchAdvancedView(this, serviceLocation);
                openTab(srch, "Advanced Search");
            }
            else {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

    private void showError(final String msg) {
        getWindow().showNotification(msg, Notification.TYPE_HUMANIZED_MESSAGE);
    }
}
