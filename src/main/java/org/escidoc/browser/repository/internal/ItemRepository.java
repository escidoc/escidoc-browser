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

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResource;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContainerHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContainerHandlerClientInterface;
import de.escidoc.core.client.interfaces.ItemHandlerClientInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;

public class ItemRepository implements Repository {
    private static final Logger LOG = LoggerFactory.getLogger(ItemRepository.class);

    private final ItemHandlerClientInterface client;

    private final ContainerHandlerClientInterface clientContainer;

    public ItemRepository(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "escidocServiceLocation is null: %s", serviceLocation);
        client = new ItemHandlerClient(serviceLocation.getEscidocUri());
        clientContainer = new ContainerHandlerClient(serviceLocation.getEscidocUri());
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        final SearchRetrieveRequestType request = new SearchRetrieveRequestType();
        request.setMaximumRecords(new NonNegativeInteger(AppConstants.MAX_RESULT_SIZE));
        return ModelConverter.itemListToModel(client.retrieveItemsAsList(request));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id) throws EscidocClientException {
        throw new UnsupportedOperationException("Not applicable for item.");
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        return new ItemProxyImpl(client.retrieve(id));
    }

    @Override
    public VersionHistory getVersionHistory(final String id) throws EscidocClientException {
        return client.retrieveVersionHistory(id);

    }

    @Override
    public Relations getRelations(final String id) throws EscidocClientException {
        return client.retrieveRelations(id);
    }

    @Override
    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
        clientContainer.setHandle(handle);
    }

    private Item create(final Item newItem) throws EscidocClientException {
        return client.create(newItem);
    }

    public Item update(final String itemId, final Item toBeUpdate) throws EscidocClientException {
        return client.update(itemId, toBeUpdate);
    }

    public Item findItemById(final String itemId) throws EscidocClientException {
        return client.retrieve(itemId);
    }

    public Item createWithParent(final Item newItem, final ResourceModel parent) throws EscidocClientException {
        Preconditions.checkNotNull(newItem, "newContainer is null: %s", newItem);
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);

        final Item createdItem = create(newItem);
        if (parent.getType().equals(ResourceType.CONTAINER)) {
            addChild(clientContainer.retrieve(parent.getId()), createdItem);
        }
        return createdItem;
    }

    private void addChild(final Container parent, final Item child) throws EscidocException, InternalClientException,
        TransportException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(parent.getLastModificationDate());
        taskParam.addResourceRef(child.getObjid());
        clientContainer.addMembers(parent, taskParam);
    }

    public void changePublicStatus(final Item item, final String publicStatus, final String comment)
        throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(item.getLastModificationDate());
        taskParam.setComment(comment);
        if (publicStatus.equals("SUBMITTED")) {
            client.submit(item, taskParam);
        }
        else if (publicStatus.equals("IN_REVISION")) {
            client.revise(item, taskParam);
        }
        else if (publicStatus.equals("RELEASED")) {
            client.release(item, taskParam);
        }
        else if (publicStatus.equals("WITHDRAWN")) {
            client.withdraw(item, taskParam);
        }

    }

    public void lockResource(final Item item, final String comment) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(item.getLastModificationDate());
        taskParam.setComment(comment);
        client.lock(item.getObjid(), taskParam);
    }

    public void unlockResource(final Item item, final String comment) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(item.getLastModificationDate());
        taskParam.setComment(comment);
        client.unlock(item.getObjid(), taskParam);
    }

    public ResourceModel findContext(final HasNoNameResource resource) throws EscidocClientException {
        final ResourceProxy resourceProxy = findById(resource.getId());
        return new ContextModel(resourceProxy.getContext());
    }

    public void finalDelete(final ResourceModel model) throws EscidocClientException {
        client.delete(model.getId());
    }

    public void finalDelete(final Item item) throws EscidocClientException {
        client.delete(item.getObjid());
    }

    public void addMetaData(final MetadataRecord metadataRecord, final Item item) throws EscidocClientException {
        final MetadataRecords itemMetadataList = item.getMetadataRecords();
        itemMetadataList.add(metadataRecord);

        client.update(item);
    }

    public void updateMetaData(final MetadataRecord metadataRecord, final Item item) throws EscidocClientException {
        final MetadataRecords itemMetadataList = item.getMetadataRecords();
        itemMetadataList.del(metadataRecord.getName());
        itemMetadataList.add(metadataRecord);
        item.setMetadataRecords(itemMetadataList);
        client.update(item);
    }

    @Override
    public List<ResourceModel> filterUsingInput(String query) throws EscidocClientException {
        final SearchRetrieveRequestType filter = new SearchRetrieveRequestType();
        filter.setQuery(query);
        List<Item> list = client.retrieveItemsAsList(filter);
        List<ResourceModel> ret = new ArrayList<ResourceModel>(list.size());
        for (Item item : list) {
            ret.add(new ItemProxyImpl(item));
        }
        return ret;
    }

    public List<ResourceModel> findItemsByContentModel(String cmId) throws EscidocClientException {
        Preconditions.checkNotNull(cmId, "cmId is null: %s", cmId);

        return filterUsingInput(findByContentModelQuery(cmId));
    }

    private String findByContentModelQuery(String cmId) {
        return "\"/properties/content-model/id\"=\"" + cmId + "\"";
    }
}