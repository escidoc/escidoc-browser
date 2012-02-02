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

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.OrgUnitModel;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.OrganizationalUnitHandlerClientInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;

public class OrganizationUnitRepository implements Repository {

    private final OrganizationalUnitHandlerClientInterface client;

    static final Logger LOG = LoggerFactory.getLogger(OrganizationUnitRepository.class);

    OrganizationUnitRepository(final EscidocServiceLocation escidocServiceLocation) {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new OrganizationalUnitHandlerClient(escidocServiceLocation.getEscidocUri());
    }

    @Override
    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    public List<ResourceModel> findTopLevel() throws EscidocClientException {
        final SearchRetrieveRequestType searchRequest = Utils.createEmptyFilter();
        searchRequest.setQuery("\"top-level-organizational-units\"=true");
        final List<OrganizationalUnit> children = client.retrieveOrganizationalUnitsAsList(searchRequest);
        final List<ResourceModel> list = new ArrayList<ResourceModel>(children.size());
        for (final OrganizationalUnit ou : children) {
            list.add(new OrgUnitModel(ou));
        }
        return list;
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        final List<OrganizationalUnit> list = client.retrieveOrganizationalUnitsAsList(Utils.createEmptyFilter());
        final List<ResourceModel> rt = new ArrayList<ResourceModel>(list.size());
        for (final OrganizationalUnit ou : list) {
            rt.add(new OrgUnitModel(ou));
        }
        return rt;
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String parentId) throws EscidocClientException {
        final List<OrganizationalUnit> children = client.retrieveChildObjectsAsList(parentId);
        final List<ResourceModel> list = new ArrayList<ResourceModel>(children.size());
        for (final OrganizationalUnit ou : children) {
            list.add(new OrgUnitModel(ou));
        }
        return list;
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        return new OrgUnitProxy(client.retrieve(id));
    }

    @Override
    public VersionHistory getVersionHistory(final String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Relations getRelations(final String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public List<ResourceModel> filterUsingInput(final String query) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public OrgUnitModel create(final OrganizationalUnit ou) throws EscidocClientException {
        final OrganizationalUnit createdOrgUnit = client.create(ou);
        return new OrgUnitModel(createdOrgUnit);
    }

    public void addMetaData(ResourceProxy ou, MetadataRecord metadataRecord) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        OrganizationalUnit orgUnit = client.retrieve(ou.getId());
        taskParam.setLastModificationDate(orgUnit.getLastModificationDate());
        taskParam.setComment("Adding a new MetaData");
        final MetadataRecords list = orgUnit.getMetadataRecords();
        list.add(metadataRecord);
        client.update(orgUnit);
    }

    public void updateMetaData(OrgUnitProxy ou, MetadataRecord metadataRecord) throws EscidocClientException {
        OrganizationalUnit orgUnit = client.retrieve(ou.getId());

        final MetadataRecords list = orgUnit.getMetadataRecords();
        list.del(metadataRecord.getName());
        list.add(metadataRecord);
        orgUnit.setMetadataRecords(list);

        client.update(orgUnit);
    }
}