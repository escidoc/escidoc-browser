package org.escidoc.browser.repository;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.om.context.Context;

public class ContextRepository implements Repository {

    private final ContextHandlerClientInterface client;

    public ContextRepository(final EscidocServiceLocation escidocServiceLocation) {
        client = new ContextHandlerClient(escidocServiceLocation.getUri());
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        final Collection<Context> contextsAsList =
            client.retrieveContextsAsList(new SearchRetrieveRequestType());
        return toModel(contextsAsList);
    }

    private static List<ResourceModel> toModel(
        final Collection<Context> contextsAsList) {
        final List<ResourceModel> models =
            new ArrayList<ResourceModel>(contextsAsList.size());
        for (final Resource context : contextsAsList) {
            models.add(new ContextModel(context));
        }
        return models;
    }
}