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
package org.escidoc.browser.repository;

import java.net.MalformedURLException;
import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.AdminHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.AdminHandlerClientInterface;
import de.escidoc.core.resources.adm.LoadExamplesResult.Entry;
import de.escidoc.core.resources.adm.MessagesStatus;
import de.escidoc.core.resources.adm.RepositoryInfo;

public class AdminRepository {

    private AdminHandlerClientInterface client;

    public AdminRepository(EscidocServiceLocation serviceLocation) throws MalformedURLException {
        client = new AdminHandlerClient(serviceLocation.getEscidocUrl());
    }

    public List<Entry> loadCommonExamples() throws EscidocClientException {
        return client.loadExamples();
    }

    public void loginWith(String token) throws EscidocClientException {
        client.setHandle(token);
    }

    public RepositoryInfo getRepositoryInfo() throws EscidocClientException {
        return client.getRepositoryInfo();
    }

    public String getIndexConfiguration() throws EscidocClientException {
        return client.getIndexConfiguration();
    }

    public MessagesStatus reindex(final Boolean shouldClearIndex, final String indexNamePrefix)
        throws EscidocClientException {
        Preconditions.checkNotNull(indexNamePrefix, "indexNamePrefix can not be null: %s", indexNamePrefix);
        Preconditions
            .checkArgument(!indexNamePrefix.isEmpty(), "indexNamePrefix can not be empty: %s", indexNamePrefix);
        return client.reindex(shouldClearIndex.booleanValue(), indexNamePrefix);
    }

    public MessagesStatus retrieveReindexStatus() throws EscidocException, InternalClientException, TransportException {
        return client.getReindexStatus();
    }
}
