/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.repository.internal;

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

    public SearchRetrieveResponse advancedSearch(String query) {
        return null;
    }

    public SearchRetrieveResponse simpleSearch(String query) {
        /*
         * 1. Split phrases in " " 2. Split words by space if they are not in " " 3. Provide support for the operators
         * +,-
         */
        final String REGEXSPLITQUOTES = "\"(.*)\"";

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
        // queryString += queryString + "&maximumRecords=100";
        try {
            // "escidoc.any-title"=b*
            return client.search(queryString, 0, 1000, "sort.escidoc.pid", ESCIDOCALL);
        }
        catch (EscidocClientException e) {
            LOG.debug("EscidocClientException");
            e.printStackTrace();
        }
        return null;
    }

    public SearchRetrieveResponse advancedSearch(
        String titleTxt, String creatorTxt, String descriptionTxt, String creationDateTxt, String mimesTxt,
        String resourceTxt, String fulltxtTxt) {
        String query = "1=1 ";

        /*
         * Do I have a resource defined? If I have a resource defined, then I need to search in the correct context.name
         * or any-title index depending on the resource
         */
        if (resourceTxt != null) {
            if (resourceTxt == "Context") {
                query += "OR escidoc.context.name=\"" + titleTxt + "\"";
            }
            else {
                query += "OR escidoc.any-title=\"" + titleTxt + "\"";
            }
        }
        else {
            if (!titleTxt.isEmpty()) {
                query += "OR escidoc.any-title=\"" + titleTxt + "\" or escidoc.context.name=\"" + titleTxt + "\"";
            }
        }

        if (!creatorTxt.isEmpty()) {
            query += " AND escidoc.created-by.name=\"" + creatorTxt + "\"";
        }
        if (!descriptionTxt.isEmpty()) {
            // query += " AND escidoc.created-by.name=\"" + creatorTxt + "\"";
        }
        if (creationDateTxt != null) {
            query +=
                " AND (escidoc.creation-date=\"" + creationDateTxt + "*\" OR (escidoc.component.creation-date=\""
                    + creationDateTxt + "*\"))";
        }
        if (mimesTxt != null) {
            query += " AND escidoc.component.mime-type=\"" + mimesTxt + "\"";
        }

        if (!fulltxtTxt.isEmpty()) {
            query += " AND escidoc.fulltext=\"" + fulltxtTxt + "\"";
        }
        try {
            LOG.debug(query);
            return client.search(query, 0, 1000, "sort.escidoc.pid", ESCIDOCALL);
        }
        catch (EscidocClientException e) {
            LOG.debug("EscidocClientException");
            e.printStackTrace();
        }
        return null;

    }
}