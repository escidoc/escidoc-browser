package org.escidoc.browser.repository;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class BaseRepository implements Repository {

    @Override
    public void loginWith(String handle) throws InternalClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
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
