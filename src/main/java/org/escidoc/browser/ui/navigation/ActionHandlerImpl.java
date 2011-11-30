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
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.navigation.menubar.ShowAddViewCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
final class ActionHandlerImpl implements Action.Handler {

    private final static Logger LOG = LoggerFactory.getLogger(ActionHandlerImpl.class);

    private final Window mainWindow;

    private final Repositories repositories;

    private final TreeDataSource treeDataSource;

    private static final String DELETE_RESOURCE_WND_NAME = "Do you really want to delete this item!?";

    private static final String DELETE_RESOURCE = "Are you confident to delete this resource!?";

    private Router router;

    public ActionHandlerImpl(final Window mainWindow, final Repositories repositories,
        final TreeDataSource treeDataSource, Router router) {

        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.treeDataSource = treeDataSource;
        this.router = router;
    }

    @Override
    public Action[] getActions(final Object target, final Object sender) {
        String contextID = findContextId(target);
        Preconditions.checkNotNull(contextID, "Context ID is null");

        // Original Browser tree element
        if (isContext(target)) {
            return new Action[] { ActionList.ACTION_ADD_RESOURCE };
        }

        if (isContainer(target)) {
            return new Action[] { ActionList.ACTION_ADD_RESOURCE, ActionList.ACTION_DELETE_CONTAINER };
        }

        if (isItem(target)) {
            return new Action[] { ActionList.ACTION_DELETE_ITEM };
        }

        return new Action[] {};

    }

    private boolean allowedToDeleteItem(final String resourceId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_ITEM).forResource(resourceId).permitted();
    }

    private static boolean isContext(final Object target) {
        return target instanceof ContextModel;
    }

    private static boolean isContainer(final Object target) {
        return target instanceof ContainerModel;
    }

    private static boolean isItem(final Object target) {
        return target instanceof ItemModel;
    }

    private Window getWindow() {
        return mainWindow;
    }

    @Override
    public void handleAction(final Action action, final Object sender, final Object target) {
        final String contextId = findContextId(target);
        try {
            doActionIfAllowed(action, target, contextId);
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage(), e);
            mainWindow.showNotification(new Window.Notification("Application Error", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE));
        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            mainWindow.showNotification(new Window.Notification("Application Error", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private void doActionIfAllowed(final Action action, final Object selectedResource, final String contextId)
        throws EscidocClientException, URISyntaxException {

        // original doActions
        if (action.equals(ActionList.ACTION_ADD_RESOURCE)) {
            tryShowCreateResourceView(selectedResource, contextId);
        }
        else if (action.equals(ActionList.ACTION_DELETE_CONTAINER)) {
            tryDeleteContainer(selectedResource);
        }
        else if (action.equals(ActionList.ACTION_DELETE_ITEM)) {
            tryDeleteItem(selectedResource);
        }
        else {
            mainWindow.showNotification("Unknown Action: " + action.getCaption(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void tryDeleteItem(final Object target) throws EscidocClientException, URISyntaxException {
        final String itemId = ((ItemModel) target).getId();
        if (allowedToDeleteItem(itemId)) {
            deleteItem((ItemModel) target);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete the item: " + itemId, Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void tryDeleteContainer(final Object target) throws EscidocClientException, URISyntaxException {
        final String containerId = ((ContainerModel) target).getId();
        if (allowedToDeleteContainer(containerId)) {
            deleteContainer((ContainerModel) target);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete a container: " + containerId,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void tryShowCreateResourceView(final Object target, final String contextId) throws EscidocClientException,
        URISyntaxException {
        if ((allowedToCreateContainer(contextId)) && (allowedToCreateItem(contextId))) {
            if (target instanceof ContextModel) {
                showCreateResourceView(target, contextId);
            }
            else if ((target instanceof ContainerModel) && allowedToAddMember((ResourceModel) target)) {
                showCreateResourceView(target, contextId);
            }
            else {
                mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                    "You do not have the right to add a container to " + ((ResourceModel) target).getName(),
                    Window.Notification.TYPE_WARNING_MESSAGE));
            }
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to create a container in context: " + contextId,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private boolean allowedToAddMember(final ResourceModel target) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.ADD_MEMBERS_TO_CONTAINER).forResource(target.getId())
            .permitted();
    }

    private boolean allowedToDeleteContainer(final String containerId) throws EscidocClientException,
        URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_CONTAINER).forResource(containerId).permitted();
    }

    private boolean allowedToCreateItem(final String contextId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_ITEM).forResource("")
            .withTypeAndInContext(ResourceType.ITEM, contextId).permitted();
    }

    private boolean allowedToCreateContainer(final String contextId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_CONTAINER).forResource("")
            .withTypeAndInContext(ResourceType.CONTAINER, contextId).permitted();
    }

    private String findContextId(final Object target) {
        if (isContext(target)) {
            return ((ContextModel) target).getId();
        }
        else if (isContainer(target)) {
            final ContainerModel containerModel = (ContainerModel) target;
            try {
                return repositories.container().findById(containerModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                getWindow().showNotification(
                    "Can not retrieve container " + containerModel.getId() + ". Reason: " + e.getMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        else if (isItem(target)) {
            final ItemModel itemModel = (ItemModel) target;
            try {
                return repositories.item().findById(itemModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                getWindow().showNotification(
                    "Unable to retrieve Item " + itemModel.getId() + ". Reason: " + e.getMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        return AppConstants.EMPTY_STRING;
    }

    private void showCreateResourceView(final Object target, final String contextId) {
        buildCommand(target, contextId).showResourceAddView();
    }

    private ShowAddViewCommand buildCommand(final Object target, final String contextId) {
        final ShowAddViewCommand showAddViewCommand =
            new ShowAddViewCommand(repositories, getWindow(), contextId, treeDataSource, router);
        showAddViewCommand.withParent((ResourceModel) target);
        return showAddViewCommand;
    }

    private void deleteItem(final ItemModel selectedItem) {
        try {
            deleteSelected(selectedItem);
        }
        catch (final EscidocClientException e) {
            getWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void deleteSelected(final ItemModel model) throws EscidocClientException {
        final Window subwindow = new Window(DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        Label message = new Label(DELETE_RESOURCE);
        subwindow.addComponent(message);

        @SuppressWarnings("serial")
        Button okConfirmed = new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    repositories.item().finalDelete(model);
                    router.getLayout().closeView(model, treeDataSource.getParent(model));
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        @SuppressWarnings("serial")
        Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    public void deleteContainer(final ContainerModel model) throws EscidocClientException {
        final Window subwindow = new Window(DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        final Label message = new Label(DELETE_RESOURCE);
        subwindow.addComponent(message);

        final Button okConfirmed = new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    repositories.container().finalDelete(model);
                    router.getLayout().closeView(model, treeDataSource.getParent(model));
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        final Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }
}
