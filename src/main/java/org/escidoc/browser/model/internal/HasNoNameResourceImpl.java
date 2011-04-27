package org.escidoc.browser.model.internal;

import org.escidoc.browser.model.ResourceType;

public class HasNoNameResourceImpl implements HasNoNameResource {

    private final String resourceId;

    private final ResourceType type;

    public HasNoNameResourceImpl(final String resourceId, final ResourceType type) {
        this.resourceId = resourceId;
        this.type = type;
    }

    public String getId() {
        return resourceId;
    }

    public ResourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HasNoNameResource [");
        if (getId() != null) {
            builder.append("getId()=").append(getId()).append(", ");
        }
        if (getType() != null) {
            builder.append("getType()=").append(getType());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HasNoNameResourceImpl other = (HasNoNameResourceImpl) obj;
        if (resourceId == null) {
            if (other.resourceId != null) {
                return false;
            }
        }
        else if (!resourceId.equals(other.resourceId)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

}
