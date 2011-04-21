package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public interface Repository {

    void loginWith(final String handle) throws InternalClientException;

    List<ResourceModel> findAll() throws EscidocClientException;

    List<ResourceModel> findTopLevelMembersById(String id)
        throws EscidocClientException;

    ResourceProxy findById(String id) throws EscidocClientException;

    VersionHistory getVersionHistory(String id) throws EscidocClientException;

    Relations getRelations(String id) throws EscidocClientException;
}
