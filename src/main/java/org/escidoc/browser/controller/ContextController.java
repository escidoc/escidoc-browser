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
package org.escidoc.browser.controller;

import java.net.URISyntaxException;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ContextController extends Controller {
    private Repositories repositories;

    private Router router;

    private ResourceProxy resourceProxy;

    private static final Logger LOG = LoggerFactory.getLogger(ContextController.class);

    public ContextController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        Preconditions.checkNotNull(repositories, "repositories is NULL");
        Preconditions.checkNotNull(router, "Router is NULL");

        this.repositories = repositories;
        this.router = router;
        this.resourceProxy = resourceProxy;
        try {
            this.view = createView(resourceProxy);
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(
                ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW + e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
            LOG.error("Failed at: ", e.getStackTrace());
        }
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    /**
     * Create the view for this controller
     * 
     * @param resourceProxy
     * @return
     * @throws EscidocClientException
     */
    private Component createView(ResourceProxy resourceProxy) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        return new ContextView(router, resourceProxy, repositories, this);
    }

    /**
     * Handle the operation of adding a new organizational unit from a context<br />
     * Called from one of the view elements of a Context
     * 
     * @param resourceProxy
     * @param orgUnitid
     */
    public void addOrgUnitToContext(ContextProxyImpl resourceProxy, String orgUnitid) {
        try {
            repositories.context().addOrganizationalUnit(resourceProxy.getId(), orgUnitid);
            showTrayMessage("Updated!", "Organizational Unit was added Successfully");
        }
        catch (EscidocClientException e) {
            showError("Unable to add the OrganizationalUnit. An error occurred " + e.getLocalizedMessage());
        }

    }

    /**
     * Handle the operation of removing an organizational unit from a context<br />
     * Called from one of the view elements of a Context
     * 
     * @param resourceProxy
     * @param orgUnitid
     */
    public void removeOrgUnitFromContext(ContextProxyImpl resourceProxy, String orgUnitid) {
        try {
            repositories.context().delOrganizationalUnit(resourceProxy.getId(), orgUnitid);
            showTrayMessage("Updated!", "Organizational Unit was removed Successfully");
        }
        catch (EscidocClientException e) {
            showError("Unable to add the OrganizationalUnit. An error occurred " + e.getLocalizedMessage());
        }
    }

    public void updateContextType(String newContextType, String contextId) throws EscidocClientException {
        repositories.context().updateType(newContextType, contextId);

    }

    public void updateContextName(String newName, String contextId) throws EscidocClientException {
        repositories.context().updateName(newName, contextId);
    }

    public void updatePublicStatus(String newStatus, String contextId, String comment) throws EscidocClientException {

        if (newStatus.equals("opened")) {
            repositories.context().updatePublicStatusOpen(comment, contextId);
        }
        else if (newStatus.equals("closed")) {
            repositories.context().updatePublicStatusClosed(comment, contextId);
        }

    }

    /**
     * PDP check if the logged-in user can Remove an Organizational Unit
     * 
     * @return boolean
     */
    public boolean canRemoveOUs() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_ORGANIZATIONAL_UNIT_ACTION)
                .forResource(resourceProxy.getId()).permitted();
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * PDP check if the logged-in user can Add a new Organizational Unit
     * 
     * @return boolean
     */
    public boolean canAddOUs() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_ORG_UNIT).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * PDP check if the logged-in user can update this context
     * 
     * @return boolean
     */
    public boolean canUpdateContext() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

}
