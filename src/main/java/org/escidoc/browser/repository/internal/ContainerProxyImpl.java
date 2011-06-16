/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.repository.internal;

import java.util.Collection;
import java.util.List;

import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.ContainerProxy;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.structmap.ContainerMemberRef;
import de.escidoc.core.resources.common.structmap.MemberRef;
import de.escidoc.core.resources.common.structmap.StructMap;
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
        return ResourceType.valueOf(containerFromCore.getResourceType().toString().toUpperCase());
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
        return containerFromCore.getProperties().getPublicStatus().toString().toLowerCase();
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
        return containerFromCore.getProperties().getCreationDate().toString("d.M.y, HH:mm");
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
        return containerFromCore.getLastModificationDate().toString("d.M.y, HH:mm");
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
        // if (containerFromCore.getVersionNumber() > 1) {
        // try {
        // return containerFromCore.getVersionHistory();
        // }
        // catch (final SystemException e) {
        // return null;
        // }
        // }
        return null;
    }

    @Override
    public MetadataRecords getMedataRecords() {
        return containerFromCore.getMetadataRecords();
    }

    public StructMap getStructMap() {
        return containerFromCore.getStructMap();
    }

    public void setStruct(String objId) {
        StructMap stMap = getStructMap();

        MemberRef m = new ContainerMemberRef(objId);

        stMap.add(m);
        containerFromCore.setStructMap(stMap);
    }

    @Override
    public Collection<Version> getVersionHistory() {
        // final VersionHistory vh = containerFromCore.getVersionHistory();
        // final Collection<Version> v = vh.getVersions();
        // return v;

        return null;
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
