package org.escidoc.browser.model;

import org.escidoc.browser.repository.Repository;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceModelFactory {

    private final Repository itemRepo;

    private final Repository containerRepo;

    private final Repository contextRepository;

    public ResourceModelFactory(final Repository itemRepo, final Repository containerRepo, Repository contextRepository) {
        Preconditions.checkNotNull(itemRepo, "itemRepo is null: %s", itemRepo);
        Preconditions.checkNotNull(containerRepo, "containerRepo is null: %s", containerRepo);
        Preconditions.checkNotNull(contextRepository, "contextRepository is null: %s", contextRepository);
        this.itemRepo = itemRepo;
        this.containerRepo = containerRepo;
        this.contextRepository = contextRepository;
    }

    public ResourceModel find(final String id, final ResourceType type) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkNotNull(type, "type is null: %s", type);
        switch (type) {
            case ITEM: {
                return itemRepo.findById(id);
            }
            case CONTAINER: {
                return containerRepo.findById(id);
            }
            case CONTEXT: {
                return contextRepository.findById(id);
            }
            default:
                throw new UnsupportedOperationException("Not supported type: " + type);
        }
    }
}
