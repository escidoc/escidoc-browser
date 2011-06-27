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

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.helper.Util;

import java.util.List;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class ContextRepository implements Repository {

    private final ContextHandlerClientInterface client;

    public ContextRepository(final EscidocServiceLocation escidocServiceLocation) {
        client = new ContextHandlerClient(escidocServiceLocation.getEscidocUri());
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.contextListToModel(client.retrieveContextsAsList(Util.createEmptyFilter()));
    }

    public List<ResourceModel> findAllWithChildrenInfo() throws EscidocClientException {
        return ModelConverter.contextListToModelWithChildInfo(client.retrieveContextsAsList(Util.createEmptyFilter()),
            this);
    }

    public boolean hasChildren(final Resource context) throws EscidocClientException {
        return !findTopLevelMembersById(context.getObjid()).isEmpty();
    }

    // FIXME: this is a hack, it sends two requests to find context's direct
    // members.
    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        final List<ResourceModel> topLevelContainers = findTopLevelContainerList(id);
        topLevelContainers.addAll(findTopLevelItemList(id));

        return topLevelContainers;
    }

    private List<ResourceModel> findTopLevelContainerList(final String id) throws EscidocException,
        InternalClientException, TransportException {
        return ModelConverter.genericResourcetoModel(client.retrieveMembersAsList(id,
            Util.createQueryForTopLevelContainers(id)));
    }

    private List<ResourceModel> findTopLevelItemList(final String id) throws EscidocException, InternalClientException,
        TransportException {
        return ModelConverter.genericResourcetoModel(client.retrieveMembersAsList(id,
            Util.createQueryForTopLevelItems(id)));
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