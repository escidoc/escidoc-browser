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

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.apache.axis.types.NonNegativeInteger;
import org.escidoc.browser.model.EscidocServiceLocation;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContentModelHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.cmm.ContentModel;

public class ContentModelRepository {
    private final ContentModelHandlerClientInterface client;

    public ContentModelRepository(
        final EscidocServiceLocation escidocServiceLocation)
        throws MalformedURLException {
        Preconditions.checkNotNull(escidocServiceLocation,
            "escidocServiceLocation is null: %s", escidocServiceLocation);
        client =
            new ContentModelHandlerClient(new URL(
                escidocServiceLocation.getEscidocUri()));
    }

    public Collection<? extends Resource> findPublicOrReleasedResources()
        throws EscidocException, InternalClientException, TransportException {
        final SearchRetrieveRequestType request =
            new SearchRetrieveRequestType();
        request.setMaximumRecords(new NonNegativeInteger("1000"));
        return client.retrieveContentModelsAsList(request);
    }

    protected SearchRetrieveRequestType userInputToFilter(final String query) {
        final SearchRetrieveRequestType filter =
            new SearchRetrieveRequestType();
        filter.setQuery(query);
        return filter;
    }

    public ContentModel findById(String id) throws EscidocClientException {
        return client.retrieve(id);
    }
}