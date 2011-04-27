package org.escidoc.browser.model;

import org.escidoc.browser.repository.Repository;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceModelFactory {

    private final Repository itemRepo;

    private final Repository containerRepo;

    public ResourceModelFactory(final Repository itemRepo,
        final Repository containerRepo) {
        Preconditions.checkNotNull(itemRepo, "itemRepo is null: %s", itemRepo);
        Preconditions.checkNotNull(containerRepo, "containerRepo is null: %s",
            containerRepo);
        this.itemRepo = itemRepo;
        this.containerRepo = containerRepo;
    }

    public ResourceModel find(final String id, final ResourceType type)
        throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkNotNull(type, "type is null: %s", type);
        switch (type) {
            case ITEM: {
                return itemRepo.findById(id);
            }
            case CONTAINER: {
                return containerRepo.findById(id);
            }
            default:
                throw new UnsupportedOperationException("Not supported type: "
                    + type);
        }
    }
}
