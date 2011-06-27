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

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(TreeClickListener.class);

    private final EscidocServiceLocation serviceLocation;

    private final MainSite mainSite;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    public TreeClickListener(final EscidocServiceLocation serviceLocation, final Repositories repositories,
        final Window mainWindow, final MainSite mainSite, final CurrentUser currentUser) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.mainSite = mainSite;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        final ResourceModel clickedResource = (ResourceModel) event.getItemId();
        if (ContextModel.isContext(clickedResource)) {
            try {
                openInNewTab(
                    new ContextView(serviceLocation, mainSite, tryToFindResource(repositories.context(),
                        clickedResource), mainWindow, currentUser, repositories), clickedResource);
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }
        else if (ContainerModel.isContainer(clickedResource)) {
            try {
                openInNewTab(
                    new ContainerView(serviceLocation, mainSite, tryToFindResource(repositories.container(),
                        clickedResource), mainWindow, currentUser, repositories), clickedResource);
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }
        else if (ItemModel.isItem(clickedResource)) {
            openInNewTab(
                new ItemView(serviceLocation, repositories, mainSite, tryToFindResource(repositories.item(),
                    clickedResource), mainWindow), clickedResource);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private ResourceProxy tryToFindResource(final Repository repository, final ResourceModel clickedResource) {
        try {
            return repository.findById(clickedResource.getId());
        }
        catch (final EscidocClientException e) {
            showErrorMessageToUser(clickedResource, e);
        }
        return null;
    }

    private void openInNewTab(final Component component, final ResourceModel clickedResource) {
        mainSite.openTab(component, clickedResource.getName());
    }

    private void showErrorMessageToUser(final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
        mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
            Notification.TYPE_ERROR_MESSAGE));
    }
}