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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.navigation;

import com.google.common.base.Preconditions;

import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerModel;
import org.escidoc.browser.model.internal.ContextModel;
import org.escidoc.browser.model.internal.ItemModel;
import org.escidoc.browser.model.internal.OrgUnitModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.CreateOrgUnitView;
import org.escidoc.browser.ui.navigation.menubar.ShowAddViewCommand;
import org.escidoc.browser.ui.view.helpers.DeleteContainerShowLogsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class ActionHandlerImpl implements Action.Handler {

    private final class OnConfirmDelete implements Button.ClickListener {
        private final Window subwindow;

        private final Object sender;

        private final ResourceModel target;

        private final String id;

        private OnConfirmDelete(Window subwindow, Object sender, ResourceModel target, String id) {
            this.subwindow = subwindow;
            this.sender = sender;
            this.target = target;
            this.id = id;
        }

        @Override
        public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
            try {
                deleteUserOrGroup();
                subwindow.getParent().removeWindow(subwindow);
                router.getLayout().closeView(target, null, sender);
                mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }

        private void deleteUserOrGroup() throws EscidocClientException {
            if (target.getType() == ResourceType.USER_ACCOUNT) {
                repositories.user().delete(id);
            }
            else if (target.getType() == ResourceType.USER_GROUP) {
                repositories.group().delete(id);
            }
        }
    }

    private final static Logger LOG = LoggerFactory.getLogger(ActionHandlerImpl.class);

    private final Window mainWindow;

    private final Repositories repositories;

    private final TreeDataSource treeDataSource;

    private static final String DELETE_RESOURCE_WND_NAME = "Do you really want to delete this item!?";

    private static final String DELETE_RESOURCE = "Are you confident to delete this resource!?";

    private Router router;

    private List<ResourceModel> results;

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
    public Action[] getActions(final Object target, @SuppressWarnings("unused") final Object sender) {
        if (target instanceof ResourceModel) {
            ResourceModel rm = (ResourceModel) target;
            ResourceType type = rm.getType();
            switch (type) {
                case CONTEXT:
                    return new Action[] { ActionList.ACTION_ADD_RESOURCE, ActionList.ACTION_DELETE_CONTEXT };
                case CONTAINER:
                    return new Action[] { ActionList.ACTION_ADD_RESOURCE, ActionList.ACTION_DELETE_CONTAINER };
                case ITEM:
                    return new Action[] { ActionList.ACTION_DELETE_RESOURCE };
                case ORG_UNIT:
                    return buildActionsForOrgUnit(rm);
                case CONTENT_MODEL:
                    return new Action[] { ActionList.ACTION_DELETE_CONTENT_MODEL };
                case USER_ACCOUNT:
                    return new Action[] { ActionList.ACTION_DELETE_USER_ACCOUNT };
                case USER_GROUP:
                    return new Action[] { ActionList.ACTION_DELETE_USER_GROUP };
                default:
                    return new Action[] {};
            }
        }
        return new Action[] {};

    }

    private Action[] buildActionsForOrgUnit(ResourceModel rm) {
        try {
            List<Action> orgUnitActionList = new ArrayList<Action>();

            if (allowedToDeleteOrgUnit(rm.getId())) {
                orgUnitActionList.add(ActionList.ACTION_DELETE_ORG);
            }

            // TODO check if the user can add an org unit. Currently there is not possible for asking PDP, if the
            // loggein use can add a org unit child.
            orgUnitActionList.add(ActionList.ACTION_ADD_CHILD);

            return orgUnitActionList.toArray(new Action[orgUnitActionList.size()]);
        }
        catch (EscidocClientException e) {
            LOG.error(e.getMessage(), e);
            mainWindow.showNotification(new Window.Notification("Application Error", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE));
        }
        catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            mainWindow.showNotification(new Window.Notification("Application Error", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE));
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
        try {
            doActionIfAllowed(action, target, findContextId(target), sender);
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

    private void doActionIfAllowed(
        final Action action, final Object selectedResource, final String contextId, Object sender)
        throws EscidocClientException, URISyntaxException {
        if (action.equals(ActionList.ACTION_ADD_RESOURCE)) {
            tryShowCreateResourceView(selectedResource, contextId);
        }
        else if (action.equals(ActionList.ACTION_DELETE_CONTAINER)) {
            tryDeleteContainer(selectedResource, sender);
        }
        else if (action.equals(ActionList.ACTION_DELETE_RESOURCE)) {
            tryDeleteItem(selectedResource, sender);
        }
        else if (action.equals(ActionList.ACTION_ADD_CHILD)) {
            tryShowAddChildOrgUnit((ResourceModel) selectedResource);
        }
        else if (action.equals(ActionList.ACTION_DELETE_CONTEXT)) {
            if (canContextbeRemoved(((ContextModel) selectedResource).getId())) {
                tryDeleteContext(selectedResource, sender);
            }
            else {
                mainWindow
                    .showNotification(new Window.Notification(
                        ViewConstants.CANNOT_REMOVE_CONTEXT_NOT_IN_STATUS_CREATED,
                        Window.Notification.TYPE_WARNING_MESSAGE));
            }
        }
        else if (action.equals(ActionList.ACTION_DELETE_CONTENT_MODEL)) {
            tryDeleteContentModel(selectedResource, sender);
        }
        else if (action.equals(ActionList.ACTION_DELETE_USER_ACCOUNT)) {
            tryDeleteUserAccount(selectedResource, sender);
        }
        else if (action.equals(ActionList.ACTION_DELETE_USER_GROUP)) {
            tryDeleteUserGroup(selectedResource, sender);
        }
        else if (action.equals(ActionList.ACTION_DELETE_ORG)) {
            tryDeleteOrg(selectedResource, sender);
        }
        else {
            mainWindow.showNotification("Unknown Action: " + action.getCaption(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void tryDeleteUserGroup(Object target, Object sender) throws EscidocClientException, URISyntaxException {
        final String id = ((ResourceModel) target).getId();
        if (isAllowedToDeleteGroup(id)) {
            deleteResource((ResourceModel) target, id, sender);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete the Group: " + id, Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private boolean isAllowedToDeleteGroup(String id) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_USER_GROUP).forResource(id).permitted();
    }

    private void tryDeleteUserAccount(Object target, Object sender) throws EscidocClientException, URISyntaxException {
        final String id = ((ResourceModel) target).getId();
        if (allowedToDeleteUserAccount(id)) {
            deleteResource((ResourceModel) target, id, sender);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete the User Account : " + id,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private boolean allowedToDeleteUserAccount(String id) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_USER_ACCOUNT).forResource(id).permitted();
    }

    private void deleteResource(final ResourceModel target, final String id, final Object sender) {
        final Window subwindow = buildSubWindow();

        final Button okConfirmed = new Button(ViewConstants.YES, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    repositories.user().delete(id);
                    router.getLayout().closeView(target, null, sender);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR + " trying to delete user",
                        e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE));
                }
            }
        });

        final Button cancel = new Button(ViewConstants.CANCEL, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private void tryDeleteContentModel(Object target, Object sender) throws EscidocClientException, URISyntaxException {
        final String cmId = ((ResourceModel) target).getId();
        if (allowedToDeleteContentModel(cmId)) {
            deleteContentModel((ResourceModel) target, cmId, sender);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete the Content Model with an ID: " + cmId,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void deleteContentModel(final ResourceModel model, final String cmId, final Object sender) {
        final Window subwindow = buildSubWindow();

        final Button okConfirmed = new Button(ViewConstants.YES, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    repositories.contentModel().delete(cmId);
                    router.getLayout().closeView(model, null, sender);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }
        });

        final Button cancel = new Button(ViewConstants.CANCEL, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private boolean allowedToDeleteContentModel(String cmId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_CONTENT_MODEL).forResource(cmId).permitted();
    }

    private void tryDeleteContext(final Object target, Object sender) throws EscidocClientException, URISyntaxException {
        final String contextId = ((ContextModel) target).getId();
        if (allowedToDeleteContext(contextId)) {
            deleteContext((ContextModel) target, sender);
        }
        else {
            mainWindow
                .showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                    "You do not have the right to delete a context: " + contextId,
                    Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    public void tryDeleteOrg(Object selectedResource, Object sender) throws EscidocClientException, URISyntaxException {
        final String orgId = ((OrgUnitModel) selectedResource).getId();
        if (allowedToDeleteOrgUnit(orgId)) {
            deleteOrgUnit((OrgUnitModel) selectedResource, sender);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete a Organization: " + orgId,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void deleteOrgUnit(final OrgUnitModel model, final Object sender) {
        final Window subwindow = buildSubWindow();

        final Button okConfirmed = new Button(ViewConstants.YES, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    deleteFromRepository(model);
                    closeView(model, sender);
                    removeFromTree(model);
                    showSuccesfulMessage();
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification("Error deleting Organization. "
                        + ViewConstants.ERROR, e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
                }
            }

            private void showSuccesfulMessage() {
                mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }

            private void removeFromTree(final OrgUnitModel model) {
                treeDataSource.remove(model);
            }

            private void deleteFromRepository(final OrgUnitModel model) throws EscidocClientException {
                repositories.organization().delete(model.getId());
            }

            private void closeView(final OrgUnitModel model, final Object sender) {
                router.getLayout().closeView(model, treeDataSource.getParent(model), sender);
            }

        });
        final Button cancel = new Button("Cancel", new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private boolean allowedToDeleteOrgUnit(String orgId) throws EscidocClientException, URISyntaxException {
        return (repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_ORGANIZATIONAL_UNIT_ACTION).forResource(orgId)
            .permitted());
    }

    private void deleteContext(final ContextModel model, final Object sender) {
        final Window subwindow = buildSubWindow();

        final Button okConfirmed = new Button(ViewConstants.YES, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    repositories.context().delete(model.getId());
                    router.getLayout().closeView(model, treeDataSource.getParent(model), sender);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification("Error deleting context" + ViewConstants.ERROR,
                        e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        final Button cancel = new Button("Cancel", new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private static Window buildSubWindow() {
        final Window subwindow = new Window(ViewConstants.DELETE_RESOURCE_WINDOW_NAME);
        subwindow.setModal(true);
        final Label message = new Label(ViewConstants.QUESTION_DELETE_RESOURCE);
        subwindow.addComponent(message);
        return subwindow;
    }

    private boolean allowedToDeleteContext(final String contextId) throws EscidocClientException, URISyntaxException {
        return (repositories.pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_CONTEXT).forResource(contextId)
            .permitted());
    }

    private void tryShowAddChildOrgUnit(final ResourceModel selectedOrgUnit) throws EscidocClientException,
        URISyntaxException {
        if (isAllowedToCreateOrgUnit() && isAllowedToAddChild()) {
            showAddChildOrgUnitView(selectedOrgUnit);
        }
    }

    private boolean isAllowedToCreateOrgUnit() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_ORG_UNIT).forResource("").permitted();
    }

    private void showAddChildOrgUnitView(final ResourceModel selectedOrgUnit) {
        new CreateOrgUnitView(mainWindow, repositories.organization(), selectedOrgUnit, treeDataSource).show();
    }

    // FIXME ask pdp if add child allowed
    private static boolean isAllowedToAddChild() {
        return true;
    }

    private boolean canContextbeRemoved(final String contextId) throws EscidocClientException {
        return repositories.context().findById(contextId).getStatus().equals("created");
    }

    private void tryDeleteItem(final Object target, Object sender) throws EscidocClientException, URISyntaxException {
        final String itemId = ((ItemModel) target).getId();
        if (allowedToDeleteItem(itemId)) {
            deleteItem((ItemModel) target, sender);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete the item: " + itemId, Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    private void tryDeleteContainer(final Object target, Object sender) throws EscidocClientException,
        URISyntaxException {
        final String containerId = ((ContainerModel) target).getId();
        if ((allowedToDeleteContainer(containerId))) {
            deleteContainer((ContainerModel) target, sender);
        }
        else {
            mainWindow.showNotification(new Window.Notification(ViewConstants.NOT_AUTHORIZED,
                "You do not have the right to delete a container: " + containerId,
                Window.Notification.TYPE_WARNING_MESSAGE));
        }
    }

    /**
     * Attempt to put the leafs or empty containers at the beginning of the list
     * 
     * @param resource
     * @return
     * @throws EscidocClientException
     */
    private void findAllChildren(final ResourceModel resource) throws EscidocClientException {
        results.add(resource);
        if (resource.getType().equals(ResourceType.CONTAINER)) {
            final List<ResourceModel> children = repositories.container().findDirectMembers(resource.getId());
            if (!children.isEmpty()) {
                for (ResourceModel child : children) {
                    findAllChildren(child);
                }
            }
        }

    }

    private void deleteAllChildrenOfContainer(ResourceModel resource, Object sender) {
        HashMap<String, String> listDeleted = new HashMap<String, String>();
        HashMap<String, String> listNotDeleted = new HashMap<String, String>();
        results = new ArrayList<ResourceModel>();
        try {
            findAllChildren(resource);
            Collections.reverse(results);

            if (!results.isEmpty()) {
                for (ResourceModel resourceModel : results) {
                    if (resourceModel.getType().equals(ResourceType.CONTAINER)) {
                        try {
                            repositories.container().finalDelete(resourceModel);
                            treeDataSource.remove(resourceModel);
                            listDeleted.put(resourceModel.getId(), resourceModel.getName().toString() + " "
                                + resourceModel.getType().toString().toLowerCase());
                        }
                        catch (EscidocClientException e) {
                            listNotDeleted.put(resourceModel.getId(), resourceModel.getName().toString() + " "
                                + resourceModel.getType() + " " + e.getLocalizedMessage());
                        }
                    }
                    else {
                        try {
                            repositories.item().finalDelete(resourceModel);
                            treeDataSource.remove(resourceModel);
                            listDeleted.put(resourceModel.getId(), resourceModel.getName().toString() + " "
                                + resourceModel.getType().toString());
                        }
                        catch (EscidocClientException e) {
                            listNotDeleted.put(resourceModel.getId(),
                                resourceModel.getName().toString() + " " + e.getLocalizedMessage());
                        }
                    }
                }
                new DeleteContainerShowLogsHelper(listDeleted, listNotDeleted, router).showWindow();
            }
            else {
                try {
                    repositories.container().finalDelete(resource);
                    router.getLayout().closeView(resource, treeDataSource.getParent(resource), sender);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR
                        + " Could not delete resource " + resource.getName(), e.getLocalizedMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }
        }
        catch (EscidocClientException e1) {
            mainWindow.showNotification(new Window.Notification("Could not retrieve members",
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
            new ShowAddViewCommand(repositories, contextId, treeDataSource, router);
        showAddViewCommand.withParent((ResourceModel) target);
        return showAddViewCommand;
    }

    private void deleteItem(final ItemModel selectedItem, final Object sender) {
        final Window subwindow = new Window(DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        Label message = new Label(DELETE_RESOURCE);
        subwindow.addComponent(message);

        Button okConfirmed = new Button(ViewConstants.YES, new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    repositories.item().finalDelete(selectedItem);
                    router.getLayout().closeView(selectedItem, treeDataSource.getParent(selectedItem), sender);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });

        Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    public void deleteContainer(final ContainerModel model, final Object sender) {
        final Window subwindow = new Window(DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        final Label message = new Label(DELETE_RESOURCE);
        subwindow.addComponent(message);

        final Button okConfirmed = new Button(ViewConstants.YES, new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                deleteAllChildrenOfContainer(model, sender);
            }

        });
        final Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
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
