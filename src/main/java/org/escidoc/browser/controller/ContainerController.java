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

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ContainerView;
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
import de.escidoc.core.resources.om.container.Container;

public class ContainerController extends Controller {
    private static final Logger LOG = LoggerFactory.getLogger(ContainerController.class);

    private static final String URI_DC = "http://purl.org/dc/elements/1.1/";

    public ContainerController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        try {
            view = new ContainerView(getRouter(), getResourceProxy(), getRepositories(), this);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification("Error cannot create view: ", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void removeMetadata(String mdId) {
        try {
            repositories.container().removeMetadata(resourceProxy.getId(), mdId);
            showTrayMessage("Removed!", "Metadata" + mdId + " was removed successfully!");
        }
        catch (EscidocClientException e) {
            showError("Unable to remove metadata " + e.getLocalizedMessage());
        }

    }

    public boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug("No Access" + e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug("Wrong URI " + e.getLocalizedMessage());
            return false;
        }
    }

    public MetadataRecord getMetadata(String name) {
        Preconditions.checkNotNull(name, "name is null: %s", name);
        try {
            return repositories.container().getMetadataRecord(resourceProxy.getId(), name);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return null;

    }

    public void updateMetadata(MetadataRecord metadataRecord) throws EscidocClientException {
        Preconditions.checkNotNull(metadataRecord, "metadataRecord is null: %s", metadataRecord);
        repositories.container().updateMetaData(resourceProxy, metadataRecord);
    }

    public void updateContainer(
        Boolean isChangedTitle, Boolean isChangedDescription, Boolean isChangedPublicStatus,
        Boolean isChangedLockStatus, String title, String description, String publicStatus, String lockStatus,
        String comment) throws EscidocClientException {
        Container container = repositories.container().findContainerById(resourceProxy.getId());
        if ((lockStatus.equals("locked")) && (!isChangedLockStatus)) {
            router.getMainWindow().showNotification(
                new Window.Notification("Cannot update since the item is in status locked",
                    Notification.TYPE_TRAY_NOTIFICATION));
        }
        else {
            if (isChangedTitle) {
                changeTitle(title, container);
                repositories.container().updateMetaData(container.getMetadataRecords().get("escidoc"), container);
            }
            if (isChangedDescription) {
                changeDescription(description, container);
                repositories.container().updateMetaData(container.getMetadataRecords().get("escidoc"), container);
                // repositories.container().updateDescrition(description, resourceProxy.getId());
            }
            if (isChangedPublicStatus) {
                repositories.container().changePublicStatus(container, publicStatus, comment);
            }
            if (isChangedLockStatus) {
                updateLockStatus(container, comment, lockStatus);
            }
        }

    }

    private void changeTitle(String title, Container container) {
        Element e = container.getMetadataRecords().get("escidoc").getContent();
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
    }

    private void changeDescription(String description, Container container) {
        Element e = container.getMetadataRecords().get("escidoc").getContent();
        if (e != null && e.getChildNodes() != null) {
            boolean found = false;
            final NodeList nodeList = e.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if (nodeName == null || nodeName.equals("")) {
                    continue;
                }
                if (nodeName.equals("description") && URI_DC.equals(nsUri)) {
                    node.getFirstChild().setNodeValue(description);
                    found = true;
                }

            }
            if (!found) {
                final Element descriptionEl = e.getOwnerDocument().createElementNS(URI_DC, "description");
                descriptionEl.setPrefix("dc");
                descriptionEl.setTextContent(description);
                e.appendChild(descriptionEl);
            }
        }
    }

    private void updateLockStatus(Container container, String comment, String lockStatus) throws EscidocClientException {
        LOG.debug("LockStatus is " + lockStatus);
        if (lockStatus.contains("unlocked")) {
            repositories.container().unlockResource(container, comment);
        }
        else {
            repositories.container().lockResource(container, comment);
        }

    }

    public boolean canUpdateContainer() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean hasAccessDelResource() {
        // TODO Auto-generated method stub
        return false;
    }
}