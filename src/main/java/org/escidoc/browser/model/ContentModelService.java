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
package org.escidoc.browser.model;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.cmm.ContentModel;

public class ContentModelService {

    private String eSciDocUri;

    private String handle;

    private ContentModelHandlerClient client;

    public ContentModelService(final String eSciDocUri, final String handle) throws InternalClientException {
        Preconditions.checkNotNull(eSciDocUri, "eSciDocUri can not be null: %s", eSciDocUri);
        Preconditions.checkNotNull(handle, "handle can not be null: %s", handle);

        this.eSciDocUri = eSciDocUri;
        this.handle = handle;
        initClient();
    }

    private void initClient() throws InternalClientException {
        client = new ContentModelHandlerClient(eSciDocUri);
        client.setTransport(TransportProtocol.REST);
        client.setHandle(handle);
    }

    public Resource create(final Resource resource) throws EscidocException, InternalClientException,
        TransportException {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        if (!(resource instanceof ContentModel)) {
            throw new RuntimeException("Not instance of content model." + resource);
        }
        return getClient().create((ContentModel) resource);
    }

    public Resource findById(final String objid) throws EscidocClientException {
        Preconditions.checkNotNull(objid, "objid is null: %s", objid);
        return getClient().retrieve(objid);
    }

    public void update(final Resource resource) throws EscidocClientException {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        if (!(resource instanceof ContentModel)) {
            throw new RuntimeException("Not instance of content model." + resource);
        }
        getClient().update((ContentModel) resource);
    }

    ContentModelHandlerClient getClient() {
        return client;
    }

    // Collection<? extends Resource> findPublicOrReleasedResources() throws EscidocException, InternalClientException,
    // TransportException {
    // return getClient().retrieveContentModelsAsList(withEmptyFilter());
    // }

    // public Collection<? extends Resource> filterUsingInput(final String query) throws EscidocException,
    // InternalClientException, TransportException {
    // return getClient().retrieveContentModelsAsList(userInputToFilter(query));
    // }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ContentModelService [");
        if (getClient() != null) {
            builder.append("getClient()=").append(getClient());
        }
        builder.append("]");
        return builder.toString();
    }

    public void delete(final String selectedId) throws EscidocClientException {
        getClient().delete(selectedId);
    }
}