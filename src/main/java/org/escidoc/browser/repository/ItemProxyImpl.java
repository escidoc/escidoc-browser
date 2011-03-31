package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;

public class ItemProxyImpl implements ResourceProxy {
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
        return itemFromCore.getProperties().getXLinkTitle();
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
        // TODO Auto-generated method stub
        return null;
    }

}
