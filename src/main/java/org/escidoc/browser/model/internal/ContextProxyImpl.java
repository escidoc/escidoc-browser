package org.escidoc.browser.model.internal;

import java.util.List;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.om.context.AdminDescriptors;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class ContextProxyImpl implements ResourceProxy {

    private final Context contextFromCore;

    public ContextProxyImpl(final Context resource) {
        contextFromCore = resource;
    }

    @Override
    public String getId() {
        return contextFromCore.getObjid();
    }

    @Override
    public String getName() {
        return contextFromCore.getXLinkTitle();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.valueOf(contextFromCore.getResourceType().toString());
    }

    @Override
    public String getDescription() {
        return contextFromCore.getProperties().getDescription();
    }

    @Override
    public String getStatus() {
        return contextFromCore.getProperties().getPublicStatus();
    }

    @Override
    public String getCreator() {
        return contextFromCore.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getCreatedOn() {
        return contextFromCore.getProperties().getCreationDate().toString("d.M.y, H:m");
    }

    @Override
    public String getModifier() {
        return contextFromCore.getProperties().getModifiedBy().getXLinkTitle();
    }

    @Override
    public String getModifiedOn() {
        return contextFromCore.getLastModificationDate().toString("d.M.y, H:m");
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public AdminDescriptors getAdminDescription() {
        return contextFromCore.getAdminDescriptors();
    }

    public OrganizationalUnitRefs getOrganizationalUnit() {
        return contextFromCore.getProperties().getOrganizationalUnitRefs();
    }

    @Override
    public Resource getContext() {
        return contextFromCore;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContextProxyImpl other = (ContextProxyImpl) obj;
        if (contextFromCore == null) {
            if (other.getId() != null)
                return false;
        }
        else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}
