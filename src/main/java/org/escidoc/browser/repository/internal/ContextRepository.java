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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.repository.internal;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.util.Utils;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.ContextHandlerClientInterface;
import de.escidoc.core.client.rest.RestContextHandlerClient;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.VersionableResource;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;
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
        List<VersionableResource> membersList =
            client.retrieveMembersAsList(id, Util.createQueryForTopLevelContainersAndItems(id));
        return ModelConverter.genericResourcetoModel(membersList);
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        return new ContextProxyImpl(client.retrieve(id));
    }

    public Context findContextById(final String id) throws EscidocClientException {
        return client.retrieve(id);
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
    public void delete(final String id) throws EscidocClientException {
        client.delete(id);

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

    public AdminDescriptor addAdminDescriptor(String id, String txtName, String txtContent)
        throws EscidocClientException, ParserConfigurationException, SAXException, IOException {
        Context context = client.retrieve(id);
        AdminDescriptor adminDescriptor = new AdminDescriptor(txtName);
        adminDescriptor.setContent(txtContent);
        AdminDescriptors adminDescriptors = context.getAdminDescriptors();
        Boolean addNew = true;

        for (AdminDescriptor adminDesc : adminDescriptors) {
            if (adminDesc.getName().equals(txtName)) {
                adminDesc.setContent(txtContent);
                addNew = false;
            }
        }

        if (addNew) {
            adminDescriptors.add(adminDescriptor);
        }
        context.setAdminDescriptors(adminDescriptors);
        client.update(context);
        return context.getAdminDescriptors().get(txtName);
    }

    public Context removeAdminDescriptor(String contextId, String name) throws EscidocClientException {
        Context context = client.retrieve(contextId);
        AdminDescriptors adminDescriptors = context.getAdminDescriptors();
        adminDescriptors.del(name);
        context.setAdminDescriptors(adminDescriptors);
        return client.update(context);
    }

    public Context delOrganizationalUnit(String contextId, String orgUnitId) throws EscidocClientException {
        Context context = client.retrieve(contextId);
        OrganizationalUnitRefs organizationalUnitRefs = context.getProperties().getOrganizationalUnitRefs();
        for (OrganizationalUnitRef organizationalUnitRef : organizationalUnitRefs) {
            if (organizationalUnitRef.getObjid().equals(orgUnitId)) {
                organizationalUnitRefs.remove(new OrganizationalUnitRef(orgUnitId));
                break;
            }
        }
        context.getProperties().setOrganizationalUnitRefs(organizationalUnitRefs);
        return client.update(context);
    }

    public Context updateType(String newContextType, String contextId) throws EscidocClientException {
        Context context = client.retrieve(contextId);
        ContextProperties properties = context.getProperties();
        properties.setType(newContextType);
        return client.update(context);
    }

    public Context updateName(String newName, String contextId) throws EscidocClientException {
        Context context = client.retrieve(contextId);
        ContextProperties properties = context.getProperties();
        properties.setName(newName);
        return client.update(context);
    }

    public void updatePublicStatusOpen(String comment, String contextId) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(client.retrieve(contextId).getLastModificationDate());
        taskParam.setComment(comment);
        client.open(client.retrieve(contextId), taskParam);
    }

    public void updatePublicStatusClosed(String comment, String contextId) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(client.retrieve(contextId).getLastModificationDate());
        taskParam.setComment(comment);
        client.close(client.retrieve(contextId), taskParam);

    }

    @Override
    public String getAsXmlString(String id) throws EscidocClientException {
        return new RestContextHandlerClient(client.getServiceAddress()).retrieve(id);
    }

    public AdminDescriptor getAdminDescriptor(String id, String metadataName) throws EscidocClientException {
        return client.retrieve(id).getAdminDescriptors().get(metadataName);
    }

    public void updateAdminDescriptor(String id, AdminDescriptor metadata) throws EscidocClientException {
        Context context = client.retrieve(id);
        context.getAdminDescriptors().get(metadata.getName()).setContent(metadata.getContent());
        client.update(context);
    }
}
