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
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.helper.Util;

import java.net.MalformedURLException;
import java.util.List;

import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.UserAccountHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.Relations;
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
        return new ResourceProxy() {

            @Override
            public ResourceType getType() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getName() {
                return u.getXLinkTitle();
            }

            @Override
            public String getId() {
                return u.getObjid();
            }

            @Override
            public String getVersionStatus() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getStatus() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public List<String> getRelations() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getModifier() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getModifiedOn() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getLockStatus() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getDescription() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getCreator() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public String getCreatedOn() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public Resource getContext() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }

            @Override
            public Resource getContentModel() {
                throw new UnsupportedOperationException("not-yet-implemented.");
            }
        };
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
    public void delete(String id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}