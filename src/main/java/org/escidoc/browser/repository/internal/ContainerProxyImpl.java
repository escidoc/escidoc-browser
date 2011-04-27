package org.escidoc.browser.repository.internal;

import java.util.Collection;
import java.util.List;

import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.ContainerProxy;

import com.google.common.base.Preconditions;

import de.escidoc.core.common.exceptions.remote.system.SystemException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.versionhistory.Version;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
import de.escidoc.core.resources.om.container.Container;

public class ContainerProxyImpl implements ContainerProxy {
    private final Container containerFromCore;

    public ContainerProxyImpl(final Container resource) {
        Preconditions.checkNotNull(resource, "resource is null.");
        containerFromCore = resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getId()
     */
    @Override
    public String getId() {
        return containerFromCore.getObjid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getName()
     */
    @Override
    public String getName() {
        return containerFromCore.getXLinkTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getType()
     */
    @Override
    public ResourceType getType() {
        return ResourceType.valueOf(containerFromCore.getResourceType().toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getDescription()
     */
    @Override
    public String getDescription() {
        return containerFromCore.getProperties().getDescription();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getStatus()
     */
    @Override
    public String getStatus() {
        return containerFromCore.getProperties().getPublicStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getCreator()
     */
    @Override
    public String getCreator() {
        return containerFromCore.getProperties().getCreatedBy().getXLinkTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getCreatedOn()
     */
    @Override
    public String getCreatedOn() {
        return containerFromCore.getProperties().getCreationDate().toString("d.M.y, H:m");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getModifier()
     */
    @Override
    public String getModifier() {
        return containerFromCore.getProperties().getCreatedBy().getXLinkTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getModifiedOn()
     */
    @Override
    public String getModifiedOn() {
        return containerFromCore.getLastModificationDate().toString("d.M.y, H:m");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#getRelations()
     */
    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.repository.ContainerProxy#hasPreviousVersion()
     */
    @Override
    public VersionHistory getPreviousVersion() {
        if (containerFromCore.getVersionNumber() > 1) {
            try {
                return containerFromCore.getVersionHistory();
            }
            catch (final SystemException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public MetadataRecords getMedataRecords() {
        return containerFromCore.getMetadataRecords();
    }

    @Override
    public Collection<Version> getVersionHistory() {
        try {

            final VersionHistory vh = containerFromCore.getVersionHistory();
            final Collection<Version> v = vh.getVersions();
            return v;
        }
        catch (final SystemException e) {
            return null;
            // e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
        final ContainerProxyImpl other = (ContainerProxyImpl) obj;
        if (containerFromCore == null) {
            if (other.getId() != null) {
                return false;
            }
        }
        else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public Resource getContext() {
        return containerFromCore.getProperties().getContext();
    }
}
