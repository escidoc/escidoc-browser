package org.escidoc.browser.repository;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.List;

import org.escidoc.browser.Util;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContainerHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;
import de.escidoc.core.client.interfaces.ItemHandlerClientInterface;

public class ItemRepository implements Repository {

    private final ItemHandlerClientInterface client;

    public ItemRepository(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation,
            "escidocServiceLocation is null: %s", serviceLocation);
        client = new ItemHandlerClient(serviceLocation.getUri());
        client.setTransport(TransportProtocol.REST);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.itemListToModel(client
            .retrieveItemsAsList(new SearchRetrieveRequestType()));

    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id)
        throws EscidocClientException {
        throw new UnsupportedOperationException("Not applicable for item.");
    }

    @Override
    public ResourceProxy findById(final String id)
        throws EscidocClientException {
        return new ItemProxyImpl(client.retrieve(id));
    }

}
