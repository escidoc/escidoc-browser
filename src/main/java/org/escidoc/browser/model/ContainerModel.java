package org.escidoc.browser.model;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.Resource;

public class ContainerModel implements ResourceModel {

    private final String id;

    private final String name;

    public ContainerModel(final Resource resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        id = resource.getObjid();
        name = resource.getXLinkTitle();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTAINER;
    }

    @Override
    public String toString() {
        return "ContainerModel [getType()=" + getType() + ", getId()=" + getId() + ", getName()=" + getName() + "]";
    }

    public static boolean isContainer(final ResourceModel resource) {
        return resource.getType().equals(ResourceType.CONTAINER);
    }

}