package org.escidoc.browser.model;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.apache.axis.types.NonNegativeInteger;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContentModelHandlerClientInterface;
import de.escidoc.core.resources.Resource;

public class ContentModelService {
    private final ContentModelHandlerClientInterface client;

    public ContentModelService(final EscidocServiceLocation escidocServiceLocation) throws MalformedURLException {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new ContentModelHandlerClient(new URL(escidocServiceLocation.getEscidocUri()));
    }

    Collection<? extends Resource> findPublicOrReleasedResources() throws EscidocException, InternalClientException,
        TransportException {
        final SearchRetrieveRequestType request = new SearchRetrieveRequestType();
        request.setMaximumRecords(new NonNegativeInteger("1000"));
        return client.retrieveContentModelsAsList(request);
    }

    public Collection<? extends Resource> filterUsingInput(final String query) throws EscidocException,
        InternalClientException, TransportException {
        return client.retrieveContentModelsAsList(userInputToFilter(query));
    }

    protected SearchRetrieveRequestType userInputToFilter(final String query) {
        final SearchRetrieveRequestType filter = new SearchRetrieveRequestType();
        filter.setQuery(query);
        return filter;
    }
}