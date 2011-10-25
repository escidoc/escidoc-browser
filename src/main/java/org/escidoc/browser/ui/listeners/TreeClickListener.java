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

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.resources.Resource;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(TreeClickListener.class);

    private final EscidocServiceLocation serviceLocation;

    private final Router router;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    public TreeClickListener(final EscidocServiceLocation serviceLocation, final Repositories repositories,
        final Window mainWindow, final Router mainSite, final CurrentUser currentUser) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.repositories = repositories;
        this.mainWindow = mainWindow;
        router = mainSite;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        openClickedResourceInNewTab((ResourceModel) event.getItemId());
    }

    private void openClickedResourceInNewTab(final ResourceModel clickedResource) {
        try {
            createView(clickedResource);
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            showErrorMessageToUser(clickedResource, e);
        }
    }

    private void createView(final ResourceModel clickedResource) throws EscidocClientException {
        router.show(clickedResource);
    }

    private void showErrorMessageToUser(final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
        mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
            Notification.TYPE_ERROR_MESSAGE));
    }

    private String findContextId(final ResourceModel clickedResource) {
        if (clickedResource instanceof ContextModel) {
            return ((ContextModel) clickedResource).getId();
        }
        else if (clickedResource instanceof ContainerModel) {
            final ContainerModel containerModel = (ContainerModel) clickedResource;
            try {
                return repositories.container().findById(containerModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                router.getMainWindow().showNotification(ViewConstants.NOT_ABLE_TO_RETRIEVE_A_CONTEXT);
            }
        }
        else if (clickedResource instanceof ItemModel) {
            final ItemModel itemModel = (ItemModel) clickedResource;
            try {
                return repositories.item().findById(itemModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                router.getMainWindow().showNotification(ViewConstants.NOT_ABLE_TO_RETRIEVE_A_CONTEXT);
            }
        }
        return AppConstants.EMPTY_STRING;
    }

    private String findContentModelId(final ResourceModel clickedResource) throws ContentModelNotFoundException {
        String contentModelId = "escidoc:";
        Resource eSciDocResource = null;
        try {
            if (clickedResource instanceof ContextModel) {
                contentModelId = AppConstants.EMPTY_STRING;
            }
            else if (clickedResource instanceof ContainerModel) {
                eSciDocResource = repositories.container().findById(clickedResource.getId()).getContentModel();
                contentModelId += (eSciDocResource.getXLinkHref().split(":"))[1];
            }
            else if (clickedResource instanceof ItemModel) {
                eSciDocResource = repositories.item().findById(clickedResource.getId()).getContentModel();
                contentModelId += (eSciDocResource.getXLinkHref().split(":"))[1];
            }
            else {
                contentModelId = null;
            }
        }
        catch (final EscidocClientException e) {
            LOG.error("Unable to retreive ContentModel data from repository object", e);
            contentModelId = null;
            throw new ContentModelNotFoundException();
        }
        return contentModelId;
    }
}
