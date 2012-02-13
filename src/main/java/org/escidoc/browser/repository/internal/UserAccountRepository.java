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
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.helper.Util;

import java.net.MalformedURLException;
import java.util.List;

import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.UserAccountHandlerClientInterface;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.aa.useraccount.UserAccountProperties;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class UserAccountRepository implements Repository {

    private final UserAccountHandlerClientInterface client;

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
        throw new UnsupportedOperationException("not-yet-implemented.");
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
}