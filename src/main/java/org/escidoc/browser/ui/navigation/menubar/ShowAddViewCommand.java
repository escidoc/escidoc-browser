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
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContainerAddView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class ShowAddViewCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(ShowAddViewCommand.class);

    private final Repositories repositories;

    private final Window mainWindow;

    private final String contextId;

    private final TreeDataSource treeDataSource;

    private final CurrentUser currentUser;

    private ResourceModel parent;

    public ShowAddViewCommand(final Repositories repositories, final Window mainWindow, final String contextId,
        final TreeDataSource treeDataSource, final CurrentUser currentUser) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.contextId = contextId;
        this.treeDataSource = treeDataSource;
        this.currentUser = currentUser;
    }

    public void withParent(final ResourceModel parent) {
        this.parent = parent;
    }

    @Override
    public void menuSelected(final MenuItem selectedItem) {
        try {
            if (isContainerSelected(selectedItem)) {
                if (allowedToCreateContainer(contextId)) {
                    if (parent.getType().equals(ResourceType.CONTEXT)) {
                        showContainerAddView();
                    }
                    else if (parent.getType().equals(ResourceType.CONTAINER) && isUserAllowedToAddMembers(parent)) {
                        showContainerAddView();
                    }
                    else {
                        mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                            "You do not have the right to add a container to " + parent.getName(),
                            Window.Notification.TYPE_WARNING_MESSAGE));
                    }
                }
                else {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                        "You do not have the right to create a container in context: " + contextId,
                        Window.Notification.TYPE_WARNING_MESSAGE));
                }
            }
            else if (isItemSelected(selectedItem)) {
                if (allowedToCreateITem(contextId)) {
                    if (parent.getType().equals(ResourceType.CONTEXT)) {
                        showItemAddView();
                    }
                    else if (parent.getType().equals(ResourceType.CONTAINER) && isUserAllowedToAddMembers(parent)) {
                        showItemAddView();
                    }
                    else {
                        mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                            "You do not have the right to add an item to " + parent.getName(),
                            Window.Notification.TYPE_WARNING_MESSAGE));
                    }
                }
                else {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                        "You do not have the right to create an item in context: " + contextId,
                        Window.Notification.TYPE_WARNING_MESSAGE));
                }
            }
            else {
                mainWindow.showNotification("Action " + selectedItem.getText());
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

    private boolean allowedToCreateContainer(final String contextId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.CREATE_CONTAINER).forResource("")
            .withTypeAndInContext(ResourceType.CONTAINER, contextId).permitted();
    }

    private boolean allowedToCreateITem(final String contextId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.CREATE_ITEM).forResource("")
            .withTypeAndInContext(ResourceType.ITEM, contextId).permitted();
    }

    private boolean isUserAllowedToAddMembers(final ResourceModel selectedItem) throws EscidocClientException,
        URISyntaxException {
        return repositories
            .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.ADD_MEMBERS_TO_CONTAINER)
            .forResource(selectedItem.getId()).permitted();
    }

    private void showWarning(final String message) {
        mainWindow.showNotification(ViewConstants.NOT_AUTHORIZED, message, Window.Notification.TYPE_WARNING_MESSAGE);
    }

    public void showItemAddView() {
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        try {
            new ItemAddView(repositories, mainWindow, parent, treeDataSource, contextId).openSubWindow();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void showContainerAddView() {
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        try {
            new ContainerAddView(repositories, mainWindow, parent, treeDataSource, contextId).openSubWindow();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private boolean isContainerSelected(final MenuItem selectedItem) {
        return selectedItem.getText().equals("Container");
    }

    private boolean isItemSelected(final MenuItem selectedItem) {
        return selectedItem.getText().equals("Item");
    }
}