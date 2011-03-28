package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ItemRepository implements Repository {

    private final EscidocServiceLocation serviceLocation;

    public ItemRepository(final EscidocServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(final String id)
        throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public ResourceProxy findById(final String id)
        throws EscidocClientException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
