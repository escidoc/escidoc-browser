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
package org.escidoc.browser.ui.navigation.menubar;

import java.net.URISyntaxException;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
final class DeleteContainerOrItemMenuCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteContainerOrItemMenuCommand.class);

    private final ResourceModel resourceModel;

    private final TreeDataSource treeDataSource;

    private final Repositories repositories;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    public DeleteContainerOrItemMenuCommand(final Window mainWindow, final Repositories repositories,
        final ResourceModel resourceModel, final TreeDataSource treeDataSource, final CurrentUser currentUser) {
        Preconditions.checkNotNull(mainWindow, "maWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(resourceModel, "resourceModel is null: %s", resourceModel);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.resourceModel = resourceModel;
        this.treeDataSource = treeDataSource;
        this.currentUser = currentUser;
    }

    @Override
    public void menuSelected(final MenuItem selectedItem) {
        try {
            switch (resourceModel.getType()) {
                case CONTAINER:
                    if (isUserAllowedToDeleteContainer()) {
                        deleteResource();
                    }
                    else {
                        showWarning();
                    }
                    break;
                case ITEM:
                    if (isUserAllowedToDeleteItem()) {
                        deleteResource();
                    }
                    else {
                        showWarning();
                    }
                    break;
                default:
                    mainWindow.showNotification("Deleting " + resourceModel.getType() + " is not yet implemented",
                        Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);

        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private boolean isUserAllowedToDeleteItem() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.DELETE_ITEM).forUser(currentUser.getUserId())
            .forResource(resourceModel.getId()).permitted();
    }

    private void showWarning() {
        mainWindow.showNotification(ViewConstants.NOT_AUTHORIZED, "You do not have the right to delete resource: "
            + resourceModel.getName(), Window.Notification.TYPE_WARNING_MESSAGE);
    }

    private boolean isUserAllowedToDeleteContainer() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.DELETE_CONTAINER).forUser(currentUser.getUserId())
            .forResource(resourceModel.getId()).permitted();
    }

    private void deleteResource() {
        try {
            switch (resourceModel.getType()) {
                case CONTAINER:
                    repositories.container().delete(resourceModel);
                    break;
                case ITEM:
                    repositories.item().delete(resourceModel);
                    break;
                default:
                    mainWindow.showNotification("Deleting " + resourceModel.getType() + " is not yet implemented",
                        Window.Notification.TYPE_ERROR_MESSAGE);
            }
            treeDataSource.remove(resourceModel);
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification("Can not delete " + resourceModel.getName(), e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
}