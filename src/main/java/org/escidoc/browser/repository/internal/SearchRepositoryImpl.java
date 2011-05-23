package org.escidoc.browser.repository.internal;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
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
         * 1. Split phrases in " " 2. Split words by space if they are not in " " 3. Provide support for the operators
         * +,-
         */
        final String REGEXSPLITQUOTES = "\"(.*)\"";
        URLEncoder.encode(query);
        Pattern p = Pattern.compile(REGEXSPLITQUOTES, Pattern.DOTALL);

        Matcher matcher = p.matcher(query);

        final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<= |^)([^ ]*)(?: |$)");
        ArrayList<String> allMatches = new ArrayList<String>();
        String match = null;
        matcher = csvPattern.matcher(query);
        allMatches.clear();

        while (matcher.find()) {
            match = matcher.group(1);
            if (match != null) {
                allMatches.add(match);
            }
            else {
                allMatches.add(matcher.group(2));
            }
        }

        // escidoc.fulltext, escidoc.metadata escidoc.context.name escidoc.creator.name.
        String queryString = "1=1 ";
        for (String string : allMatches) {
            queryString +=
                " or escidoc.any-title=\"" + string + "\" or escidoc.fulltext=\"" + string
                    + "\" or escidoc.metadata=\"" + string + "\" or escidoc.context.name=\"" + string
                    + "\" or escidoc.creator.name=\"" + string + "\"";
        }
        System.out.println(queryString);
        try {
            // "escidoc.any-title"=b*
            return client.search(queryString, ESCIDOCALL);
        }
        catch (EscidocClientException e) {
            LOG.debug("EscidocClientException");
            e.printStackTrace();
        }
        return null;
    }
}