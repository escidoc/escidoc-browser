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
package org.escidoc.browser.model;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.axis.types.NonNegativeInteger;
import org.escidoc.browser.AppConstants;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.interfaces.UserAccountHandlerClientInterface;
import de.escidoc.core.resources.aa.role.Role;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.common.reference.RoleRef;

public class UserService {

    private UserAccountHandlerClientInterface client;

    private final Map<String, UserAccount> userAccountById = new ConcurrentHashMap<String, UserAccount>();

    private final String eSciDocUri;

    private final String handle;

    private Collection<UserAccount> userAccounts;

    private UserAccount user;

    private GrantProperties grantProps;

    private final String ORGANIZATIONAL_UNIT_DEFAULT_ATTRIBUTE_NAME = "o";

    public UserService(final String eSciDocUri, final String handle) throws InternalClientException {
        this.eSciDocUri = eSciDocUri;
        this.handle = handle;
        initClient();
    }

    private void initClient() throws InternalClientException {
        client = new UserAccountHandlerClient(eSciDocUri);
        client.setTransport(TransportProtocol.REST);
        client.setHandle(handle);
    }

    public Collection<UserAccount> findAll() throws EscidocClientException {
        userAccounts = client.retrieveUserAccountsAsList(withEmptyFilter());
        putInMap();
        return userAccounts;
    }

    private SearchRetrieveRequestType withEmptyFilter() {
        final SearchRetrieveRequestType request = new SearchRetrieveRequestType();
        request.setMaximumRecords(new NonNegativeInteger(AppConstants.MAX_RESULT_SIZE));
        return request;
    }

    private void putInMap() {
        for (final UserAccount user : userAccounts) {
            userAccountById.put(user.getObjid(), user);
        }
    }

    public UserAccount retrieve(final String userObjectId) throws EscidocClientException {
        Preconditions.checkNotNull(userObjectId, "userObjectId is null: %s", userObjectId);
        return client.retrieve(userObjectId);
    }

    // public UserAccount update(final String objid, final String newName) throws EscidocClientException {
    // assert !(newName == null || newName.isEmpty()) : "name must not be null or empty";
    //
    // final UserAccount userAccount = retrieve(objid);
    // // TODO name the class with its responsibility
    // // final UserAccount updatedUserAccount =
    // // new UserAccountFactory().update(getSelectedUser(objid)).name(newName).build();
    // final UserAccount updatedUserAccount = new UserAccountFactory().update(userAccount).name(newName).build();
    //
    // return client.update(updatedUserAccount);
    // }

