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

import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContainerHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.ContainerHandlerClientInterface;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class OrganizationUnitRepository implements Repository {

    private final ContainerHandlerClientInterface client;

    static final Logger LOG = LoggerFactory.getLogger(OrganizationUnitRepository.class);

    OrganizationUnitRepository(final EscidocServiceLocation escidocServiceLocation) {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new ContainerHandlerClient(escidocServiceLocation.getEscidocUri());
    }

    @Override
    public void loginWith(String handle) throws InternalClientException {
        // TODO Auto-generated method stub
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(String id) throws EscidocClientException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResourceProxy findById(String id) throws EscidocClientException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VersionHistory getVersionHistory(String id) throws EscidocClientException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Relations getRelations(String id) throws EscidocClientException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(ResourceModel model) throws EscidocClientException {
        // TODO Auto-generated method stub

    }
}
