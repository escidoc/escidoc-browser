package org.escidoc.browser.repository;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.common.MetadataRecord;
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
        return ResourceType.valueOf(itemFromCore.getResourceType().toString());
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
        return itemFromCore
            .getProperties().getCreationDate().toString("d.M.y, H:m");
    }

    @Override
    public String getModifier() {
        return itemFromCore
            .getProperties().getVersion().getModifiedBy().getXLinkTitle();
    }

    @Override
    public String getModifiedOn() {
        return itemFromCore
            .getProperties().getVersion().getDate().toString("d.M.y, H:m");
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
        return Boolean.FALSE;
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
    public List<String> getMedataRecords() {
        final List<String> metadataList = new ArrayList<String>();
        for (final MetadataRecord metadataRecord : itemFromCore
            .getMetadataRecords()) {
            metadataList.add(metadataRecord.getName());
        }

        return metadataList;
    }

}
