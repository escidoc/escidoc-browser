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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.layout.SimpleLayout;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.escidoc.browser.ui.maincontent.SearchAdvancedView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.cmm.ContentModel;

public class Router extends VerticalLayout {

    private static final String ERROR_NO_LAYOUT = "Couldn't create a layout for you.";

    private final BrowserApplication app;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private LayoutDesign layout;

    private final String layoutname = "SimpleLayout.java";

    private Properties browserProperties;

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    /**
     * The mainWindow should be revised whether we need it or not the appHeight is the Height of the Application and I
     * need it for calculations in the inner elements
     * 
     * @param mainWindow
     * @param repositories
     * @throws EscidocClientException
     */
    public Router(final Window mainWindow, final EscidocServiceLocation serviceLocation, final BrowserApplication app,
        final CurrentUser currentUser, final Repositories repositories) throws EscidocClientException {

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
            browserProperties.load(this.getClass().getClassLoader().getResourceAsStream("browser.properties"));
            final String pluginString = browserProperties.getProperty("plugin");
            final String[] plugins = pluginString.split(",");

            for (final String plugin : plugins) {
                browserProperties.load(this.getClass().getClassLoader().getResourceAsStream(plugin));
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initiate Layout / Default or another one
     * 
     * @return
     */
    private void createLayout() {
        final String layoutClassName = browserProperties.getProperty("design");
        try {
            // Class cls = Class.forName(layoutClassName);
            // layout = cls.newInstance();
            layout = new SimpleLayout(mainWindow, serviceLocation, app, currentUser, repositories, this);
        }
        catch (final EscidocClientException e) {
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
     * This method handles the open of a new tab on the right section of the mainWindow This is the perfect place to
     * inject Views that represent objects
     * 
     * @param cmp
     * @param tabname
     */
    @Deprecated
    public void openTab(final Component cmp, final String tabname) {
        ((SimpleLayout) layout).openView(cmp, tabname);

    }

    public int getApplicationHeight() {
        return app.getApplicationHeight();
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    /**
     * This is the Permanent Link entry point Check if there is a tab variable set in the GET Method of the page and
     * open a tab containing the object with that ID URL Example
     * http://escidev4:8084/browser/mainWindow?tab=escidoc:16037
     * &type=Item&escidocurl=http://escidev4.fiz-karlsruhe.de:8080
     */
    private void permanentURLelement() {
        final Map<String, String[]> parameters = app.getParameters();
        if (Util.hasTabArg(parameters) && Util.hasObjectType(parameters)) {
            final String escidocID = parameters.get(AppConstants.ARG_TAB)[0];

            final ResourceModelFactory resourceFactory = new ResourceModelFactory(repositories);

            if (parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTEXT")) {
                try {
                    final ContextProxyImpl context =
                        (ContextProxyImpl) resourceFactory.find(escidocID, ResourceType.CONTEXT);
                    openTab(new ContextView(serviceLocation, this, context, mainWindow, currentUser, repositories),
                        context.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Context");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTAINER")) {
                try {
                    final ContainerProxy container =
                        (ContainerProxy) resourceFactory.find(escidocID, ResourceType.CONTAINER);
                    openTab(new ContainerView(serviceLocation, this, container, mainWindow, currentUser, repositories),
                        container.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Container");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("ITEM")) {
                try {
                    final ItemProxy item = (ItemProxy) resourceFactory.find(escidocID, ResourceType.ITEM);
                    openTab(new ItemView(serviceLocation, repositories, this, item, mainWindow, currentUser),
                        item.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Item");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("sa")) {
                final SearchAdvancedView srch = new SearchAdvancedView(this, serviceLocation);
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

    public void show(final ResourceModel clickedResource) throws EscidocClientException {
        if (ContextModel.isContext(clickedResource)) {
            openTab(new ContextView(serviceLocation, this, tryToFindResource(repositories.context(), clickedResource),
                mainWindow, currentUser, repositories), clickedResource.getName());
        }
        else if (ContainerModel.isContainer(clickedResource)) {
            openTab(
                new ContainerView(serviceLocation, this, tryToFindResource(repositories.container(), clickedResource),
                    mainWindow, currentUser, repositories), clickedResource.getName());
        }
        else if (ItemModel.isItem(clickedResource)) {
            String controllerId = "org.escidoc.browser.Item";
            final ItemProxy itemProxy = (ItemProxy) tryToFindResource(repositories.item(), clickedResource);
            final ContentModel contentModel =
                repositories.contentModel().findById(itemProxy.getContentModel().getObjid());
            final String description = contentModel.getProperties().getDescription();

            LOG.debug("Description is " + description);
            final Pattern controllerIdPattern = Pattern.compile("org.escidoc.browser.Controller=([^;]*);");
            final Matcher controllerIdMatcher = controllerIdPattern.matcher(description);

            if (controllerIdMatcher.find()) {
                controllerId = controllerIdMatcher.group(1);
            }
            LOG.debug("ControllerID[" + controllerId + "]");

            if (controllerId.equals("org.escidoc.browser.Item")) {
                openTab(new ItemView(serviceLocation, repositories, this, itemProxy, mainWindow, currentUser),
                    itemProxy.getName());
            }

            Controller controller;
            try {
                final Class<?> controllerClass = Class.forName(browserProperties.getProperty(controllerId));
                controller = (Controller) controllerClass.newInstance();
                controller.init(itemProxy);
                controller.showView(layout);
            }
            catch (final ClassNotFoundException e) {
                // TODO tell the user what happens.
                LOG.error("Controller class could not be found. " + e.getMessage());
            }
            catch (final InstantiationException e) {
                // TODO tell the user what happens.
                LOG.error("Controller class could not be created. " + e.getMessage());
            }
            catch (final IllegalAccessException e) {
                // TODO tell the user what happens.
                LOG.error("Access issues creating controller class. " + e.getMessage());
            }
        }
        else {
            throw new UnsupportedOperationException("Unknown resource. Can not be shown.");
        }

    }

    private ResourceProxy tryToFindResource(final Repository repository, final ResourceModel clickedResource)
        throws EscidocClientException {
        return repository.findById(clickedResource.getId());
    }
}
