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
package org.escidoc.browser.repository.internal;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.HasNoNameResource;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.helper.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.client.ContainerHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContainerHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;

public class ContainerRepository implements Repository {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerRepository.class);

    private static final String DELETE_RESOURCE_WND_NAME = "Do you really want to delete this item!?";

    private static final String DELETE_RESOURCE = "Are you confident to delete this resource!?";

    private final ContainerHandlerClientInterface client;

    private final Window mainWindow;

    ContainerRepository(final EscidocServiceLocation escidocServiceLocation, final Window mainWindow) {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new ContainerHandlerClient(escidocServiceLocation.getEscidocUri());
        this.mainWindow = mainWindow;
    }

    @Override
    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.containerListToModel(client.retrieveContainersAsList(new SearchRetrieveRequestType()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkArgument(!id.isEmpty(), "id is empty: %s", id);
        return findDirectMembers(id);
    }

    private List<ResourceModel> findDirectMembers(final String id) throws EscidocException, InternalClientException,
        TransportException {

        final List<ResourceModel> results = new ArrayList<ResourceModel>();
        for (final SearchResultRecord record : findAllDirectMembers(id)) {
            Util.addToResults(results, record.getRecordData());
        }

        return results;
    }

    private List<SearchResultRecord> findAllDirectMembers(final String id) throws EscidocException,
        InternalClientException, TransportException {
        return client.retrieveMembers(id, new SearchRetrieveRequestType()).getRecords();
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        return new ContainerProxyImpl(client.retrieve(id));
    }

    @Override
    public VersionHistory getVersionHistory(final String id) throws EscidocClientException {
        return client.retrieveVersionHistory(id);
    }

    @Override
    public Relations getRelations(final String id) throws EscidocClientException {
        return client.retrieveRelations(id);

    }

    public List<Container> findParents(final HasNoNameResource resource) throws EscidocClientException {
        final SearchRetrieveRequestType requestType = new SearchRetrieveRequestType();

        if (resource.getType().equals(ResourceType.ITEM)) {
            final String query = "\"/struct-map/item/id\"=\"" + resource.getId() + "\"";

            requestType.setQuery(query);
        }
        else if (resource.getType().equals(ResourceType.CONTAINER)) {
            final String query = "\"/struct-map/container/id\"=\"" + resource.getId() + "\"";
            requestType.setQuery(query);
        }
        else {
            throw new UnsupportedOperationException("find Parents is not supported for type: " + resource.getType());
        }

        return new ArrayList<Container>(client.retrieveContainersAsList(requestType));

    }

    public ResourceModel findContext(final HasNoNameResource resource) throws EscidocClientException {
        final ResourceProxy container = findById(resource.getId());
        final Resource context = container.getContext();
        return new ContextModel(context);
    }

    public Container findContainerById(final String containerId) throws EscidocClientException {
        return client.retrieve(containerId);

    }

    public Container create(final Container newContainer) throws EscidocClientException {
        return client.create(newContainer);
    }

    public Container update(final Container resource) throws EscidocClientException {
        return client.update(resource);
    }

    public Container createWithParent(final Container newContainer, final ResourceModel parent)
        throws EscidocClientException {
        Preconditions.checkNotNull(newContainer, "newContainer is null: %s", newContainer);
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);

        final Container child = create(newContainer);
        if (parent.getType().equals(ResourceType.CONTAINER)) {
            addChild(client.retrieve(parent.getId()), child);
        }
        return child;
    }

    private void addChild(final Container parent, final Container child) throws EscidocException,
        InternalClientException, TransportException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(parent.getLastModificationDate());
        taskParam.addResourceRef(child.getObjid());
        client.addMembers(parent, taskParam);
    }

    public void changePublicStatus(final Container container, final String publicStatus, final String comment) {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(container.getLastModificationDate());
        taskParam.setComment(comment);
        if (publicStatus.equals("SUBMITTED")) {
            try {
                client.submit(container, taskParam);
                mainWindow.showNotification(new Window.Notification(ViewConstants.SUBMITTED,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
        else if (publicStatus.equals("IN_REVISION")) {
            try {
                client.revise(container, taskParam);
                mainWindow.showNotification(new Window.Notification(ViewConstants.IN_REVISION,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
        else if (publicStatus.equals("RELEASED")) {
            try {
                client.release(container, taskParam);
                mainWindow.showNotification(new Window.Notification(ViewConstants.RELEASED,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }

        }
        else if (publicStatus.equals("WITHDRAWN")) {
            try {
                client.withdraw(container, taskParam);
                mainWindow.showNotification(new Window.Notification(ViewConstants.WITHDRAWN,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
        else if (publicStatus.equals("DELETE")) {
            try {
                this.delete(container);
            }
            catch (final EscidocClientException e) {
                if (e.getMessage().toString().contains("An error occured removing member entries for container")) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR,
                        "Cannot remove the resource as it belongs to a resource which is not deletable",
                        Notification.TYPE_ERROR_MESSAGE));
                }
                else {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        }
    }

    public void changeLockStatus(final Container container, final String lockStatus, final String comment) {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(container.getLastModificationDate());
        taskParam.setComment(comment);
        if (lockStatus.contains("LOCKED")) {
            try {
                client.lock(container.getObjid(), taskParam);
                mainWindow.showNotification(new Window.Notification(ViewConstants.LOCKED,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
        else {
            try {
                client.unlock(container.getObjid(), taskParam);
                mainWindow.showNotification(new Window.Notification(ViewConstants.UNLOCKED,
                    Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
    }

    @Override
    public void delete(final ResourceModel model, final TreeDataSource treeDataSource, Router router)
        throws EscidocClientException {
        final Window subwindow = new Window(DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        final Label message = new Label(DELETE_RESOURCE);
        subwindow.addComponent(message);

        @SuppressWarnings("serial")
        final Button okConfirmed = new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    finalDelete(model);
                    treeDataSource.remove(model);
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        @SuppressWarnings("serial")
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

    public void finalDelete(final ResourceModel model) throws EscidocClientException {
        client.delete(model.getId());
        mainWindow
            .showNotification(new Window.Notification(ViewConstants.DELETED, Notification.TYPE_TRAY_NOTIFICATION));
    }

    private void delete(final Container container) throws EscidocClientException {
        final Window subwindow = new Window(DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        final Label message = new Label(DELETE_RESOURCE);
        subwindow.addComponent(message);

        @SuppressWarnings("serial")
        final Button okConfirmed = new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    finalDelete(container);
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        @SuppressWarnings("serial")
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

    private void finalDelete(final Container container) throws EscidocClientException {
        client.delete(container.getObjid());
        mainWindow
            .showNotification(new Window.Notification(ViewConstants.DELETED, Notification.TYPE_TRAY_NOTIFICATION));
    }

    public void updateMetaData(final MetadataRecord metadata, final Container container) throws EscidocClientException {
        final MetadataRecords containerMetadataList = container.getMetadataRecords();

        containerMetadataList.del(metadata.getName());
        containerMetadataList.add(metadata);
        container.setMetadataRecords(containerMetadataList);

        client.update(container);
    }

    public void addMetaData(final MetadataRecord metadata, final Container container) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(container.getLastModificationDate());
        taskParam.setComment("Adding a new MetaData");
        final MetadataRecords containerMetadataList = container.getMetadataRecords();
        containerMetadataList.add(metadata);

        client.update(container);
    }

    @Override
    public List<ResourceModel> filterUsingInput(final String query) throws EscidocClientException {
        final SearchRetrieveRequestType filter = new SearchRetrieveRequestType();
        filter.setQuery(query);
        final List<Container> list = client.retrieveContainersAsList(filter);
        final List<ResourceModel> ret = new ArrayList<ResourceModel>(list.size());
        for (final Container resource : list) {
            ret.add(new ContainerProxyImpl(resource));
        }
        return ret;
    }

}
