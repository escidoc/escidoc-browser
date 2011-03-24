package org.escidoc.browser.model;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.Resource;

public abstract class AbstractResourceModel implements ResourceModel {

    private final String id;

    private final String name;

    public AbstractResourceModel(final Resource resource) {
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

    public abstract ResourceType getType();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final AbstractResourceModel other = (AbstractResourceModel) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        }
        else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}