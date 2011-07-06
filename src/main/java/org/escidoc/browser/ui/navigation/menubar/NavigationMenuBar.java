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

import org.escidoc.browser.ActionIdConstants;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;

import com.google.common.base.Preconditions;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class NavigationMenuBar extends CustomComponent {

    private ShowContainerAddViewMenuCommand showContainerAddViewMenuCommand;

    private final MenuBar menuBar = new MenuBar();

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private MenuBar.MenuItem add;

    private MenuItem itemMenuItem;

    private MenuItem contextMenuItem;

    private MenuItem containerMenuItem;

    private MenuItem deleteMenuItem;

    private final Window mainWindow;

    private final TreeDataSource treeDataSource;

    public NavigationMenuBar(final CurrentUser currentUser, final Repositories repositories, final Window mainWindow,
        final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        this.currentUser = currentUser;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.treeDataSource = treeDataSource;
        setCompositionRoot(menuBar);
        init();
        bindRole();
    }

    private void bindRole() {
        try {
            menuBar.setEnabled(isCreateContextAllowed());
        }
        catch (final EscidocClientException e) {
            e.printStackTrace();
        }
        catch (final URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private boolean isCreateContextAllowed() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.CREATE_CONTEXT).forUser(currentUser.getUserId()).forResource("")
            .permitted();
    }

    private void init() {
        menuBar.setSizeFull();
        addCreateMenu();
        addDeleteMenu();
        update(null);
    }

    private void addCreateMenu() {
        add = menuBar.addItem(ViewConstants.ADD, null);
        contextMenuItem = add.addItem(ResourceType.CONTEXT.asLabel(), showContainerAddViewMenuCommand);
        containerMenuItem = add.addItem(ResourceType.CONTAINER.asLabel(), showContainerAddViewMenuCommand);
        itemMenuItem = add.addItem(ResourceType.ITEM.asLabel(), showContainerAddViewMenuCommand);
    }

    private void addDeleteMenu() {
        final Window window = getWindow();
        if (window == null) {
            return;
        }
        deleteMenuItem = menuBar.addItem(ViewConstants.DELETE, null);
        deleteMenuItem.setEnabled(false);
    }

    public void update(final ResourceModel resourceModel) {
        if (resourceModel == null) {
            showAddContext();
        }
        else {
            switch (resourceModel.getType()) {
                case CONTEXT:
                    showAddContainerAndItem();
                    break;
                case CONTAINER:
                    showContainerAddViewMenuCommand =
                        new ShowContainerAddViewMenuCommand(repositories, mainWindow,
                            getContextIdForContainer(resourceModel), treeDataSource);
                    showContainerAddViewMenuCommand.withParent(resourceModel);
                    containerMenuItem.setCommand(showContainerAddViewMenuCommand);
                    showAddContainerAndItem();
                    break;
                case ITEM:
                    menuBar.setEnabled(false);
            }
        }
    }

    private String getContextIdForContainer(final ResourceModel resourceModel) {
        try {
            return repositories.container().findById(resourceModel.getId()).getContext().getObjid();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return AppConstants.EMPTY_STRING;
    }

    private void showAddContext() {
        menuBar.setEnabled(true);
        contextMenuItem.setVisible(true);
        containerMenuItem.setVisible(false);
        itemMenuItem.setVisible(false);
    }

    private void showAddContainerAndItem() {
        menuBar.setEnabled(true);
        contextMenuItem.setVisible(false);
        containerMenuItem.setVisible(true);
        itemMenuItem.setVisible(true);
    }
}