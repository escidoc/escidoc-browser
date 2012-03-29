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

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.GroupModel;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.helper.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import de.escidoc.core.client.UserGroupHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.UserGroupHandlerClientInterface;
import de.escidoc.core.resources.aa.usergroup.Selector;
import de.escidoc.core.resources.aa.usergroup.SelectorType;
import de.escidoc.core.resources.aa.usergroup.Selectors;
import de.escidoc.core.resources.aa.usergroup.UserGroup;
import de.escidoc.core.resources.aa.usergroup.UserGroupProperties;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class GroupRepository implements Repository {

    public static final String ORGANIZATIONL_UNIT = "o";

    private final static Logger LOG = LoggerFactory.getLogger(GroupRepository.class);

    private UserGroupHandlerClientInterface c;

    public GroupRepository(EscidocServiceLocation esl) throws MalformedURLException {
        Preconditions.checkNotNull(esl, "esl is null: %s", esl);
        c = new UserGroupHandlerClient(esl.getEscidocUrl());
    }

    @Override
    public void loginWith(String handle) throws InternalClientException {
        c.setHandle(handle);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.groupToList(c.retrieveUserGroupsAsList(Util.createEmptyFilter()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceProxy findById(String id) throws EscidocClientException {
        UserGroup userGroup = c.retrieve(id);
        return new UserGroupModel(userGroup);
    }

    public GroupModel find(String id) throws EscidocClientException {
        return new GroupModel(c.retrieve(id));
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
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void delete(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getAsXmlString(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public UserGroup createGroup(String name) throws EscidocClientException {
        UserGroup ug = new UserGroup();
        addProperties(name, ug);

        UserGroup created = c.create(ug);

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

        c.addSelectors(updated, tp);

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
        UserGroup original = c.retrieve(id);
        original.getProperties().setName(newName);
        UserGroup updated = c.update(id, original);
        return updated;
    }

    public void removeOrganization(String groupId, String id) throws EscidocClientException {
        TaskParam tp = new TaskParam();
        UserGroup updated = c.retrieve(groupId);
        tp.setLastModificationDate(updated.getLastModificationDate());
        tp.addResourceRef(id);

        // Selectors selectors = updated.getSelectors();
        //
        // Selectors selectorList = new Selectors();
        // // FIXME not optimal
        // for (Selector selector : selectors) {
        // if (selector.getContent().equals(id)) {
        // // selectorList.add(selector);
        //
        // selectorList.add(new Selector(id, "o", SelectorType.USER_ATTRIBUTE));
        // }
        //
        // }
        // tp.setSelectors(selectorList);
        c.removeSelectors(groupId, tp);
    }

    public UserGroup updateGroup(String id, String newName) throws EscidocClientException {

        UserGroup updated = updateName(id, newName);

        TaskParam tp = new TaskParam();
        tp.setLastModificationDate(updated.getLastModificationDate());

        LOG.debug("A user group is with an id: " + updated.getObjid() + " is updated.");
        return updated;
    }
}