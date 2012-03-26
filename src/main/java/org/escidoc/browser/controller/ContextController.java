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

import com.vaadin.ui.Window;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;

public class ContextController extends Controller {

    public ContextController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        try {
            view = new ContextView(getRouter(), getResourceProxy(), getRepositories(), this);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification("Error", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public void refreshView() {

        try {
            getRouter().show(resourceProxy, true);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
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
            getRepositories().context().addOrganizationalUnit(resourceProxy.getId(), orgUnitid);
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
    public void removeOrgUnitFromContext(String orgUnitid) {
        try {
            getRepositories().context().delOrganizationalUnit(resourceProxy.getId(), orgUnitid);
            showTrayMessage("Updated!", "Organizational Unit was removed Successfully");
        }
        catch (EscidocClientException e) {
            showError("Unable to add the OrganizationalUnit. An error occurred " + e.getLocalizedMessage());
        }
    }

    public void updateContextType(String newContextType, String contextId) throws EscidocClientException {
        getRepositories().context().updateType(newContextType, contextId);

    }

    public void updateContextName(String newName, String contextId) throws EscidocClientException {
        getRepositories().context().updateName(newName, contextId);
    }

    public void updatePublicStatus(String newStatus, String contextId, String comment) throws EscidocClientException {

        if (newStatus.equals("opened")) {
            getRepositories().context().updatePublicStatusOpen(comment, contextId);
        }
        else if (newStatus.equals("closed")) {
            getRepositories().context().updatePublicStatusClosed(comment, contextId);
        }
    }

    public AdminDescriptor addAdminDescriptor(String txtName, String txtContent) {
        try {
            return getRepositories().context().addAdminDescriptor(resourceProxy.getId(), txtName, txtContent);
        }
        catch (ParserConfigurationException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (SAXException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (IOException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return null;

    }

    public void removeAdminDescriptor(String name) {
        try {
            getRepositories().context().removeAdminDescriptor(resourceProxy.getId(), name);
            this.showTrayMessage(ViewConstants.ADMINDESCRIPTION_REMOVE, ViewConstants.ADMINDESCRIPTION_REMOVED);

        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public AdminDescriptors retrieveAdmDescriptors() {
        try {
            return getRepositories().context().findContextById(resourceProxy.getId()).getAdminDescriptors();
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * PDP check if the logged-in user can Remove an Organizational Unit
     * 
     * @return boolean
     */
    public boolean canRemoveOUs() {
        try {
            return getRepositories()
                .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_ORGANIZATIONAL_UNIT_ACTION)
                .forResource(getResourceProxy().getId()).permitted();
        }
        catch (final EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (final URISyntaxException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
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
            return getRepositories()
                .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_ORG_UNIT)
                .forResource(getResourceProxy().getId()).permitted();
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
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
            return getRepositories()
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER)
                .forResource(getResourceProxy().getId()).permitted();
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

}
