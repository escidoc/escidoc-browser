package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class ContainerModel extends AbstractResourceModel {

    public ContainerModel(final Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTAINER;
    }

    @Override
    public String toString() {
        return "ContainerModel [getType()=" + getType() + ", getId()="
            + getId() + ", getName()=" + getName() + "]";
    }
}