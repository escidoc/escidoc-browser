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

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class NavigationMenuBar extends CustomComponent {

    private static final String UNLOCK = "unlock";

    private static final String IN_REVISION = "in-revision";

    private static final String PENDING = "pending";

    private static final Logger LOG = LoggerFactory.getLogger(NavigationMenuBar.class);

    private final MenuBar menuBar = new MenuBar();

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private final Window mainWindow;

    private final TreeDataSource treeDataSource;

    private MenuBar.MenuItem add;

    private MenuItem itemMenuItem;

    private MenuItem contextMenuItem;

    private MenuItem containerMenuItem;

    private MenuItem deleteMenuItem;

    private ShowAddViewCommand showAddViewCommand;

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
            LOG.error(e.getMessage());
            mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
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
        try {
            updateMenuBar(null);
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (final URISyntaxException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void addCreateMenu() {
        add = menuBar.addItem(ViewConstants.ADD, null);
        contextMenuItem = add.addItem(ResourceType.CONTEXT.asLabel(), showAddViewCommand);
        containerMenuItem = add.addItem(ResourceType.CONTAINER.asLabel(), null);
        itemMenuItem = add.addItem(ResourceType.ITEM.asLabel(), null);
    }

    private void addDeleteMenu() {
        final Window window = getWindow();
        if (window == null) {
            return;
        }
        deleteMenuItem = menuBar.addItem(ViewConstants.DELETE, null);
        deleteMenuItem.setEnabled(false);
    }

    public void updateMenuBar(final ResourceModel resourceModel) throws EscidocClientException, URISyntaxException {
        if (resourceModel == null) {
            showAddContext();
        }
        else {
            switch (resourceModel.getType()) {
                case CONTEXT:
                    buildCommand(resourceModel, resourceModel.getId());
                    showAddContainerAndItem();
                    break;
                case CONTAINER:
                    buildCommand(resourceModel, getContextIdForContainer(resourceModel));
                    showAddContainerAndItem();
                    updateDeleteMenu(resourceModel);
                    break;
                case ITEM:
                    updateDeleteMenu(resourceModel);
                    add.setEnabled(false);
            }
        }
    }

    private void updateDeleteMenu(final ResourceModel resourceModel) throws EscidocClientException, URISyntaxException {
        if (resourceCanBeDeleted(resourceModel)) {
            showDelete(resourceModel);
        }
        else {
            hideDelete(resourceModel);
        }
    }

    private void hideDelete(final ResourceModel resourceModel) {
        if (deleteMenuItem == null) {
            return;
        }
        deleteMenuItem.setEnabled(false);
    }

    private boolean resourceCanBeDeleted(final ResourceModel resourceModel) throws EscidocClientException,
        URISyntaxException {
        switch (resourceModel.getType()) {
            case CONTAINER:
                return canUserDeleteContainer(resourceModel) && isDeletable(retrieveContainer(resourceModel));
            case ITEM:
                return canUserDeleteItem(resourceModel) && isDeletable(retrieveItem(resourceModel));
            default:
                return false;
        }
    }

    private ResourceProxy retrieveItem(final ResourceModel resourceModel) throws EscidocClientException {
        return repositories.item().findById(resourceModel.getId());
    }

    private ResourceProxy retrieveContainer(final ResourceModel resourceModel) throws EscidocClientException {
        return repositories.container().findById(resourceModel.getId());
    }

    private boolean isDeletable(final ResourceProxy container) {
        return container.getStatus().equals(PENDING) || container.getStatus().equals(IN_REVISION)
            || container.getLockStatus().equals(UNLOCK);

    }

    private boolean canUserDeleteContainer(final ResourceModel resourceModel) throws EscidocClientException,
        URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.DELETE_CONTAINER).forUser(currentUser.getUserId())
            .forResource(resourceModel.getId()).permitted();
    }

    private boolean canUserDeleteItem(final ResourceModel resourceModel) throws EscidocClientException,
        URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.DELETE_CONTAINER).forUser(currentUser.getUserId())
            .forResource(resourceModel.getId()).permitted();
    }

    private void showDelete(final ResourceModel resourceModel) {
        if (deleteMenuItem == null) {
            addDeleteMenu();
        }
        deleteMenuItem.setCommand(new DeleteContainerOrItemMenuCommand(mainWindow, repositories, resourceModel,
            treeDataSource));
        deleteMenuItem.setEnabled(true);
    }

    private void buildCommand(final ResourceModel resourceModel, final String contextId) {
        showAddViewCommand = new ShowAddViewCommand(repositories, mainWindow, contextId, treeDataSource);
        showAddViewCommand.withParent(resourceModel);
        containerMenuItem.setCommand(showAddViewCommand);
        itemMenuItem.setCommand(showAddViewCommand);
    }

    private String getContextIdForContainer(final ResourceModel resourceModel) {
        try {
            return retrieveContainer(resourceModel).getContext().getObjid();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return AppConstants.EMPTY_STRING;
    }

    private void showAddContext() throws EscidocClientException, URISyntaxException {
        final boolean isCreateContextAllowed = isCreateContextAllowed();
        menuBar.setEnabled(isCreateContextAllowed);
        contextMenuItem.setVisible(isCreateContextAllowed);
        containerMenuItem.setVisible(false);
        itemMenuItem.setVisible(false);
    }

    private void showAddContainerAndItem() throws EscidocClientException, URISyntaxException {
        final boolean createItemAllowed = isCreateItemAllowed();
        final boolean createContainerAllowed = isCreateContainerAllowed();
        menuBar.setEnabled(createItemAllowed || createContainerAllowed);
        add.setEnabled(createItemAllowed || createContainerAllowed);
        contextMenuItem.setVisible(false);
        containerMenuItem.setVisible(createContainerAllowed);
        itemMenuItem.setVisible(createItemAllowed);
    }

    private boolean isCreateContainerAllowed() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.CREATE_CONTAINER).forUser(currentUser.getUserId()).forResource("")
            .permitted();
    }

    private boolean isCreateItemAllowed() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.CREATE_ITEM).forUser(currentUser.getUserId()).forResource("").permitted();
    }
}