    public void deactivate(final UserAccount userAccount) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(userAccount.getLastModificationDate());
        client.deactivate(userAccount.getObjid(), taskParam);
    }

    public void activate(final UserAccount updatedUserAccount) throws EscidocClientException {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(updatedUserAccount.getLastModificationDate());
        client.activate(updatedUserAccount.getObjid(), taskParam);
    }

    // public UserAccount create(final String name, final String loginName) throws EscidocException,
    // InternalClientException, TransportException {
    // assert !(name == null || name.isEmpty()) : "name can not be null or empty";
    // assert !(loginName == null || loginName.isEmpty()) : "Login name can not be null or empty";
    //
    // final UserAccount backedUserAccount = new UserAccountFactory().create(name, loginName).build();
    //
    // final UserAccount createdUserAccount = client.create(backedUserAccount);
    // assert createdUserAccount != null : "Got null reference from the server.";
    // assert createdUserAccount.getObjid() != null : "ObjectID can not be null.";
    // assert userAccountById != null : "userAccountById is null";
    // final int sizeBefore = userAccountById.size();
    // userAccountById.put(createdUserAccount.getObjid(), createdUserAccount);
    // final int sizeAfter = userAccountById.size();
    // assert sizeAfter > sizeBefore : "user account is not added to map.";
    // return createdUserAccount;
    // }

    public UserAccount delete(final String objectId) throws EscidocClientException {
        client.delete(objectId);
        return userAccountById.remove(objectId);
    }

    public UserAccount getUserById(final String objectId) throws EscidocClientException {

        if (userAccounts == null) {
            findAll();
        }

        return userAccountById.get(objectId);
    }

    public Collection<Grant> retrieveCurrentGrants(final String objectId) throws InternalClientException,
        TransportException, EscidocClientException {
        return getRestClient().retrieveCurrentGrants(objectId);
    }

    private UserAccountHandlerClientInterface getRestClient() {
        client.setTransport(TransportProtocol.REST);
        return client;
    }

    public void assign(final String userId, final String roleId) throws EscidocClientException {
        final Grant grant = new Grant();
        final GrantProperties gProp = new GrantProperties();
        gProp.setRole(new RoleRef(roleId));
        grant.setGrantProperties(gProp);
        client.createGrant(userId, grant);
    }

    public UserService assign(final UserAccount user) {
        if (user == null) {
            throw new IllegalArgumentException("UserAccount can not be null.");
        }
        this.user = user;
        return this;
    }

    public UserService withRole(final Role selectedRole) {
        if (selectedRole == null) {
            throw new IllegalArgumentException("Role can not be null.");
        }
        if (user == null) {
            throw new IllegalArgumentException("You must sign a role to a user.");
        }
        grantProps = new GrantProperties();
        grantProps.setRole(new RoleRef(selectedRole.getObjid()));
        return this;
    }

    public UserService onResources(final Set<ContextRef> selectedResources) {
        for (final Reference resourceRef : selectedResources) {
            grantProps.setAssignedOn(resourceRef);
        }
        return this;
    }

    public void execute() throws EscidocClientException {
        final Grant grant = new Grant();
        grant.setGrantProperties(grantProps);
        client.createGrant(user.getObjid(), grant);
    }

    public void revokeGrant(final String userId, final Grant grant, final String comment) throws EscidocClientException {
        final TaskParam tp = new TaskParam();
        tp.setLastModificationDate(grant.getLastModificationDate());
        tp.setComment(comment);
        client.revokeGrant(userId, grant.getObjid(), tp);
    }

    public void updatePassword(final UserAccount user, final String newPassword) throws EscidocClientException {

        preconditions(user, newPassword);
        client.updatePassword(user.getObjid(), with(user, newPassword));
    }

    public void updatePassword(final String userId, final String newPassword, final DateTime lastModificationDate)
        throws EscidocClientException {
        preconditions(userId, newPassword, lastModificationDate);

        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(lastModificationDate);
        taskParam.setPassword(newPassword);

        client.updatePassword(userId, taskParam);
    }

    private void preconditions(final String userId, final String newPassword, final DateTime lastModificationDate) {

        Preconditions.checkNotNull(userId, "userId is null: %s", userId);
        Preconditions.checkNotNull(newPassword, "newPassword is null: %s", newPassword);
        Preconditions.checkNotNull(lastModificationDate, "lastModificationDate is null: %s", lastModificationDate);

        Preconditions.checkArgument(!userId.isEmpty(), "userId is empty: %s", userId);
        Preconditions.checkArgument(!newPassword.isEmpty(), "newPassword is empty: %s", newPassword);
    }

    private TaskParam with(final UserAccount user, final String newPassword) {
        final TaskParam taskParam = new TaskParam();
        taskParam.setLastModificationDate(user.getLastModificationDate());
        taskParam.setPassword(newPassword);
        return taskParam;
    }

    private void preconditions(final UserAccount user, final String newPassword) {

        Preconditions.checkNotNull(user, "user is null: %s", user);
        Preconditions.checkNotNull(newPassword, "newPassword is null: %s", newPassword);

        Preconditions.checkNotNull(user.getObjid(), "user.getObjid() is null: %s", user.getObjid());
        Preconditions.checkArgument(!newPassword.isEmpty(), "newPassword is empty: %s", newPassword);
    }

    public List<String> retrieveOrgUnitsFor(final String objectId) throws EscidocClientException {
        Preconditions.checkNotNull(objectId, "objectId is null: %s", objectId);
        Preconditions.checkArgument(!objectId.isEmpty(), objectId, "objectId is empty: %s");

        final List<String> orgUnits = new ArrayList<String>();
        for (final Attribute attribute : client.retrieveAttributes(objectId)) {
            if (nameIsEqualsO(attribute)) {
                orgUnits.add(attribute.getValue());
            }
        }

        return orgUnits;
    }

    public Attributes retrieveAttributes(final String objectId) throws EscidocClientException {
        return client.retrieveAttributes(objectId);
    }

    private boolean nameIsEqualsO(final Attribute attribute) {
        return !attribute.getName().isEmpty() && ORGANIZATIONAL_UNIT_DEFAULT_ATTRIBUTE_NAME.equals(attribute.getName());
    }

    public void assign(final String objectId, final Attribute attribute) throws EscidocClientException {
        Preconditions.checkNotNull(objectId, "objectId is null: %s", objectId);
        Preconditions.checkNotNull(attribute, "attribute is null: %s", attribute);
        client.createAttribute(objectId, attribute);
    }

    public void updateAttribute(final String objectId, final Attribute attribute) throws EscidocClientException {
        Preconditions.checkNotNull(objectId, "objectId is null: %s", objectId);
        Preconditions.checkNotNull(attribute, "attribute is null: %s", attribute);
        client.updateAttribute(objectId, attribute);
    }

    public UserAccount getCurrentUser() throws EscidocClientException {
        return client.retrieveCurrentUser();
    }

    public void removeAttribute(final String objectId, final Attribute attribute) throws EscidocClientException {
        Preconditions.checkNotNull(objectId, "objectId is null: %s", objectId);
        Preconditions.checkNotNull(attribute, "attribute is null: %s", attribute);
        client.deleteAttribute(objectId, attribute.getObjid());
    }

}
