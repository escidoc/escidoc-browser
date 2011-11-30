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
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;
import org.escidoc.browser.model.ContentModelProxyImpl;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repository;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContentModelHandlerClientInterface;
import de.escidoc.core.client.rest.RestContentModelHandlerClient;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class ContentModelRepository implements Repository {

    private final ContentModelHandlerClientInterface client;

    public ContentModelRepository(final EscidocServiceLocation escidocServiceLocation) throws MalformedURLException {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new ContentModelHandlerClient(new URL(escidocServiceLocation.getEscidocUri()));
    }

    public Collection<? extends Resource> findPublicOrReleasedResources() throws EscidocException,
        InternalClientException, TransportException {
        final SearchRetrieveRequestType request = new SearchRetrieveRequestType();
        request.setMaximumRecords(new NonNegativeInteger("1000"));
        return client.retrieveContentModelsAsList(request);
    }

    protected SearchRetrieveRequestType userInputToFilter(final String query) {
        final SearchRetrieveRequestType filter = new SearchRetrieveRequestType();
        filter.setQuery(query);
        return filter;
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        return new ContentModelProxyImpl(client.retrieve(id));
    }

    public String getAsXmlString(String id) throws EscidocClientException {
        return new RestContentModelHandlerClient(client.getServiceAddress()).retrieve(id);
    }

    @Override
    public void loginWith(String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.contentModelListToModel(client
            .retrieveContentModelsAsList(new SearchRetrieveRequestType()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public VersionHistory getVersionHistory(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public Relations getRelations(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public List<ResourceModel> filterUsingInput(String query) throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");

    }
}