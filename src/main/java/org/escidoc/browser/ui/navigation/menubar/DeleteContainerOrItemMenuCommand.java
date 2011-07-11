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

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;

import com.google.common.base.Preconditions;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
final class DeleteContainerOrItemMenuCommand implements Command {

    private final ResourceModel resourceModel;

    private final TreeDataSource treeDataSource;

    private final Repositories repositories;

    private final Window mainWindow;

    public DeleteContainerOrItemMenuCommand(final Window maWindow, final Repositories repositories,
        final ResourceModel resourceModel, final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(maWindow, "maWindow is null: %s", maWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(resourceModel, "resourceModel is null: %s", resourceModel);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        mainWindow = maWindow;
        this.repositories = repositories;
        this.resourceModel = resourceModel;
        this.treeDataSource = treeDataSource;
    }

    @Override
    public void menuSelected(final MenuItem selectedItem) {
        deleteResource();
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