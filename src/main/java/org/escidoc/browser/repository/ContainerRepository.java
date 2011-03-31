package org.escidoc.browser.repository;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.escidoc.browser.Util;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContainerHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContainerHandlerClientInterface;
import de.escidoc.core.resources.sb.Record;
import de.escidoc.core.resources.sb.search.records.ResourceRecord;

public class ContainerRepository implements Repository {

    private static final Logger LOG = LoggerFactory
        .getLogger(ContainerRepository.class);

    private final ContainerHandlerClientInterface client;

    public ContainerRepository(
        final EscidocServiceLocation escidocServiceLocation) {
        Preconditions.checkNotNull(escidocServiceLocation,
            "escidocServiceLocation is null: %s", escidocServiceLocation);
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
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkArgument(!id.isEmpty(), "id is empty: %s", id);
        return findDirectMembers(id);
    }

    private List<ResourceModel> findDirectMembers(final String id)
        throws EscidocException, InternalClientException, TransportException {

        final List<ResourceModel> results = new ArrayList<ResourceModel>();
        for (final Record<?> record : findAllDirectMembers(id)) {
            if (record instanceof ResourceRecord) {
                Util.addToResults(results, record);
            }
            else {
                LOG.warn("Unrecognized type: " + record.getClass());
            }
        }

        return results;
    }

    private Collection<Record<?>> findAllDirectMembers(final String id)
        throws EscidocException, InternalClientException, TransportException {
        return client.retrieveMembers(client.retrieve(id),
            new SearchRetrieveRequestType()).getRecords();
    }

    @Override
    public ResourceProxy findById(final String id)
        throws EscidocClientException {
        return new ContainerProxyImpl(client.retrieve(id));
    }
}