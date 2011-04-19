package org.escidoc.browser.repository.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.ContainerProxy;

import com.google.common.base.Preconditions;

import de.escidoc.core.common.exceptions.remote.system.SystemException;
import de.escidoc.core.resources.common.MetadataRecord;
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
        return ResourceType.valueOf(containerFromCore
            .getResourceType().toString());
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
        return containerFromCore
            .getProperties().getCreationDate().toString("d.M.y, H:m");
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
        return containerFromCore.getLastModificationDate().toString(
            "d.M.y, H:m");
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
    public Boolean hasPreviousVersion() {
        if (containerFromCore.getVersionNumber() > 1)
            return true;

        return false;
    }

    @Override
    public List<String> getMedataRecords() {
        List<String> metadataList = new ArrayList<String>();
        for (MetadataRecord metadataRecord : containerFromCore
            .getMetadataRecords()) {
            metadataList.add(metadataRecord.getName());
        }
        return metadataList;
    }

    @Override
    public Collection<Version> getVersionHistory() {
        try {

            VersionHistory vh = containerFromCore.getVersionHistory();
            Collection<Version> v = vh.getVersions();
            return v;
        }
        catch (SystemException e) {
            return null;
            // e.printStackTrace();
        }

    }

}
