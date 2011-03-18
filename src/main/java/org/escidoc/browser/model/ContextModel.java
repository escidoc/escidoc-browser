package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class ContextModel extends AbstractResourceModel {

    public ContextModel(final Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTEXT;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ContextModel [");
        if (getType() != null) {
            builder.append("getType()=").append(getType()).append(", ");
        }
        if (getId() != null) {
            builder.append("getId()=").append(getId()).append(", ");
        }
        if (getName() != null) {
            builder.append("getName()=").append(getName());
        }
        builder.append("]");
        return builder.toString();
    }

    public static boolean isContext(final ResourceModel resource) {
        return resource.getType().equals(ResourceType.CONTEXT);
    }
}