package org.escidoc.browser.repository;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.client.ContainerHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContainerHandlerClientInterface;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.Record;
import de.escidoc.core.resources.sb.search.records.ResourceRecord;

public class ContainerRepository implements Repository {

    private final ContainerHandlerClientInterface client;

    public ContainerRepository(
        final EscidocServiceLocation escidocServiceLocation) {
        client = new ContainerHandlerClient(escidocServiceLocation.getUri());
        client.setTransport(TransportProtocol.REST);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.containerListToModel(client
            .retrieveContainersAsList(new SearchRetrieveRequestType()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id)
        throws EscidocClientException {
        usingSearch(id);
        return Collections.EMPTY_LIST;
    }

    private void usingSearch(final String id) throws EscidocException,
        InternalClientException, TransportException {

        for (final Record<?> record : client.retrieveMembers(
            client.retrieve(id), new SearchRetrieveRequestType()).getRecords()) {
            // TODO: get resource type

            // if container, do foo
            final List<ResourceModel> containers = getContainers(record);
            // if item, do bar
            final List<ResourceModel> items = getItems(record);
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private List<ResourceModel> getItems(final Record<?> record) {
        final List<ResourceModel> containers = new ArrayList<ResourceModel>();
        containers.add(new ItemModel(getSRWResourceRecordData(record,
            Item.class)));
        return containers;
    }

    private List<ResourceModel> getContainers(final Record<?> record) {
        final List<ResourceModel> containers = new ArrayList<ResourceModel>();
        containers.add(new ContainerModel(getSRWResourceRecordData(record,
            Container.class)));
        return containers;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSRWResourceRecordData(
        final Record<?> record, final Class<T> resource) {

        if (record instanceof ResourceRecord<?>) {
            final ResourceRecord<?> rRecord = (ResourceRecord<?>) record;

            if (rRecord.getRecordDataType() == resource) {
                return (T) record.getRecordData();
            }
        }
        return null;
    }

    @Override
    public ResourceProxy findById(final String id)
        throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}