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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.repository.internal;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.structmap.ContainerMemberRef;
import de.escidoc.core.resources.common.structmap.MemberRef;
import de.escidoc.core.resources.common.structmap.StructMap;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.container.ContainerProperties;

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

    @Override
    public void setName(final String name) {
        final MetadataRecords mdRecs = getMedataRecords();
        Document doc;
        try {
            doc = createNewDocument();
            for (final MetadataRecord metadataRecord : mdRecs) {
                if (metadataRecord.getName() == "escidoc") {
                    final Element element = doc.createElementNS(AppConstants.DC_NAMESPACE, "dc");
                    final Element titleElmt = doc.createElementNS(AppConstants.DC_NAMESPACE, "title");
                    titleElmt.setPrefix("dc");
                    titleElmt.setTextContent(name);
                    element.replaceChild(titleElmt, element);
                }
            }
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
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

    @Override
    public void setStatus(final String status) {
        PublicStatus pubStatus = null;
        if (status.equals("pending")) {
            pubStatus = PublicStatus.PENDING;
        }
        final ContainerProperties prop = new ContainerProperties();
        prop.setPublicStatus(pubStatus);
        containerFromCore.setProperties(prop);
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
    public boolean getPreviousVersion() {

        if (containerFromCore.getProperties().getLatestVersion().getNumber() != "1") {
            return true;
        }
        return false;
    }

    @Override
    public MetadataRecords getMedataRecords() {
        return containerFromCore.getMetadataRecords();
    }

    public StructMap getStructMap() {
        return containerFromCore.getStructMap();
    }

    public void setStruct(final String objId) {
        final StructMap stMap = getStructMap();

        final MemberRef m = new ContainerMemberRef(objId);

        stMap.add(m);
        containerFromCore.setStructMap(stMap);
    }

    @Override
    public boolean getVersionHistory() {
        final String version = containerFromCore.getProperties().getVersion().getNumber();
        final int versionNumber = Integer.parseInt(version);
        if (versionNumber > 1) {
            return true;
        }
        return false;
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
