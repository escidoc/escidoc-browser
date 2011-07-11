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
package org.escidoc.browser.ui.navigation;

import java.net.URISyntaxException;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.navigation.menubar.ShowAddViewCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.Action;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
final class ActionHandlerImpl implements Action.Handler {

    private static final Logger LOG = LoggerFactory.getLogger(ActionHandlerImpl.class);

    private final Window mainWindow;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private final TreeDataSource treeDataSource;

    public ActionHandlerImpl(final Window mainWindow, final Repositories repositories, final CurrentUser currentUser,
        final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.currentUser = currentUser;
        this.treeDataSource = treeDataSource;
    }

    @Override
    public Action[] getActions(final Object target, final Object sender) {
        LOG.debug("target: " + target + "\nsender: " + sender);
        try {
            if (isItem(target) && allowedToDeleteItem((ItemModel) target) && isInStatusPending((ItemModel) target)) {
                return new Action[] { ActionList.ACTION_DELETE_ITEM };
            }
            if (isContainer(target) && allowedToCreateContainer()) {
                return new Action[] { ActionList.ACTION_ADD_CONTAINER, ActionList.ACTION_ADD_ITEM,
                    ActionList.ACTION_DELETE_CONTAINER };
            }
            if (isContext(target) && allowedToCreateContainer()) {
                return new Action[] { ActionList.ACTION_ADD_CONTAINER, ActionList.ACTION_ADD_ITEM };
            }
            return new Action[] {};
        }
        catch (final EscidocClientException e) {
            getWindow().showNotification(e.getMessage());
        }
        catch (final URISyntaxException e) {
            getWindow().showNotification(e.getMessage());
        }
        return new Action[] {};
    }

    private boolean isContext(final Object target) {
        return target instanceof ContextModel;
    }

    private boolean isContainer(final Object target) {
        return target instanceof ContainerModel;
    }

    private boolean isItem(final Object target) {
        return target instanceof ItemModel;
    }

    private boolean isInStatusPending(final ItemModel target) throws EscidocClientException {
        return repositories.item().findById(target.getId()).getStatus().equalsIgnoreCase(AppConstants.PENDING);
    }

    private boolean allowedToCreateContainer() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.CREATE_CONTAINER).forResource("")
            .permitted();
    }

    private Window getWindow() {
        return mainWindow;
    }

    private boolean allowedToDeleteItem(final ItemModel selected) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.DELETE_ITEM)
            .forResource(selected.getId()).permitted();
    }

    @Override
    public void handleAction(final Action action, final Object sender, final Object target) {
        LOG.debug(action + "/" + sender + "/" + target);

        final String contextId = findContextId(target);

        if (action.equals(ActionList.ACTION_ADD_CONTAINER)) {
            showCreateContainerView(target, contextId);
        }
        else if (action.equals(ActionList.ACTION_ADD_ITEM)) {
            showCreateItemView(target, contextId);
        }
        else if (action.equals(ActionList.ACTION_DELETE_ITEM)) {
            deleteItem((ItemModel) target);
        }
        else if (action.equals(ActionList.ACTION_DELETE_CONTAINER)) {
            deleteContainer((ContainerModel) target);
        }
        else {
            mainWindow.showNotification("Unknown Action: " + action.getCaption(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void deleteContainer(final ContainerModel selected) {
        try {
            repositories.container().delete(selected);
            treeDataSource.remove(selected);
        }
        catch (final EscidocClientException e) {
            getWindow().showNotification("Can not delete " + selected.getName(), e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private String findContextId(final Object target) {
        if (isContext(target)) {
            return (((ContextModel) target).getId());
        }
        else if (isContainer(target)) {
            final ContainerModel containerModel = (ContainerModel) target;
            try {
                return repositories.container().findById(containerModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                getWindow().showNotification(ViewConstants.NOT_ABLE_TO_RETRIEVE_A_CONTEXT);
            }
        }
        else if (isItem(target)) {
            final ItemModel itemModel = (ItemModel) target;
            try {
                return repositories.item().findById(itemModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                getWindow().showNotification(ViewConstants.NOT_ABLE_TO_RETRIEVE_A_CONTEXT);
            }
        }
        throw new RuntimeException("Can not find context id for: " + target);
    }

    private void showCreateContainerView(final Object target, final String contextId) {
        final ShowAddViewCommand showAddViewCommand = buildCommand(target, contextId);
        showAddViewCommand.showContainerAddView();
    }

    private void showCreateItemView(final Object target, final String contextId) {
        final ShowAddViewCommand showAddViewCommand = buildCommand(target, contextId);
        showAddViewCommand.showItemAddView();
    }

    private ShowAddViewCommand buildCommand(final Object target, final String contextId) {
        final ShowAddViewCommand showAddViewCommand =
            new ShowAddViewCommand(repositories, getWindow(), contextId, treeDataSource);
        showAddViewCommand.withParent((ResourceModel) target);
        return showAddViewCommand;
    }

    private void deleteItem(final ItemModel selectedItem) {
        try {
            deleteSelected(selectedItem);
            treeDataSource.remove(selectedItem);
        }
        catch (final EscidocClientException e) {
            getWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void deleteSelected(final ItemModel selected) throws EscidocClientException {
        repositories.item().delete(selected.getId());
    }
}