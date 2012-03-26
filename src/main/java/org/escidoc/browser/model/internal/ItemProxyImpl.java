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
package org.escidoc.browser.model.internal;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

public class ItemProxyImpl implements ItemProxy {
    private final Item itemFromCore;

    public ItemProxyImpl(final Item resource) {
        itemFromCore = resource;
    }

    @Override
    public String getId() {
        return itemFromCore.getObjid();
    }

    @Override
    public String getName() {
        return itemFromCore.getXLinkTitle();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.valueOf(itemFromCore.getResourceType().toString().toUpperCase());
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getStatus() {
        return itemFromCore.getProperties().getPublicStatus().toString().toLowerCase();
    }

    @Override
    public String getCreator() {
        return itemFromCore.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getCreatedOn() {
        return itemFromCore.getProperties().getCreationDate().toString("d.M.y, H:mm");
    }

    @Override
    public String getModifier() {
        return itemFromCore.getProperties().getVersion().getModifiedBy().getXLinkTitle();
    }

    @Override
    public String getModifiedOn() {
        return itemFromCore.getProperties().getVersion().getDate().toString("d.M.y, H:mm");
    }

    @Override
    public List<String> getRelations() {
        final List<String> relationList = new ArrayList<String>();
        for (final Relation relation : itemFromCore.getRelations()) {
            relationList.add(relation.getXLinkTitle());
        }
        return relationList;
    }

    @Override
    public Boolean hasPreviousVersion() {
        final String version = itemFromCore.getProperties().getVersion().getNumber();
        final int versionNumber = Integer.parseInt(version);
        if (versionNumber > 1) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean hasComponents() {
        return Boolean.valueOf(itemFromCore.getComponents().size() != 0);
    }

    /**
     * Get the first component in an Item Make sure the Item contains components
     * 
     * @return
     */
    public Component getFirstelementProperties() {
        return itemFromCore.getComponents().getFirst();
    }

    @Override
    public Components getElements() {
        return itemFromCore.getComponents();
    }

    @Override
    public MetadataRecords getMedataRecords() {
        return itemFromCore.getMetadataRecords();
    }

    @Override
    public String getContentUrl() {
        return itemFromCore.getProperties().getXLinkHref();
    }

    @Override
    public Resource getContext() {
        return itemFromCore.getProperties().getContext();
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ItemProxyImpl other = (ItemProxyImpl) obj;
        if (itemFromCore == null) {
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
    public String getLockStatus() {
        return itemFromCore.getProperties().getLockStatus().toString().toLowerCase();
    }

    @Override
    public String getLatestVersionId() {
        return itemFromCore.getProperties().getLatestVersion().getObjid();
    }

    @Override
    public String getVersionStatus() {
        return itemFromCore.getProperties().getVersion().getStatus();
    }

    public String getReleasedBy() {
        return itemFromCore.getProperties().getVersion().getModifiedBy().getXLinkTitle();
    }

    public String getLatestVersionModifiedOn() {
        return itemFromCore.getProperties().getVersion().getDate().toString("d.M.y, HH:mm");
    }

    @Override
    public Resource getContentModel() {
        return itemFromCore.getProperties().getContentModel();
    }

    @Override
    public String getCurrentVersionId() {
        return itemFromCore.getProperties().getVersion().getObjid();
    }

    @Override
    public Boolean hasVersionHistory() {
        final String version = itemFromCore.getProperties().getVersion().getNumber();
        final int versionNumber = Integer.parseInt(version);
        if (versionNumber > 1) {
            return true;
        }
        return false;
    }

    @Override
    public Resource getResource() {
        return itemFromCore;
    }

    public Component getComponent(String id) {
        return itemFromCore.getComponents().get(id);
    }
}
