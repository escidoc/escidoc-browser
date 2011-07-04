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

import java.net.MalformedURLException;

import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.maincontent.ContainerAddView;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.maincontent.ContainerAddView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class ShowContainerAddView implements Command {

    private final Repositories repositories;

    private final Window mainWindow;

    private ResourceModel parent;

    private final String contextId;

    private final TreeDataSource treeDataSource;

    public ShowContainerAddView(final Repositories repositories, final Window mainWindow, final String contextId,
        final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.contextId = contextId;
        this.treeDataSource = treeDataSource;
        final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.contextId = contextId;
        this.treeDataSource = treeDataSource;
    }

    public void withParent(final ResourceModel parent) {
        this.parent = parent;
    }

    public void withParent(final ResourceModel parent) {
        this.parent = parent;
    }

    @Override
    public void menuSelected(final MenuItem selectedItem) {
        if (isContainerSelected(selectedItem)) {
            showIt();
        }
        else {
            mainWindow.showNotification("Action " + selectedItem.getText());
        }
    }

    public void showIt() {
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        // TreeDataSource treeDataSource;
        try {
            // treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
            // treeDataSource.init();
            final ContainerAddView containerAddView =
                new ContainerAddView(repositories, mainWindow, parent, treeDataSource, contextId);
            containerAddView.openSubWindow();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (final MalformedURLException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        }
    }

    public void showIt() {
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        // TreeDataSource treeDataSource;
        try {
            // treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
            // treeDataSource.init();
            final ContainerAddView containerAddView =
                new ContainerAddView(repositories, mainWindow, parent, treeDataSource, contextId);
            containerAddView.openSubWindow();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (final MalformedURLException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
    }

    private boolean isContainerSelected(final MenuItem selectedItem) {
        return selectedItem.getText().equals("Container");
        return selectedItem.getText().equals("Container");
    }
}