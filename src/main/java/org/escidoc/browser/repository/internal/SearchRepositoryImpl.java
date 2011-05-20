package org.escidoc.browser.repository.internal;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.SearchHandlerClientInterface;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;

public class SearchRepositoryImpl {
    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private static final String ESCIDOCALL = "escidoc_all";

    private final SearchHandlerClientInterface client;

    public SearchRepositoryImpl(final EscidocServiceLocation escidocServiceLocation) {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new SearchHandlerClient(escidocServiceLocation.getEscidocUri());
        client.setTransport(TransportProtocol.REST);
    }

    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    public SearchRetrieveResponse search(String query) {
    	/*
    	 * 1. Split phrases in " "
    	 * 2. Split words by space if they are not in " "
    	 * 3. Provide support for the operators +,-
    	 */
    	if (query.matches("#^\"(.*)\"$#"))
    		LOG.debug("Matched");
    	
        try {
            // "escidoc.any-title"=b*
            return client.search("\"escidoc.any-title\"=" + query + " or \"escidoc.context.name\"=" + query + "",
                ESCIDOCALL);
        }
        catch (EscidocClientException e) {
            LOG.debug("EscidocClientException");
            e.printStackTrace();
        }
        return null;
    }
}