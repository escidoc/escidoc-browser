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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.controller;

import java.net.URISyntaxException;

import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.escidoc.browser.ui.view.helpers.ItemComponentsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.component.Component;

public class ItemController extends Controller {

    private static final Logger LOG = LoggerFactory.getLogger(ItemController.class);

    private static final String URI_DC = "http://purl.org/dc/elements/1.1/";

    public ItemController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        view = new ItemView(getRouter(), getResourceProxy(), this);
    }

    /**
     * Check if the user has access and provide some operations on the view
     * 
     * @return boolean
     */
    public boolean canUpdateItem() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ITEM).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification("EscidocClientException " + e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (final URISyntaxException e) {
            router.getMainWindow().showNotification("URISyntaxException " + e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

    public void removeComponent(Object target, ItemComponentsView table) {

        try {
            getRepositories().item().deleteComponent(resourceProxy.getId(), target.toString());
            showTrayMessage("Updated!", "Component was removed successfully");
            table.removeItemFromTable(target.toString());
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification("Error cannot remove component! ", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public void removeMetadata(String mdId) {
        try {
            repositories.item().removeMetadata(resourceProxy.getId(), mdId);
            showTrayMessage("Removed!", "Metadata " + mdId + " was removed successfully!");
        }
        catch (EscidocClientException e) {
            showError("Unable to remove metadata " + e.getLocalizedMessage());
        }
    }

    public boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ITEM).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug("No Access " + e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug("Wrong URI " + e.getLocalizedMessage());
            return false;
        }
    }

    public void removeComponentMetadata(String mdId, String itemId, String compId) {
        try {
            repositories.item().removeComponentMetadata(mdId, itemId, compId);
            showTrayMessage("Removed!", "Metadata " + mdId + " was removed successfully!");
        }
        catch (EscidocClientException e) {
            showError("Unable to remove metadata " + e.getLocalizedMessage());
        }

    }

    public void updateComponent(Component component, String id) throws EscidocClientException {
        repositories.item().updateComponent(component, id);

    }

    public void updateMetaDataComponent(MetadataRecord metadataRecord, ItemProxyImpl itemProxy, Component component) {
        try {
            repositories.item().updateComponentMetadata(component, itemProxy.getId(), metadataRecord);
            showTrayMessage("Updated!", "Metadata " + metadataRecord.getName() + " was updated successfully!");
        }
        catch (EscidocClientException e) {
            showError("Unable to update metadata " + e.getLocalizedMessage());

        }

    }

    public void updateComponentCategory(String componentId, String newCatType, String itemId) {
        try {
            repositories.item().updateComponentCategoryType(componentId, newCatType, itemId);
            showTrayMessage("Updated!", "Component category type was updated successfully!");
        }
        catch (EscidocClientException e) {
            showError("Unable to update metadata " + e.getLocalizedMessage());
        }

    }

    public MetadataRecord getMetadata(String name) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(name, "name is null: %s", name);
        return repositories.item().getMetadataRecord(resourceProxy.getId(), name);
    }

    public void updateMetadata(MetadataRecord metadata) throws EscidocClientException {
        Preconditions.checkNotNull(metadata, "metadata is null: %s", metadata);
        repositories.item().updateMetaData(resourceProxy, metadata);
    }

    public boolean hasComponents() {
        return ((ItemProxy) resourceProxy).hasComponents().booleanValue();
    }

    public boolean hasAccessDelResource() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_ITEM).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final UnsupportedOperationException e) {
            showTrayMessage("Error", e.getMessage());
            e.printStackTrace();
            return false;
        }
        catch (final EscidocClientException e) {
            showTrayMessage("Error", e.getMessage());
            e.printStackTrace();
            return false;
        }
        catch (final URISyntaxException e) {
            showTrayMessage("Error", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void updateItem(
        Boolean isChangedTitle, Boolean isChangedPublicStatus, Boolean isChangedLockStatus, String title,
        String publicStatus, String locStatus, String comment) throws EscidocClientException {
        Item item = repositories.item().findItemById(resourceProxy.getId());

        if ((locStatus.equals("locked")) && (!isChangedLockStatus)) {
            router.getMainWindow().showNotification(
                new Window.Notification("Cannot update since the item is in status locked",
                    Notification.TYPE_TRAY_NOTIFICATION));
        }
        else {
            if (isChangedTitle) {
                Element e = item.getMetadataRecords().get("escidoc").getContent();

                if (e != null && e.getChildNodes() != null) {
                    final NodeList nodeList = e.getChildNodes();

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        final String nodeName = node.getLocalName();
                        final String nsUri = node.getNamespaceURI();

                        if (nodeName == null || nodeName.equals("")) {
                            continue;
                        }
                        if (nodeName.equals("title") && URI_DC.equals(nsUri)) {
                            node.getFirstChild().setNodeValue(title);
                        }
                    }
                }
                repositories.item().updateMetaData(item.getMetadataRecords().get("escidoc"), item);
            }
            if (isChangedPublicStatus) {
                repositories.item().changePublicStatus(item, publicStatus, comment);
                LOG.debug("Ndryshem u ndryshua Public Status");
            }
            if (isChangedLockStatus) {
                updateLockStatus(item, comment, locStatus);
                LOG.debug("Ndryshem u ndryshua LOCK Status");
            }
        }

    }

    private void updateLockStatus(final Item item, final String comment, String lockStatusTxt)
        throws EscidocClientException {
        if (lockStatusTxt.contains("LOCKED")) {
            repositories.item().lockResource(item, comment);
        }
        else {
            repositories.item().unlockResource(item, comment);

        }

    }
}
