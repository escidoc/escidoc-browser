package org.escidoc.browser.repository;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;

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
        return ModelConverter.toModel(client
            .retrieveContextsAsList(new SearchRetrieveRequestType()));
    }

    @Override
    public List<ResourceModel> findMembersById(final String id)
        throws EscidocClientException {
        return ModelConverter.genericResourcetoModel(client
            .retrieveMembersAsList(id, new SearchRetrieveRequestType()));
    }

}