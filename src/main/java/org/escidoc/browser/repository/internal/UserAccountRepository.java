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

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.UserModel;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.RoleRepository.RoleModel;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.UserAccountHandlerClientInterface;
import de.escidoc.core.client.rest.RestContentModelHandlerClient;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.aa.useraccount.UserAccountProperties;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContainerRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.ItemRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.common.reference.RoleRef;
import de.escidoc.core.resources.common.reference.UserAccountRef;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;

public class UserAccountRepository implements Repository {

    private final static Logger LOG = LoggerFactory.getLogger(UserAccountRepository.class);

    private final UserAccountHandlerClientInterface client;

    private UserModel user;

    private GrantProperties grantProps;

    public UserAccountRepository(final EscidocServiceLocation escidocServiceLocation) throws MalformedURLException {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        client = new UserAccountHandlerClient(escidocServiceLocation.getEscidocUrl());
    }

    @Override
    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.userAccountToList(client.retrieveUserAccountsAsList(Util.createEmptyFilter()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceProxy findById(final String id) throws EscidocClientException {
        final UserAccount u = client.retrieve(id);
        return new UserProxy(u);
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
        final SearchRetrieveRequestType filter = Utils.createEmptyFilter();
        filter.setQuery(query);
        final List<UserAccount> list = client.retrieveUserAccountsAsList(filter);
        final List<ResourceModel> ret = new ArrayList<ResourceModel>(list.size());
        for (final UserAccount resource : list) {
            ret.add(new UserModel(resource));
        }
        return ret;
    }

    @Override
    public void delete(String id) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkNotNull(client, "client is null: %s", client);
        client.delete(id);
    }

    public void updateName(String newName) throws EscidocClientException {
        UserAccount usr = client.retrieveCurrentUser();
        UserAccountProperties prop = new UserAccountProperties();
        prop = usr.getProperties();
        prop.setName(newName);
        usr.setProperties(prop);
        client.update(usr);
    }

    public void updatePassword(final String newPassword) throws EscidocClientException {
        UserAccount user = client.retrieveCurrentUser();
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(user.getLastModificationDate());
        taskParam.setPassword(newPassword);
        client.updatePassword(user.getObjid(), taskParam);
    }

    public Preferences getPreferences(UserProxy up) throws EscidocClientException {
        return client.retrievePreferences(up.getId());
    }

    public void updatePassword(UserProxy userProxy, String pw) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(userProxy.getResource().getLastModificationDate());
        taskParam.setPassword(pw);
        client.updatePassword(userProxy.getId(), taskParam);
    }

    public Preference createPreference(UserProxy userProxy, Preference preference) throws EscidocClientException {
        return client.createPreference(userProxy.getId(), preference);
    }

    public void removePreference(UserProxy userProxy, String preferenceName) throws EscidocClientException {
        Preconditions.checkNotNull(userProxy, "userProxy is null: %s", userProxy);
        Preconditions.checkNotNull(preferenceName, "preferenceName is null: %s", preferenceName);

        client.deletePreference(userProxy.getId(), preferenceName);
    }

    public Attributes getAttributes(UserProxy userProxy) throws EscidocClientException {
        return client.retrieveAttributes(userProxy.getId());
    }

    public void create(UserAccount u, String password) throws EscidocClientException {
        updatePassword(new UserProxy(client.create(u)), password);
    }

    public void updateName(UserProxy userProxy, String string) throws EscidocClientException {
        UserAccount ua = client.retrieve(userProxy.getId());
        ua.getProperties().setName(string);
        client.update(ua);
    }

    public void createAttribute(UserProxy userProxy, Attribute attribute) throws EscidocClientException {
        client.createAttribute(userProxy.getId(), attribute);
    }

    public void removeAttribute(UserProxy userProxy, String attributeName) throws EscidocClientException {
        Preconditions.checkNotNull(userProxy, "userProxy is null: %s", userProxy);
        Preconditions.checkNotNull(attributeName, "attributeName is null: %s", attributeName);
        client.deleteAttribute(userProxy.getId(),
            findAttribute(client.retrieveAttributes(userProxy.getId()), attributeName));
    }

    private static String findAttribute(Attributes list, String attributeName) {
        for (Attribute attribute : list) {
            if (attribute.getName().equals(attributeName)) {
                return attribute.getObjid();
            }
        }
        return null;
    }

    @Override
    public String getAsXmlString(String id) throws EscidocClientException {
        return new RestContentModelHandlerClient(client.getServiceAddress()).retrieve(id);
    }

    public UserAccountRepository assign(UserModel user) {
        if (user == null) {
            throw new IllegalArgumentException("UserAccount can not be null.");
        }
        this.user = user;
        return this;
    }

    public UserAccountRepository withRole(RoleModel role) {
        Preconditions.checkNotNull(role, "role is null: %s", role);
        Preconditions.checkNotNull(user, "user is null: %s", user);
        grantProps = new GrantProperties();
        grantProps.setRole(new RoleRef(role.getId()));
        return this;
    }

    public UserAccountRepository onResources(Set<ResourceModel> resources) {
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
        Grant createdGrant = client.createGrant(user.getId(), grant);
        LOG.debug("Grant created: " + createdGrant.getObjid());
        return createdGrant;
    }

    public Grants getGrants(String userId) throws EscidocClientException {
        return client.retrieveCurrentGrants(userId);
    }
}