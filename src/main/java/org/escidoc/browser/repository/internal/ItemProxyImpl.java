package org.escidoc.browser.repository.internal;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.ItemProxy;

import de.escidoc.core.common.exceptions.remote.system.SystemException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;
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
        return itemFromCore.getProperties().getDescription();
    }

    @Override
    public String getStatus() {
        return itemFromCore.getProperties().getPublicStatus();
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
    public VersionHistory getPreviousVersion() {
        if (itemFromCore.getVersionNumber() > 1)
            try {
                return itemFromCore.getVersionHistory();
            }
            catch (SystemException e) {
                return null;
            }
        return null;
    }

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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemProxyImpl other = (ItemProxyImpl) obj;
        if (itemFromCore == null) {
            if (other.getId() != null)
                return false;
        }
        else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}
