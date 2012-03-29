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

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.GuestUser;
import org.escidoc.browser.model.internal.LoggedInUser;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.aa.useraccount.UserAccountProperties;
import de.escidoc.core.resources.common.TaskParam;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final UserAccountHandlerClient client;

    private String token;

    public UserRepositoryImpl(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        client = new UserAccountHandlerClient(serviceLocation.getEscidocUri());
    }

    public void withToken(final String token) {
        this.token = token;
        client.setHandle(token);
    }

    @Override
    public CurrentUser findCurrentUser() {
        try {
            if (token == null || token.trim().length() <= 0) {
                throw new InternalClientException("No user token available.");
            }
            return new LoggedInUser(client.retrieveCurrentUser(), token);
        }
        catch (final EscidocClientException e) {
            LOG.debug("The user is not logged in");
            return new GuestUser();
        }
        catch (Throwable t) {
            LOG.error("The user has to log in first.");
            return new GuestUser();
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserRepositoryImpl [");
        if (client != null) {
            builder.append("client=").append(client);
        }
        builder.append("]");
        return builder.toString();
    }

    public String getUserRealName() throws EscidocClientException {
        return client.retrieveCurrentUser().getProperties().getName();
    }

    public UserAccount getCurrentUser() throws EscidocClientException {
        return client.retrieveCurrentUser();
    }

    public void updateName(String newName) throws EscidocClientException {
        UserAccount usr = client.retrieveCurrentUser();
        UserAccountProperties prop = new UserAccountProperties();
        prop = usr.getProperties();
        prop.setName(newName);
        usr.setProperties(prop);
        client.update(usr);
    }

    @Override
    public void updatePassword(final String newPassword) throws EscidocClientException {
        UserAccount user = client.retrieveCurrentUser();
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(user.getLastModificationDate());
        taskParam.setPassword(newPassword);
        client.updatePassword(user.getObjid(), taskParam);
    }

    @Override
    public Preferences getUserPreferences() throws EscidocClientException {
        return client.retrievePreferences(getCurrentUser());
    }

    @Override
    public Preference createUserPreference(Preference preference) throws EscidocClientException {
        return client.createPreference(getCurrentUser().getObjid(), preference);
    }

    @Override
    public Preference updateUserPreference(Preference preference) throws EscidocClientException {
        return client.updatePreference(getCurrentUser(), preference);
    }

    public void removeUserPreference(String preferenceName) throws EscidocClientException {
        client.deletePreference(getCurrentUser().getObjid(), preferenceName);
    }

    @Override
    public void updatePassword(UserProxy userProxy, String pw) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(userProxy.getResource().getLastModificationDate());
        taskParam.setPassword(pw);
        client.updatePassword(userProxy.getId(), taskParam);
    }

}