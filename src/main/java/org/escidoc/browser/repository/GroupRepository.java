package org.escidoc.browser.repository;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.helper.Util;

import java.net.MalformedURLException;
import java.util.List;

import de.escidoc.core.client.UserGroupHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.UserGroupHandlerClientInterface;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class GroupRepository implements Repository {

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
        throw new UnsupportedOperationException("not-yet-implemented.");
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
}