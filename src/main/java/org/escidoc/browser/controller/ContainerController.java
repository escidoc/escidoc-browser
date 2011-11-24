package org.escidoc.browser.controller;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ContainerController extends Controller {

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Repositories repositories;

    private Window mainWindow;

    private Router router;

    private static Logger LOG = LoggerFactory.getLogger(ContainerController.class);

    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router router, ResourceProxy resourceProxy,
        Window mainWindow) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is NULL");
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        Preconditions.checkNotNull(repositories, "repositories is NULL");
        Preconditions.checkNotNull(mainWindow, "mainWindow is NULL");
        Preconditions.checkNotNull(router, "Router is NULL");
        this.serviceLocation = serviceLocation;
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.router = router;
        try {
            this.view = createView(resourceProxy);
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW + e.getLocalizedMessage(),
                Notification.TYPE_ERROR_MESSAGE);
            LOG.error("Failed at: ", e.getStackTrace());
        }
        this.setResourceName(resourceProxy.getName());

    }

    private Component createView(ResourceProxy resourceProxy) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        return new ContainerView(serviceLocation, router, resourceProxy, mainWindow, repositories);
    }

}
