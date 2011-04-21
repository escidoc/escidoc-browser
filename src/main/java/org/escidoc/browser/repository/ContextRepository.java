package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.helper.Util;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class ContextRepository implements Repository {

    private final ContextHandlerClientInterface client;

    public ContextRepository(final EscidocServiceLocation escidocServiceLocation) {
        client = new ContextHandlerClient(escidocServiceLocation.getEscidocUri());
        client.setTransport(TransportProtocol.REST);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.contextListToModel(client.retrieveContextsAsList(Util.createEmptyFilter()));
    }

    // FIXME: this is a hack, it sends two requests to find context's direct
    // members.
    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id) throws EscidocClientException {

        final List<ResourceModel> topLevelContainers =
            ModelConverter.genericResourcetoModel(client.retrieveMembersAsList(id,
                Util.createQueryForTopLevelContainers(id)));

        final List<ResourceModel> topLevelItems =
            ModelConverter
                .genericResourcetoModel(client.retrieveMembersAsList(id, Util.createQueryForTopLevelItems(id)));

        topLevelContainers.addAll(topLevelItems);

        return topLevelContainers;
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        return new ContextProxyImpl(client.retrieve(id));
    }

    @Override
    public VersionHistory getVersionHistory(final String id) throws EscidocClientException {
        return null;
    }

    @Override
    public Relations getRelations(final String id) throws EscidocClientException {
        return null;
    }

    @Override
    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
    }

}