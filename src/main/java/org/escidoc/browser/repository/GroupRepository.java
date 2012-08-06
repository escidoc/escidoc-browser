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
package org.escidoc.browser.repository;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.GroupModel;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.RoleRepository.RoleModel;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.UserGroupHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.UserGroupHandlerClientInterface;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.usergroup.Selector;
import de.escidoc.core.resources.aa.usergroup.SelectorType;
import de.escidoc.core.resources.aa.usergroup.Selectors;
import de.escidoc.core.resources.aa.usergroup.UserGroup;
import de.escidoc.core.resources.aa.usergroup.UserGroupProperties;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContainerRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.ItemRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.common.reference.RoleRef;
import de.escidoc.core.resources.common.reference.UserAccountRef;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class GroupRepository implements Repository {

    public static final String ORGANIZATIONL_UNIT = "o";

    private final static Logger LOG = LoggerFactory.getLogger(GroupRepository.class);

    private UserGroupHandlerClientInterface client;

    private GroupModel group;

    private GrantProperties grantProps;

    public GroupRepository(EscidocServiceLocation esl) throws MalformedURLException {
        Preconditions.checkNotNull(esl, "esl is null: %s", esl);
        client = new UserGroupHandlerClient(esl.getEscidocUrl());
    }

    @Override
    public void loginWith(String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.groupToList(client.retrieveUserGroupsAsList(Util.createEmptyFilter()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceProxy findById(String id) throws EscidocClientException {
        UserGroup userGroup = client.retrieve(id);
        return new UserGroupModel(userGroup);
    }

    public GroupModel find(String id) throws EscidocClientException {
        return new GroupModel(client.retrieve(id));
    }

    @Override
    public VersionHistory getVersionHistory(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Relations getRelations(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public List<ResourceModel> filterUsingInput(String query) throws EscidocClientException {
        final SearchRetrieveRequestType filter = Utils.createEmptyFilter();
        filter.setQuery(query);
        final List<UserGroup> list = client.retrieveUserGroupsAsList(filter);
        final List<ResourceModel> ret = new ArrayList<ResourceModel>(list.size());
        for (final UserGroup resource : list) {
            ret.add(new UserGroupModel(resource));
        }
        return ret;
    }

    @Override
    public void delete(String id) throws EscidocClientException {
        client.delete(id);
    }

    @Override
    public String getAsXmlString(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public UserGroup createGroup(String name) throws EscidocClientException {
        UserGroup ug = new UserGroup();
        addProperties(name, ug);

        UserGroup created = client.create(ug);

        LOG.debug("A user group is created with an id: " + created.getObjid());
        return created;
    }

    private static void addProperties(String name, UserGroup ug) {
        UserGroupProperties p = new UserGroupProperties();
        p.setName(name);
        p.setLabel(name + "_" + UUID.randomUUID());
        ug.setProperties(p);

        p.setDescription("Description, automated generated .");
        p.setEmail("E-mail, automated generated email.");
        p.setType("Group type, automated generated");
    }

    public UserGroup updateGroup(String id, String newName, List<ResourceModel> list) throws EscidocClientException {
        UserGroup updated = updateName(id, newName);

        if (list.isEmpty()) {
            return updated;
        }
        TaskParam tp = new TaskParam();
        tp.setLastModificationDate(updated.getLastModificationDate());

        Selectors s = updated.getSelectors();
        s.clear();

        for (ResourceModel rm : list) {
            s.add(new Selector(rm.getId(), ORGANIZATIONL_UNIT, SelectorType.USER_ATTRIBUTE));
        }
        tp.setSelectors(s);

        client.addSelectors(updated, tp);

        LOG.debug("A user group is with an id: " + updated.getObjid() + " is updated.");
        return updated;
    }

    private static TaskParam setLastModificationDate(UserGroup updated) {
        TaskParam tp = new TaskParam();
        tp.setLastModificationDate(updated.getLastModificationDate());
        return tp;
    }

    private static Selectors setSelectorList(List<ResourceModel> list, UserGroup updated) {
        Selectors s = updated.getSelectors();
        for (ResourceModel rm : list) {
            s.add(new Selector(rm.getId(), ORGANIZATIONL_UNIT, SelectorType.USER_ATTRIBUTE));
        }
        return s;
    }

    private UserGroup updateName(String id, String newName) throws EscidocException, InternalClientException,
        TransportException {
        UserGroup original = client.retrieve(id);
        original.getProperties().setName(newName);
        UserGroup updated = client.update(id, original);
        return updated;
    }

    public void removeOrganization(String groupId, String id) throws EscidocClientException {
        TaskParam tp = new TaskParam();
        UserGroup updated = client.retrieve(groupId);

        tp.setLastModificationDate(updated.getLastModificationDate());

        for (Selector selector : updated.getSelectors()) {
            if (selector.getContent().equals(id)) {
                tp.addResourceRef(selector.getObjid());
            }
        }
        client.removeSelectors(groupId, tp);
    }

    public UserGroup updateGroup(String id, String newName) throws EscidocClientException {

        UserGroup updated = updateName(id, newName);

        TaskParam tp = new TaskParam();
        tp.setLastModificationDate(updated.getLastModificationDate());

        LOG.debug("A user group is with an id: " + updated.getObjid() + " is updated.");
        return updated;
    }

    public GroupRepository assign(GroupModel group) {
        if (group == null) {
            throw new IllegalArgumentException("Group can not be null.");
        }
        this.group = group;
        return this;
    }

    public GroupRepository assign(String groupId) throws EscidocClientException {
        if (groupId == null) {
            throw new IllegalArgumentException("GroupId can not be null");
        }
        this.group = new GroupModel(client.retrieve(groupId));
        return this;
    }

    public GroupRepository withRole(RoleModel role) {
        Preconditions.checkNotNull(role, "role is null: %s", role);
        Preconditions.checkNotNull(group, "user is null: %s", group);
        grantProps = new GrantProperties();
        grantProps.setRole(new RoleRef(role.getId()));
        return this;
    }

    public GroupRepository onResources(Set<ResourceModel> resources) {
        for (final ResourceModel rm : resources) {
            ResourceType type = rm.getType();
            Reference ref = null;

            switch (type) {
                case CONTEXT:
                    ref = new ContextRef(rm.getId());
                    break;
                case CONTAINER:
                    ref = new ContainerRef(rm.getId());
                    break;
                case ITEM:
                    ref = new ItemRef(rm.getId());
                    break;
                case USER_ACCOUNT:
                    ref = new UserAccountRef(rm.getId());
                    break;
                default:
                    break;
            }

            grantProps.setAssignedOn(ref);
        }
        return this;
    }

    public Grant execute() throws EscidocClientException {
        final Grant grant = new Grant();
        grant.setProperties(grantProps);
        Grant createdGrant = client.createGrant(group.getId(), grant);
        LOG.debug("Grant created: " + createdGrant.getObjid());
        return createdGrant;
    }

    /**
     * Add Organizational units to the User-Group
     * 
     * @param uGroupId
     * @param orgUnitId
     * @return
     * @throws EscidocClientException
     */
    public UserGroup addOrgUnit(String uGroupId, String orgUnitId) throws EscidocClientException {
        UserGroup userGroup = client.retrieve(uGroupId);

        TaskParam tp = new TaskParam();
        tp.setLastModificationDate(userGroup.getLastModificationDate());
        Selector selector = new Selector(orgUnitId, ORGANIZATIONL_UNIT, SelectorType.USER_ATTRIBUTE);
        List<Selector> selectors = tp.getSelectors();
        selectors.add(selector);
        tp.setSelectors(selectors);
        return client.addSelectors(uGroupId, tp);
    }

    public Grants getGrantsForGroup(String groupId) throws EscidocClientException {
        try {
            return client.retrieveCurrentGrants(groupId);
        }
        catch (RuntimeException e) {
            String foo = "";
        }
        return null;
    }

    public void revokeGrant(String groupId, Grant grant) throws EscidocClientException {
        Preconditions.checkNotNull(groupId, "groupId is null: %s", groupId);
        Preconditions.checkNotNull(grant, "grant is null: %s", grant);
        final TaskParam tp = new TaskParam();
        tp.setLastModificationDate(grant.getLastModificationDate());
        client.revokeGrant(groupId, grant.getObjid(), tp);
    }

    public void activateUserGroup(String id) throws EscidocClientException {
        UserGroup usr = client.retrieve(id);
        final TaskParam tp = new TaskParam();
        tp.setLastModificationDate(usr.getLastModificationDate());
        client.activate(id, tp);

    }

    public void deactivateUserGroup(String id) throws EscidocClientException {
        UserGroup usr = client.retrieve(id);
        final TaskParam tp = new TaskParam();
        tp.setLastModificationDate(usr.getLastModificationDate());
        client.deactivate(id, tp);

    }
}
