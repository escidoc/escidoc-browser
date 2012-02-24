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

import com.google.common.base.Preconditions;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.enums.ContentModelTypeEnum;
import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContentModelProxyImpl;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.ui.maincontent.SearchAdvancedView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class Router {

    private static final String ITEM_CONTROLLER_ID = "org.escidoc.browser.Item";

    private static final String CONTAINER_CONTROLLER_ID = "org.escidoc.browser.Container";

    private static final String CONTEXT_CONTROLLER_ID = "org.escidoc.browser.Context";

    private static final String ORG_UNIT_CONTROLLER_ID = "org.escidoc.browser.OrgUnit";

    private static final String USER_ACCOUNT_CONTROLLER_ID = "org.escidoc.browser.UserAccount";

    private static final String CONTENT_MODEL_CONTROLLER_ID = "org.escidoc.browser.ContentModel";

    private static final String FAIL_RETRIEVING_RESOURCE =
        "Cannot retrieve resource, or you don't have access to see this resource";

    private final BrowserApplication app;

    private final Window mainWindow;

    private final Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private LayoutDesign layout;

    private Properties browserProperties;

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

    /**
     * The mainWindow should be revised whether we need it or not the appHeight is the Height of the Application and I
     * need it for calculations in the inner elements
     * 
     * @param mainWindow
     * @param repositories
     * @throws EscidocClientException
     */
    public Router(final Window mainWindow, final EscidocServiceLocation serviceLocation, final BrowserApplication app,
        final Repositories repositories) throws EscidocClientException {

        this.serviceLocation = serviceLocation;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        init();
    }

    private void init() {
        initiatePlugins();
        createLayout();
        permanentURLelement();
        cachePredefinedContentModels();
    }

    /**
     * Read the Plugins from a directory
     */
    private void initiatePlugins() {
        // Determine here the Layout and additional Controllers that have to be used
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
            mainWindow.showNotification(ViewConstants.LAYOUT_ERR_CANNOT_FIND_CLASS, Notification.TYPE_ERROR_MESSAGE);
        }

    }

    /**
     * Initiate Layout / Default or another one
     * 
     */
    private void createLayout() {
        final String layoutClassName = browserProperties.getProperty("design");
        if (layoutClassName == null) {
            this.getMainWindow().showNotification(ViewConstants.LAYOUT_ERR_CANNOT_LOAD_CLASS,
                Notification.TYPE_ERROR_MESSAGE);
        }
        else {
            Class<?> layoutClass;
            try {
                layoutClass = Class.forName(layoutClassName);
                final LayoutDesign layoutInstance = (LayoutDesign) layoutClass.newInstance();
                layoutInstance.init(mainWindow, serviceLocation, app, repositories, this);
                layout = layoutInstance;

            }
            catch (final ClassNotFoundException e) {
                this.getMainWindow().showNotification(ViewConstants.LAYOUT_ERR_CANNOT_FIND_CLASS,
                    Notification.TYPE_ERROR_MESSAGE);
                LOG.error(ViewConstants.LAYOUT_ERR_CANNOT_FIND_CLASS + e.getLocalizedMessage());
            }
            catch (final InstantiationException e) {
                this.getMainWindow().showNotification(ViewConstants.LAYOUT_ERR_INSTANTIATE_CLASS,
                    Notification.TYPE_ERROR_MESSAGE);
                LOG.error(ViewConstants.LAYOUT_ERR_INSTANTIATE_CLASS + e.getLocalizedMessage());
            }
            catch (final IllegalAccessException e) {
                this.getMainWindow().showNotification(ViewConstants.LAYOUT_ERR_ILLEG_EXEP,
                    Notification.TYPE_ERROR_MESSAGE);
                LOG.error(ViewConstants.LAYOUT_ERR_ILLEG_EXEP + e.getLocalizedMessage());
            }
            catch (final EscidocClientException e) {
                this.getMainWindow().showNotification(e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
                LOG.error(e.getLocalizedMessage());
            }
            catch (final UnsupportedOperationException e) {
                this.getMainWindow().showNotification(e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
                LOG.error(e.getLocalizedMessage());
            }
            catch (final URISyntaxException e) {
                this.getMainWindow().showNotification(e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
                LOG.error(e.getLocalizedMessage());
            }
        }

    }

    /**
     * 
     * @return Layout to the MainWindow normally
     */
    public LayoutDesign getLayout() {
        return layout;
    }

    /**
     * The router should be passed a controller and the controller should open its personal view Should be the default
     * method for binding ControllerViews to the Layout. + Should be the default call
     * 
     * @param controller
     */
    public void openControllerView(final Controller controller, final Boolean doReloadView) {
        if (doReloadView.booleanValue()) {
            controller.showViewByReloading();
        }
        else {
            controller.showView();
        }
    }

    /**
     * public void openControllerView(final Controller cnt, final Boolean doReloadView) { // cnt.init(repositories,
     * this, resourceProxy); if (!doReloadView) { cnt.showView(layout); } else { cnt.showViewByReloading(layout); } }
     * 
     * /** This method handles the open of a new tab on the right section of the mainWindow <br />
     * If you have a controller for the view, please use the method @openControllerView which is the perfect place to
     * inject Views that represent objects <br />
     * The desired method for opening a View is by passing a controller as a reference to the Router. The Controller
     * will be responsible for its view
     * 
     * @param cmp
     * @param tabname
     */
    public void openTab(final Component cmp, final String tabname) {
        layout.openView(cmp, tabname);
    }

    /**
     * This is the Permanent Link entry point Check if there is a tab variable set in the GET Method of the page and
     * open a tab containing the object with that ID URL Example
     * http://escidev4:8084/browser/mainWindow?id=escidoc:16037
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
                    openControllerView(new ContextController(repositories, this, context), true);
                }
                catch (final EscidocClientException e) {
                    showError(FAIL_RETRIEVING_RESOURCE);
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTAINER")) {
                try {
                    final ContainerProxy container =
                        (ContainerProxy) resourceFactory.find(escidocID, ResourceType.CONTAINER);
                    openControllerView(new ContainerController(repositories, this, container), true);
                }
                catch (final EscidocClientException e) {
                    showError(FAIL_RETRIEVING_RESOURCE);
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("ITEM")) {
                try {
                    final ItemProxy item = (ItemProxy) resourceFactory.find(escidocID, ResourceType.ITEM);
                    openControllerView(new ItemController(repositories, this, item), true);
                }
                catch (final EscidocClientException e) {
                    showError(FAIL_RETRIEVING_RESOURCE);
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("sa")) {
                final SearchAdvancedView srch = new SearchAdvancedView(this, serviceLocation);
                layout.openView(srch, "Advanced Search");
            }
            else {
                throw new UnsupportedOperationException("Unknown parameter: "
                    + parameters.get(AppConstants.ARG_TYPE)[0]);
            }
        }
    }

    private void showError(final String msg) {
        mainWindow.showNotification(msg, Notification.TYPE_ERROR_MESSAGE);
    }

    public void show(final ResourceModel clickedResource, final boolean shouldReloadView) throws EscidocClientException {
        Preconditions.checkNotNull(clickedResource, "clickedResource is null: %s", clickedResource);

        final Controller controller = buildController(clickedResource);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        if (shouldReloadView) {
            controller.showViewByReloading();
        }
        else {
            controller.showView();
        }
    }

    // FIXME we should only use the reflection if ResourceModel.type is either container or item, otherwise use a normal
    // constructor.
    private Controller buildController(final ResourceModel clickedResource) throws EscidocClientException {
        final Controller controller = null;
        try {

            final String controllerId = getControllerId(clickedResource);
            final String controllerClassName = browserProperties.getProperty(controllerId);

            // TODO find a better solution for "controller ID not configured"
            if (controllerClassName == null) {
                final String msg = "Could not resolve controller ID. ";
                LOG.error(msg + controllerId);
                throw new IllegalArgumentException(msg);
            }

            return (Controller) Class
                .forName(controllerClassName).getConstructor(Repositories.class, Router.class, ResourceProxy.class)
                .newInstance(repositories, this, fetchResource(clickedResource));
        }
        catch (final ClassNotFoundException e) {
            this.getMainWindow().showNotification(ViewConstants.CONTROLLER_ERR_CANNOT_FIND_CLASS,
                Notification.TYPE_ERROR_MESSAGE);
            LOG.error(ViewConstants.CONTROLLER_ERR_CANNOT_FIND_CLASS + e.getLocalizedMessage());
        }
        catch (final InstantiationException e) {
            this.getMainWindow().showNotification(ViewConstants.CONTROLLER_ERR_INSTANTIATE_CLASS,
                Notification.TYPE_ERROR_MESSAGE);
            LOG.error(ViewConstants.CONTROLLER_ERR_INSTANTIATE_CLASS + e.getLocalizedMessage());
        }
        catch (final IllegalAccessException e) {
            this.getMainWindow().showNotification(ViewConstants.CONTROLLER_ERR_ILLEG_EXEP,
                Notification.TYPE_ERROR_MESSAGE);
            LOG.error(ViewConstants.CONTROLLER_ERR_ILLEG_EXEP + e.getLocalizedMessage());
        }
        catch (final SecurityException e) {
            LOG.error(ViewConstants.CONTROLLER_ERR_SECU_EXEP + e.getLocalizedMessage());
        }
        catch (final InvocationTargetException e) {
            LOG.error(ViewConstants.CONTROLLER_ERR_INVOKE_EXEP + e.getTargetException().getMessage());
        }
        catch (final NoSuchMethodException e) {
            LOG.error(ViewConstants.CONTROLLER_ERR_NOSUCHMETH_EXEP + e.getLocalizedMessage());
        }
        return controller;
    }

    private String getControllerId(final ResourceModel clickedResource) throws EscidocClientException {
        ResourceType type = clickedResource.getType();

        String controllerId = null;
        switch (type) {
            case CONTEXT:
                controllerId = CONTEXT_CONTROLLER_ID;
                break;
            case CONTAINER:
                controllerId = CONTAINER_CONTROLLER_ID;
                controllerId = findContollerId(clickedResource, controllerId);
                break;
            case ITEM:
                controllerId = ITEM_CONTROLLER_ID;
                controllerId = findContollerId(clickedResource, controllerId);
                break;
            case ORG_UNIT:
                controllerId = ORG_UNIT_CONTROLLER_ID;
                break;
            case USER_ACCOUNT:
                controllerId = USER_ACCOUNT_CONTROLLER_ID;
                break;
            case CONTENT_MODEL:
                controllerId = CONTENT_MODEL_CONTROLLER_ID;
                break;
            default:
                throw new UnsupportedOperationException("Unknown resource type: "
                    + clickedResource.getType().getLabel() + "can not be shown.");

        }
        LOG.debug("ControllerID[" + controllerId + "]");
        return controllerId;
    }

    private String findContollerId(final ResourceModel clickedResource, final String oldId)
        throws EscidocClientException {

        final String contentModelDescription = getContentModelDescription(clickedResource);
        if (contentModelDescription == null) {
            return oldId;
        }

        final Matcher controllerIdMatcher =
            Pattern.compile("org.escidoc.browser.Controller=([^;]*);").matcher(contentModelDescription);
        if (controllerIdMatcher.find()) {
            return controllerIdMatcher.group(1);
        }

        return oldId;
    }

    private String getContentModelDescription(final ResourceModel clickedResource) throws EscidocClientException {
        return repositories
            .contentModel().findById(fetchResource(clickedResource).getContentModel().getObjid()).getDescription();
    }

    private ResourceProxy fetchResource(final ResourceModel clickedResource) throws EscidocClientException {
        switch (clickedResource.getType()) {
            case CONTEXT:
                return repositories.context().findById(clickedResource.getId());
            case CONTAINER:
                return repositories.container().findById(clickedResource.getId());
            case ITEM:
                return repositories.item().findById(clickedResource.getId());
            case ORG_UNIT:
                return repositories.organization().findById(clickedResource.getId());
            case USER_ACCOUNT:
                return repositories.user().findById(clickedResource.getId());
            case CONTENT_MODEL:
                return repositories.contentModel().findById(clickedResource.getId());
            default:
                throw new UnsupportedOperationException(clickedResource.getType() + " is unsupported");
        }
    }

    private void cachePredefinedContentModels() {
        List<ResourceModel> contentModels = null;
        try {
            final String SEARCH_STRING_FOR_MATCHER = AppConstants.CMM_DESCRIPTION_MATCHER;
            final Pattern controllerPattern = Pattern.compile(SEARCH_STRING_FOR_MATCHER);
            contentModels = repositories.contentModel().findAll();

            if (contentModels == null) {
                return;
            }

            synchronized (ELabsCache.getContentModels()) {

                for (final ResourceModel resourceModel : contentModels) {
                    if (resourceModel instanceof ContentModelProxyImpl) {
                        final ContentModelProxyImpl contentModelProxy = (ContentModelProxyImpl) resourceModel;
                        final String description = contentModelProxy.getDescription();
                        if (description == null || description.isEmpty()) {
                            continue;
                        }
                        final Matcher controllerMatcher = controllerPattern.matcher(description);
                        String controllerName = null;
                        if (controllerMatcher.find()) {
                            controllerName = controllerMatcher.group(1);
                        }
                        if (controllerName == null || controllerName.isEmpty()) {
                            continue;
                        }
                        final String cmmId = contentModelProxy.getId();

                        if (!ELabsCache.getContentModels().containsKey(cmmId)) {
                            if (controllerName.equals(AppConstants.CMM_DESCRIPTION_INSTRUMENT)) {
                                ELabsCache.getContentModels().put(cmmId, ContentModelTypeEnum.INSTRUMENT);
                            }
                            else if (controllerName.equals(AppConstants.CMM_DESCRIPTION_RIG)) {
                                ELabsCache.getContentModels().put(cmmId, ContentModelTypeEnum.RIG);
                            }
                            else if (controllerName.equals(AppConstants.CMM_DESCRIPTION_INVESTIGATION)) {
                                ELabsCache.getContentModels().put(cmmId, ContentModelTypeEnum.INVESTIGATION);
                            }
                            else if (controllerName.equals(AppConstants.CMM_DESCRIPTION_STUDY)) {
                                ELabsCache.getContentModels().put(cmmId, ContentModelTypeEnum.STUDY);
                            }
                            else if (controllerName.equals(AppConstants.CMM_DESCRIPTION_INVESTIGATION_RESULTS)) {
                                ELabsCache.getContentModels().put(cmmId, ContentModelTypeEnum.INVESTIGATION_RESULT);
                            }
                        }
                    }
                }
            }
        }
        catch (final ClassCastException e) {
            LOG.error(e.getMessage());
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
        }
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    public EscidocServiceLocation getServiceLocation() {
        return serviceLocation;
    }

    public Repositories getRepositories() {
        return repositories;
    }

    public BrowserApplication getApp() {
        return this.app;
    }
}