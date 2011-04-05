package org.escidoc.browser.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentProperties;

public class ItemProxyImpl implements ItemProxy {
    private final Item itemFromCore;
    
    public ItemProxyImpl(Item resource) {
        itemFromCore=resource;
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
        return ResourceType.valueOf(itemFromCore
            .getResourceType().toString());
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
        return itemFromCore.getProperties().getCreatedBy().getObjid();
    }

    @Override
    public String getCreatedOn() {
        return itemFromCore.getProperties().getCreationDate().toString();
    }

    @Override
    public String getModifier() {
        return itemFromCore.getProperties().getVersion().getModifiedBy().getObjid();
    }

    @Override
    public String getModifiedOn() {
        return itemFromCore.getProperties().getVersion().getDate().toString();    
    }

    @Override
    public List<String> getRelations() {
        List<String> relationList = new ArrayList<String>();
        for (Relation relation : itemFromCore.getRelations()) {
            relationList.add(relation.getXLinkTitle());
        }
        return relationList;
    }

    @Override
    public Boolean hasPreviousVersion() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Boolean hasComponents(){
        if ( (itemFromCore.getComponents().size())!=0 )
            return true;
        return false;
    }
    
    /**
     * Get the first component in an Item
     * Make sure the Item contains components
     * @return
     */
    public  Component getFirstelementProperties(){
        return itemFromCore.getComponents().getFirst();
    }

    public List<String> getMedataRecords() {
        List<String> metadataList = new ArrayList<String>();
        for (MetadataRecord metadataRecord : itemFromCore
            .getMetadataRecords()) {
            metadataList.add(metadataRecord.getName());
        }
        return metadataList;
    }
    

}
