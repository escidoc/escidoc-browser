package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.Util;
import org.escidoc.browser.model.ContextProxyImpl;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;

public class ContextRepository implements Repository {

    private final ContextHandlerClientInterface client;

    public ContextRepository(final EscidocServiceLocation escidocServiceLocation) {
        client = new ContextHandlerClient(escidocServiceLocation.getUri());
        client.setTransport(TransportProtocol.REST);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.contextListToModel(client
            .retrieveContextsAsList(Util.createEmptyFilter()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id)
        throws EscidocClientException {
        return ModelConverter.genericResourcetoModel(client
            .retrieveMembersAsList(id, Util.createTopLevelQuery(id)));
    }

    @Override
    public ResourceProxy findById(final String id)
        throws EscidocClientException {
        return new ContextProxyImpl(client.retrieve(id));
    }
}