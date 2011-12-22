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

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.util.Utils;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

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
        return ModelConverter.contextListToModelWithChildInfo(client.retrieveContextsAsList(Util.createEmptyFilter()));
    }

    public boolean hasChildren(final Resource context) throws EscidocClientException {
        return !findTopLevelMembersById(context.getObjid()).isEmpty();
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        return findTopLevelMemberList(id);
    }

    private List<ResourceModel> findTopLevelMemberList(final String id) throws EscidocClientException {
        return ModelConverter.genericResourcetoModel(client.retrieveMembersAsList(id,
            Util.createQueryForTopLevelContainersAndItems(id)));
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

    @Override
    public List<ResourceModel> filterUsingInput(final String query) throws EscidocClientException {
        final SearchRetrieveRequestType filter = Utils.createEmptyFilter();
        filter.setQuery(query);
        final List<Context> list = client.retrieveContextsAsList(filter);
        final List<ResourceModel> ret = new ArrayList<ResourceModel>(list.size());
        for (final Context resource : list) {
            ret.add(new ContextProxyImpl(resource));
        }
        return ret;
    }

    @Override
    public void delete(final String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");

    }

    public Context create(Context context) throws EscidocClientException {
        return client.create(context);
    }

    public void open(final Context context) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(context.getLastModificationDate());
        taskParam.setComment("Initially Open");
        client.open(context, taskParam);

    }

    public Context addOrganizationalUnit(String contextId, String orgUnitId) throws EscidocClientException {
        Context context = client.retrieve(contextId);
        ContextProperties properties = context.getProperties();
        OrganizationalUnitRefs organizationalUnitRefs = properties.getOrganizationalUnitRefs();
        organizationalUnitRefs.add(new OrganizationalUnitRef(orgUnitId));
        properties.setOrganizationalUnitRefs(organizationalUnitRefs);
        return client.update(context);
    }

    public Context delOrganizationalUnit(String contextId, String orgUnitId) throws EscidocClientException {
        Context context = client.retrieve(contextId);
        ContextProperties properties = context.getProperties();
        OrganizationalUnitRefs organizationalUnitRefs = properties.getOrganizationalUnitRefs();
        for (OrganizationalUnitRef organizationalUnitRef : organizationalUnitRefs) {
            if (organizationalUnitRef.getObjid().equals(orgUnitId)) {
                organizationalUnitRefs.remove(new OrganizationalUnitRef(orgUnitId));
            }
        }
        properties.setOrganizationalUnitRefs(organizationalUnitRefs);
        return client.update(context);
    }
}
