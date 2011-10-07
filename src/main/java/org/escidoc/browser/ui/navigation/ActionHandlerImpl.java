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
import org.escidoc.browser.elabsmodul.constants.ELabsConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
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
import de.escidoc.core.resources.Resource;

@SuppressWarnings("serial")
final class ActionHandlerImpl implements Action.Handler {

    private final static Logger LOG = LoggerFactory.getLogger(ActionHandlerImpl.class);

    private final Window mainWindow;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private final TreeDataSource treeDataSource;

    public ActionHandlerImpl(final Window mainWindow, final Repositories repositories, final CurrentUser currentUser, final TreeDataSource treeDataSource) {

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
        String contextID = findContextId(target);
        Preconditions.checkNotNull(contextID, "Context ID is null");

        if (contextID.equals(ELabsConstants.ELABS_DEFAULT_CONTEXT_ID)) {
            // Integrated eLabs Browser tree element
            String contentModelId = findContentModelId(target);
            Preconditions.checkNotNull(contentModelId, "Content Model ID is null");

            if (isContext(target) && contentModelId.equals(AppConstants.EMPTY_STRING)) {
                return new Action[] { ActionList.ACTION_ADD_STUDY, ActionList.ACTION_ADD_RIG, ActionList.ACTION_ADD_INSTRUMENT };
            }
            else if (isContainer(target)) {
                if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_STUDY_CMODEL_ID)) {
                    return new Action[] { ActionList.ACTION_ADD_INVESTIGATION, ActionList.ACTION_MODIFY_STUDY, ActionList.ACTION_DELETE_STUDY };
                }
                else if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_INVESTIGATION_CMODEL_ID)) {
                    return new Action[] { ActionList.ACTION_MODIFY_INVESTIGATION, ActionList.ACTION_DELETE_INVESTIGATION };
                }
                else {
                    LOG.error("Unsupported BW eLabs ContentModel in the context menu of the object tree!");
                    return new Action[] {};
                }
            }
            else if (isItem(target)) {
                if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_RIG_CMODEL_ID)) {
                    return new Action[] { ActionList.ACTION_MODIFY_RIG, ActionList.ACTION_DELETE_RIG };
                }
                else if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_INSTR_CMODEL_ID)) {
                    return new Action[] { ActionList.ACTION_MODIFY_INSTRUMENT, ActionList.ACTION_DELETE_INSTRUMENT };
                }
                else if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_GENERATED_ITEM_CMODEL_ID)) {
                    return new Action[] { ActionList.ACTION_DELETE_ITEM };
                }
                else {
                    LOG.error("Unsupported BW eLabs ContentModel in the context menu of the object tree!");
                    return new Action[] {};
                }
            }
            else {
                LOG.error("Unsupported BW eLabs ContentModel in the context menu of the object tree!");
                return new Action[] {};
            }
        }
        else {
            // Original Browser tree element
            if (isContext(target)) {
                return new Action[] { ActionList.ACTION_ADD_CONTAINER, ActionList.ACTION_ADD_ITEM };
            }

            if (isContainer(target)) {
                return new Action[] { ActionList.ACTION_ADD_CONTAINER, ActionList.ACTION_ADD_ITEM, ActionList.ACTION_DELETE_CONTAINER };
            }

            if (isItem(target)) {
                return new Action[] { ActionList.ACTION_DELETE_ITEM };
            }

            return new Action[] {};
        }

    }

    private boolean allowedToDeleteItem(final String resourceId) throws EscidocClientException, URISyntaxException {
        return repositories.pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.DELETE_ITEM).forResource(resourceId).permitted();
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
            mainWindow.showNotification(new Window.Notification("Application Error", e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            mainWindow.showNotification(new Window.Notification("Application Error", e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private void doActionIfAllowed(final Action action, final Object selectedResource, final String contextId) throws EscidocClientException,
        URISyntaxException {

        // original doActions
        if (action.equals(ActionList.ACTION_ADD_CONTAINER)) {
            tryShowCreateContainerView(selectedResource, contextId);
        }
        else if (action.equals(ActionList.ACTION_ADD_ITEM)) {
            tryShowCreateItemView(selectedResource, contextId);
        }
        else if (action.equals(ActionList.ACTION_DELETE_CONTAINER)) {
            tryDeleteContainer(selectedResource);
        }
        else if (action.equals(ActionList.ACTION_DELETE_ITEM)) {
            tryDeleteItem(selectedResource);
        }
        // new bw-elabs doActions
        else if (action.equals(ActionList.ACTION_ADD_STUDY)) {
        }
        else if (action.equals(ActionList.ACTION_MODIFY_STUDY)) {
        }
        else if (action.equals(ActionList.ACTION_DELETE_STUDY)) {
        }
        else if (action.equals(ActionList.ACTION_ADD_INVESTIGATION)) {
        }
        else if (action.equals(ActionList.ACTION_MODIFY_INVESTIGATION)) {
        }
        else if (action.equals(ActionList.ACTION_DELETE_INVESTIGATION)) {
        }
        else if (action.equals(ActionList.ACTION_ADD_RIG)) {
        }
        else if (action.equals(ActionList.ACTION_MODIFY_RIG)) {
        }
        else if (action.equals(ActionList.ACTION_DELETE_RIG)) {
        }
        else if (action.equals(ActionList.ACTION_ADD_INSTRUMENT)) {
        }
        else if (action.equals(ActionList.ACTION_MODIFY_INSTRUMENT)) {
        }
        else if (action.equals(ActionList.ACTION_DELETE_INSTRUMENT)) {
        }
        else {
            mainWindow.showNotification("Unknown Action: " + action.getCaption(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void tryDeleteItem(final Object target) throws EscidocClientException, URISyntaxException {
        final String itemId = ((ItemModel) target).getId();
        if (allowedToDeleteItem(itemId)) {
            deleteItem((ItemModel) target);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED, "You do not have the right to delete the item: " + itemId,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void tryDeleteContainer(final Object target) throws EscidocClientException, URISyntaxException {
        final String containerId = ((ContainerModel) target).getId();
        if (allowedToDeleteContainer(containerId)) {
            deleteContainer((ContainerModel) target);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete a container: " + containerId, Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void tryShowCreateItemView(final Object target, final String contextId) throws EscidocClientException, URISyntaxException {
        if (allowedToCreateItem(contextId)) {
            if (target instanceof ContextModel) {
                showCreateItemView(target, contextId);
            }
            else if (target instanceof ContainerModel && allowedToAddMember((ResourceModel) target)) {
                showCreateItemView(target, contextId);
            }
            else {
                mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED, "You do not have the right to add an item to "
                    + ((ResourceModel) target).getName(), Window.Notification.TYPE_WARNING_MESSAGE));
            }
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED, "You do not have the right to create an item in context: "
                + contextId, Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void tryShowCreateContainerView(final Object target, final String contextId) throws EscidocClientException, URISyntaxException {
        if (allowedToCreateContainer(contextId)) {
            if (target instanceof ContextModel) {
                showCreateContainerView(target, contextId);
            }
            else if ((target instanceof ContainerModel) && allowedToAddMember((ResourceModel) target)) {
                showCreateContainerView(target, contextId);
            }
            else {
                mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED, "You do not have the right to add a container to "
                    + ((ResourceModel) target).getName(), Window.Notification.TYPE_WARNING_MESSAGE));
            }
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED, "You do not have the right to create a container in context: "
                + contextId, Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private boolean allowedToAddMember(final ResourceModel target) throws EscidocClientException, URISyntaxException {
        return repositories.pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.ADD_MEMBERS_TO_CONTAINER).forResource(target.getId()).permitted();
    }

    private boolean allowedToDeleteContainer(final String containerId) throws EscidocClientException, URISyntaxException {
        return repositories.pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.DELETE_CONTAINER).forResource(containerId).permitted();
    }

    private boolean allowedToCreateItem(final String contextId) throws EscidocClientException, URISyntaxException {
        return repositories.pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.CREATE_ITEM).forResource("")
            .withTypeAndInContext(ResourceType.ITEM, contextId).permitted();
    }

    private boolean allowedToCreateContainer(final String contextId) throws EscidocClientException, URISyntaxException {
        return repositories.pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.CREATE_CONTAINER).forResource("")
            .withTypeAndInContext(ResourceType.CONTAINER, contextId).permitted();
    }

    private void deleteContainer(final ContainerModel selected) {
        try {
            repositories.container().delete(selected);
            treeDataSource.remove(selected);
        }
        catch (final EscidocClientException e) {
            getWindow().showNotification("Can not delete " + selected.getName(), e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
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
        return AppConstants.EMPTY_STRING;
    }

    private void showCreateContainerView(final Object target, final String contextId) {
        buildCommand(target, contextId).showContainerAddView();
    }

    private void showCreateItemView(final Object target, final String contextId) {
        buildCommand(target, contextId).showItemAddView();
    }

    private ShowAddViewCommand buildCommand(final Object target, final String contextId) {
        final ShowAddViewCommand showAddViewCommand = new ShowAddViewCommand(repositories, getWindow(), contextId, treeDataSource, currentUser);
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
        repositories.item().delete(selected);
    }

    private String findContentModelId(final Object target) {
        String contentModelId = "escidoc:";
        Resource eSciDocResource = null;
        try {
            if (isContext(target)) {
                contentModelId = AppConstants.EMPTY_STRING;
            }
            else if (isContainer(target)) {
                eSciDocResource = repositories.container().findById(((ResourceModel) target).getId()).getContentModel();
                contentModelId += (eSciDocResource.getXLinkHref().split(":"))[1];
            }
            else if (isItem(target)) {
                eSciDocResource = repositories.item().findById(((ResourceModel) target).getId()).getContentModel();
                contentModelId += (eSciDocResource.getXLinkHref().split(":"))[1];
            }
            else {
                contentModelId = null;
            }
        }
        catch (EscidocClientException e) {
            LOG.error("Unable to retreive ContentModel data from repository object", e);
            contentModelId = null;
        }
        return contentModelId;
    }
}